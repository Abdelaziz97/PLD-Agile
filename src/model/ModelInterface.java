package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import tsp.TSPListener;
import tsp.TemplateTSP;
import util.Pair;


/**
 * Cette classe permet de faire le lien entre le modele et le controleur  
 * @author Hexanome H4202
 * @version 1.0
 */
public class ModelInterface extends Observable {

	private Map map;
	private List<DeliverySpot> deliverySpots;
	private List<Node> unreachableSpots;
	private HashMap<Pair<Node, Node>, Path> pathList;
	private Node store;
	private LocalTime startHour;
	private Graph graph;
	private List<Tour> tourList;
	private int deliveryMenNumber;
	private List<TourFinder> tourFinderList;
	private int nbTSPFinished = 0;
	private boolean areTSPFinished = false;

	public ModelInterface() {
		this.map = new Map();
		this.deliverySpots = new ArrayList<DeliverySpot>();
		this.unreachableSpots = new LinkedList<Node>();
		this.store = null;
		this.pathList = new HashMap<Pair<Node, Node>, Path>();
		this.tourList = new ArrayList<Tour>();
		this.deliveryMenNumber = 1;
	}
	
	/**
	 * Méthode permettant de réinitialiser les variables après avoir déjà réalisé 
	 * un calcul dans l'éventualité où on souhaite refaire un calcul avec des nouvelles
	 * tournées ou un nouveau nombre de tournées
	 */
	public void reinitializeCalcul() {
		this.unreachableSpots = new LinkedList<Node>();
		unreachableSpots.clear();
		this.tourList = new ArrayList<Tour>();
		tourList.clear();
		this.nbTSPFinished = 0;
		this.areTSPFinished = false;
	}
	
	/**
	 * Appel le dijkstra de la classe Map en prenant comme point de depart l'entrepot et 
	 * l'ensemble des points de livraisons
	 * @throws Exception
	 */
	public void calculateDijkstra() throws Exception {
		graph = new Graph(this);
		this.map.dijkstra(graph, this, this.getStore());
		for (DeliverySpot ds : this.getDeliverySpots()) {
			this.map.dijkstra(graph, this, ds.getAddress());
		}
	}
	
	/**
	 * Methode permettant de lancer le clustering des points de livraisons puis calcul
	 * des tours avec calcul de TSP
	 * @param duration : duree maximale de calcul pour le TSP
	 * @throws InterruptedException
	 */

	public void calculateKMeans(int duration) throws InterruptedException {
		this.tourList.clear();
		int nbTour = this.deliveryMenNumber;
		if (nbTour > deliverySpots.size())
			nbTour = deliverySpots.size();
		EKMeansCalculator kmcalcul = new EKMeansCalculator(nbTour, deliverySpots);
		kmcalcul.run();
		List<Integer>[] clusterList = kmcalcul.getClusterList();
		List<Graph> subgraphList = new ArrayList<Graph>();
		for (int i = 0; i < clusterList.length; ++i) {
			Graph subgraph = graph.subGraph(clusterList[i]);
			subgraphList.add(subgraph);
			this.tourList.add(new Tour());
		}
		calculateTours(subgraphList, duration);
	}
	
	/**
	 * Calcule la tournee optimale (TSP) pour chaque sousGraphe dans un temps limite
	 * @param subgraphList : Liste de sous-graphe générés par clustering des points de livraison
	 * @param limitTime : duree maximale de calcul pour les TSP
	 */

	private void calculateTours(List<Graph> subgraphList, int limitTime) {
		tourFinderList = new ArrayList<TourFinder>();
		tourFinderList.clear();
		for (Graph subgraph : subgraphList) {
			TourFinder tcalcul = new TourFinder(subgraph, limitTime);
			tcalcul.registerTSPListener(new TSPListener1(subgraph, tcalcul));
			tourFinderList.add(tcalcul);
		}

		for (TourFinder tcalcul : tourFinderList) {
			tcalcul.start();
		}
	}

	/**
	 * Méthode permettant d'interrompre le calcul du TSP
	 */
	public void interruptTSP() {
		for (TourFinder tourFinder : tourFinderList) {
			if (tourFinder != null) {
				tourFinder.interruptTSP();
			}
		}
	}

	
	/**
	 * Instancie l'objet tour avec les chemins qu'il contient ainsi que la durée de chaque 
	 * chemin et la duree total de la tournee
	 * @param tcalcul : TourFinder contenant le tour pour lequel on doit construire les trajets
	 * @return Tour
	 */
	private Tour buildTours(TourFinder tcalcul) {
		tcalcul.buildTour();
		Tour t = tcalcul.getTour();
		t.buildPaths(store, pathList);
		for (Path path : t.getPathList()) {
			t.setDuration(t.getDuration() + (path.getDuration()));
		}
		return t;
	}

	/**
	 * Methode permettant d'ajouter un point de livraison à la liste des deliverySpots
	 * @param deliverySpot: point de livraison à ajouter
	 */
	public void addDeliverySpot(DeliverySpot deliverySpot) {
		if (deliverySpot.getAddress().getSectionsList().size() == 0) {
			this.unreachableSpots.add(deliverySpot.getAddress());
		} else
			this.deliverySpots.add(deliverySpot);
		this.setChanged();
		this.notifyObservers(this.deliverySpots);
	}

	/**
	 * Methode permettant de supprimer un point de livraison à la liste des deliverySpots
	 * @param deliverySpot: point de livraison à supprimer
	 */
	public void removeDeliverySpot(DeliverySpot deliverySpot) {
		this.deliverySpots.remove(deliverySpot);
		Tour tour = null;
		for (Tour t : this.tourList) {
			for (Node ds : t.getVertexList()) {
				if (ds == deliverySpot.getAddress()) {
					tour = t;
					break;
				}
			}
		}
		if (tour != null)
			tour.removeDeliverySpot(deliverySpot.getAddress(), this.pathList, this.store);
		this.setChanged();
		this.notifyObservers();
	}

	
	/**
	 * Méthode permettant d'ajouter un point de livraison dans une tournee particuliere avant 
	 * le noeud passe en parametre 
	 * @param deliverySpot: point de livraison à ajouter
	 * @param previousNode : noeud correspondant au deliverySpot précédant le point que l'on souhaite ajouter 
	 * @param tour : tournée pour laquelle on souhaite ajouter le point de livraison
	 * @throws Exception
	 */
	public void addDeliverySpotToTour(DeliverySpot deliverySpot, Node previousNode, Tour tour) throws Exception {
		if (deliverySpot.getAddress().getSectionsList().size() == 0) {
			this.unreachableSpots.add(deliverySpot.getAddress());
		} else {
			this.deliverySpots.add(deliverySpot);
			this.graph.addDeliverySpot(deliverySpot);
			this.map.dijkstra(graph, this, previousNode);
			this.map.dijkstra(graph, this, deliverySpot.getAddress());
			tour.addDeliverySpot(deliverySpot, previousNode, this.pathList, this.store);
		}
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Méthode permettant de changer la position d'un noeud dans une tournee en l'ajoutant avant le noeud passe en
	 * parametre
	 * @param deliverySpot: point de livraison à ajouter
	 * @param newPreviousNode: noeud correspondant au deliverySpot précédant le point que l'on souhaite repositionner
	 * @param tour: tournée pour laquelle on souhaite ajouter le point de livraison
	 */
	public void changePositionInTour(DeliverySpot deliverySpot, Node newPreviousNode, Tour tour) {
		tour.changePositionInTour(deliverySpot, newPreviousNode, this.store, this.pathList);
		this.setChanged();
		this.notifyObservers();
	}
	

	public Map getMap() {
		return this.map;
	}

	public List<DeliverySpot> getDeliverySpots() {
		return this.deliverySpots;
	}
	
	public int getDeliveryMenNumber() {
		return deliveryMenNumber;
	}

	public Node getStore() {
		return this.store;
	}

	public void setStore(Node store) {
		this.store = store;
	}

	public LocalTime getStartHour() {
		return this.startHour;
	}

	public void setStartHour(LocalTime startHour) {
		this.startHour = startHour;
	}

	public HashMap<Pair<Node, Node>, Path> getPathList() {
		return this.pathList;
	}

	public void addPath(Node origin, Node destination, Path path) {
		this.pathList.put(new Pair<Node, Node>(origin, destination), path);
	}

	public List<Tour> getTourList() {
		return this.tourList;
	}

	public void setDeliveryMenNumber(int number) {
		this.deliveryMenNumber = number;
	}

	public void notifyMap() {
		this.setChanged();
		this.notifyObservers(this.map);
	}

	public List<Node> getUnreachLol() {
		return this.unreachableSpots;
	}
	
	/**
	 * Renvoie le noeud precedant (s'il appartient a une tournee) le point de livraison 
	 * passe en parametre
	 * @param deliverySpot : point de livraison pour lequel on souhaite avoir l'information
	 * @return Pair<Tour, Node> : Pair contenant le tour auquel appartient le point de livraison ainsi que le noeud le précédent
	 */
	public Pair<Tour, Node> getDeliveryPosition(DeliverySpot deliverySpot) {
		Tour currentTour = null;
		Node previousNodeSupp = null;
		l1: for (Tour t : this.getTourList()) {
			if (t.getVertexList().contains(deliverySpot.getAddress())) {
				currentTour = t;
				int i = t.getVertexList().lastIndexOf(deliverySpot.getAddress()) - 1;
				if (i >= 0)
					previousNodeSupp = t.getVertexList().get(i);
				else
					previousNodeSupp = store;
				break l1;
			}
		}
		return (new Pair<Tour, Node>(currentTour, previousNodeSupp));
	}

	/**
	 * Indique si le calcul du TSP est terminé
	 * @return TRUE si le TSP est fini, sinon FAUX
	 */
	public boolean areTSPFinished() {
		if (this.areTSPFinished) {
			this.areTSPFinished = false;
			return true;
		}
		return false;
	}

	public class TSPListener1 implements TSPListener {

		Graph subgraph;
		TourFinder tcalcul;

		public TSPListener1(Graph subgraph, TourFinder tcalcul) {
			this.subgraph = subgraph;
			this.tcalcul = tcalcul;
		}

		/**
		 * Methode de callback permettant à chaque calcul de tsp la 
		 * découverte d'une nouvelle solution optimale
		 */
		public void onNewBestSolutionFound(TemplateTSP tsp) {
			Tour t = new Tour();
			int nbVertices = subgraph.getDeliveryTime().length;
			for (int j = 1; j < nbVertices; j++) {
				int nextIndex = tsp.getBestSolution(j);
				Node nextVertex = subgraph.getDeliverySpotFromIndex(nextIndex);
				t.addVertex(nextVertex);
			}

			double duration = 0;
			for (double dur : subgraph.getDeliveryTime()) {
				duration += dur;
			}
			t.setDuration(duration);
			t.buildPaths(store, pathList);

			t = buildTours(tcalcul);
			int index = tcalcul.getIndex();
			tourList.set(index, t);

			synchronized (ModelInterface.this) {
				setChanged();
				notifyObservers();
			}
		}
		
		
		/**
		 * Methode de callback permettant à chaque calcul de tsp de
		 * prévenir la fin du calcul
		 */
		@Override
		public void onTSPFinished() {
			nbTSPFinished++;
			if (nbTSPFinished == tourFinderList.size()) {
				areTSPFinished = true;
				setChanged();
				notifyObservers();
			}
		}

	}
}
