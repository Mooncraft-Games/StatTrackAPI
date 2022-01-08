package org.madblock.lib.stattrack.storage.database;

public enum TransactionStatus {
    SUCCESS, // The SQL Query worked.
    FAILURE, // The SQL Query worked but did not meet pre-determined requirements
    DB_UNREACHABLE, // The SQL Query could not reach the database.
    ERROR, // A plugin-side error occured.
    NO_ACTION_TAKEN // No SQL was required in a method
}
