package org.madblock.lib.stattrack.util;

public class Verify {

    public static boolean statisticID(String id) {
        return id.matches("([A-Za-z_]*)");
    }

    public static String andCorrectStatisticID(String id) {
        return id.replaceAll("([^A-Za-z_]+)", "_").toLowerCase();
    }

}
