package org.madblock.lib.stattrack.statistic.id.server;

import cn.nukkit.Player;
import org.madblock.lib.commons.style.Check;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;

public final class PlayerWrapperID implements ITrackedHolderID {

    public static final String XBOX_PLAYER_TYPE = "mco_player";
    public static final String UNAUTHENTICATED_PLAYER_TYPE = "basic_player";

    private final String type;
    private final String id;

    public PlayerWrapperID(Player player) {
        Check.nullParam(player, "player");

        if(player.getLoginChainData().isXboxAuthed()) {
            this.type = XBOX_PLAYER_TYPE;
            this.id = player.getLoginChainData().getXUID();

        } else {
            this.type = UNAUTHENTICATED_PLAYER_TYPE;
            this.id = player.getLoginChainData().getUsername();
        }
    }



    @Override
    public String getEntityType() {
        return this.type;
    }

    @Override
    public String getStoredID() {
        return this.id;
    }


    @Override
    public String toString() {
        return this.id+"@"+this.type;
    }
}
