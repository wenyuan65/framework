package com.wy.panda.mvc;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.wy.panda.common.TextUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SocketClient {

	public static void main(String[] args) throws Exception {
//		 * length 		4字节
//		 * packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
//		 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
//		 * serverType 	4字节
//		 * serverId 	4字节
//		 * command		32字节
//		 * data			n字节
		Socket s = new Socket("192.168.31.123", 10001);
		InputStream in = s.getInputStream();
		OutputStream out = s.getOutputStream();
//		param1=123&param2=235&command=test@getTestInfo
		byte[] arr = TextUtil.toByte("test@getTestInfo");
		byte[] newArr = new byte[32];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		
		byte[] contents = TextUtil.toByte("param1=123&param2=235");
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(37 + contents.length);
		buf.writeByte(0);
		buf.writeInt(1);
		buf.writeBytes(newArr);
		buf.writeBytes(contents);
		
		byte[] dst = new byte[buf.readableBytes()];
		buf.readBytes(dst);
		
		out.write(dst);
		buf.release();
		
		byte[] result = new byte[2048];
		int read = in.read(result);
		
		ByteBuf resultBuf = Unpooled.buffer();
		resultBuf.writeBytes(result, 0, read);
		
		int len = resultBuf.readInt();
		byte type = resultBuf.readByte();
		System.out.println(len);
		System.out.println(type);
		System.out.println(resultBuf.readInt());
		
		byte[] command = new byte[32];
		resultBuf.readBytes(command);
		System.out.println(TextUtil.toString(command).trim());
		
		byte[] data = new byte[len - 37];
		resultBuf.readBytes(data);
		System.out.println(TextUtil.toString(data));
		resultBuf.release();
		
		s.close();
	}

}
