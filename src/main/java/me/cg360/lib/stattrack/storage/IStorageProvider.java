package me.cg360.lib.stattrack.storage;

import me.cg360.lib.stattrack.statistic.ITrackedEntityID;

import java.util.Optional;

public interface IStorageProvider {

    Optional<Double> fetchRemoteValue(ITrackedEntityID entity, String statisticID);

    boolean pushRemoteTotalValue(ITrackedEntityID entityID, String statisticID, double total);
    boolean pushRemoteDeltaValue(ITrackedEntityID entityID, String statisticID, double delta);

}