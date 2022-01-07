package org.madblock.lib.stattrack.statistic;

import org.madblock.lib.stattrack.StatTrackAPI;
import org.madblock.lib.stattrack.statistic.id.ITrackedHolderID;

import java.util.Optional;

public class StatisticWatcher {

    protected ITrackedHolderID target;
    protected String statisticID;
    protected double valueRemote; // Tracks the storage value of the statistic.
    protected double valueDelta; // Tracks the amount increased/decreased on the server. Resets whenever changes are pushed to the storage provider.
    protected boolean isAutoSaveEnabled;

    // Needs at least 1 successful fetch from storage to be true (Or no records present in storage)
    // Once true, it shouldn't be changed.
    protected boolean hasFetched;

    protected StatisticWatcher(ITrackedHolderID target, String statisticID, boolean isAutoSaveEnabled) {
        this.target = target;
        this.statisticID = statisticID;
        this.valueRemote = 0;
        this.valueDelta = 0;

        this.isAutoSaveEnabled = isAutoSaveEnabled;
        this.hasFetched = false;
    }

    // Used by StatisticCollection
    protected synchronized void addFetchedRemoteBulkEntry(double remoteValue) {
        this.hasFetched = true;
        this.valueRemote = remoteValue;
    }


    public boolean fetchRemote() {
        Optional<Double> val = StatTrackAPI.get().getStorageProvider().fetchRemoteValue(this.target, this.statisticID);

        if(val.isPresent()) {
            this.addFetchedRemoteBulkEntry(val.get());
            return true;
        }
        return false;
    }

    public boolean pushRemote() {
        double valDeltaInitial;
        synchronized (this) {  // Reset the delta upfront to block other calls from resetting the delta
            valDeltaInitial = this.valueDelta;
            this.valueRemote += valueDelta;
            this.valueDelta = 0;
        }

        if(valDeltaInitial > 0) {
            boolean result = StatTrackAPI.get().getStorageProvider().pushRemoteDeltaValue(this.target, this.statisticID, valDeltaInitial);

            if (!result) {
                synchronized (this) {
                    this.valueRemote -= valDeltaInitial;
                    this.valueDelta += valDeltaInitial;
                }
                return false;
            }
            return true;
        }
        return true;
    }




    public boolean resetRemote(){
        boolean result = StatTrackAPI.get().getStorageProvider().pushRemoteTotalValue(this.target, this.statisticID, 0);
        if(result) synchronized (this) { this.valueRemote = 0; }
        return result;
    }

    public synchronized void resetLocal(){
        this.valueDelta = 0;
    }

    public synchronized void increment(){ modify( 1); }
    public synchronized void decrement(){ modify(-1); }
    public synchronized void modify(int amount) {
        this.valueDelta += amount;
    }


    /** @return the local server's perceived value of this statistic. */
    public double getValue() {
        return getValueRemote() + getValueDelta();
    }

    /** @return the remote storage's value of this statistic. */
    public synchronized double getValueRemote() {
        return valueRemote;
    }

    /** @return the local server's changes to this statistic which haven't been saved yet. */
    public synchronized double getValueDelta() {
        return valueDelta;
    }
}
