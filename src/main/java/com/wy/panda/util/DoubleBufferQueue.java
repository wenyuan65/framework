package com.wy.panda.util;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 双缓冲队列
 * @author wenyuan
 */
public class DoubleBufferQueue<E> extends AbstractQueue<E> {
	
	/** 缓冲队列 */
	@SuppressWarnings("unchecked")
	private ArrayList<E>[] buffers = new ArrayList[2];
	/** 存活队列 */
	private List<E> survivorList = new ArrayList<>(128);
	
	/** 当前队列游标 */
	private volatile int cursor = 0;
	/** 游标并发锁 */
	private Object lock = new Object();
	
	public DoubleBufferQueue(int defaultCapacity) {
		buffers[0] = new ArrayList<E>(defaultCapacity);
		buffers[1] = new ArrayList<E>(defaultCapacity);
	}
	
	@Override
	public boolean offer(E e) {
		synchronized (this) {
			return getWriteBuffer().add(e);
		}
	}

	@Override
	public E poll() {
		List<E> buffer = getReadBuffer();
		return buffer.size() > 0 ? buffer.remove(0) : null;
	}
	
	public List<E> poll(int size) {
		List<E> list = new ArrayList<>(size);
		synchronized (this) {
			List<E> buffer = getReadBuffer();
			
			if (buffer.size() <= size) {
				list.addAll(buffer);
				buffer.clear();
			} else {
				list.addAll(buffer.subList(0, size));
				survivorList.addAll(buffer.subList(size, buffer.size()));
				buffer.clear();
				buffer.addAll(survivorList);
			}
		}
		
		return list;
	}
	
	public List<E> pollAll() {
		List<E> buffer = getReadBuffer();
		List<E> list = new ArrayList<>(buffer.size());
		list.addAll(buffer);
		buffer.clear();
		
		return list;
	}

	@Override
	public E peek() {
		List<E> buffer = getReadBuffer();
		return buffer.size() > 0 ? buffer.get(0) : null;
	}

	@Override
	public Iterator<E> iterator() {
		return getReadBuffer().iterator();
	}

	@Override
	public int size() {
		return getReadBuffer().size();
	}
	
	@Override
	public void clear() {
		getReadBuffer().clear();
	}
	
	/**
	 * 切换读写队列
	 */
	public void flip() {
		synchronized (lock) {
			cursor = 1 - cursor;
		}
	}
	
	/**
	 * 当前可读的列表
	 * @return
	 */
	public List<E> getReadBuffer() {
		int curCursor = 0;
		synchronized (lock) {
			curCursor = 1 - cursor;
		}
		
		return buffers[curCursor];
	}
	
	/**
	 * 当前可写的列表
	 * @return
	 */
	public List<E> getWriteBuffer() {
		int curCursor = 0;
		synchronized (lock) {
			curCursor = cursor;
		}
		
		return buffers[curCursor];
	}
	
	/**
	 * @return
	 */
	public int getCursor() {
		return cursor;
	}
	
}
