package com.vanillaci.slave.heartbeat;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 6:08 PM
 */
public class TriggerJobHeartbeatItem extends HeartbeatItem {
	private final String id;
	private final String jobName;

	/**
	 * Constructor used only for deserialization. Do not ever call this method.
	 */
	@Deprecated
	TriggerJobHeartbeatItem() {
		this.id = null;
		this.jobName = null;
	}

	/**
	 * @param id The id of this item. Must be globally unique.
	 * @param name The name of the job to trigger.
	 */
	public TriggerJobHeartbeatItem(String id, String name) {
		this.id = id;
		this.jobName = name;
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return The name of the job that is being triggered.
	 */
	public String getName() {
		return jobName;
	}
}
