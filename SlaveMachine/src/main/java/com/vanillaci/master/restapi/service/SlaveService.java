package com.vanillaci.master.restapi.service;

import com.vanillaci.core.BaseServlet;
import com.vanillaci.core.HttpMethod;
import com.vanillaci.core.ServiceResponse;
import com.vanillaci.slave.Slave;
import com.vanillaci.slave.SlaveRepository;
import com.vanillaci.slave.restapi.annotations.EndPoint;
import com.vanillaci.slave.restapi.config.Config;
import com.vanillaci.slave.util.Confirm;
import com.vanillaci.slave.util.RequestUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 3:17 PM
 */
@WebServlet(name="SlaveService", urlPatterns = "/slave/*")
public class SlaveService extends BaseServlet {
	@EndPoint(value="/add", accepts = {HttpMethod.POST})
	public ServiceResponse add(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		String name = Confirm.notNull("name", request.getParameter("name"));
		String url = Confirm.notNull("url", request.getParameter("url"));
		List<String> labels = RequestUtils.getParameters(request, "labels");

		Slave slave = new Slave(name, new URI(url), labels);

		SlaveRepository slaveRepository = Config.getSlaveRepository();
		slaveRepository.add(slave);

		return new ServiceResponse(slave);
	}

	@EndPoint(value="/remove", accepts = {HttpMethod.DELETE})
	public ServiceResponse remove(HttpServletRequest request, HttpServletResponse response) {
		String name = Confirm.notNull("name", request.getParameter("name"));
		SlaveRepository slaveRepository = Config.getSlaveRepository();
		Slave slave = slaveRepository.get(name);
		slaveRepository.remove(slave);

		return new ServiceResponse(slave);
	}

	@EndPoint("/get")
	public ServiceResponse get(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		SlaveRepository slaveRepository = Config.getSlaveRepository();
		if(name != null) {
			Slave slave = slaveRepository.get(name);
			return new ServiceResponse(slave);
		} else {
			Collection<Slave> allSlaves = slaveRepository.getAll();
			return new ServiceResponse(allSlaves);
		}
	}
}
