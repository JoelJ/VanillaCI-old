package com.joelj.vanillaci.restapi.core;

import com.joelj.vanillaci.exceptions.NullResponseException;
import com.joelj.vanillaci.exceptions.UnboundUrlException;
import com.joelj.vanillaci.exceptions.UnhandledException;
import com.joelj.vanillaci.restapi.annotations.EndPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:08 PM
 */
public abstract class BaseServlet extends HttpServlet {
	protected abstract String getUrlEndPoint();

	private ServiceResponse process(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod) throws IOException {
		String url = request.getPathInfo().substring(getUrlEndPoint().length());
		int index = url.indexOf("/");
		String name = url.substring(0, index);

		Method[] declaredMethods = this.getClass().getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			EndPoint annotation = declaredMethod.getAnnotation(EndPoint.class);
			if(annotation != null) {
				if(name.equals(annotation.value())) {
					try {
						for (HttpMethod supportedHttpMethod : annotation.accepts()) {
							if(supportedHttpMethod == httpMethod) {
								Object result = declaredMethod.invoke(this, request, response);
								if(result != null) {
									return (ServiceResponse) result;
								} else {
									response.setStatus(500);
									throw new NullResponseException(request.getPathInfo(), httpMethod);
								}
							}
						}
					} catch (IllegalAccessException e) {
						throw new UnhandledException(e);
					} catch (InvocationTargetException e) {
						throw new UnhandledException(e);
					}
				}
			}
		}
		response.setStatus(404);
		throw new UnboundUrlException(request.getPathInfo(), httpMethod);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.GET);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.POST);
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.TRACE);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.OPTIONS);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.DELETE);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.PUT);
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response, HttpMethod.HEAD);
	}
}
