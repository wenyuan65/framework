package com.wy.panda.fight.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A* 寻路算法:
 * 后续优化：
 * <ul>
 * <li>加入地形排除寻路节点</li>
 * <li>寻路失败走到最近点</li>
 * <li>寻路的路径整理，尽量走直线</li>
 * </ul>
 * @author wenyuan
 */
public class AStar {
	
	private GameMap map;
	
	private PriorityQueue<Node> newOpenList = new PriorityQueue<Node>((o1, o2) -> o1.fCost - o2.fCost);
	private Set<String> openSet = new HashSet<>();
	private Set<String> closeSet = new HashSet<>();
	
	private int excludeLandform;
	
	public AStar(GameMap map) {
		this(map, 0);
	}
	
	public AStar(GameMap map, int excludeLandform) {
		this.map = map;
	}
	
	public List<Node> findPath() {
		return findPath(map.getStartNode(), map.getEndNode());
	}
	
	public List<Node> findPath(Node startNode, Node endNode) {
		newOpenList.add(startNode);
		Node currNode = null;
		while ((currNode = newOpenList.poll()) != null) {
			removeKey(openSet, currNode.x, currNode.y);
			addKey(closeSet, currNode.x, currNode.y);

			ArrayList<Node> neighborNodes = findNeighborNodes(currNode);
			for (Node nextNode : neighborNodes) {
				int gCost = calcNodeCost(currNode, nextNode) + currNode.gCost;
				if (contains(openSet, nextNode.x, nextNode.y)) {
					if (gCost < nextNode.gCost) {
						nextNode.parent = currNode;
						nextNode.gCost = gCost;
						nextNode.fCost = nextNode.gCost + nextNode.hCost;
					}
				} else {
					nextNode.parent = currNode;
					nextNode.gCost = gCost;
					nextNode.hCost = calcNodeCost(nextNode, endNode);
					nextNode.fCost = nextNode.gCost + nextNode.hCost;
					newOpenList.add(nextNode);
					
					addKey(openSet, nextNode.x, nextNode.y);
				}
			}
			
			if (contains(openSet, endNode.x, endNode.y)) {
				Node node = findOpenList(newOpenList, endNode);
				return getPathList(node);
			}
		}

		Node node = findOpenList(newOpenList, endNode);
		return getPathList(node);
	}
	
	private ArrayList<Node> findNeighborNodes(Node currentNode) {
		ArrayList<Node> arrayList = new ArrayList<Node>();
		checkAndAddNode(arrayList, currentNode.x, currentNode.y - 1);
		checkAndAddNode(arrayList, currentNode.x, currentNode.y + 1);
		checkAndAddNode(arrayList, currentNode.x - 1, currentNode.y);
		checkAndAddNode(arrayList, currentNode.x + 1, currentNode.y);
		return arrayList;
	}
	
	private void checkAndAddNode(List<Node> list, int x, int y) {
		if (map.canReach(x, y) && !contains(closeSet, x, y)) {
			list.add(new Node(x, y));
		}
	}
	

	private int calcNodeCost(Node node1, Node node2) {
		return Math.abs(node2.x - node1.x) + Math.abs(node2.y - node1.y);
	}
	
	private Node findOpenList(PriorityQueue<Node> nodes, Node point) {
		for (Node n : nodes) {
			if ((n.x == point.x) && (n.y == point.y)) {
				return n;
			}
		}
		return null;
	}
	
	public List<Node> getPathList(Node parent) {
		List<Node> list = new ArrayList<>();
		while (parent != null) {
			list.add(new Node(parent.x, parent.y));
			parent = parent.parent;
		}
		return list;
	}
	
	private void addKey(Set<String> set, int x, int y) {
		set.add(getKey(x, y));
	}
	
	private void removeKey(Set<String> set, int x, int y) {
		set.remove(getKey(x, y));
	}
	
	private boolean contains(Set<String> set, int x, int y) {
		return set.contains(getKey(x, y));
	}
	
	private String getKey(int x, int y) {
		StringBuilder sb = new StringBuilder();
		sb.append(x).append('_').append(y);
		return sb.toString();
	}
	
}
