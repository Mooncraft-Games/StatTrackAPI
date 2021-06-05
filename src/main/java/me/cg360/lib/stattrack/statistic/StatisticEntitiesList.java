package me.cg360.lib.stattrack.statistic;

import java.util.HashMap;
import java.util.Optional;

public class StatisticEntitiesList {

    protected static StatisticEntitiesList entityList;
    protected HashMap<String, StatisticCollection> statisticEntities;

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

        if(statisticEntities.containsKey(fID)) {
            return statisticEntities.get(fID);

        } else {
            StatisticCollection collection = new StatisticCollection(entityID, fetchIfNotLoaded);
            this.statisticEntities.put(fID, collection);
            return collection;
        }
    }



    public Optional<StatisticCollection> getCollection(ITrackedEntityID entityID) {
        String fID = genEntityID(entityID);
        return Optional.ofNullable(statisticEntities.get(fID));
    }

    public static StatisticEntitiesList get() {
        return entityList;
    }
}