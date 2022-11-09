package com.panda.framework.fight.map;

/**
 * 地形
 * @author wenyuan
 */
public class Landform {

	public static final int OBSTACLE = 1;
	public static final int WATERS = 2;
	public static final int OBSTACLE2 = 4;
	
	public static boolean checkLandform(int currLandform, int exceptedLandform) {
		return (currLandform & exceptedLandform) != 0;
	}
	
}
