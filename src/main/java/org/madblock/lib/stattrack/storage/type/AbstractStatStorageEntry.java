package org.madblock.lib.stattrack.storage.type;

import org.madblock.lib.stattrack.storage.database.TransactionStatus;

public abstract class AbstractStatStorageEntry {

    public abstract String getTableCreationSQL();


    /** Updates the database's copy of this storage entry. */
    public abstract TransactionStatus commit();

    /** Updates this storage entry with a more up-to-date database variant. */
    public abstract TransactionStatus fetch();

}
