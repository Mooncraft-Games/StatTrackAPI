package org.madblock.lib.stattrack.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.statistic.FinalEntityID;
import org.madblock.lib.stattrack.statistic.ITrackedEntityID;

public class Util {

    public static ITrackedEntityID getPlayerEntityID(Player player) {
        String xuid = player.getLoginChainData().getXUID();
        return ((xuid == null) || (xuid.length() == 0)) ?
                new FinalEntityID(ITrackedEntityID.BEDROCK_NO_AUTH_PLAYER_TYPE, player.getLoginChainData().getUsername()): // Not authenticated with XBL
                new FinalEntityID(ITrackedEntityID.BEDROCK_AUTH_PLAYER_TYPE, xuid); // Authenticated with XBL
    }

    public static ITrackedEntityID getServerEntityID() {
        Server server = StatTrackAPI.get().getServer();
        return new FinalEntityID(ITrackedEntityID.SERVER_TYPE, server.getIp()+":"+String.valueOf(server.getPort()));
    }

    public static String getOSFriendlyName(int os) {
        switch (os) {
            case 7:
                return "win10";
            default:
                return String.valueOf(os);
        }
    }

}
