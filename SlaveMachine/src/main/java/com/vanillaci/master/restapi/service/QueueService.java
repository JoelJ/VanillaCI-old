package com.vanillaci.master.restapi.service;

import com.vanillaci.core.BaseServlet;
import com.vanillaci.core.ServiceResponse;
import com.vanillaci.slave.restapi.annotations.EndPoint;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Joel Johnson
 * Date: 2/3/13
 * Time: 8:07 PM
 */
@WebServlet(name="QueueService", urlPatterns = "/queue/*")
public class QueueService extends BaseServlet {
	@EndPoint("/schedule")
	public ServiceResponse schedule(HttpServletRequest request, HttpServletResponse response) {
		return new ServiceResponse("schedule");
	}

	@EndPoint("/cancel")
	public ServiceResponse cancel(HttpServletRequest request, HttpServletResponse response) {
		return new ServiceResponse("cancel");
	}
}
