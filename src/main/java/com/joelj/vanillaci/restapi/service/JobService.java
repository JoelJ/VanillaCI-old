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
@WebServlet(urlPatterns= JobService.URL_END_POINT)
public class JobService extends BaseServlet {
	public static final String URL_END_POINT = "/job/*";

	@EndPoint(value="/execute", accepts = {HttpMethod.POST})
	public ServiceResponse execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getOutputStream().println("Hello from JobService! " + request.getMethod());

		return null;
	}

	protected String getUrlEndPoint() {
		return URL_END_POINT;
	}
}
