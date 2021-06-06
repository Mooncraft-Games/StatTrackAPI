package org.madblock.lib.stattrack.storage.database;

import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.statistic.ITrackedEntityID;
import org.madblock.lib.stattrack.storage.IStorageProvider;
import org.madblock.lib.stattrack.util.Verify;
import org.madblock.database.ConnectionWrapper;
import org.madblock.database.DatabaseAPI;
import org.madblock.database.DatabaseStatement;
import org.madblock.database.DatabaseUtility;

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


    public static final String CREATE_STATS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(10), %s VARCHAR(48), %s VARCHAR(20), %s DOUBLE, PRIMARY KEY (%s, %s));",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID);
    public static final String FETCH_BULK_REMOTE = String.format("SELECT %s, %s FROM %s WHERE %s=? AND %s=?;",
            COLUMN_STAT_ID, COLUMN_VALUE, TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID);
    public static final String FETCH_REMOTE = String.format("SELECT %s FROM %s WHERE %s=? AND %s=? AND %s=?;",
            COLUMN_VALUE, TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID);
    public static final String PUSH_ABSOLUTE = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE %s=? WHERE %s=? AND %s=? AND %s=?;",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_VALUE, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID);
    public static final String PUSH_DELTA = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE %s = %s + ? WHERE %s=? AND %s=? AND %s=?;",
            TABLE_NAME, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID, COLUMN_VALUE, COLUMN_VALUE, COLUMN_VALUE, COLUMN_TARGET_TYPE, COLUMN_TARGET_ID, COLUMN_STAT_ID);

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
                synchronized (this) { this.isInitialized = true; }

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
            double value;

            try {
                wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                stmt = wrapper.prepareStatement(new DatabaseStatement(FETCH_REMOTE, new Object[]{ entityID.getEntityType(), entityID.getStoredID(), statID } ));
                ResultSet results = stmt.executeQuery();
                value = results.next() ? results.getDouble(COLUMN_VALUE) : 0;
                stmt.close();

            } catch (SQLException exception) {
                StatTrackAPI.getSyncLogger().error(String.format("Failed to get statistic value for %s#%s: %s", entityID.getEntityType(), entityID.getStoredID(), statID));
                exception.printStackTrace();
                return Optional.empty();

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }
            return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public Optional<HashMap<String, Double>> fetchRemoteTrackedEntity(ITrackedEntityID entityID) {
        if(isInitialized) {
            ConnectionWrapper wrapper = null;
            PreparedStatement stmt = null;

            boolean error = false;
            int duplicates = 0;
            HashMap<String, Double> data = new HashMap<>();

            try {
                wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                stmt = wrapper.prepareStatement(new DatabaseStatement(FETCH_BULK_REMOTE, new Object[]{ entityID.getEntityType(), entityID.getStoredID()} ));
                ResultSet results = stmt.executeQuery();

                while (results.next()) {
                    try {
                        // Could correct invalid/duplicate data here? Ensure the stat keys are correct at least
                        String statKey = Verify.andCorrectStatisticID(results.getString(COLUMN_STAT_ID));
                        double value = results.getDouble(COLUMN_VALUE);
                        duplicates += (data.put(statKey, value) == null ? 0 : 1);
                    } catch (Exception somewhatIgnored) { error = true; }
                }
                stmt.close();

            } catch (SQLException exception) {
                StatTrackAPI.getSyncLogger().error(String.format("Failed to get entity statistics for %s#%s", entityID.getEntityType(), entityID.getStoredID()));
                exception.printStackTrace();
                return Optional.empty();

            } finally {
                if (stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }

            if(error) StatTrackAPI.getSyncLogger().warning(String.format("Error encountered while gathering stats for %s#%s. Results may be incomplete.", entityID.getEntityType(), entityID.getStoredID()));
            if(duplicates > 0) StatTrackAPI.getSyncLogger().warning(String.format("Duplicate statistic keys found while gathering stats for %s#%s.", entityID.getEntityType(), entityID.getStoredID()));

            return Optional.of(data);
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
                stmt = wrapper.prepareStatement(new DatabaseStatement(isDelta ? PUSH_DELTA : PUSH_ABSOLUTE, new Object[]{ entityID.getEntityType(), entityID.getStoredID(), statID, value, value, entityID.getEntityType(), entityID.getStoredID(), statID } ));
                code = stmt.executeUpdate();
                stmt.close();

            } catch (SQLException exception) {
                StatTrackAPI.getSyncLogger().error(String.format("Failed to push statistic %s value.", isDelta ? "delta" : "total"));
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
