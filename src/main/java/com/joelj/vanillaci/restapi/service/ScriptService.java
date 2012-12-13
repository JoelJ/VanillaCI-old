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
 * Time: 10:18 PM
 */
@WebServlet(urlPatterns = ScriptService.URL_END_POINT)
public class ScriptService extends BaseServlet {
	public static final String URL_END_POINT = "/script/*";

	@EndPoint(value="/getScripts", accepts = {HttpMethod.GET})
	public ServiceResponse getScripts(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getOutputStream().println("Hello from ScriptService! " + request.getMethod());
		return null;
	}

	@EndPoint(value="/addScripts", accepts = {HttpMethod.POST})
	public ServiceResponse addScripts(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getOutputStream().println("Hello from ScriptService! " + request.getMethod());
		return null;
	}

	@Override
	protected String getUrlEndPoint() {
		return URL_END_POINT;
	}
}
