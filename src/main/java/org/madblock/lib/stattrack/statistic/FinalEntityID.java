package org.madblock.lib.stattrack.statistic;

public final class FinalEntityID implements ITrackedEntityID {

    private final String type;
    private final String id;

    public FinalEntityID(String type, String id) { this.type = type; this.id = id; }

    @Override public String getEntityType() { return type; }
    @Override public String getStoredID() { return id; }
}
