package org.madblock.lib.stattrack.storage;

import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;

import java.util.HashMap;
import java.util.Optional;

public interface IStorageProvider {

    Optional<Double> fetchRemoteValue(ITrackedHolderID entity, String statisticID);
    Optional<HashMap<String, Double>> fetchRemoteTrackedEntity(ITrackedHolderID entity);

    boolean pushRemoteTotalValue(ITrackedHolderID entityID, String statisticID, double total);
    boolean pushRemoteDeltaValue(ITrackedHolderID entityID, String statisticID, double delta);

}
