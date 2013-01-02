package com.joelj.vanillaci.restapi.service;

import com.joelj.vanillaci.restapi.annotations.EndPoint;
import com.joelj.vanillaci.restapi.core.BaseServlet;
import com.joelj.vanillaci.restapi.core.HttpMethod;
import com.joelj.vanillaci.restapi.core.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:40 PM
 */
@WebServlet(name="HeartbeatService", urlPatterns= "/heartbeat/*")
public class HeartbeatService extends BaseServlet {
	@EndPoint(value = "/get")
	public ServiceResponse get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return new ServiceResponse("hello there");
	}

	@EndPoint(value = "/reset", accepts = HttpMethod.POST)
	public ServiceResponse reset(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return new ServiceResponse("hello there");
	}

	@EndPoint(value = "/add", accepts = HttpMethod.PUT)
	public ServiceResponse add(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return new ServiceResponse("hello there");
	}
}
