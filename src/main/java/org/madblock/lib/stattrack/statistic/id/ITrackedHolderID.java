package org.madblock.lib.stattrack.statistic.id;


/**
 * Used to identify a target undergoing statistic collection
 * such as a player, an event type, or an object.
 *
 * "Entity" in this case does not force it to be equivalent to a
 * Minecraft Entity.
 */
public interface ITrackedHolderID {

    String SERVER_TYPE = "server"; // ip@port

    String getEntityType();
    String getStoredID();

}
