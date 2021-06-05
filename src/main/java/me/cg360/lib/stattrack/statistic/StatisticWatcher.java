package me.cg360.lib.stattrack.statistic;

import me.cg360.lib.stattrack.StatTrackAPI;

import java.util.Optional;

public class StatisticWatcher {

    protected ITrackedEntityID target;
    protected String statisticID;
    protected double valueRemote; // Tracks the storage value of the statistic.
    protected double valueDelta; // Tracks the amount increased/decreased on the server. Resets whenever changes are pushed to the storage provider.

    // Needs at least 1 successful fetch from storage to be true (Or no records present in storage)
    // Once true, it shouldn't be changed.
    protected boolean hasFetched;

    protected StatisticWatcher(ITrackedEntityID target, String statisticID) {
        this.target = target;
        this.statisticID = statisticID;
        this.valueRemote = 0;
        this.valueDelta = 0;

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
        if(valueDelta > 0) {
            boolean result = StatTrackAPI.get().getStorageProvider().pushRemoteDeltaValue(this.target, this.statisticID, this.valueDelta);

            if (result) {
                // If successfully pushed, reset the delta.
                // The remote value could be due a reset but, for now, retain it.
                synchronized (this) {
                    this.valueRemote += valueDelta;
                    this.valueDelta = 0;
                }
                return true;
            }
            return false;
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
