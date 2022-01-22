package org.madblock.lib.stattrack;

import org.madblock.lib.stattrack.storage.type.counter.CounterStatStorage;

/**
 * A list of statistic id's used within the
 * plugin. Would be a good practice for every plugin to use
 * a list of id's.
 */
public class StatisticIDs {

    public static final CounterStatStorage NETWORK_PLAYER_JOIN = new CounterStatStorage("network.joins", 0);

    public static final String SERVER_VERSION_JOINS_PREFIX = "server.client_ver.";
    public static final String SERVER_PLATFORM_JOINS_PREFIX = "server.client_platform.";

}
