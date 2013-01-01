package com.joelj.vanillaci.heartbeat;

import com.joelj.vanillaci.machine.SlaveMachine;

import javax.resource.spi.work.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:00 PM
 */
public class Heartbeat extends HttpServlet {
	public static final int MILLISECONDS = 1;
	private Timer timer;
	private Map<String, SlaveMachine> machines;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		machines = new ConcurrentHashMap<String, SlaveMachine>();

		timer = new Timer();
		timer.schedule(new HeartbeatTask(machines), 0, 100 * MILLISECONDS);
	}
}
