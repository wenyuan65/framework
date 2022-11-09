package com.panda.framework.util;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DoubleBufferQueueTest {
	
	public static DoubleBufferQueue<String> queue = new DoubleBufferQueue<>(1024);
	
	public static void main(String[] args) {
		CountDownLatch cdl = new CountDownLatch(2);
		Thread t1 = new Thread(new TestTask("task1", queue, cdl));
		Thread t2 = new Thread(new TestTask("task2", queue, cdl));
		t1.start();
		t2.start();
		
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int count = 0;
		for (String c : queue) {
			count ++;
		}
		System.out.println(count);
		queue.clear();
		queue.flip();
		count = 0;
		for (String c : queue) {
			count ++;
		}
		queue.clear();
		System.out.println(count);
	}
	
}

class TestTask implements Runnable {
	
	private DoubleBufferQueue<String> queue;
	private String name;
	private CountDownLatch cdl;
	
	public TestTask(String name, DoubleBufferQueue<String> queue, CountDownLatch cdl) {
		this.queue = queue;
		this.name = name;
		this.cdl = cdl;
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 500000; i++) {
			String content = String.format("%s-%d", name, i);
			synchronized (TestTask.class) {
				queue.add(content);
			}
			
			if (rand.nextBoolean()) {
				queue.flip();
			}
		}
		long end = System.currentTimeMillis();
		System.out.printf("%s:%d%n", name, end - start);
		
		cdl.countDown();
	}
	
}

