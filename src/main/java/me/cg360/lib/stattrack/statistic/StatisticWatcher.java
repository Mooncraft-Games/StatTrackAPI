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

    public boolean fetchRemote() {
        Optional<Double> val = StatTrackAPI.get().getStorageProvider().fetchRemoteValue(this.target);

        if(val.isPresent()) {
            this.hasFetched = true;
            this.valueRemote = val.get();
            return true;
        }
        return false;
    }

    /** @return the local server's perceived value of this statistic. */
    public double getValue() {
        return valueRemote + valueDelta;
    }

    /** @return the remote storage's value of this statistic. */
    public double getValueRemote() {
        return valueRemote;
    }

    /** @return the local server's changes to this statistic which haven't been saved yet. */
    public double getValueDelta() {
        return valueDelta;
    }
}
