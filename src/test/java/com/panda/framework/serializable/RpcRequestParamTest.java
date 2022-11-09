package com.panda.framework.serializable;

import java.io.FileOutputStream;

import com.panda.framework.rpc.RpcRequestParams;
import com.panda.framework.rpc.SerializeUtils;

public class RpcRequestParamTest {

	public static void main(String[] args) throws Exception {
		byte[] contents = serialize(1, 100L, new Persion("tony", 18, null));
		System.out.println(contents.length);
		
		FileOutputStream fos = new FileOutputStream("C:\\Users\\wenyuan\\Desktop\\hello3.txt");
		fos.write(contents);
		fos.flush();
		fos.close();
		
		Object result = deserialized(contents);
		System.out.println(result);
		RpcRequestParams params = (RpcRequestParams)result;
		for (Object obj : params.getArgs()) {
			System.out.println(obj);
		}
	}
	
	public static byte[] serialize(Object... args) {
		RpcRequestParams params = new RpcRequestParams(args);
		return SerializeUtils.serialize(params, true);
	}
	
	public static Object deserialized(byte[] contents) {
		return SerializeUtils.deserialize(contents, true);
	}
	
}
