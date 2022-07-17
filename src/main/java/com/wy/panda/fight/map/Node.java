package com.wy.panda.fight.map;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append('(').append(x).append(',').append(y).append(')');
    	return sb.toString();
    }
}