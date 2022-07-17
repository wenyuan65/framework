package com.wy.panda.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.concurrent.ScheduledThread;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class Configuration {
	
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	/** 文件的扫描路径 */
	private static final String CONFIG_PATH = "." + File.separator + "apps";
	/** 通用配置文件类型 */
	private static final String CONFIG_TYPE_COMMON = "common";
	
	/** 检查文件修改的间隔时间 */
	private static final long CHECK_MODIFIED_INTERVEL = TimeUnit.SECONDS.toMillis(10);
	/** 上次检查时间 */
	private static ConcurrentHashMap<String, Long> lastModifyTimeMap = new ConcurrentHashMap<>();
	
	/** common文件的集合 */
	private static Set<String> commonFileNames = new HashSet<>();
	
	/** 配置属性的存储 */
	private static ConcurrentHashMap<String, Map<String, String>> map = new ConcurrentHashMap<>();
	/** 线程是否启动 */
	private static volatile boolean started = false;
	/** 配置文件扫描线程 */
	private static ScheduledThread configurationScanner = null;
	
	/** 配置项为bool的缓存 */
	private static ConcurrentHashMap<String, Boolean> booleanMap = new ConcurrentHashMap<>();
	/** 配置项为整数的缓存 */
	private static ConcurrentHashMap<String, Integer> intMap = new ConcurrentHashMap<>();
	/** 配置项为long的缓存 */
	private static ConcurrentHashMap<String, Long> longMap = new ConcurrentHashMap<>();
	/** 配置项为double的缓存 */
	private static ConcurrentHashMap<String, Double> doubleMap = new ConcurrentHashMap<>();
	/** 配置项为float的缓存 */
	private static ConcurrentHashMap<String, Float> floatMap = new ConcurrentHashMap<>();
	/** 配置项为正则表达式的缓存 */
	private static ConcurrentHashMap<String, Pattern> patternMap = new ConcurrentHashMap<>();
	
	static {
		// 启动线程
		configurationScanner = new ScheduledThread("Configuration-scanner", CHECK_MODIFIED_INTERVEL,
				new ConfigurationScanTask());
		configurationScanner.start();
	}
	
	/**
	 * 初始化通用配置文件
	 * @param commonFileNameList
	 */
	public static void init(List<String> commonFileNameList) {
		if (started) {
			return;
		}
		
		commonFileNames.addAll(commonFileNameList);
		
		// 扫描文件
		reloadConfig(CONFIG_PATH);
		started = true;
	}
	
	/**
	 * 重载配置文件
	 * @param path
	 */
	private static void reloadConfig(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] subFiles = file.list();
			for (String subPath : subFiles) {
				reloadConfig(path + File.separator + subPath);
			}
		} else {
			reloadFileConfig(file, path);
		}
	}
	
	/**
	 * 加载文件
	 * @param file
	 */
	private static void reloadFileConfig(File file, String filePath) {
		if (!filePath.endsWith(".properties")) {
			return;
		}
		
		Long lastModifyTime = lastModifyTimeMap.get(file.getPath());
		if (lastModifyTime != null && lastModifyTime == file.lastModified()) {
			return;
		} else if (lastModifyTime != null) {
			logger.info("reload config {}", filePath);
		}
		
		String name = file.getName();
		String configFileType = "";
		if (commonFileNames.contains(name)) {
			configFileType = CONFIG_TYPE_COMMON;
		} else {
			configFileType = getFileNameWithoutPrefix(name);
		}
		
		logger.info("found properties file:{}, flag: {}", name, configFileType);
		map.putIfAbsent(configFileType, new ConcurrentHashMap<>());
		Map<String, String> currConfigMap = map.get(configFileType);
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file.getAbsolutePath()));
			
			for (Entry<Object, Object> entry : prop.entrySet()) {
				// 更新属性
				currConfigMap.put((String)entry.getKey(), (String)entry.getValue());
				// 清理缓存
				clearMap((String)entry.getKey());
			}
			
			lastModifyTimeMap.put(file.getPath(), file.lastModified());
		} catch (IOException e) {
			logger.error("load properties error: {} ", e, filePath);
		}
	}
	
	/**
	 * 获取common中的key对应值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return getProperty(CONFIG_TYPE_COMMON, key);
	}
	
	/**
	 * 从yx配置文件中获取key对应的值
	 * @param yx
	 * @param key
	 * @return
	 */
	public static String getProperty(String yx, String key) {
		Map<String, String> map2 = map.get(yx);
		return map2 != null ? map2.get(key) : null;
	}
	
	/**
	 * 优先从yx配置文件中获取channelId.key对应的值,其次取key对应的值
	 * @param yx
	 * @param channelId
	 * @param key
	 * @return
	 */
	public static String getProperty(String yx, String channelId, String key) {
		String value = null;
		if (StringUtils.isNotBlank(yx)) {
			value = getProperty(yx, channelId + "." + key);
			if (StringUtils.isBlank(value)) {
				value = getProperty(yx, key);
			}
		}
		
		if (StringUtils.isBlank(value)) {
			value = getProperty(channelId + "." + key);
		} 
		
		if (StringUtils.isBlank(value)) {
			value = getProperty(key);
		} 
		
		return value;
	}
	
	/**
	 * 使用两段字符串拼接起来的key，查找配置
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String getValue(String prefix, String suffix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append('.').append(suffix);
		
		return getProperty(sb.toString());
	}
	
	/**
	 * 获取Boolean型配置属性，并且缓存
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
		Boolean cache = booleanMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			boolean value = Boolean.parseBoolean(propValue);
			booleanMap.putIfAbsent(key, value);
			
			return value;
		}
		
		return defaultValue;
	}
	
	/**
	 * 获取int型配置属性，并且缓存
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getIntProperty(String key, int defaultValue) {
		Integer cache = intMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			int value = Integer.parseInt(propValue);
			intMap.putIfAbsent(key, value);
			
			return value;
		}
		
		return defaultValue;
	}
	
	/**
	 * 获取long型配置属性，并且缓存
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long getLongProperty(String key, long defaultValue) {
		Long cache = longMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			long value = Long.parseLong(propValue);
			longMap.putIfAbsent(key, value);
			
			return value;
		}
		
		return defaultValue;
	}
	
	/**
	 * 获取double型配置属性，并且缓存
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static double getDoubleProperty(String key, double defaultValue) {
		Double cache = doubleMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			double value = Double.parseDouble(propValue);
			doubleMap.putIfAbsent(key, value);
			
			return value;
		}
		
		return defaultValue;
	}
	
	/**
	 * 获取float型配置属性，并且缓存
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static double getFloatProperty(String key, float defaultValue) {
		Float cache = floatMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			float value = Float.parseFloat(propValue);
			floatMap.putIfAbsent(key, value);
			
			return value;
		}
		
		return defaultValue;
	}
	
	/**
	 * 获取正则表达式配置属性，并且缓存
	 * @param key
	 * @return
	 */
	public static Pattern getPatternProperty(String key) {
		Pattern cache = patternMap.get(key);
		if (cache != null) {
			return cache;
		}
		
		String propValue = getProperty(key);
		if (propValue != null) {
			Pattern p = Pattern.compile(propValue);
			patternMap.putIfAbsent(key, p);
			
			return p;
		}
		
		return null;
	}
	
	/**
	 * 从缓存map中移除key的属性
	 * @param key
	 */
	public static void clearMap(String key) {
		intMap.remove(key);
		longMap.remove(key);
		doubleMap.remove(key);
		floatMap.remove(key);
		patternMap.remove(key);
	}
	
	/**
	 * 获取文件名，例如 server.properties == > server
	 * @param fileName
	 * @return
	 */
	private static String getFileNameWithoutPrefix(String fileName) {
		int index = fileName.indexOf(".");
		return index >= 0 ? fileName.substring(0, index) : fileName;
	}
	
	public static class ConfigurationScanTask implements Runnable {

		@Override
		public void run() {
			if (!started) {
				return;
			}
			
			reloadConfig(CONFIG_PATH);
		}
		
	}
	
}
