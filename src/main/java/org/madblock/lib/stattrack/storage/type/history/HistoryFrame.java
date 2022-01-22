package org.madblock.lib.stattrack.storage.type.history;

import lombok.Getter;


public final class HistoryFrame {

    @Getter private final String statisticID;
    @Getter private final long publishedTimestamp;
    @Getter private final int value;

    HistoryFrame(String statisticID, long publishedTimestamp, int value) {
        this.statisticID = statisticID;
        this.publishedTimestamp = publishedTimestamp;
        this.value = value;
    }



    public String toString() {
        return "HistoryFrame(statisticID=" + this.getStatisticID() + ", publishedTimestamp=" + this.getPublishedTimestamp() + ", value=" + this.getValue() + ")";
    }
}
