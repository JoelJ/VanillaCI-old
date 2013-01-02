package com.joelj.vanillaci.heartbeat;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 6:13 PM
 */
public class WaitForJobHeartbeatItem extends HeartbeatItem {
	private final String id;
	private final String jobName;
	private final int buildNumber;

	/**
	 * Constructor used only for deserialization. Do not ever call this method.
	 */
	@Deprecated
	WaitForJobHeartbeatItem() {
		id = null;
		jobName = null;
		buildNumber = 0;
	}

	/**
	 * @param id The id of this item. Must be globally unique.
	 * @param jobName The name of the job to wait for.
	 * @param buildNumber The build number of the job that is being waited on.
	 */
	public WaitForJobHeartbeatItem(String id, String jobName, int buildNumber) {
		this.id = id;
		this.jobName = jobName;
		this.buildNumber = buildNumber;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getJobName() {
		return jobName;
	}

	public int getBuildNumber() {
		return buildNumber;
	}
}
