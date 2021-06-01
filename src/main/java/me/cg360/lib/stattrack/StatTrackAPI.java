package me.cg360.lib.stattrack;

import cn.nukkit.plugin.PluginBase;

public class StatTrackAPI extends PluginBase {

    protected static StatTrackAPI plugin = null;

    @Override
    public void onEnable() {

        try {
            plugin = this;

        } catch (Exception err) {
            plugin = null;
            err.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public static StatTrackAPI get() { return plugin; }
    public static boolean isActive() { return get() != null; }
}
