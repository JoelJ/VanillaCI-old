package com.vanillaci.slave;

import com.google.common.collect.ImmutableList;
import com.vanillaci.master.heartbeat.Heartbeat;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.slave.util.Confirm;
import com.vanillaci.slave.util.Logger;
import org.quartz.*;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 3:22 PM
 */
public class Slave implements Serializable {
	private static final Logger log = Logger.getLogger(Slave.class);

	private final String name;
	private final URI location;
	private final List<String> labels;

	private boolean heartbeating;
	private Date lastSuccessfulHeartbeat;

	public Slave(String name, URI location, Collection<String> labels) {
		this.name = name;
		this.location = location;
		this.labels = ImmutableList.copyOf(Confirm.notNull(labels));
		heartbeating = false;
	}

	public String getName() {
		return name;
	}

	public URI getLocation() {
		return location;
	}

	@SuppressWarnings("UnusedDeclaration")
	public List<String> getLabels() {
		return labels;
	}

	public String getHeartbeatGroup() {
		return "heartbeat";
	}

	@SuppressWarnings("UnusedDeclaration")
	public Date getLastSuccessfulHeartbeat() {
		return lastSuccessfulHeartbeat;
	}

	/*package*/ void scheduleHeartbeat() {
		if(heartbeating) {
			log.warn("Calling scheduleHeartbeat() after heartbeat already started. " + this);
			return;
		}
		try {
			Scheduler scheduler = Heartbeat.SCHEDULER_FACTORY.getScheduler();
			JobDetail jobDetail = createJobDetail();
			SimpleTrigger trigger = createTrigger();
			scheduler.scheduleJob(jobDetail, trigger);
			heartbeating = true;

			log.info("Scheduled heartbeat for " + this + ". " + "groupId: " + getHeartbeatGroup());
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	private JobDetail createJobDetail() {
		return newJob(SlaveHeartbeatTask.class)
				.withIdentity(getName(), getHeartbeatGroup())
				.withDescription("Polls " + this + " to get status updates and to make sure the machine can be accessed.")
				.build();
	}

	private SimpleTrigger createTrigger() {
		return newTrigger()
				.withIdentity(getName(), getHeartbeatGroup())
				.forJob(getName(), getHeartbeatGroup())
				.withSchedule(simpleSchedule()
						.repeatForever()
						.withIntervalInMilliseconds(Config.getHeartbeatIntervalInMilliseconds())
						.withMisfireHandlingInstructionNextWithRemainingCount()) //having read the javadoc, this makes it sound like it will just ignore misfires and continue on as if it had worked. That's what I'm wanting here.
				.build();
	}

	@Override
	public String toString() {
		return "Slave{" +
				"name='" + name + '\'' +
				", location=" + location +
				'}';
	}

	public static class SlaveHeartbeatTask implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			String name = context.getJobDetail().getKey().getName();
			System.out.println(name);
			Slave slave = Config.getSlaveRepository().get(name);
			log.debug("polling " + slave);
			slave.lastSuccessfulHeartbeat = new Date();
		}
	}
}
