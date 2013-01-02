package com.joelj.vanillaci.heartbeat;

import com.joelj.vanillaci.Assert;
import com.joelj.vanillaci.util.UuidUtil;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 4:40 PM
 */
public class HeartbeatTrackerTest {
	@Test
	public void testAddingJobReturnsValidID() throws Exception {
		HeartbeatTracker heartbeatTracker = new HeartbeatTracker();
		String uuid = heartbeatTracker.queueJob("Jimmy James");
		Assert.assertTrue(UuidUtil.isUuid(uuid), "Queuing a job should generate an unique ID to reference the request with.");

		Map<String, HeartbeatItem> heartbeatTasks = heartbeatTracker.getHeartbeatTasks();
		HeartbeatItem heartbeatItem = heartbeatTasks.get(uuid);
		Assert.assertFalse(heartbeatItem == null, "A new item with the returned ID should be queued");
	}

	@Test
	public void testRemovingJob() {
		HeartbeatTracker heartbeatTracker = new HeartbeatTracker();
		boolean badRemove = heartbeatTracker.remove("something that's not there yet");
		Assert.assertFalse(badRemove, "Removing an invalid task should return false");

		String uuid = heartbeatTracker.queueJob("Jimmy James");
		boolean goodRemove = heartbeatTracker.remove(uuid);
		Assert.assertTrue(goodRemove, "Removing an item that's in the list should return true");

		boolean duplicateRemove = heartbeatTracker.remove(uuid);
		Assert.assertFalse(duplicateRemove, "Removing an item that has already been removed should return false");
	}
}
