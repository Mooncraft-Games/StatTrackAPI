package me.cg360.lib.stattrack.statistic;


/**
 * Used to identify a target undergoing statistic collection
 * such as a player, an event type, or an object.
 *
 * "Entity" in this case does not force it to be equivalent to a
 * Minecraft Entity.
 */
public interface ITrackedEntity {

    String getEntityType();
    String getStoredID();

}
