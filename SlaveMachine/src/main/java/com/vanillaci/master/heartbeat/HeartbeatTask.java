package com.vanillaci.master.heartbeat;

import com.vanillaci.slave.Slave;
import com.vanillaci.slave.SlaveRepository;
import com.vanillaci.slave.util.Confirm;
import com.vanillaci.slave.util.Logger;

import java.net.URI;
import java.util.Map;
import java.util.TimerTask;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:19 PM
 */
public class HeartbeatTask extends TimerTask {
	private static final Logger log = Logger.getLogger(HeartbeatTask.class);
	private SlaveRepository machines;

	public HeartbeatTask(SlaveRepository machines) {
		this.machines = Confirm.notNull("machines", machines);
	}

	@Override
	public void run() {
		if(machines.getCount() <= 0) {
			log.debug("There are no machines being tracked. Skipping heartbeat.");
		} else {
			for (Slave slave : machines.getAll()) {
				log.debug("Checking heartbeat of "+slave.toString());
			}
		}
	}
}
