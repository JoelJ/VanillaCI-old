package com.joelj.vanillaci.heartbeat;

import java.io.Serializable;

/**
 * Represents data that the Slave needs to tell to the Master node.
 * All fields must be serializable.
 *
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 5:59 PM
 */
public interface HeartbeatItem extends Serializable {
	/**
	 * @return The globally unique id for this Item.
	 * This should be generated by {@link HeartbeatTracker} when the Item is queued.
	 */
	String getId();
}