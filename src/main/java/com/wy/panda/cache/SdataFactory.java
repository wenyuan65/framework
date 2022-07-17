package com.wy.panda.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wy.panda.common.ScanUtil;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class SdataFactory {
	
	private static final Logger log = LoggerFactory.getLogger(SdataFactory.class);
	
	private String path;
	
	private SdataLoader loader = null;
	
	private Set<Class<? extends Cache<?, ?>>> cacheClazzSet = new HashSet<>();
	
	private Map<Class<? extends Cache<?, ?>>, Cache<?, ?>> cacheMap = new HashMap<>(); 
	
	public SdataFactory() {}
	
	@SuppressWarnings("unchecked")
	public void init() {
		log.info("register sdata begin");
		
		Set<Class<?>> cacheClazzSet = ScanUtil.scan(path);
		for (Class<?> clazz : cacheClazzSet) {
			if (!AbstractCache.class.isAssignableFrom(clazz)) {
				continue;
			}
			
			this.cacheClazzSet.add((Class<? extends Cache<?, ?>>) clazz);
			
			log.info("register sdata {}", clazz.getSimpleName());
		}
		
		try {
			reload();
		} catch (Exception e) {
			log.error("init sdata error", e);
		}
	}
	
	public void reload() throws Exception {
		// TODO: 双缓冲队列
		log.info("reload sdata begin");
		long start = System.currentTimeMillis();
		for (Class<? extends Cache<?, ?>> clazz : cacheClazzSet) {
			Cache<?, ?> cache = clazz.newInstance();
			cache.setSdataLoader(loader);
			
			cache.reload();
			cacheMap.put(clazz, cache);
		}
		long end = System.currentTimeMillis();
		log.info("reload sdata end, cost: {} ms", end - start);
	}
	
	public Cache<?, ?> getCache(Class<?> cacheClass) {
		return cacheMap.get(cacheClass);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SdataLoader getLoader() {
		return loader;
	}

	public void setLoader(SdataLoader loader) {
		this.loader = loader;
	}
	
}
