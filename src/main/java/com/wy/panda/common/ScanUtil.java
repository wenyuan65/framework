package com.wy.panda.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanUtil {
	
	/** 扫描路径的分割符号 */
	public static final String MULTI_SCANPATH_SEPARATOR = ",|;";

	public static Set<Class<?>> scan(String path) {
		String[] packages = path.split(MULTI_SCANPATH_SEPARATOR);
		
		Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
		for (String currPack : packages) {
			doScanPackage(classSet, currPack);
		}
		return classSet;
	}
	
	private static void doScanPackage(Set<Class<?>> classSet, String pack) {
		String packageName = pack;
		String packageDirName = pack.replace(".", "/");
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if("file".equals(protocol)){
					String filePath = URLDecoder.decode(url.getFile(), "utf-8");
					packageName = pack;
					findAndAddClassInPackageByFile(classSet, packageName,filePath);
				}else if("jar".equals(protocol)){
					JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
					findAndAddClassesInJar(classSet, packageName, packageDirName, jar);
				}else if(".class".equals(protocol)){
					try {
						classSet.add(Thread.currentThread().getContextClassLoader()
								.loadClass(url.getFile()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void findAndAddClassesInJar(Set<Class<?>> clazz, String packageName, String packageDirName, JarFile jar)
			throws IOException {
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			foundClassInJar(clazz, packageName, packageDirName, entry);
		}
	}

	private static String foundClassInJar(Set<Class<?>> clazz, String packageName, String packageDirName, JarEntry entry) {
		String name = entry.getName();
		if(name.charAt(0)=='/'){
			name = name.substring(1);
		}
		if(name.startsWith(packageDirName)){
			int idx = name.lastIndexOf('/');
			if(idx != -1){
				packageName = name.substring(0, idx).replace('/', '.');
			}
			if(name.endsWith(".class") && !entry.isDirectory() ){
				String className = name.substring(packageName.length()+1, name.length()-6);
				try {
					clazz.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + "." + className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return packageName;
	}
	
	private static void findAndAddClassInPackageByFile(Set<Class<?>> clazz, String packageName, String filePath) {
		File dir = new File(filePath);
		if(!dir.exists() || !dir.isDirectory()){
			return ;
		}
		File[] dirFiles = dir.listFiles(new FileFilter() {

			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
			
			
		});
		
		for(File file:dirFiles){
			if(file.isDirectory()){
				findAndAddClassInPackageByFile(clazz, packageName+"."+file.getName(), file.getAbsolutePath());
			}else {
				String className = file.getName().substring(0, file.getName().length()-6);
				try {
					clazz.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + "." + className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
