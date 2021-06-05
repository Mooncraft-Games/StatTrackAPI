package me.cg360.lib.stattrack.storage.database;

import me.cg360.lib.stattrack.StatTrackAPI;
import me.cg360.lib.stattrack.statistic.ITrackedEntityID;
import me.cg360.lib.stattrack.storage.IStorageProvider;
import me.cg360.lib.stattrack.util.Verify;
import net.mooncraftgames.mantle.database.ConnectionWrapper;
import net.mooncraftgames.mantle.database.DatabaseAPI;
import net.mooncraftgames.mantle.database.DatabaseStatement;
import net.mooncraftgames.mantle.database.DatabaseUtility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class MySQLProvider implements IStorageProvider {

    public static final String TABLE_NAME = "stat_tracker";
    public static final String COLUMN_TARGET_TYPE = "target_type";
    public static final String COLUMN_TARGET_ID = "target_id";
    public static final String COLUMN_STAT_ID = "stat_id";
    public static final String COLUMN_VALUE = "value";


    public static final String CREATE_STATS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(8), %s VARCHAR(48), %s VARCHAR(20), %s DOUBLE, PRIMARY KEY (%s, %s));",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID);
    public static final String FETCH_BULK_REMOTE = String.format("SELECT %s, %s FROM %s WHERE %s=? AND %s=?;",
            COLUMN_STAT_ID, COLUMN_VALUE, TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID);
    public static final String FETCH_REMOTE = String.format("SELECT %s FROM %s WHERE %s=? AND %s=? AND %s=?;",
            COLUMN_VALUE, TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID);
    public static final String PUSH_ABSOLUTE = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE %s=?;",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_VALUE);
    public static final String PUSH_DELTA = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE %s = %s + ?;",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_VALUE, COLUMN_VALUE);

    protected String dataSourceName;
    protected boolean isInitialized;

    public MySQLProvider(String dataSourceName, boolean initImmediatley) {
        this.dataSourceName = dataSourceName;
        this.isInitialized = false;
        if(initImmediatley) this.init();
    }

    public boolean init() {
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
                return false;

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }
        }
        return true;
    }

    @Override
    public Optional<Double> fetchRemoteValue(ITrackedEntityID entityID, String statisticID) {
        if(isInitialized) {
            String statID = Verify.andCorrectStatisticID(statisticID);

            ConnectionWrapper wrapper = null;
            PreparedStatement stmt = null;
            Double value = null;
            try {
                wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                stmt = wrapper.prepareStatement(new DatabaseStatement(FETCH_REMOTE, new Object[]{ entityID.getEntityType(), entityID.getStoredID(), statID } ));
                ResultSet results = stmt.executeQuery();
                if(results.next()) value = results.getDouble(COLUMN_VALUE);
                stmt.close();

            } catch (SQLException exception) {
                StatTrackAPI.get().getLogger().error(String.format("Failed to get statistic value for %s#%s: %s", entityID.getEntityType(), entityID.getStoredID(), statID));
                exception.printStackTrace();
                return Optional.empty();

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    @Override
    public Optional<HashMap<String, Double>> fetchRemoteTrackedEntity(ITrackedEntityID entityID) {
        if(isInitialized) {

        }
        return Optional.empty();
    }

    @Override
    public boolean pushRemoteTotalValue(ITrackedEntityID entityID, String statisticID, double total) {
        return pushValue(entityID, statisticID, total, false);
    }

    @Override
    public boolean pushRemoteDeltaValue(ITrackedEntityID entityID, String statisticID, double delta) {
        return pushValue(entityID, statisticID, delta, true);
    }

    protected boolean pushValue(ITrackedEntityID entityID, String statisticID, double value, boolean isDelta) {
        if(isInitialized) {
            String statID = Verify.andCorrectStatisticID(statisticID);

            ConnectionWrapper wrapper = null;
            PreparedStatement stmt = null;
            int code;
            try {
                wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                stmt = wrapper.prepareStatement(new DatabaseStatement(isDelta ? PUSH_DELTA : PUSH_ABSOLUTE, new Object[]{ entityID.getEntityType(), entityID.getStoredID(), statID, value, value } ));
                code = stmt.executeUpdate();
                stmt.close();

            } catch (SQLException exception) {
                StatTrackAPI.get().getLogger().error(String.format("Failed to push statistic %s value.", isDelta ? "delta" : "total"));
                exception.printStackTrace();
                return false;

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }
            return code > 0;
        }
        return false;
    }


    public String getDataSourceName() { return dataSourceName; }
    public boolean isInitialized() { return isInitialized; }
}
