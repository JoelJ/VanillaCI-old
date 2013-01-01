package com.joelj.vanillaci.heartbeat;

import com.joelj.vanillaci.machine.SlaveMachine;

import java.net.URI;
import java.util.Map;
import java.util.TimerTask;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:19 PM
 */
public class HeartbeatTask extends TimerTask {
	private Map<String, SlaveMachine> machines;

	public HeartbeatTask(Map<String, SlaveMachine> machines) {
		this.machines = machines;
	}

	@Override
	public void run() {
		for (String machineName : machines.keySet()) {
			SlaveMachine slaveMachine = machines.get(machineName);
			URI location = slaveMachine.getLocation();

		}
		System.out.println("Hello there");
	}
}
