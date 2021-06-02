package me.cg360.lib.stattrack.storage.database;

import me.cg360.lib.stattrack.statistic.ITrackedEntityID;
import me.cg360.lib.stattrack.storage.IStorageProvider;

import java.util.HashMap;
import java.util.Optional;

public class MySQLProvider implements IStorageProvider {

    public static final String CREATE_STATS_TABLE = "CREATE TABLE IF NOT EXISTS stat_tracker ( target_type VARCHAR(8), target_id VARCHAR(48), stat_id VARCHAR(20), value INT, PRIMARY KEY (target_type, target_id));";
    public static final String FETCH_BULK_REMOTE = "SELECT stat_id, value FROM stat_tracker WHERE target_type=? AND target_id=?;";
    public static final String FETCH_REMOTE = "SELECT value FROM stat_tracker WHERE target_type=? AND target_id=? AND stat_id=?;";
    public static final String PUSH_ABSOLUTE = "INSERT INTO stat_tracker (target_type, target_id, stat_id, value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
    public static final String PUSH_DELTA = "INSERT INTO stat_tracker (target_type, target_id, stat_id, value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE value = value + ?;";

    protected String dataSourceName;

    public MySQLProvider(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public Optional<Double> fetchRemoteValue(ITrackedEntityID entity, String statisticID) {
        return Optional.empty();
    }

    @Override
    public HashMap<String, Double> fetchRemoteTrackedEntity(ITrackedEntityID entity) {
        return null;
    }

    @Override
    public boolean pushRemoteTotalValue(ITrackedEntityID entityID, String statisticID, double total) {
        return false;
    }

    @Override
    public boolean pushRemoteDeltaValue(ITrackedEntityID entityID, String statisticID, double delta) {
        return false;
    }
}
