package me.cg360.lib.stattrack;

import cn.nukkit.plugin.PluginBase;
import me.cg360.lib.stattrack.storage.IStorageProvider;
import me.cg360.lib.stattrack.storage.database.MySQLProvider;

public class StatTrackAPI extends PluginBase {

    private static StatTrackAPI plugin = null;

    protected IStorageProvider storageProvider; // Configurable storage sources. Uses MySQL right now.

    @Override
    public void onEnable() {

        try {
            plugin = this;
            this.storageProvider = new MySQLProvider("MAIN", true);

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

    public static StatTrackAPI get() { return plugin; }
    public static boolean isActive() { return get() != null; }
}
