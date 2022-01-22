package org.madblock.lib.stattrack.statistic.id.server;

import cn.nukkit.Server;
import org.madblock.lib.commons.style.Check;
import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.util.Verify;

import java.util.Objects;

public class ServerWrapperID implements ITrackedHolderID {

    public static final String TYPE = "server";

    private final String id;

    public ServerWrapperID() {
        this(StatTrackAPI.get().getServer().getIp(), StatTrackAPI.get().getServer().getPort());
    }

    public ServerWrapperID(String ip, int port) {
        Check.notEmptyString(ip, "ip");
        Check.inclusiveBounds(port, 1024, 65535, "port");
        this.id = ip + ":" + port;

        if(Verify.isIPAddress(this.id))
            throw new IllegalArgumentException("IP is not of a valid format");
    }

    public ServerWrapperID(String ip) {
        this.id =  Check.notEmptyString(ip, "ip");

        if(Verify.isIPAddress(this.id))
            throw new IllegalArgumentException("IP is not of a valid format");
    }


    @Override
    public String getEntityType() {
        return TYPE;
    }

    @Override
    public String getStoredID() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id+"@"+SERVER_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerWrapperID that = (ServerWrapperID) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
