package com.panda.framework.concurrent;

import com.panda.framework.common.MixUtil;

public class SegmentLock {
	
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    
	/**
	 * 计算离散的值
	 * @param value
	 * @param bits
	 * @return
	 */
	public static int calcMod(long value, int bits) {
		long oldseed = value;
		long nextseed = (oldseed * multiplier + addend) & mask;

		return (int) (nextseed >>> (48 - bits));
	}
	
	private Object[] locks = null;
	
	private int length;

	public SegmentLock(int size) {
		if (size <= 1 || size > (1 << 31)) {
			throw new RuntimeException("SegmentLock size error:" + size);
		}
		
		this.length = MixUtil.getBinaryNumGreatOrEquipThan(size);
		this.locks = new Object[this.length];
		for (int i = 0; i < this.length; i++) {
			this.locks[i] = new Object();
		}
	}
	
	/**
	 * 获取一把锁
	 * @param id
	 * @return
	 */
	public Object getLock(int id) {
		return this.locks[ id & (this.length - 1) ];
	}
	
	/**
	 * 获取一把锁
	 * @param id
	 * @return
	 */
	public Object getLock(long id) {
		return this.locks[ (int) (id & (this.length - 1)) ];
	}
	
	/**
	 * 适用于二进制后N位大部分一样的id
	 * @param id
	 * @return
	 */
	public Object getDiscreteLock(int id) {
		return this.locks[ calcMod(id, 31) & (this.length - 1) ];
	}
	
	/**
	 * 适用于二进制后N位大部分一样的id
	 * @param id
	 * @return
	 */
	public Object getDiscreteLock(long id) {
		return this.locks[ calcMod(id, 31) & (this.length - 1) ];
	}
	
}
