package com.wy.panda.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 常用工具类
 * @author wenyuan
 */
public class MixUtil {
	
	/**
	 * 获取本地所有的ip地址
	 * @return
	 * @throws SocketException
	 */
	public static List<InetAddress> getAllLocalAddresses() throws SocketException {
		List<InetAddress> list = new ArrayList<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
			while (addrs.hasMoreElements()) {
				list.add(addrs.nextElement());
			}
		}
		
		return list;
	}

	/**
	 * 检查端口号是否可用
	 * @param port
	 * @throws IOException
	 */
	public static void checkPortAvailible(int port) throws IOException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
			while (addrs.hasMoreElements()) {
				InetAddress addr = addrs.nextElement();
				Socket s = null;
				try {
					s = new Socket();
					s.bind(new InetSocketAddress(addr.getHostAddress(), port));
				} catch (IOException e) {
					throw e;
				} finally {
					if (s != null) {
						s.close();
					}
				}
			}
		}
	}
	
	/**
	 * 获取一个大于或等于num的二进制数2^n, 最大值取0x40000000, 最小值为2
	 * @param num
	 * @return
	 */
	public static int getBinaryNumGreatOrEquipThan(int num) {
		int result = 1;
		for (int i = 0; i < 30; i++) {
			result <<= 1;
			
			if (result >= num) {
				break;
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 31; i++) {
			int num = (1 << i) + 1;
			int result = getBinaryNumGreatOrEquipThan(num);
			System.out.printf("num: %d, result:%d, %s ==> %s%n", num, result, Integer.toHexString(num), Integer.toHexString(result));
		}
		
	}
	
}
