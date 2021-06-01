package me.cg360.lib.stattrack.statistic;

import me.cg360.lib.stattrack.util.Check;
import me.cg360.lib.stattrack.util.Verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class StatisticCollection {

    protected ITrackedEntityID target;
    protected HashMap<String, StatisticWatcher> statisticWatchers;

    protected StatisticCollection(ITrackedEntityID target) {
        Check.nullParam(target, "target");

        this.target = target;
        this.statisticWatchers = new HashMap<>();
    }

    /**
     * Creates a statistic watcher if one doesn't already exist for
     * this collection.
     *
     * @return a new StatisticWatcher if not present, else the currently present StatisticWatcher
     */
    public StatisticWatcher createStatistic(String id) {
        String fID = Verify.andCorrectStatisticID(id);

        if(statisticWatchers.containsKey(fID)) {
            return statisticWatchers.get(fID);

        } else {
            StatisticWatcher watcher = new StatisticWatcher(target, fID);
            this.statisticWatchers.put(fID, watcher);
            return watcher;
        }
    }

    /** @return an optional wrapping. If present, the optional will contain the appropriate StatisticWatcher. */
    public Optional<StatisticWatcher> getStatistic(String id) {
        String fID = Verify.andCorrectStatisticID(id);
        return Optional.ofNullable(statisticWatchers.get(fID));
    }

    /** @return the entity associated with the listed StatisticWatcher instances. */
    public ITrackedEntityID getTarget() {
        return target;
    }
}
