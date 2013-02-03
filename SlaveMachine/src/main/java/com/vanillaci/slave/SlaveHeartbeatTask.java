package com.vanillaci.slave;

import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.slave.util.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * User: Joel Johnson
 * Date: 2/3/13
 * Time: 1:02 AM
 */
public class SlaveHeartbeatTask implements Job {
	private static final Logger log = Logger.getLogger(SlaveHeartbeatTask.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String name = context.getJobDetail().getKey().getName();
		if(name == null) {
			log.error("SlaveHeartbeatTask got a null name.");
			return;
		}
		Slave slave = Config.getSlaveRepository().get(name);
		if(slave == null) {
			log.error("SlaveHeartbeatTask got a null slave for name: '" + name + "'. Was it removed?");
			return;
		}
		slave.heartbeat();
	}
}
