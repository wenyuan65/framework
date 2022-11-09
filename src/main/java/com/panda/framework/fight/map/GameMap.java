package com.panda.framework.fight.map;

/**
 *
 * @author maowy
 * @since 上午11:49:41
 *
 */
public class GameMap {
	
	private int w;
	
	private int h;
	
	private int[][] map;
	
	private Node startNode;
	
	private Node endNode;
	
	public GameMap(int w, int h) {
		this.map = new int[w][h];
		this.w = w;
		this.h = h;
	}
	
	public void init() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				map[i][j] = 1;
			}
		}
	}
	
	public boolean canReach(int x, int y) {
		return x >= 0 && x < w && y >= 0 && y < h && map[x][y] == 0;
	}
	
	public int[][] getMap() {
		return map;
	}
	
	public void setMapValue(int x, int y, int value) {
		map[x][y] = value;
	}
	
	public int getMapValue(int x, int y) {
		return map[x][y];
	}

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
	
}
