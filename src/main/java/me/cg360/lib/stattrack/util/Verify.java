package me.cg360.lib.stattrack.util;

public class Verify {

    public boolean statisticID(String id) {
        return id.matches("([A-Za-z_]*)");
    }

    public String andCorrectStatisticID(String id) {
        return id.replaceAll("([^A-Za-z_]+)", "_").toLowerCase();
    }

}
