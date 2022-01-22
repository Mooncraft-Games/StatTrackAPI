package org.madblock.lib.stattrack.storage.type.history;

import org.madblock.database.ConnectionWrapper;
import org.madblock.database.DatabaseAPI;
import org.madblock.database.DatabaseStatement;
import org.madblock.database.DatabaseUtility;
import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.storage.database.TransactionStatus;
import org.madblock.lib.stattrack.storage.type.AbstractStatStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

//TODO: Add a way to remove specific history entries;
// Also finish this.
//

public class HistoryStatStorage extends AbstractStatStorage<Double> {

    public static final String CREATE_STAT_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS stat_history (target_type VARCHAR(16), target_id VARCHAR(64), stat_id VARCHAR(64), timestamp INT8, value DOUBLE, PRIMARY KEY (target_type, target_id, stat_id, timestamp));";
    public static final String CLEAR_REMOTE_STATISTIC = "DELETE FROM stat_history WHERE target_type = ? AND target_id = ? AND stat_id=?";

    public static final String TYPE = "history";

    protected HashMap<ITrackedHolderID, HistoryStatistic> entries;


    protected HistoryStatStorage(String statisticID, double defaultValue) {
        super(CREATE_STAT_HISTORY_TABLE, TYPE, statisticID, defaultValue);
        this.entries = new HashMap<>();
    }


    @Override
    public void put(ITrackedHolderID holder, Double value) {

    }

    @Override
    public void discardLocal(ITrackedHolderID holder) {

    }

    @Override
    public TransactionStatus discardRemote(ITrackedHolderID holder) {
        TransactionStatus status = TransactionStatus.ERROR;
        String srcName = StatTrackAPI.get().getStorageProvider().getDataSourceName();
        ConnectionWrapper wrapper = null;
        PreparedStatement stmt = null;




        return status;
    }

    @Override
    public ITrackedHolderID[] getStatHolders() {
        return this.entries.keySet().toArray(new ITrackedHolderID[0]);
    }


    @Override
    public TransactionStatus commit(ITrackedHolderID holder) {
        return null;
    }

    @Override
    public TransactionStatus fetch(ITrackedHolderID holder) {
        return null;
    }
}
