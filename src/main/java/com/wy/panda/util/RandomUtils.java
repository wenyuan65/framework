package com.wy.panda.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
	
	private static ThreadLocalRandom getInstance() {
		return ThreadLocalRandom.current();
	}
	
	public static double nextGaussian() {
		return getInstance().nextGaussian();
	}
	
	public static double next() {
		return getInstance().nextDouble();
	}
	
	public static double next(double n) {
		return getInstance().nextDouble(n);
	}
	
	public static double next(double n, double m) {
		return getInstance().nextDouble(n, m);
	}
	
	public static float nextFloat() {
		return getInstance().nextFloat();
	}
	
	public static float nextFloat(float n) {
		return getInstance().nextFloat() * n;
	}
	
	public static float nextFloat(float n, float m) {
		return getInstance().nextFloat() * (m - n) + n;
	}
	
	public static int nextInt() {
		return getInstance().nextInt();
	}
	
	public static int nextInt(int n) {
		return getInstance().nextInt(n);
	}
	
	public static int nextInt(int n, int m) {
		return getInstance().nextInt(n, m);
	}

	public static int nextIntInclude(int n, int m) {
		return getInstance().nextInt(n, m + 1);
	}
	
	public static long nextLong() {
		return getInstance().nextLong();
	}
	
	public static long nextLong(long n) {
		return getInstance().nextLong(n);
	}
	
	public static long nextLong(long n, long m) {
		return getInstance().nextLong(n, m);
	}
	
	public static boolean nextBoolean() {
		return getInstance().nextBoolean();
	}
	
	public static byte[] nextByte(byte[] bytes) {
		getInstance().nextBytes(bytes);
		return bytes;
	}
	
	public static byte[] nextByte(int len) {
		byte[] bytes = new byte[len];
		getInstance().nextBytes(bytes);
		return bytes;
	}
	
}
