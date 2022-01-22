package org.madblock.lib.stattrack.statistic;

import org.madblock.lib.commons.style.Immutable;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.storage.type.AbstractStatStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** A lookup connecting together multiple statistic storage types. */
public final class StatisticList {

    private HashMap<String, AbstractStatStorage<?>> stores;
    private HashMap<ITrackedHolderID, ArrayList<String>> statistics;

    public StatisticList() {
        this.stores = new HashMap<>();
        this.statistics = new HashMap<>();
    }


    public List<? extends AbstractStatStorage<?>> getStatStores() {
        return new ArrayList<>(stores.values());
    }

    public ITrackedHolderID[] getTrackedEntities() {
        return statistics.keySet().toArray(new ITrackedHolderID[0]);
    }

}
