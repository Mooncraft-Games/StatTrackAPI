package org.madblock.lib.stattrack.statistic.id;

import org.madblock.lib.commons.style.Check;

import java.util.Objects;

public final class CustomHolderID implements ITrackedHolderID {

    private final String type;
    private final String id;

    public CustomHolderID(String type, String id) {
        this.type = Check.nullParam(type, "type");
        this.id = Check.nullParam(id, "id");
    }

    @Override public String getEntityType() { return type; }
    @Override public String getStoredID() { return id; }

    @Override
    public String toString() {
        return this.id+"@"+this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomHolderID that = (CustomHolderID) o;
        return type.equals(that.type) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
