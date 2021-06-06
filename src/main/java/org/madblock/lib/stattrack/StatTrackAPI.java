package org.madblock.lib.stattrack;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.AsyncTask;
import org.madblock.lib.stattrack.statistic.ITrackedEntityID;
import org.madblock.lib.stattrack.statistic.StatisticCollection;
import org.madblock.lib.stattrack.statistic.StatisticEntitiesList;
import org.madblock.lib.stattrack.storage.IStorageProvider;
import org.madblock.lib.stattrack.storage.database.MySQLProvider;
import org.madblock.lib.stattrack.util.Util;

import java.util.Optional;

public class StatTrackAPI extends PluginBase implements Listener {

    private static StatTrackAPI plugin = null;

    protected StatisticEntitiesList statisticEntitiesList;
    protected IStorageProvider storageProvider; // Configurable storage sources. Uses MySQL right now.

    // TODO: Config options
    protected boolean config_handlePlayerStatistics = true;
    protected boolean config_handleServerStatistics = true;
    protected boolean config_countPlayerJoins = true; // requires above player stats ^
    protected int config_updateTicks = 4000;

    @Override
    public void onEnable() {

        try {
            plugin = this;
            this.statisticEntitiesList = new StatisticEntitiesList();
            this.storageProvider = new MySQLProvider("MAIN", true);

            this.statisticEntitiesList.setAsPrimaryList();

            this.getServer().getPluginManager().registerEvents(this, this);
            this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                StatisticCollection[] stats = statisticEntitiesList.getStatisticEntities();

                for(StatisticCollection s: stats) {
                    s.pushStatisticsToStorage(true);
                    s.fetchStatisticsFromStorage();
                }

            }, config_updateTicks, config_updateTicks, true);

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
        if(config_handlePlayerStatistics) {
            ITrackedEntityID id = Util.getPlayerEntityID(event.getPlayer());
            StatisticCollection collection = StatisticEntitiesList.get().createCollection(id);
            this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {

                @Override
                public void onRun() {
                    if(!collection.fetchStatisticsFromStorage()) {
                        getSyncLogger().warning(String.format("Failed to fetch player %s's stat records.", Util.getPlayerEntityID(event.getPlayer())));

                    } else if(config_countPlayerJoins) {
                        collection.createStatistic(StatisticIDs.NETWORK_PLAYER_JOIN).increment();
                    }
                }

            });
        }

        if(config_handleServerStatistics) {
            ITrackedEntityID id = Util.getServerEntityID();
            StatisticCollection collection = StatisticEntitiesList.get().createCollection(id);

            String clientVersion = event.getPlayer().getLoginChainData().getGameVersion();
            clientVersion = ((clientVersion == null) || clientVersion.length() == 0) ? "unknown" : clientVersion;

            String clientOS = Util.getOSFriendlyName(event.getPlayer().getLoginChainData().getDeviceOS());

            collection.createStatistic(StatisticIDs.SERVER_VERSION_JOINS_PREFIX + clientVersion);
            collection.createStatistic(StatisticIDs.SERVER_PLATFORM_JOINS_PREFIX + clientOS);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(config_handlePlayerStatistics) {
            ITrackedEntityID id = Util.getPlayerEntityID(event.getPlayer());
            Optional<StatisticCollection> check = StatisticEntitiesList.get().getCollection(id);

            if(check.isPresent()) {
                StatisticCollection collection = check.get();
                this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {

                    @Override
                    public void onRun() {
                        int fails = collection.pushStatisticsToStorage();
                        if(fails > 0) {
                            getSyncLogger().warning(String.format("Push to storage missed %s/%s of player %s's stat records.",
                                    fails, collection.getStatisticRecordIDs().length, Util.getPlayerEntityID(event.getPlayer())
                            ));
                        }
                    }

                });
            }
        }
    }

    public IStorageProvider getStorageProvider() { return storageProvider; }
    public StatisticEntitiesList getStatisticEntitiesList() { return statisticEntitiesList; }

    public static StatTrackAPI get() { return plugin; }
    public static synchronized PluginLogger getSyncLogger() { return plugin.getLogger(); }
    public static boolean isActive() { return get() != null; }
}
