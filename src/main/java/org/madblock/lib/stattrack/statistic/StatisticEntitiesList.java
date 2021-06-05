package org.madblock.lib.stattrack.statistic;

import java.util.HashMap;
import java.util.Optional;

public class StatisticEntitiesList {

    protected static StatisticEntitiesList entityList;
    protected final HashMap<String, StatisticCollection> statisticEntities;

    public StatisticEntitiesList() {
        this.statisticEntities = new HashMap<>();
    }


    public boolean setAsPrimaryList() {
        if(entityList == null) {
            entityList = this;
            return true;
        }
        return false;
    }


    protected String genEntityID(ITrackedEntityID entityID) {
        return entityID.getEntityType().toLowerCase() + "#" +entityID.getStoredID();
    }


    // Don't block.
    public StatisticCollection createCollection(ITrackedEntityID entityID) {
        return createCollection(entityID, false);
    }

    public StatisticCollection createCollection(ITrackedEntityID entityID, boolean fetchIfNotLoaded) {
        String fID = genEntityID(entityID);
        StatisticCollection collection;

        synchronized (statisticEntities) {
            if (statisticEntities.containsKey(fID)) {
                return statisticEntities.get(fID);

            } else {
                collection = new StatisticCollection(entityID, false);
                this.statisticEntities.put(fID, collection);
            }
        }

        if(fetchIfNotLoaded) collection.fetchStatisticsFromStorage();
        return collection;
    }



    public synchronized Optional<StatisticCollection> getCollection(ITrackedEntityID entityID) {
        String fID = genEntityID(entityID);
        return Optional.ofNullable(statisticEntities.get(fID));
    }


    public synchronized StatisticCollection[] getStatisticEntities() {
        return statisticEntities.values().toArray(new StatisticCollection[0]);
    }

    public static StatisticEntitiesList get() {
        return entityList;
    }
}
