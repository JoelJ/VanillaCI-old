package com.vanillaci.slave.restapi.service;

import com.google.common.collect.ImmutableMap;
import com.vanillaci.slave.restapi.annotations.EndPoint;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.core.BaseServlet;
import com.vanillaci.core.HttpMethod;
import com.vanillaci.core.ServiceResponse;
import com.vanillaci.slave.util.HashUtils;
import com.vanillaci.slave.script.ScriptName;
import com.vanillaci.slave.script.ScriptRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:18 PM
 */
@WebServlet(name="ScriptService", urlPatterns = "/script/*")
public class ScriptService extends BaseServlet {

	@EndPoint(value="/get", accepts = {HttpMethod.GET})
	public ServiceResponse getScripts(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<ScriptName> scripts = Config.getScriptRepository().getScripts();
		return new ServiceResponse(scripts);
	}

	@EndPoint(value="/add", accepts = {HttpMethod.POST})
	public ServiceResponse addScripts(HttpServletRequest request, HttpServletResponse response, List<File> uploadedFiles) throws IOException {
		ScriptRepository scriptRepository = Config.getScriptRepository();
		ImmutableMap.Builder<String, DeploymentStatus> result = ImmutableMap.builder();
		for (File uploadedFile : uploadedFiles) {
			String filename = uploadedFile.getName();
			int dashIndex = filename.lastIndexOf("-");
			int dotIndex = filename.lastIndexOf(".");
			if(dashIndex < 0 || dotIndex < 0) {
				result.put(filename, DeploymentStatus.NameError);
				continue;
			}

			String name = filename.substring(0, dashIndex);
			String hash = filename.substring(dashIndex+1, dotIndex);

			String sha = HashUtils.sha(uploadedFile);
			if(!sha.equals(hash)) {
				result.put(filename, DeploymentStatus.HashMismatch);
				continue;
			}

			boolean deployed = scriptRepository.addScript(name, hash, uploadedFile);
			result.put(filename, deployed ? DeploymentStatus.Deployed : DeploymentStatus.AlreadyDeployed);
		}
		Map<String, DeploymentStatus> map = result.build();
		return new ServiceResponse(map);
	}

	private static enum DeploymentStatus {
		NameError, HashMismatch, AlreadyDeployed, Deployed
	}
}
