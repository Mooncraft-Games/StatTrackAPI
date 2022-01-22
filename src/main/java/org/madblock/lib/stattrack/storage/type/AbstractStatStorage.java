package org.madblock.lib.stattrack.storage.type;

import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;
import org.madblock.lib.stattrack.storage.database.TransactionStatus;

import java.util.HashMap;

public abstract class AbstractStatStorage<T> {

    private final String sql;
    private final String type;
    private final String statisticID;

    private final T defaultValue;

    protected AbstractStatStorage(String tableSQL, String type, String statisticID, T defaultValue) {
        this.sql = tableSQL;
        this.type = type;
        this.statisticID = statisticID;
        this.defaultValue = defaultValue;
    }

    public final String getTableCreationSQL() {
        return this.sql;
    }

    public final String getTypeID() {
        return this.type;
    }

    public final String getStatisticID() {
        return this.statisticID;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    /** Stores a set of data */
    public abstract void put(ITrackedHolderID holder, T value);

    /** Discards any local changes and fetches a database copy. */
    public abstract void discardLocal(ITrackedHolderID holder);

    /** Discards any remote values and uses the local copy. */
    public abstract TransactionStatus discardRemote(ITrackedHolderID holder);

    /** Discards any remote and local values to clear the stat. */
    public TransactionStatus discardStat(ITrackedHolderID holder) {
        this.discardLocal(holder);
        return this.discardRemote(holder);
    }

    /** @return all the holders with this stat currently tracked. */
    public abstract ITrackedHolderID[] getStatHolders();


    /** Updates the database's copy of this storage entry. */
    public abstract TransactionStatus commit(ITrackedHolderID holder);

    /** Updates this storage entry with a more up-to-date database variant. */
    public abstract TransactionStatus fetch(ITrackedHolderID holder);


    /** Updates the database's copy of this stat for all tracked holders. */
    public TransactionStatus commitAll() {
        for(ITrackedHolderID holderID: this.getStatHolders()) {
            switch (this.commit(holderID)) {
                case DB_UNREACHABLE:
                    return TransactionStatus.DB_UNREACHABLE;
                case ERROR:
                    return TransactionStatus.ERROR;
                case FAILURE:
                    return TransactionStatus.FAILURE;
            }
        }

        return TransactionStatus.SUCCESS;
    }


    /** Updates this stat with a more up-to-date database variant for all tracked holders. */
    public TransactionStatus fetchAll() {
        for(ITrackedHolderID holderID: this.getStatHolders()) {
            switch (this.fetch(holderID)) {
                case DB_UNREACHABLE:
                    return TransactionStatus.DB_UNREACHABLE;
                case ERROR:
                    return TransactionStatus.ERROR;
                case FAILURE:
                    return TransactionStatus.FAILURE;
            }
        }

        return TransactionStatus.SUCCESS;
    }

}
