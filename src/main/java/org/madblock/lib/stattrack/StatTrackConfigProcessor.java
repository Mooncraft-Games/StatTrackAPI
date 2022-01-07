package org.madblock.lib.stattrack;

import org.madblock.lib.commons.data.store.DefaultKey;
import org.madblock.lib.commons.data.store.settings.ControlledSettings;
import org.madblock.lib.commons.data.store.settings.Settings;
import org.madblock.lib.commons.json.JsonObject;
import org.madblock.lib.commons.json.io.JsonIO;
import org.madblock.lib.commons.json.io.JsonUtil;
import org.madblock.lib.commons.json.io.error.JsonEmptyException;
import org.madblock.lib.commons.json.io.error.JsonFormatException;
import org.madblock.lib.commons.json.io.error.JsonParseException;
import org.madblock.lib.commons.style.Check;

import java.io.*;

public final class StatTrackConfigProcessor {

    //TODO: Move an abstracted version to commons

    public static final DefaultKey<Boolean> TRACK_PLAYER_STATS = new DefaultKey<>("track_player_stats", true);
    public static final DefaultKey<Boolean> TRACK_SERVER_STATS = new DefaultKey<>("track_server_stats", true);

    public static final DefaultKey<Integer> UPDATE_TICKS = new DefaultKey<>("update_tick_interval", 4000);

    public static String DEFAULT_CONFIG =
            "{" + "\n" +
                    "    " + formatLine(TRACK_PLAYER_STATS) + "," + "\n" +
                    "    " + formatLine(TRACK_SERVER_STATS) + "," + "\n" +

                    "    " + formatLine(UPDATE_TICKS) + "\n" + // When extending, add comma!
            "}";

    private static int verifyAllDefaultKeys(Settings settings) {
        int replacements = 0;

        if(isSettingNull(settings, TRACK_PLAYER_STATS)) replacements++;
        if(isSettingNull(settings, TRACK_SERVER_STATS)) replacements++;
        if(isSettingNull(settings, UPDATE_TICKS)) replacements++;

        return replacements;
    }

    public static ControlledSettings load(File directory, String name, boolean fillInDefaults) {
        StatTrackAPI.getSyncLogger().info("Loading configuration...");

        Check.nullParam(directory, "directory");
        directory.mkdirs();
        File configFile = new File(directory, name);

        JsonIO json = new JsonIO();
        ControlledSettings loadedSettings;
        JsonObject root;

        try {
            root = json.read(configFile);

        } catch (FileNotFoundException | JsonEmptyException err) {
            StatTrackAPI.getSyncLogger().warning("No server configuration found! Creating a copy from default settings.");
            root = json.read(StatTrackConfigProcessor.DEFAULT_CONFIG);

            try {
                FileWriter writer = new FileWriter(configFile);
                BufferedWriter write = new BufferedWriter(writer);
                write.write(StatTrackConfigProcessor.DEFAULT_CONFIG);
                write.close();

            } catch (IOException err2) {
                StatTrackAPI.getSyncLogger().error("Unable to write a new server configuration copy:");
                err2.printStackTrace();
            }

        } catch (JsonFormatException err) {
            StatTrackAPI.getSyncLogger().error("Unable to parse json configuration! Using default settings: "+err.getMessage());
            root = json.read(StatTrackConfigProcessor.DEFAULT_CONFIG);

        } catch (JsonParseException err) {
            StatTrackAPI.getSyncLogger().error("Unable to parse json configuration due to an internal error! Using default settings.");
            err.printStackTrace();
            root = json.read(StatTrackConfigProcessor.DEFAULT_CONFIG);
        }

        // Locking it as it really shouldn't be messed with.
        // If I add plugin support, I might change this ?   idk
        loadedSettings = JsonUtil.jsonToSettings(root, false);


        if(fillInDefaults) {
            StatTrackAPI.getSyncLogger().info("Filling in the blanks!");
            int replacements = verifyAllDefaultKeys(loadedSettings);

            StatTrackAPI.getSyncLogger().info("Using the defaults for "+replacements+" properties!");
        }

        StatTrackAPI.getSyncLogger().info("Loaded configuration!");
        return loadedSettings.lock();
    }

    // Just going to assume settings isn't null as this isn't
    // an important utility method.
    public static <T> boolean isSettingNull(Settings settings, DefaultKey<T> key) {
        if(settings.get(key) == null) {
            settings.set(key, key.getDefaultValue());
            return true; // invalid, replaced.
        }

        return false; // valid
    }

    // A little method to cleanup the default config
    public static <T> String formatLine(DefaultKey<T> key) {

        Object value = key.getDefaultValue();

        // String can't be extended, this is fine.
        if(key.getDefaultValue() instanceof String)
            value = "\"" + key.getDefaultValue() + "\"";

        return String.format("\"%s\": %s", key.get(), value);
    }
}
