package com.panda.framework.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	
	public static byte[] read(InputStream in) throws IOException {
		return read(in, false, true);
	}
	
	public static byte[] read(InputStream in, boolean isCompressed) throws IOException {
		return read(in, isCompressed, true);
	}
	
	public static byte[] read(InputStream in, boolean isCompressed, boolean autoClose) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(in.available());
		int n = -1;
		byte[] buf = new byte[1024];
		while ((n = in.read(buf)) != -1) {
			baos.write(buf, 0, n);
		}
		
		if (autoClose) {
			in.close();
		}
		
		return baos.toByteArray();
	}
	
}
