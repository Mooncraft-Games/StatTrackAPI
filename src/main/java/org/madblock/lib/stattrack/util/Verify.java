package org.madblock.lib.stattrack.util;

public class Verify {

    /**
     * Matches for an IP address of either the format:
     * - xxx.xxx.xxx.xxx  or
     * - xxx.xxx.xxx.xxx:xxxxx
     * @param ip the string to test
     * @return true if the string matches the formatting.
     */
    public static boolean isIPAddress(String ip) {
        return ip.matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(:[0-9]{1,5})?)");
    }

    public static boolean isStatisticID(String id) {
        return id.matches("([A-Za-z_.]*)");
    }

    public static String andCorrectStatisticID(String id) {
        return id.replaceAll("([^A-Za-z_.]+)", "_").toLowerCase();
    }

}
