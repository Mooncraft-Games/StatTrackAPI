package me.cg360.lib.stattrack.statistic;

import me.cg360.lib.stattrack.util.Check;
import me.cg360.lib.stattrack.util.Verify;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticCollection {

    protected ITrackedEntityID target;
    protected HashMap<String, StatisticWatcher> statisticWatchers;

    protected StatisticCollection(ITrackedEntityID target) {
        Check.nullParam(target, "target");

        this.target = target;
        this.statisticWatchers = new HashMap<>();
    }

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

}
