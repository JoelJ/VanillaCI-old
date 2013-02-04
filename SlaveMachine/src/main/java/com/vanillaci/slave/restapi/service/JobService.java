package com.vanillaci.slave.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.vanillaci.slave.job.JobRepository;
import com.vanillaci.slave.restapi.annotations.EndPoint;
import com.vanillaci.core.HttpMethod;
import com.vanillaci.slave.run.Run;
import com.vanillaci.slave.job.Job;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.core.BaseServlet;
import com.vanillaci.core.ServiceResponse;
import com.vanillaci.slave.script.ScriptName;
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
import java.util.Set;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:40 PM
 */
@WebServlet(name="JobService", urlPatterns= "/job/*")
public class JobService extends BaseServlet {
	private Map<String, Run> runDb = new HashMap<String, Run>();

	/**
	 * Adds a job to the local repository for later use.
	 * This is an endpoint designed and used specifically for master nodes.
	 * Items added in this method are persisted and can be executed later on remote slaves via the {@link com.vanillaci.master.restapi.service.QueueService}.
	 */
	@EndPoint(value="/add", accepts = {HttpMethod.POST})
	public ServiceResponse add(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Job job = getJobFromRequest(request);
		JobRepository jobRepository = Config.getJobRepository();
		Set<ScriptName> missingScriptsForJob = jobRepository.findMissingScriptsForJob(job);
		if(missingScriptsForJob.size() > 0) {
			response.setStatus(404);
			return new ServiceResponse(missingScriptsForJob);
		} else {
			jobRepository.add(job);
			return new ServiceResponse(job);
		}
	}

	@EndPoint(value="/remove", accepts = {HttpMethod.DELETE})
	public ServiceResponse remove(HttpServletRequest request, HttpServletResponse response) {
		String name = Confirm.notNull("name", request.getParameter("name"));
		JobRepository jobRepository = Config.getJobRepository();
		Job job = jobRepository.remove(name);
		return new ServiceResponse(job);
	}

	/**
	 * This endpoint is designed to be used specifically in a machine running as a slave via a request from the master node.
	 * It runs an arbitrary job with an arbitrary build number both defined in the request.
	 * The machine that this request runs on will store the result only until the requester gets the result (TODO).
	 */
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
