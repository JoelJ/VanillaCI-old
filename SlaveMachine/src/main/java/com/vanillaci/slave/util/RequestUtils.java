package com.vanillaci.slave.util;

import com.google.common.collect.ImmutableList;

import javax.servlet.ServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 4:14 PM
 */
public class RequestUtils {
	public static int getInt(ServletRequest request, String parameterName) {
		Confirm.notNull("request", request);
		Confirm.notNull("parameterName", parameterName);

		String parameter = Confirm.notNull(parameterName, request.getParameter(parameterName));
		return Integer.parseInt(parameter);
	}

	public static List<String> getParameters(ServletRequest request, String parameterName) {
		Confirm.notNull("request", request);
		Confirm.notNull("parameterName", parameterName);

		String[] parameterValues = request.getParameterValues(parameterName);
		if(parameterValues == null) {
			return Collections.emptyList();
		}
		return ImmutableList.copyOf(parameterValues);
	}
}
