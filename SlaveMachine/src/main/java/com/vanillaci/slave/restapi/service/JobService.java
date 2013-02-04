package com.vanillaci.slave.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.vanillaci.slave.restapi.annotations.EndPoint;
import com.vanillaci.core.HttpMethod;
import com.vanillaci.slave.run.Run;
import com.vanillaci.slave.job.Job;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.core.BaseServlet;
import com.vanillaci.core.ServiceResponse;
import com.vanillaci.slave.util.Confirm;
import com.vanillaci.slave.util.JsonUtils;
import com.vanillaci.slave.util.RequestUtils;
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
		return RequestUtils.getInt(request, "buildNumber");
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
