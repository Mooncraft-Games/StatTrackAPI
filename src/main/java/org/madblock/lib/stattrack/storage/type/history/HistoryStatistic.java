package org.madblock.lib.stattrack.storage.type.history;


import java.util.ArrayList;

public class HistoryStatistic {

    protected String statID;
    protected ArrayList<HistoryFrame> frames;

    public HistoryFrame addFrameForNow(int value) {
        return addFrameForTimestamp(value, System.currentTimeMillis());
    }

    public HistoryFrame addFrameForTimestamp(int value, long timestamp) {
        HistoryFrame frame = new HistoryFrame(statID, timestamp, value);
        this.frames.add(frame);
        return frame;
    }



}
