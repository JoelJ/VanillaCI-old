package com.vanillaci.master.heartbeat;

import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.slave.util.Logger;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:00 PM
 */
@WebServlet(name="HeartbeatScheduler", urlPatterns = "/heartbeatScheduler", loadOnStartup = 0)
public class Heartbeat extends HttpServlet {
	private static final Logger log = Logger.getLogger(Heartbeat.class);

	public static final DirectSchedulerFactory SCHEDULER_FACTORY = DirectSchedulerFactory.getInstance();

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		try {
			log.info("Initializing scheduler.");
			SCHEDULER_FACTORY.createVolatileScheduler(Config.getNumberOfHeartbeatThreads());
			Scheduler scheduler = SCHEDULER_FACTORY.getScheduler();
			scheduler.start();
			log.info("Done initializing scheduler.");
		} catch (SchedulerException e) {
			log.error("failed to initialize heartbeat scheduler. Application cannot run.", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			log.info("Shutting down Heartbeat");
			SCHEDULER_FACTORY.getScheduler().shutdown(false);
			log.info("Done shutting down Heartbeat");
		} catch (SchedulerException e) {
			log.error("Error shutting down Heartbeat", e);
		}
	}
}
