package model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import util.Util;

/**
 * La classe Map correspond à une représentation orientée objet de la carte.
 * Elle est caracterisée par une liste de noeud ainsi qu'un noeud particulier qui est l'entrepot (point de
 * depart de toutes nos livraisons)
 * @author Hexanome H4202
 * @version 1.0
 */
public class Map {

	private HashMap<Long, Node> nodeList;
	private Node store;
	private double totalLengthForDijsktra = 0d;
	private PriorityQueue<NodeDistance> priorityQueue;
	private HashSet<Node> unreachableNode;

	public Map() {
		this.unreachableNode = new HashSet<Node>();
		this.nodeList = new HashMap<Long, Node>();
		this.store = null;
		this.unreachableNode = new HashSet<Node>();
	}
	
	/**
	 * Renvoie le noeud le plus proche du point definit par sa latitude et sa longitude
	 * @param longitude
	 * @param latitude
	 * @return Node
	 */
	public Node nearestNode(double longitude, double latitude) {
		Node nearestNode = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Entry<Long, Node> entry : this.nodeList.entrySet()) {
			double distance = this.distanceSquared(entry.getValue(), longitude, latitude);
			if (distance < minDistance) {
				minDistance = distance;
				nearestNode = entry.getValue();
			}
		}
		return nearestNode;
	}
	
	/**
	 * Renvoie la distance au carre entre le point(longitude du noeud,latitude du noeud)
	 * et le point(longitude,latitude) 
	 * 
	 * @param node1 
	 * @param longitude 
	 * @param latitude
	 * @return
	 */
	private double distanceSquared(Node node1, double longitude, double latitude) {
		return (node1.getLatitude() - latitude) * (node1.getLatitude() - latitude)
				+ (node1.getLongitude() - longitude) * (node1.getLongitude() - longitude);
	}

	/**
	 * Renvoie le noeud au cout le moins élevé lors du dijkstra
	 * @return
	 */
	private Node minDistance() {
		NodeDistance nodeDistance = priorityQueue.remove();
		return nodeDistance.node;
	}
	
	/**
	 * Calcul les chemins les plus courts en terme de distance 
	 * a partir des elements de la classe map a partir d'un noeud source
	 * et instancie une matrice des couts des plus courts chemins entre les point des livraisons, 
	 * et les chemins qui existe entre ces points de livraison dans l'instancde de l'objet ModelInterface
	 * @param g Graphe
	 * @param mi ModelInterface
	 * @param src Noeud source 
	 * @throws Exception
	 */

	public void dijkstra(Graph g, ModelInterface mi, Node src) throws Exception {
		int nbNodeMap = this.getNodeList().size();
		Integer sourceIndex = g.getIndexFromDeliverySpot(src);
		if (sourceIndex == null)
			throw new Exception("Noeud source null");

		HashMap<Node, Double> distance = new HashMap<Node, Double>();
		HashMap<Node, Node> pred = new HashMap<Node, Node>();
		HashMap<Node, Boolean> visited = new HashMap<Node, Boolean>();

		priorityQueue = new PriorityQueue<NodeDistance>(nbNodeMap, new NodeDistance());

		double max = Double.MAX_VALUE;
		// put the distance to max for each vertex

		for (HashMap.Entry<Long, Node> entry : this.getNodeList().entrySet()) {
			Node node = entry.getValue();
			distance.put(node, max);
			pred.put(node, null);
			visited.put(node, false);
		}

		priorityQueue.add(new NodeDistance(src, 0d));
		distance.put(src, 0d);

		while (!priorityQueue.isEmpty()) {
			Node minNode = minDistance();
			visited.put(minNode, true);

			HashSet<Section> successor = this.getNodeList().get(minNode.getId()).getSectionsList();

			if (successor != null) {
				for (Section s : successor) {
					if (visited.get(s.getDestination()) == false
							&& distance.get(minNode) + s.getLength() < distance.get(s.getDestination())) {

						double distanceToAdd = distance.get(minNode) + s.getLength();
						distance.put(s.getDestination(), distanceToAdd);
						priorityQueue.add(new NodeDistance(s.getDestination(), distanceToAdd));
						pred.put(s.getDestination(), minNode);
						Integer destinationIndex = g.getIndexFromDeliverySpot(s.getDestination());

						if (destinationIndex != null) {
							g.setCost(sourceIndex, destinationIndex, distanceToAdd);
						}
					}

				}
			}
		}
		this.fillPaths(pred, mi, src);
	}
	
	/**
	 * Instancie la liste des intersection dans l'instance de modelInterface a partir
	 * de la table des precedance calculee lors du dijkstra, et d'un noeud de depart
	 * @param pred Table de précédence calculée lors du dijkstra
	 * @param mi ModelInterface
	 * @param src Noeud Source
	 */
	private void fillPaths(HashMap<Node, Node> pred, ModelInterface mi, Node src) {
		for (DeliverySpot DeliverySpotdest : mi.getDeliverySpots()) {
			Node dest = DeliverySpotdest.getAddress();
			if (dest.getId() != src.getId()) {
				LinkedList<Section> sections = this.createPath(pred, mi.getMap(), dest);
				double duration = this.totalLengthForDijsktra;
				mi.addPath(src, dest, new Path(sections, duration));
			}
		}
		if (mi.getStore().getId() != src.getId()) {
			LinkedList<Section> sections = this.createPath(pred, mi.getMap(), mi.getStore());
			double duration = this.totalLengthForDijsktra;
			mi.addPath(src, mi.getStore(), new Path(sections, duration));
		}
	}
	
	/**
	 * Cree une liste de sections (qui correspond a un chemin entre deux noeuds) 
	 * a partir d'un noeud destination et de la liste des predecesseurs calculee lors du 
	 * dijkstra
	 * @param pred Table de précédence calculée lors du dijkstra
	 * @param map
	 * @param dest Noeud destination
	 * @return le chemin créé
	 */
	private LinkedList<Section> createPath(HashMap<Node, Node> pred, Map map, Node dest) {
		this.totalLengthForDijsktra = 0d;
		LinkedList<Section> pathRes = new LinkedList<Section>();
		Node child = dest;
		Node father = dest;
		while (true) {
			father = pred.get(child);
			if (father == null)
				break;
			Section toAdd = map.getSection(father, child);
			this.totalLengthForDijsktra += toAdd.getLength();
			child = father;
			pathRes.addFirst(toAdd);
		}

		return pathRes;
	}
	
	/**
	 * Renvoie la section la plus proche du point definit par sa latitude et sa longitude.
	 * @param longitude
	 * @param latitude
	 * @return la section la plus proche du point
	 */
	public Section getNearestSection(double longitude, double latitude) {
		Section nearSection = null;
		double x1, y1, x2, y2, a, b = 0d;
		double dmin = Double.MAX_VALUE;
		double d = 0;
		double[] point1 = new double[2];
		double[] point2 = new double[2];
		double[] point3 = new double[2];
		point3[0] = longitude;
		point3[1] = latitude;
		for (Entry<Long, Node> entry : this.nodeList.entrySet()) {
			HashSet<Section> sections = entry.getValue().getSectionsList();
			for (Section section : sections) {
				x1 = section.getSource().getLongitude();
				y1 = section.getSource().getLatitude();
				point1[0] = x1;
				point1[1] = y1;

				x2 = section.getDestination().getLongitude();
				y2 = section.getDestination().getLatitude();
				point2[0] = x2;
				point2[1] = y2;

				a = (y2 - y1) / (x2 - x1);
				b = y1 - a * x1;

				d = Util.LineToPointDistance2D(point1, point2, point3, true);

				if (d < dmin) {
					dmin = d;
					nearSection = section;
				}

			}
		}
		return nearSection;
	}

	/**
	 * Permet de retrouver l'ensemble des intersections qui ne peuvent etre atteint par le store 
	 * et qu'on ne peut pas ainsi considerer comme des points de livraisons
	 * Remplie l'attribut unreachableNode par ces intersections
	 */
	public void checkReachable() {
		for (HashMap.Entry<Long, Node> entry : this.nodeList.entrySet()) {
			if (entry.getValue().getSectionsList().size() == 0) {
				entry.getValue().setReachable(false);
				this.unreachableNode.add(entry.getValue());
			}
		}
		boolean foundUnreachable = true; 
		while(foundUnreachable == true) {
			foundUnreachable = false; 
			for(HashMap.Entry<Long,Node> entry : this.nodeList.entrySet()) {
				boolean realDestination = false;
				if(entry.getValue().isReachable() == true) {
					HashSet<Section> sectionList = entry.getValue().getSectionsList();
					for(Section s : sectionList) {
						if(this.unreachableNode.contains(s.getDestination()) == false) {
							realDestination = true;
							break;
						}
					}
					if(realDestination == false) {
						foundUnreachable = true;
						entry.getValue().setReachable(false);
						this.unreachableNode.add(entry.getValue());
					}
				}
			}
		}
	}
	
	/**
	 * Methode utilisee par la methode DFS
	 * @param node intersection depuis laquelle on veut determiner les intersetions pouvant etre
	 * atteints et ceux qui ne peuvent etre atteints 
	 * @param visited hashmap contenant l'ensemble des intersections de la map comme clef
	 * et un booleen comme valeur pour determiner s'il a ete visite ou pas
	 */
    public void DFSUtil(Node node,HashMap<Node, Boolean> visited) { 
        visited.put(node, true); 
        
        HashSet<Section> listNode = node.getSectionsList();
        for (Section sec : listNode) { 
            if (visited.get(sec.getDestination()) == false) { 
                DFSUtil(sec.getDestination(), visited); 
            }
        } 
    } 
    
    /**
	 * Methode permettant de determiner les intersections pouvant etre atteints par le parametre 
	 * origin et ceux qui ne peuvent pas etre atteints 
	 * Remplissant ainsi l'attribut unreachableNode par les intersections ne pouvant pas 
	 * etre atteints depuis origin
	 * @param origin intersection depuis laquelle on veut determiner les intersetions pouvant etre
	 * atteints et ceux qui ne peuvent etre atteints 
	 */
    public void DFS(Node origin) { 
    	HashMap<Node, Boolean> visited = new HashMap<Node, Boolean>();
        
        for (HashMap.Entry<Long, Node> entry : this.getNodeList().entrySet()) {
			Node node = entry.getValue();
			visited.put(node, false);
		}

        DFSUtil(origin, visited);
        for (HashMap.Entry<Node, Boolean> entry : visited.entrySet()) {
        	if (entry.getValue() == false) {
        		entry.getKey().setReachable(false);
        		this.unreachableNode.add(entry.getKey());
        	}
        }
    }
    
	public Node getStore() {
		return store;
	}
	
	public HashMap<Long, Node> getNodeList() {
		return nodeList;
	}
	
	public HashSet<Node> getUnreachableNode() {
		return unreachableNode;
	}
	
	public Section getSection(Node src, Node dest) {
		HashSet<Section> toSearch = this.nodeList.get(src.getId()).getSectionsList();
		for (Section section : toSearch) {
			if (section.getDestination().getId() == dest.getId())
				return section;
		}
		return null;

	}
	
	public void addNode(Node toAdd) {
		this.nodeList.put(toAdd.getId(), toAdd);
	}

	public void addSection(Section toAdd) {
		this.nodeList.get(toAdd.getSource().getId()).getSectionsList().add(toAdd);
	}

	class NodeDistance implements Comparator<NodeDistance> {
		public Node node;
		public double cost;

		public NodeDistance() {
		}

		public NodeDistance(Node node, double cost) {
			this.node = node;
			this.cost = cost;
		}

		@Override
		public int compare(NodeDistance node1, NodeDistance node2) {
			if (node1.cost < node2.cost)
				return -1;
			if (node1.cost > node2.cost)
				return 1;
			return 0;
		}
	}
	
}
