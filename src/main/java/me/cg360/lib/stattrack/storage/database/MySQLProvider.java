package me.cg360.lib.stattrack.storage.database;

import me.cg360.lib.stattrack.StatTrackAPI;
import me.cg360.lib.stattrack.statistic.ITrackedEntityID;
import me.cg360.lib.stattrack.storage.IStorageProvider;
import net.mooncraftgames.mantle.database.ConnectionWrapper;
import net.mooncraftgames.mantle.database.DatabaseAPI;
import net.mooncraftgames.mantle.database.DatabaseStatement;
import net.mooncraftgames.mantle.database.DatabaseUtility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class MySQLProvider implements IStorageProvider {

    public static final String CREATE_STATS_TABLE = "CREATE TABLE IF NOT EXISTS stat_tracker ( target_type VARCHAR(8), target_id VARCHAR(48), stat_id VARCHAR(20), value INT, PRIMARY KEY (target_type, target_id));";
    public static final String FETCH_BULK_REMOTE = "SELECT stat_id, value FROM stat_tracker WHERE target_type=? AND target_id=?;";
    public static final String FETCH_REMOTE = "SELECT value FROM stat_tracker WHERE target_type=? AND target_id=? AND stat_id=?;";
    public static final String PUSH_ABSOLUTE = "INSERT INTO stat_tracker (target_type, target_id, stat_id, value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
    public static final String PUSH_DELTA = "INSERT INTO stat_tracker (target_type, target_id, stat_id, value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE value = value + ?;";

    protected String dataSourceName;
    protected boolean isInitialized;

    public MySQLProvider(String dataSourceName, boolean initImmediatley) {
        this.dataSourceName = dataSourceName;
        this.isInitialized = false;
        if(initImmediatley) this.init();
    }

    public void init() {
        if(!isInitialized) {
            ConnectionWrapper wrapper = null;
            PreparedStatement stmt = null;
            try {
                wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                stmt = wrapper.prepareStatement(new DatabaseStatement(CREATE_STATS_TABLE));
                stmt.execute();
                this.isInitialized = true;

            } catch (SQLException exception) {
                exception.printStackTrace();
                StatTrackAPI.get().getLogger().error("Failed to add a statistics table on DB "+dataSourceName);

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }
        }
    }

    @Override
    public Optional<Double> fetchRemoteValue(ITrackedEntityID entity, String statisticID) {
        if(isInitialized) {

        }
        return Optional.empty();
    }

    @Override
    public Optional<HashMap<String, Double>> fetchRemoteTrackedEntity(ITrackedEntityID entity) {
        if(isInitialized) {

        }
        return Optional.empty();
    }

    @Override
    public boolean pushRemoteTotalValue(ITrackedEntityID entityID, String statisticID, double total) {
        if(isInitialized) {

        }
        return false;
    }

    @Override
    public boolean pushRemoteDeltaValue(ITrackedEntityID entityID, String statisticID, double delta) {
        if(isInitialized) {

        }
        return false;
    }

    public String getDataSourceName() { return dataSourceName; }
    public boolean isInitialized() { return isInitialized; }
}
