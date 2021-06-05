package org.madblock.lib.stattrack.statistic;


/**
 * Used to identify a target undergoing statistic collection
 * such as a player, an event type, or an object.
 *
 * "Entity" in this case does not force it to be equivalent to a
 * Minecraft Entity.
 */
public interface ITrackedEntityID {

    String BEDROCK_NO_AUTH_PLAYER_TYPE = "playerBE";
    String BEDROCK_AUTH_PLAYER_TYPE = "xuid";
    String SERVER_TYPE = "server"; // ip@port

    String getEntityType();
    String getStoredID();

}
