package me.cg360.lib.stattrack.statistic;

import me.cg360.lib.stattrack.StatTrackAPI;
import me.cg360.lib.stattrack.util.Check;
import me.cg360.lib.stattrack.util.Verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StatisticCollection {

    protected ITrackedEntityID target;
    protected HashMap<String, StatisticWatcher> statisticWatchers;

    protected StatisticCollection(ITrackedEntityID target, boolean fetchFromStorage) {
        Check.nullParam(target, "target");

        this.target = target;
        this.statisticWatchers = new HashMap<>();
        if(fetchFromStorage) fetchStatisticsFromStorage();
    }

    public boolean fetchStatisticsFromStorage() {
        Optional<HashMap<String, Double>> remoteStats = StatTrackAPI.get().getStorageProvider().fetchRemoteTrackedEntity(this.target);

        if(remoteStats.isPresent()) {
            HashMap<String, Double> stats = remoteStats.get();

            for(Map.Entry<String, Double> s: stats.entrySet()) {
                createStatistic(s.getKey()).addFetchedRemoteBulkEntry(s.getValue());
            }
            return true;
        }
        return false;
    }

    /** @return the amount of failed pushes. */
    public int pushStatisticsToStorage() {
        int failedPushes = 0;
        for(StatisticWatcher watcher: new HashMap<>(statisticWatchers).values()) {
            failedPushes += watcher.pushRemote() ? 0 : 1;
        }
        return failedPushes;
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

    public String[] getStatisticRecordIDs() {
        return this.statisticWatchers.keySet().toArray(new String[0]);
    }
}
