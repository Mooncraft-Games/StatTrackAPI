package org.madblock.lib.stattrack;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.AsyncTask;
import org.madblock.lib.commons.data.store.settings.ControlledSettings;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.statistic.StatisticCollection;
import org.madblock.lib.stattrack.statistic.StatisticHolderList;
import org.madblock.lib.stattrack.statistic.id.server.PlayerWrapperID;
import org.madblock.lib.stattrack.statistic.id.server.ServerWrapperID;
import org.madblock.lib.stattrack.storage.IStorageProvider;
import org.madblock.lib.stattrack.storage.database.MySQLProvider;
import org.madblock.lib.stattrack.util.Util;

import java.io.File;
import java.util.Optional;

public class StatTrackAPI extends PluginBase implements Listener {

    private static StatTrackAPI plugin = null;

    protected StatisticHolderList statisticEntitiesList;
    protected IStorageProvider storageProvider; // Configurable storage sources. Uses MySQL right now.

    public ControlledSettings configuration;

    @Override
    public void onEnable() {

        try {
            plugin = this;

            File cfgDirectory = this.getDataFolder();
            this.configuration = StatTrackConfigProcessor.load(cfgDirectory, "config.json", true);

            this.statisticEntitiesList = new StatisticHolderList();
            this.storageProvider = new MySQLProvider("MAIN", true);

            this.statisticEntitiesList.setAsPrimaryList();

            this.getServer().getPluginManager().registerEvents(this, this);

            int update = this.configuration.getOrDefault(StatTrackConfigProcessor.UPDATE_TICKS);
            this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                StatisticCollection[] stats = statisticEntitiesList.getStatisticEntities();

                for(StatisticCollection s: stats) {
                    s.pushStatisticsToStorage(true);
                    s.fetchStatisticsFromStorage();
                }

            }, update, update, true);

        } catch (Exception err) {
            plugin = null;
            err.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if(plugin != null) {
            StatisticCollection[] stats = statisticEntitiesList.getStatisticEntities();
            for(StatisticCollection s: stats) s.pushStatisticsToStorage();
        }
        plugin = null;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_PLAYER_STATS)) {
            ITrackedHolderID id = new PlayerWrapperID(event.getPlayer());
            StatisticCollection collection = StatisticHolderList.get().createCollection(id);
            this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {

                @Override
                public void onRun() {
                    if(!collection.fetchStatisticsFromStorage()) {
                        getSyncLogger().warning(String.format("Failed to fetch player %s's stat records.", id));

                    } else {
                        collection.createStatistic(StatisticIDs.NETWORK_PLAYER_JOIN).increment();
                    }
                }

            });
        }

        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_SERVER_STATS)) {
            ITrackedHolderID id = new ServerWrapperID();
            StatisticCollection collection = StatisticHolderList.get().createCollection(id);

            String clientVersion = event.getPlayer().getLoginChainData().getGameVersion();
            clientVersion = ((clientVersion == null) || clientVersion.length() == 0) ? "unknown" : clientVersion;

            String clientOS = Util.getOSFriendlyName(event.getPlayer().getLoginChainData().getDeviceOS());

            collection.createStatistic(StatisticIDs.SERVER_VERSION_JOINS_PREFIX + clientVersion);
            collection.createStatistic(StatisticIDs.SERVER_PLATFORM_JOINS_PREFIX + clientOS);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_PLAYER_STATS)) {
            ITrackedHolderID id = new PlayerWrapperID(event.getPlayer());
            Optional<StatisticCollection> check = StatisticHolderList.get().getCollection(id);

            if(check.isPresent()) {
                StatisticCollection collection = check.get();
                this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {

                    @Override
                    public void onRun() {
                        int fails = collection.pushStatisticsToStorage();
                        if(fails > 0) {
                            getSyncLogger().warning(String.format("Push to storage missed %s/%s of player %s's stat records.",
                                    fails, collection.getStatisticRecordIDs().length, id)
                            );
                        }
                    }

                });
            }
        }
    }

    public IStorageProvider getStorageProvider() { return storageProvider; }
    public StatisticHolderList getStatisticEntitiesList() { return statisticEntitiesList; }

    public static StatTrackAPI get() { return plugin; }
    public static synchronized PluginLogger getSyncLogger() { return plugin.getLogger(); }
    public static boolean isActive() { return get() != null; }
}
