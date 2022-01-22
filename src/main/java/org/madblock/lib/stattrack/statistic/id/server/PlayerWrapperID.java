package org.madblock.lib.stattrack.statistic.id.server;

import cn.nukkit.Player;
import org.madblock.lib.commons.style.Check;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;

import java.util.Objects;

public final class PlayerWrapperID implements ITrackedHolderID {

    public static final String XBOX_PLAYER_TYPE = "mcoplayer";
    public static final String UNAUTHENTICATED_PLAYER_TYPE = "basicplayer";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerWrapperID that = (PlayerWrapperID) o;
        return type.equals(that.type) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
