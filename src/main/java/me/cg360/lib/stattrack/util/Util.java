package me.cg360.lib.stattrack.util;

import cn.nukkit.Player;
import me.cg360.lib.stattrack.statistic.FinalEntityID;
import me.cg360.lib.stattrack.statistic.ITrackedEntityID;

public class Util {

    public static ITrackedEntityID getPlayerEntityID(Player player) {
        String xuid = player.getLoginChainData().getXUID();
        return ((xuid == null) || (xuid.length() == 0)) ?
                new FinalEntityID(ITrackedEntityID.BEDROCK_NO_AUTH_PLAYER_TYPE, player.getLoginChainData().getUsername()): // Not authenticated with XBL
                new FinalEntityID(ITrackedEntityID.BEDROCK_AUTH_PLAYER_TYPE, xuid); // Authenticated with XBL
    }

}
