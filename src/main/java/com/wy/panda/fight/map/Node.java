package com.wy.panda.fight.map;

public class Node {
	public Node parent;
	public int x;
    public int y;
    
    public int fCost;
    public int hCost;
    public int gCost;
    
    public Node(int x, int y) {  
        this.x = x;  
        this.y = y;  
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append('(').append(x).append(',').append(y).append(')');
    	return sb.toString();
    }
}