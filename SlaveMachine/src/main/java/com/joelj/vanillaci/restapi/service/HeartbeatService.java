package com.joelj.vanillaci.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.joelj.vanillaci.heartbeat.HeartbeatItem;
import com.joelj.vanillaci.heartbeat.HeartbeatTracker;
import com.joelj.vanillaci.heartbeat.TriggerJobHeartbeatItem;
import com.joelj.vanillaci.restapi.annotations.EndPoint;
import com.joelj.vanillaci.restapi.core.BaseServlet;
import com.joelj.vanillaci.restapi.core.HttpMethod;
import com.joelj.vanillaci.restapi.core.ServiceResponse;
import com.joelj.vanillaci.util.Confirm;
import com.joelj.vanillaci.util.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:40 PM
 */
@WebServlet(name="HeartbeatService", urlPatterns= "/heartbeat/*")
public class HeartbeatService extends BaseServlet {
	private static final Logger log = Logger.getLogger(HeartbeatService.class);

	public static final String ERROR_UNSUPPORTED_HEARTBEAT_ITEM = "Unsupported";
	private final HeartbeatTracker heartbeatService = new HeartbeatTracker();

	@EndPoint(value = "/get")
	public ServiceResponse get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String,HeartbeatItem> heartbeatTasks = heartbeatService.getHeartbeatTasks();
		return new ServiceResponse(heartbeatTasks);
	}

	@EndPoint(value = "/add", accepts = HttpMethod.PUT)
	public ServiceResponse add(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ImmutableMap.Builder<String, String> result = ImmutableMap.builder();

		String requestId = Confirm.notNull("requestId", request.getParameter("requestId"));
		String typeParameter = Confirm.notNull("type", request.getParameter("type"));

		if(TriggerJobHeartbeatItem.class.getSimpleName().equals(typeParameter)) {
			String name = Confirm.notNull("name", request.getParameter("name"));
			String id = heartbeatService.queueJob(name);
			log.infop("added job to heartbeat: '%s' with id: '%s' from request: '%s'", name, id, requestId);
			result.put(requestId, id);
		} else {
			log.warnp("Invalid request. Unknown HeartbeatItem type '%s'", typeParameter);
			result.put(requestId, ERROR_UNSUPPORTED_HEARTBEAT_ITEM);
		}

		Map<String, String> build = result.build();
		return new ServiceResponse(build);
	}

	@EndPoint(value = "/remove", accepts = HttpMethod.POST)
	public ServiceResponse reset(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return new ServiceResponse("hello there");
	}
}
