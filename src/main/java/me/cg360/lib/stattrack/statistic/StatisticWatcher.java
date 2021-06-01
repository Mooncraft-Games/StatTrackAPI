package me.cg360.lib.stattrack.statistic;

public class StatisticWatcher {

    protected ITrackedEntity target;
    protected String statisticID;
    protected double valueRemote; // Tracks the storage value of the statistic.
    protected double valueDelta; // Tracks the amount increased/decreased on the server. Resets whenever changes are pushed to the storage provider.

    protected boolean isInitialized;

    public StatisticWatcher(ITrackedEntity target, String statisticID) {
        this.target = target;
        this.statisticID = statisticID;
        this.valueRemote = 0;
        this.valueDelta = 0;
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
