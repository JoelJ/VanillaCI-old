package com.vanillaci.slave.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * User: Joel Johnson
 * Date: 12/21/12
 * Time: 7:36 PM
 */
public class Logger {
	public static final Level DEFAULT_LOG_LEVEL = Level.FINEST;

	private final java.util.logging.Logger logger;

	public static Logger getLogger(Class loggedClass) {
		//TODO: read from a file the level for the class
		Handler handler = new ConsoleHandler();
		return new Logger(loggedClass.getCanonicalName(), DEFAULT_LOG_LEVEL, handler);
	}

	private Logger(String name, Level level, Handler handler) {
		this.logger = java.util.logging.Logger.getLogger(name);
		this.logger.setLevel(level);
		handler.setLevel(level);
		this.logger.addHandler(handler);
	}

	public void trace(String message, Throwable e) {
		logger.finer(message + getStackTrace(e));
	}

	public void debug(String message, Throwable e) {
		logger.fine(message + getStackTrace(e));
	}

	public void info(String message, Throwable e) {
		logger.info(message + getStackTrace(e));
	}

	public void warn(String message, Throwable e) {
		logger.warning(message + getStackTrace(e));
	}

	public void error(String message, Throwable e) {
		logger.severe(message + getStackTrace(e));
	}

	public void trace(String message) {
		logger.finer(message);
	}

	public void debug(String message) {
		logger.fine(message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void infop(String printfFormat, Object... vars) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(String.format(printfFormat, vars));
		}
	}

	public void warn(String message) {
		logger.warning(message);
	}

	public void warnp(String printfFormat, Object... vars) {
		if(logger.isLoggable(Level.INFO)) {
			logger.warning(String.format(printfFormat, vars));
		}
	}

	public void error(String message) {
		logger.severe(message);
	}


	public void enteringMethod() {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest("entering: " + getCaller());
		}
	}

	public void exitingMethod() {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest("exiting: " + getCaller());
		}
	}

	private String getCaller() {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		return stackTrace[2].getClassName() + "." + stackTrace[2].getMethodName();
	}

	private String getStackTrace(Throwable e) {
		if(e == null) {
			return "";
		}
		return "\n" + ExceptionUtils.getStackTrace(e);
	}
}
