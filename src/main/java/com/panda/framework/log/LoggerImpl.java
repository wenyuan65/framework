package com.panda.framework.log;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerImpl implements Logger {
	
	/** 异常日志的堆栈深度 */
	public static int stackDepth = 10;
	/** 相同异常出现次数阈值  */
	public static int exceptionTimesThreshold = 200;
	/** 换行符 */
	private static String lineSeparator;
	
	static {
		String stackDepth = System.getProperty("stack.depth", "10");
		LoggerImpl.stackDepth = Integer.parseInt(stackDepth);
		
		String appearTimesThreshold = System.getProperty("exception.times.threshold", "200");
		LoggerImpl.exceptionTimesThreshold = Integer.parseInt(appearTimesThreshold);
		
		lineSeparator = System.lineSeparator();
	}
	
	private org.slf4j.Logger logger;
	
	/** 错误源计数 */
	private ConcurrentHashMap<String, Integer> errorRecordMap = new ConcurrentHashMap<>();
	
	public LoggerImpl(org.slf4j.Logger logger) {
		Objects.requireNonNull(logger, "logger cannot be null");
		this.logger = logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void trace(String msg) {
		if (logger.isTraceEnabled()) {
			logger.trace(msg);
		}
	}

	@Override
	public void trace(String format, Object arg) {
		if (logger.isTraceEnabled()) {
			logger.trace(format, arg);
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (logger.isTraceEnabled()) {
			logger.trace(format, arg1, arg2);
		}
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (logger.isTraceEnabled()) {
			logger.trace(format, arguments);
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		if (logger.isTraceEnabled()) {
			logger.trace(msg, t);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(msg);
		}
	}

	@Override
	public void debug(String format, Object arg) {
		if (logger.isDebugEnabled()) {
			logger.debug(format, arg);
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (logger.isDebugEnabled()) {
			logger.debug(format, arg1, arg2);
		}
	}

	@Override
	public void debug(String format, Object... arguments) {
		if (logger.isDebugEnabled()) {
			logger.debug(format, arguments);
		}
	}

	@Override
	public void debug(String msg, Throwable t) {
		if (logger.isDebugEnabled()) {
			logger.debug(msg, t);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		if (logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	@Override
	public void info(String format, Object arg) {
		if (logger.isInfoEnabled()) {
			logger.info(format, arg);
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (logger.isInfoEnabled()) {
			logger.info(format, arg1, arg2);
		}
	}

	@Override
	public void info(String format, Object... arguments) {
		if (logger.isInfoEnabled()) {
			logger.info(format, arguments);
		}
	}

	@Override
	public void info(String msg, Throwable t) {
		if (logger.isInfoEnabled()) {
			logger.info(msg, t);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		if (logger.isWarnEnabled()) {
			logger.warn(msg);
		}
	}

	@Override
	public void warn(String format, Object arg) {
		if (logger.isWarnEnabled()) {
			logger.warn(format, arg);
		}
	}

	@Override
	public void warn(String format, Object... arguments) {
		if (logger.isWarnEnabled()) {
			logger.warn(format, arguments);
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (logger.isWarnEnabled()) {
			logger.warn(format, arg1, arg2);
		}
	}

	@Override
	public void warn(String msg, Throwable t) {
		if (logger.isWarnEnabled()) {
			logger.warn(msg, t);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		if (logger.isErrorEnabled()) {
			logger.error(msg);
		}
	}

	@Override
	public void error(String format, Object arg) {
		if (logger.isErrorEnabled()) {
			logger.error(format, arg);
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (logger.isErrorEnabled()) {
			logger.error(format, arg1, arg2);
		}
	}

	@Override
	public void error(String format, Object... arguments) {
		if (logger.isErrorEnabled()) {
			logger.error(format, arguments);
		}
	}
	
	@Override
	public void error(String msg, Throwable t) {
		if (logger.isErrorEnabled()) {
			StringBuilder sb = new StringBuilder(256);
			String exceptionMsg = t.toString();
			sb.append(exceptionMsg).append(':').append(msg);
			
			Integer count = errorRecordMap.getOrDefault(exceptionMsg, 0);
			errorRecordMap.put(exceptionMsg, count + 1);
			if (count < exceptionTimesThreshold) {
				StackTraceElement[] stackTrace = t.getStackTrace();
				for (int i = 0; i < stackTrace.length && i < stackDepth; i++) {
					StackTraceElement ste = stackTrace[i];
					
					sb.append(lineSeparator).append("  at ")
						.append(ste.getClassName()).append('.')
						.append(ste.getMethodName()).append('(')
						.append(ste.getFileName()).append(':')
						.append(ste.getLineNumber()).append(')');
				}
			}
			
			logger.error(sb.toString());
		}
	}
	
	@Override
	public void error(String msg, Throwable t, Object... arguments) {
		if (logger.isErrorEnabled()) {
			StringBuilder sb = new StringBuilder();
			String[] segements = msg.split("{}");
			for (int i = 0; i < segements.length; i++) {
				sb.append(segements[i]);
				
				if (i < arguments.length) {
					sb.append(arguments[i]);
				} else {
					sb.append("{}");
				}
			}
			
			error(sb.toString(), t);
		}
	}

}
