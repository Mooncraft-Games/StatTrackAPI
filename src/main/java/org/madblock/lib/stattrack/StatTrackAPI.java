package org.madblock.lib.stattrack;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.AsyncTask;
import org.madblock.lib.commons.data.store.settings.ControlledSettings;
import org.madblock.lib.stattrack.statistic.StatisticList;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.statistic.id.server.PlayerWrapperID;
import org.madblock.lib.stattrack.statistic.id.server.ServerWrapperID;
import org.madblock.lib.stattrack.storage.database.MySQLProvider;
import org.madblock.lib.stattrack.storage.type.AbstractStatStorage;
import org.madblock.lib.stattrack.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatTrackAPI extends PluginBase implements Listener {

    private static StatTrackAPI plugin = null;

    protected MySQLProvider storageProvider; // Configurable storage sources. Uses MySQL right now.
    protected StatisticList statisticList;

    protected ControlledSettings configuration;

    @Override
    public void onEnable() {

        try {
            plugin = this;

            File cfgDirectory = this.getDataFolder();
            this.configuration = StatTrackConfigProcessor.load(cfgDirectory, "config.json", true);
            this.storageProvider = new MySQLProvider("MAIN");
            this.statisticList = new StatisticList();


            this.getServer().getPluginManager().registerEvents(this, this);

            int update = this.configuration.getOrDefault(StatTrackConfigProcessor.UPDATE_TICKS);
            this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                List<? extends AbstractStatStorage<?>> stats = this.getStatisticList().getStatStores();

                for(AbstractStatStorage<?> s: stats) {
                    s.commitAll();
                    s.fetchAll();
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
            List<? extends AbstractStatStorage<?>> stats = this.getStatisticList().getStatStores();
            for(AbstractStatStorage<?> s: stats) s.commitAll();
        }

        plugin = null;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_PLAYER_STATS)) {
            ITrackedHolderID id = new PlayerWrapperID(event.getPlayer());
            StatisticList collection = this.getStatisticList(); //TODO:

            this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {

                @Override
                public void onRun() {

                }

            });
        }

        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_SERVER_STATS)) {
            //TODO: Track versions
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(this.configuration.getOrDefault(StatTrackConfigProcessor.TRACK_PLAYER_STATS)) {
            ITrackedHolderID id = new PlayerWrapperID(event.getPlayer());

            //TODO
        }
    }

    public MySQLProvider getStorageProvider() { return this.storageProvider; }
    public StatisticList getStatisticList() { return this.statisticList;}

    public static StatTrackAPI get() { return plugin; }
    public static synchronized PluginLogger getSyncLogger() { return plugin.getLogger(); }
    public static boolean isActive() { return get() != null; }
}
