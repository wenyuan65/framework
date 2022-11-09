package com.panda.framework.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	public static <T> void shuffle(List<T> list) {
		Collections.shuffle(list, getInstance());
	}

	public static <T> T random(List<T> list) {
		if (list == null || list.size() == 0) {
			return null;
		}

		int index = nextInt(list.size());
		return list.get(index);
	}

	public static <T> List<T> random(List<T> list, int num, boolean duplication) {
		if (list == null || list.size() == 0 || num == 0) {
			return Collections.emptyList();
		}

		List<T> result = new ArrayList<>(num);
		if (duplication) {
			for (int i = 0; i < num; i++) {
				int index = nextInt(list.size());
				result.add(list.get(index));
			}
		} else {
			List<T> container = new ArrayList<>(list);
			Collections.shuffle(container, getInstance());

			num = Math.min(num, container.size());
			for (int i = 0; i < num; i++) {
				result.add(container.get(i));
			}
		}

		return result;
	}

	public static <T> T randomByWeight(List<T> list, WeightCalculator<T> calculator) {
		int totalWeight = 0;
		for (T t : list) {
			totalWeight += calculator.getWeight(t);
		}

		if (totalWeight <= 0) {
			return null;
		}

		int randWeight = nextInt(totalWeight);
		totalWeight = 0;
		for (T t : list) {
			totalWeight += calculator.getWeight(t);
			if (totalWeight > randWeight) {
				return t;
			}
		}

		return null;
	}

	public static <T> List<T> randomByWeight(List<T> list, int num, boolean duplication, WeightCalculator<T> calculator) {
		if (list == null || list.size() == 0 || num <= 0) {
			return Collections.emptyList();
		}

		if (list.size() <= num && !duplication) {
			List<T> container = new ArrayList<>(list);
			Collections.shuffle(container, getInstance());
			return container;
		}

		List<T> result = new ArrayList<>(num);
		if (num == 1) {
			T t = randomByWeight(list, calculator);
			result.add(t);
			return result;
		}

		if (duplication) {
			for (int i = 0; i < num; i++) {
				result.add(randomByWeight(list, calculator));
			}
		} else {
			List<T> container = new ArrayList<>(list);
			for (int i = 0; i < num; i++) {
				T t = randomByWeight(container, calculator);
				result.add(t);
				container.remove(t);
			}
		}

		return result;
	}
	
}
