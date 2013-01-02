package com.joelj.vanillaci.heartbeat;

import com.google.common.collect.ImmutableMap;
import com.joelj.vanillaci.util.UuidUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *     Tracks the various information that needs to be passed to master.
 *     This can include things such as jobs that need to be triggered or jobs that the slave is waiting to finish.
 * </p>
 * <p>
 *     All data in the HeartbeatTracker is transient and is lost when the service is shut down.
 * </p>
 *
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 4:35 PM
 */
public class HeartbeatTracker {
	private final Map<String, HeartbeatItem> heartbeatTasks = new ConcurrentHashMap<String, HeartbeatItem>();

	/**
	 * Adds a task in the heartbeat that is eventually used to notify the master to queue a job.<br/>
	 * See also: {@link com.joelj.vanillaci.restapi.service.HeartbeatService}
	 * @param jobName Name of the job to queue.
	 * @return A unique ID that can be used to reference the scheduled task.
	 */
	public String queueJob(String jobName) {
		String id = UuidUtil.generateUuid();
		HeartbeatItem item = new TriggerJobHeartbeatItem(id, jobName);
		heartbeatTasks.put(id, item);
		return id;
	}

	/**
	 * Removes the item from the Heartbeat Tasks map.
	 * @param uuid The ID of the task to remove.
	 * @return True if the item was in the list and was properly removed. Otherwise, false is returned.
	 */
	public boolean remove(String uuid) {
		HeartbeatItem heartbeatItem = heartbeatTasks.remove(uuid);
		return heartbeatItem != null;
	}

	/**
	 * @return An immutable {@link Map} containing all the Tasks being waited on.
	 * The map's key is the globally unique ID of the task.
	 * Everything in the map should be serializable, so the result can be directly serialized.
	 */
	public Map<String, HeartbeatItem> getHeartbeatTasks() {
		return ImmutableMap.copyOf(heartbeatTasks);
	}
}
