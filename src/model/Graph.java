package model;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Cette classe represente le graphe. Il est caracterisé par sa matrice d'adjacence avec 
 * ses éléments numerotés, une tableau qui permet de faire l'association entre 
 * un noeud et son indice, et enfin d'un tableau qui permet de faire l'association entre
 * un noeud et le temps de déchargement du noeud visité.
 * @author Hexanome H4202
 * @version 1.0
 */

public class Graph {
	
	private int index = 0; 
	private double[][] cost; 
	private double[] deliveryTimes; 
	private HashMap<Node,Integer> nodeToIndex; 
	
	public Graph(ModelInterface mi){
		int nbVertices = mi.getDeliverySpots().size()+1;
		this.cost = new double[nbVertices][nbVertices];
		this.deliveryTimes = new double[nbVertices];
		List<DeliverySpot> deliverySpotList = mi.getDeliverySpots();
		this.nodeToIndex = new HashMap<Node,Integer>();
		this.nodeToIndex.put(mi.getStore(), index);
		++index;
		for(DeliverySpot ds : deliverySpotList) {
			this.nodeToIndex.put(ds.getAddress(), index);
			this.deliveryTimes[index] = ds.getUnloadingTime();
			++index;
		}
		this.cost = new double[nbVertices][nbVertices];
		this.deliveryTimes = new double[nbVertices];
	}
	
	public Graph(double[][] cost, double[] deliveryTimes, HashMap<Node, Integer> nodeToIndex){
		this.cost = cost;
		this.deliveryTimes = deliveryTimes;
		this.nodeToIndex = nodeToIndex;
	}
	
	/**
	 * Construit un sous graphe a partir d'une liste d'indices représentant les points de livraison 
	 * @param indexList : liste d'indices représentant les points de livraison 
	 * @return sous-graphe associé
	 */
	public Graph subGraph(List<Integer> indexList) {
		int nbVertices = indexList.size();
		double[][] subCost = new double[nbVertices][nbVertices];
		double[] subDeliveryTimes = new double[nbVertices];
		HashMap<Node, Integer> subNodeToIndex = new HashMap<Node, Integer>();
		
		for (int i = 0; i < indexList.size(); ++i) {
			for (int j = 0; j < indexList.size(); ++j) {
				subCost[i][j] = this.cost[indexList.get(i)][indexList.get(j)];
			}
			subDeliveryTimes[i] = this.deliveryTimes[indexList.get(i)];
			Node toPutInMap = null;
			for (Entry<Node, Integer> entry : this.nodeToIndex.entrySet()) {
				if (entry.getValue() == indexList.get(i)) {
					toPutInMap = entry.getKey();
				}
			}
			subNodeToIndex.put(toPutInMap, i);
		}
		
		return new Graph(subCost, subDeliveryTimes, subNodeToIndex);
	}
	
	/**
	 * Ajoute le noeud au graphe
	 * @param node : noeud à ajouter
	 */
	public void addNode(Node node){
		if(nodeToIndex.containsKey(node))
			return;
		else {
			nodeToIndex.put(node, index);
			++index;
		}
	}
	
	/**
	 * Renvoie le noeud associé à l'indice donné en paramètre
	 * @param index : indice du noeud que l'on souhaite récupérer
	 * @return node : noeud que l'on souhaite récupérer
	 */
	public Node getDeliverySpotFromIndex(int index) {
		if(nodeToIndex.containsValue(index)) {
			for (Node key : nodeToIndex.keySet()){
			    if(nodeToIndex.get(key)==index)
			    	return key;
			}
		}
		return null;
	}
	

	/**
	 * Méthode permettant d'ajouter un point de livraison à la structure
	 * @param deliverySpot : point de livraison à ajouter
	 */
	public void addDeliverySpot(DeliverySpot deliverySpot) {
		//recreating cost array
		double[][] newCost = new double[cost.length+1][cost.length+1];
		for(int i = 0; i < this.cost.length; ++i) {
			for(int j = 0; j < this.cost.length; ++j) {
				newCost[i][j] = this.cost[i][j];
			}
		}
		this.cost = newCost;
		//recreating deliveryTime
		double[] newDeliveryTime = new double[this.deliveryTimes.length+1];
		for(int i = 0; i < this.deliveryTimes.length; ++i) {
			newDeliveryTime[i] = this.deliveryTimes[i];
		}
		newDeliveryTime[newDeliveryTime.length-1] = deliverySpot.getUnloadingTime();
		this.deliveryTimes = newDeliveryTime;
		//adding to nodeToIndex
		this.nodeToIndex.put(deliverySpot.getAddress(),index);
		++index;
	}
	
	public double[][] getCost(){
		return this.cost;
	}
	
	public void setCost(int origin, int destination, double cost) {
		this.cost[origin][destination] = cost; 
	}

	public double[] getDeliveryTime() {
		return this.deliveryTimes;
	}
	
	public Integer getIndexFromDeliverySpot(Node node) {
		return (nodeToIndex.containsKey(node)) ? nodeToIndex.get(node) : null;
	}
}
