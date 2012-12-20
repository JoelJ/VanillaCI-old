package com.joelj.vanillaci.restapi.service;

import com.joelj.vanillaci.restapi.annotations.EndPoint;
import com.joelj.vanillaci.restapi.core.BaseServlet;
import com.joelj.vanillaci.restapi.core.HttpMethod;
import com.joelj.vanillaci.restapi.core.ServiceResponse;
import com.joelj.vanillaci.script.ScriptName;
import com.joelj.vanillaci.script.ScriptRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:18 PM
 */
@WebServlet(urlPatterns = "/script/*")
public class ScriptService extends BaseServlet {

	@EndPoint(value="/get", accepts = {HttpMethod.GET})
	public ServiceResponse getScripts(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<ScriptName> scripts = getScriptRepository().getScripts();
		return new ServiceResponse(scripts);
	}

	@EndPoint(value="/add", accepts = {HttpMethod.POST})
	public ServiceResponse addScripts(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getOutputStream().println("Hello from ScriptService! " + request.getMethod());
		return null;
	}

	public ScriptRepository getScriptRepository() {
		//TODO: create config file
		File scriptRepo = new File("scriptRepo");
		scriptRepo.mkdirs();
		return new ScriptRepository(scriptRepo);
	}
}
