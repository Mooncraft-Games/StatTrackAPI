package me.cg360.lib.stattrack;

import cn.nukkit.plugin.PluginBase;
import me.cg360.lib.stattrack.statistic.StatisticEntitiesList;
import me.cg360.lib.stattrack.storage.IStorageProvider;
import me.cg360.lib.stattrack.storage.database.MySQLProvider;

public class StatTrackAPI extends PluginBase {

    private static StatTrackAPI plugin = null;

    protected StatisticEntitiesList statisticEntitiesList;
    protected IStorageProvider storageProvider; // Configurable storage sources. Uses MySQL right now.

    @Override
    public void onEnable() {

        try {
            plugin = this;
            this.statisticEntitiesList = new StatisticEntitiesList();
            this.storageProvider = new MySQLProvider("MAIN", true);

            this.statisticEntitiesList.setAsPrimaryList();

        } catch (Exception err) {
            plugin = null;
            err.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public IStorageProvider getStorageProvider() { return storageProvider; }
    public StatisticEntitiesList getStatisticEntitiesList() { return statisticEntitiesList; }

    public static StatTrackAPI get() { return plugin; }
    public static boolean isActive() { return get() != null; }
}
