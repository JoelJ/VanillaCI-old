package com.vanillaci.slave;

import com.vanillaci.master.heartbeat.Heartbeat;
import com.vanillaci.slave.util.Confirm;
import com.vanillaci.slave.util.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 3:22 PM
 */
public class SlaveRepository {
	private static final Logger log = Logger.getLogger(SlaveRepository.class);
	private final Map<String, Slave> slaves = new ConcurrentHashMap<String, Slave>();

	public void add(Slave slave) {
		Confirm.notNull("slave", slave);
		Confirm.isFalse("slave", has(slave), "Cannot add slave. Duplicate name. " + slave.toString()); //TODO: Localize
		slaves.put(slave.getName(), slave);
		slave.scheduleHeartbeat();
	}

	public boolean has(Slave slave) {
		Confirm.notNull("slave", slave);
		return slaves.containsKey(slave.getName());
	}

	public void remove(Slave slave) {
		Confirm.notNull("slave", slave);
		Slave removed = slaves.remove(slave.getName());

		Confirm.isTrue("slave", removed != null, "Can't remove: slave doesn't exist. " + slave.toString()); //TODO: Localize
		assert removed != null;

		if(!slave.getLocation().equals(removed.getLocation())) {
			slaves.put(removed.getName(), removed);
			throw new IllegalStateException("Slave in repository (" + removed + ") doesn't match given slave (" + slave + ")"); //TODO: Localize
		}

		try {
			Heartbeat.SCHEDULER_FACTORY.getScheduler().deleteJob(new JobKey(slave.getName(), slave.getHeartbeatGroup()));
		} catch (SchedulerException e) {
			log.error("Could not unschedule slave: " + slave + ". groupId: " +slave.getHeartbeatGroup());
			throw new RuntimeException(e);
		}
	}

	public Slave get(String name) {
		Confirm.notNull("name", name);
		Slave slave = slaves.get(name);
		Confirm.isTrue("slave", slave != null, "No slave with given name. " + name); //TODO: Localize
		return slave;
	}

	public Collection<Slave> getAll() {
		return slaves.values();
	}

	public int getCount() {
		return slaves.size();
	}
}
