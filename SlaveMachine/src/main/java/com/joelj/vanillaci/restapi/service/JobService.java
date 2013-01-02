package com.joelj.vanillaci.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.joelj.vanillaci.job.Job;
import com.joelj.vanillaci.restapi.annotations.EndPoint;
import com.joelj.vanillaci.restapi.config.Config;
import com.joelj.vanillaci.restapi.core.BaseServlet;
import com.joelj.vanillaci.restapi.core.HttpMethod;
import com.joelj.vanillaci.restapi.core.ServiceResponse;
import com.joelj.vanillaci.run.Run;
import com.joelj.vanillaci.util.Confirm;
import com.joelj.vanillaci.util.JsonUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:40 PM
 */
@WebServlet(name="JobService", urlPatterns= "/job/*")
public class JobService extends BaseServlet {
	private Map<String, Run> runDb = new HashMap<String, Run>();

	@EndPoint(value="/execute", accepts = {HttpMethod.POST})
	public ServiceResponse execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Job job = getJobFromRequest(request);
		int buildNumber = getBuildNumberFromRequest(request);

		Map<String, String> parameters = getParametersFromRequest(request);
		ImmutableMap.Builder<String, String> environment = ImmutableMap.builder();
		environment.putAll(parameters);

		Run run = job.execute(Config.getWorkspaceDirectory(), Config.getScriptRepository(), buildNumber, environment.build());
		addRun(run);

		return new ServiceResponse(run.getId());
	}

	private Map<String, String> getParametersFromRequest(HttpServletRequest request) throws IOException {
		String parametersJson = request.getParameter("parameterValues");
		if(parametersJson == null || parametersJson.isEmpty()) {
			return Collections.emptyMap();
		}
		return JsonUtils.parse(parametersJson, new TypeReference<Map<String, String>>() {});
	}

	private int getBuildNumberFromRequest(HttpServletRequest request) {
		return Integer.parseInt(request.getParameter("buildNumber"));
	}

	private Job getJobFromRequest(HttpServletRequest request) throws IOException {
		String jobJson = request.getParameter("job");
		Confirm.notNull("job", jobJson);
		return JsonUtils.parse(jobJson, new TypeReference<Job>() {});
	}

	private void addRun(Run run) {
		runDb.put(run.getId(), run);
	}

	private Run getRun(String id) {
		//TODO: if run is done running, we need to serialize it to our DB.
		return runDb.get(id);
	}
}
