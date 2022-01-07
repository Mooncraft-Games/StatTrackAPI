package org.madblock.lib.stattrack.statistic.id;

public final class CustomHolderID implements ITrackedHolderID {

    private final String type;
    private final String id;

    public CustomHolderID(String type, String id) { this.type = type; this.id = id; }

    @Override public String getEntityType() { return type; }
    @Override public String getStoredID() { return id; }

    @Override
    public String toString() {
        return this.id+"@"+this.type;
    }
}
