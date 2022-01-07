package org.madblock.lib.stattrack.util;

public class Util {

    //https://github.com/gomint/gomint/blob/195db7dcdafc4bf130df563b3f04ef08f118d447/gomint-api/src/main/java/io/gomint/player/DeviceInfo.java
    public static String getOSFriendlyName(int os) {
        switch (os) {
            // popular platforms for madblock
            case 1:
                return "andro";
            case 2:
                return "ios";
            case 7:
                return "win64";


            // thanks gomint for below:
            case 3:
                return "osx";
            case 4:
                return "amazon";
            case 5:
                return "oculus";
            case 6:
                return "hololens"; // but why.
            case 8:
                return "win32";
            case 9:
                return "dedicated";
            case 10:
                return "tv";
            case 11:
                return "sony";
            case 12:
                return "nintendo";
            case 13:
                return "xbox";
            case 14:
                return "winphone";

            default:
                return String.valueOf(os);
        }
    }

}
