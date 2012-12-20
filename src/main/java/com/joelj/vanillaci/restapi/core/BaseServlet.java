package com.joelj.vanillaci.restapi.core;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.joelj.vanillaci.exceptions.InvalidResponseException;
import com.joelj.vanillaci.exceptions.NullResponseException;
import com.joelj.vanillaci.exceptions.UnboundUrlException;
import com.joelj.vanillaci.exceptions.UnhandledException;
import com.joelj.vanillaci.restapi.annotations.EndPoint;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:08 PM
 */
public abstract class BaseServlet extends HttpServlet {
	public static final int MEGABYTES = 1024 * 1024;

	private void process(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod) throws IOException {
		int defaultStatus = response.getStatus();
		try {
			ServiceResponse serviceResponse = processRequest(request, response, httpMethod);
			handleResponse(request, response, serviceResponse);
		} catch (Exception e) {
			//If the response Status hasn't been set yet
			if(response.getStatus() != defaultStatus) {
				response.setStatus(500);
			}
			ServiceResponse serviceResponse = new ServiceResponse(e);
			handleResponse(request, response, serviceResponse);
		}
	}

	private ServiceResponse processRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod) throws IOException, FileUploadException {
		String name = request.getPathInfo();

		Method[] declaredMethods = this.getClass().getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			EndPoint annotation = declaredMethod.getAnnotation(EndPoint.class);
			if(annotation != null) {
				if(name.equals(annotation.value())) {
					try {
						for (HttpMethod supportedHttpMethod : annotation.accepts()) {
							if(supportedHttpMethod == httpMethod) {
								Object result;
								if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
									File tempUploadDir = Files.createTempDir();
									try {
										List<File> uploadedFiles = getUploadedFiles(tempUploadDir, request);
										result = declaredMethod.invoke(this, request, response, uploadedFiles);
									} finally {
										FileUtils.deleteDirectory(tempUploadDir);
									}
								} else {
									result = declaredMethod.invoke(this, request, response);
								}

								if(result != null) {
									if(result instanceof ServiceResponse) {
										return (ServiceResponse) result;
									} else {
										response.setStatus(500);
										throw new InvalidResponseException(ServiceResponse.class, result.getClass());
									}
								} else {
									response.setStatus(500);
									throw new NullResponseException(request.getPathInfo(), httpMethod);
								}
							}
						}
					} catch (IllegalAccessException e) {
						throw new UnhandledException(e);
					} catch (InvocationTargetException e) {
						Throwable cause = e.getCause();
						if(cause instanceof RuntimeException) {
							throw (RuntimeException)cause;
						} else if(cause instanceof IOException) {
							throw (IOException)cause;
						}
						throw new UnhandledException(cause);
					}
				}
			}
		}
		response.setStatus(404);
		throw new UnboundUrlException(request.getPathInfo(), httpMethod);
	}

	private List<File> getUploadedFiles(File tmpDir, HttpServletRequest request) throws FileUploadException, IOException {
		assert tmpDir.isDirectory() : "tmpDir should be a directory";
		assert request != null : "request can't be null";
		assert "multipart/form-data".equals(request.getContentType()) : "request must be a multipart form";

		ImmutableList.Builder<File> result = ImmutableList.builder();

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(tmpDir);
		fileItemFactory.setSizeThreshold(5 * MEGABYTES);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

		@SuppressWarnings("unchecked")
		List<FileItem> list = uploadHandler.parseRequest(request);

		for (FileItem item : list) {
			if(!item.isFormField()) {
				File file = new File(tmpDir, item.getName());
				try {
					item.write(file);
					result.add(file);
				} catch (Exception e) {
					//Honestly, who puts 'throws Exception' on a method?
					throw new IOException(e);
				}
			}
		}

		return result.build();
	}

	private void handleResponse(HttpServletRequest request, HttpServletResponse response, ServiceResponse result) throws IOException {
		//TODO: support XML and CSV

		result.writeJson(response.getOutputStream());
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
