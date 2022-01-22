package org.madblock.lib.stattrack.storage.type.counter;

import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.storage.database.TransactionStatus;
import org.madblock.lib.stattrack.storage.type.AbstractStatStorage;

public class CounterStatStorage extends AbstractStatStorage<Integer> {

    public static String CREATE_STAT_COUNTER_TABLE = "CREATE TABLE IF NOT EXISTS stat_history (target_type VARCHAR(16), target_id VARCHAR(64), stat_id VARCHAR(64), value INT4, PRIMARY KEY (target_type, target_id, stat_id, timestamp));";

    public static String TYPE = "counter";


    public CounterStatStorage(String statisticID, Integer defaultValue) {
        super(CREATE_STAT_COUNTER_TABLE, TYPE, statisticID, defaultValue);
    }



    @Override
    public void put(ITrackedHolderID holder, Integer value) {

    }

    @Override
    public void discardLocal(ITrackedHolderID holder) {

    }

    @Override
    public TransactionStatus discardRemote(ITrackedHolderID holder) {
        return null;
    }

    @Override
    public ITrackedHolderID[] getStatHolders() {
        return new ITrackedHolderID[0];
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
