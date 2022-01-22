package org.madblock.lib.stattrack.storage.database;

import org.madblock.database.DatabaseStatement;
import org.madblock.lib.commons.style.Check;
import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.storage.type.AbstractStatStorage;
import org.madblock.database.ConnectionWrapper;
import org.madblock.database.DatabaseAPI;
import org.madblock.database.DatabaseUtility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLProvider {

    public static final String TABLE_COUNTER = "stat_counter"; // Version 1 behaviour
    public static final String TABLE_HIGHEST = "stat_highest"; // Only updated for new highest values, used for high scores, k/d ratios, etc
    public static final String TABLE_HISTORY = "stat_history"; // Used to track a history of changes, timestamped from publish

    public static final String COLUMN_TARGET_TYPE = "target_type";
    public static final String COLUMN_TARGET_ID = "target_id";
    public static final String COLUMN_STAT_ID = "stat_id";
    public static final String COLUMN_TIMESTAMP = "timestamp"; // only on history
    public static final String COLUMN_VALUE = "value";


    public static final String CREATE_STAT_COUNTER_TABLE = "CREATE TABLE IF NOT EXISTS stat_counter (target_type VARCHAR(16), target_id VARCHAR(64), stat_id VARCHAR(64), value INT8, PRIMARY KEY (target_type, target_id, stat_id));";

    public static final String CREATE_STAT_HIGHEST_TABLE = "CREATE TABLE IF NOT EXISTS stat_highest (target_type VARCHAR(16), target_id VARCHAR(64), stat_id VARCHAR(64), value DOUBLE, PRIMARY KEY (target_type, target_id, stat_id));";


    // Used to track if the appropriate tables have been created
    @SuppressWarnings("rawtypes")
    protected ArrayList<Class<? extends AbstractStatStorage>> processedTypes;

    protected String dataSourceName;

    public MySQLProvider(String dataSourceName) {
        this.dataSourceName = dataSourceName;

        this.processedTypes = new ArrayList<>();
    }


    public TransactionStatus checkTypeInitialization(AbstractStatStorage<?> type) {
        Check.nullParam(type, "type");

        if(!this.processedTypes.contains(type.getClass())) {
            String sql = type.getTableCreationSQL();
            Check.notEmptyString(sql, "type->tableCreationSQL");

            TransactionStatus status = TransactionStatus.ERROR;
            ConnectionWrapper wrapper = null;
            PreparedStatement stmt = null;

            try {
                try {
                    wrapper = DatabaseAPI.getConnection(this.dataSourceName);
                } catch (SQLException err) {
                    StatTrackAPI.get().getLogger().error("Failed to connect to create a stat table on DB " + dataSourceName + " ("+type.getClass().getName()+")");
                    err.printStackTrace();
                    status = TransactionStatus.DB_UNREACHABLE;
                }

                if(wrapper != null) {
                    stmt = wrapper.prepareStatement(new DatabaseStatement(type.getTableCreationSQL()));
                    stmt.execute();
                    this.processedTypes.add(type.getClass());
                    status = TransactionStatus.SUCCESS;
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
                StatTrackAPI.get().getLogger().error("Failed to create a necessary stat table on DB "+dataSourceName + " ("+type.getClass().getName()+")");
                status = TransactionStatus.ERROR;

            } finally {
                if(stmt != null) DatabaseUtility.closeQuietly(stmt);
                if (wrapper != null) DatabaseUtility.closeQuietly(wrapper);
            }

            return status;
        }

        return TransactionStatus.NO_ACTION_TAKEN;
    }


    public String getDataSourceName() {
        return dataSourceName;
    }
}
