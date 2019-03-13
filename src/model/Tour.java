package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.Pair;

public class Tour {

	private List<Node> vertexList;
	private List<Path> pathList;
	private double totalTime;

	public Tour() {
		vertexList = new ArrayList<Node>();
		pathList = new ArrayList<Path>();
	}

	public void addVertex(Node vertex) {
		vertexList.add(vertex);
	}

	public void buildPaths(Node store, HashMap<Pair<Node, Node>, Path> pathDict) {
		if (vertexList.size() > 0) {
			Path p = getPath(store, vertexList.get(0), pathDict);
			pathList.add(p);
			for (int i = 1; i < vertexList.size(); ++i) {
				p = getPath(vertexList.get(i - 1), vertexList.get(i), pathDict);
				pathList.add(p);
			}
			p = getPath(vertexList.get(vertexList.size() - 1), store, pathDict);
			pathList.add(p);
		}
	}
	
	public void removeDeliverySpot(Node node, HashMap<Pair<Node, Node>, Path> pathDict,Node store) {
		int size = this.vertexList.size();
		int i; 
		if(this.vertexList.get(0) == node) 
			i = 0;
		else if(this.vertexList.get(size-1) == node)
			i = size - 1;
		else
			i = this.vertexList.indexOf(node);	
		if(size == 1) {
			this.vertexList.clear();
			this.pathList.clear();
		} else {
			this.vertexList.remove(node);
			this.pathList.remove(i);
			this.pathList.remove(i);
			Path p = null;
			if(i == 0) 
				p = this.getPath(store, this.vertexList.get(i), pathDict);
			else if(i == size-1) 
				p = this.getPath(this.vertexList.get(i-1), store, pathDict); //ca marche (je pense)
			else 
				p = this.getPath(this.vertexList.get(i-1), this.vertexList.get(i), pathDict);
			this.pathList.add(i,p);
		}
	}
	
	public void addDeliverySpot(DeliverySpot deliverySpot, Node previousNode, HashMap<Pair<Node, Node>, Path> pathDict, Node store) {
		//getting index of previous node
		int indexNode;
		int size = this.vertexList.size();
		Node nextNode;
		if(previousNode == store) {
			indexNode = 0;
			if (size == 0) {
				nextNode = store;
			}
			else {
				nextNode = this.vertexList.get(0);
			}
		}
		else if(previousNode == this.vertexList.get(size-1)) {
			indexNode = size;
			nextNode = store;
		}
		else {
			indexNode = this.vertexList.indexOf(previousNode)+1;
			nextNode = this.vertexList.get(indexNode);
		}
		this.vertexList.add(indexNode,deliverySpot.getAddress());
		Path p1 = this.getPath(previousNode, deliverySpot.getAddress(), pathDict);
		Path p2 = this.getPath(deliverySpot.getAddress(), nextNode, pathDict);
		if (size > 0) {
		this.pathList.remove(indexNode);
		}
		this.pathList.add(indexNode, p2);
		this.pathList.add(indexNode,p1);
	}

	public void changePositionInTour(DeliverySpot deliverySpot, Node newPreviousNode, Node store, HashMap<Pair<Node, Node>, Path> pathDict) {
		int indexDeliverySpot = this.vertexList.indexOf(deliverySpot.getAddress());
		Node previousNode, nextNode;
		if(indexDeliverySpot == 0) 
			previousNode = store;
		else 
			previousNode = this.vertexList.get(indexDeliverySpot-1);
		if(indexDeliverySpot == (this.vertexList.size()-1))
			nextNode = store; 
		else 
			nextNode = this.vertexList.get(indexDeliverySpot+1);
		// removing path from actual position
		this.pathList.remove(indexDeliverySpot);
		this.pathList.remove(indexDeliverySpot);
		Path p = this.getPath(previousNode, nextNode, pathDict);
		this.pathList.add(indexDeliverySpot,p);
		
		// adding after newPreviousNode
		this.vertexList.remove(indexDeliverySpot);
		int indexNewPrevious = -1;
		if(newPreviousNode != store)
			indexNewPrevious = this.vertexList.indexOf(newPreviousNode);
		Node newNextNode; 
		if(indexNewPrevious == (this.vertexList.size()-1))
			newNextNode = store;
		else 
			newNextNode = this.vertexList.get(indexNewPrevious+1); //+1
		this.pathList.remove(indexNewPrevious+1); //+1
		Path p1 = this.getPath(newPreviousNode, deliverySpot.getAddress(), pathDict);
		Path p2 = this.getPath(deliverySpot.getAddress(), newNextNode, pathDict);
		this.pathList.add(indexNewPrevious+1,p2); //+1
		this.pathList.add(indexNewPrevious+1,p1); //+1
		this.vertexList.add(indexNewPrevious+1,deliverySpot.getAddress()); //+1
	}
	

	public List<Path> getPathList() {
		return pathList;
	}
	
	public void setDuration(double duration) {
		this.totalTime = duration;
	}
	
	public double getDuration() {
		return this.totalTime;
	}

	private Path getPath(Node origin, Node destination, HashMap<Pair<Node, Node>, Path> pathDict) {
		for (Pair<Node, Node> key : pathDict.keySet()) {
			if (key.first.getId() == origin.getId() && key.second.getId() == destination.getId()) {
				return pathDict.get(key);
			}
		}
		return null;
	}

	public List<Node> getVertexList() {
		return vertexList;
	}

}
