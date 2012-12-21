package com.joelj.vanillaci.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.joelj.vanillaci.job.Job;
import com.joelj.vanillaci.restapi.annotations.EndPoint;
import com.joelj.vanillaci.restapi.core.BaseServlet;
import com.joelj.vanillaci.restapi.core.HttpMethod;
import com.joelj.vanillaci.restapi.core.ServiceResponse;
import com.joelj.vanillaci.run.Run;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:40 PM
 */
@WebServlet(urlPatterns= "/job/*")
public class JobService extends BaseServlet {

	@EndPoint(value="/execute", accepts = {HttpMethod.POST})
	public ServiceResponse execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Job job = getJobFromRequest(request);
		int buildNumber = getBuildNumberFromRequest(request);

		Map<String, String> parameters = getParametersFromRequest(request);
		ImmutableMap.Builder<String, String> environment = ImmutableMap.builder();
		environment.putAll(parameters);

		Run run = job.execute(buildNumber, environment.build());
		addRun(run);


		return new ServiceResponse("Hello from JobService!");
	}

	private Map<String, String> getParametersFromRequest(HttpServletRequest request) {
		return null;
	}

	private int getBuildNumberFromRequest(HttpServletRequest request) {
		return Integer.parseInt(request.getParameter("buildNumber"));
	}

	private Job getJobFromRequest(HttpServletRequest request) {
		return null;
	}


	private Map<String, Run> runDb = new HashMap<String, Run>(); //TODO: make this DB backed
	private void addRun(Run run) {
		runDb.put(run.getId(), run);
	}
}
