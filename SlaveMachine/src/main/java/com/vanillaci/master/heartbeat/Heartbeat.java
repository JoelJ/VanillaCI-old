package com.vanillaci.master.heartbeat;

import com.vanillaci.slave.Slave;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.slave.util.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:00 PM
 */
public class Heartbeat extends HttpServlet {
	private static final Logger log = Logger.getLogger(Heartbeat.class);
	public static final int MILLISECONDS = 1;
	private Timer timer;
	private Map<String, Slave> machines;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		machines = new ConcurrentHashMap<String, Slave>();

		timer = new Timer();
		timer.schedule(new HeartbeatTask(Config.getSlaveRepository()), 0, 100 * MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				timer.cancel();
			}
		}));
	}
}
