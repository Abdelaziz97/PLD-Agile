package model;

import tsp.TSP3;
import tsp.TSPListener;

public class TourFinder extends Thread{
	
	private Graph graph;
	private int limitTime;
	private Tour tour;
	public static int nbTour = 0;
	public int index;
	private TSP3 tsp;	
	
	public TourFinder(Graph graph, int limitTime) {
		this.graph = graph;
		this.limitTime = limitTime; 
		this.tsp = new TSP3();
		this.index = nbTour ;
		++nbTour;
	}
	
	public void run() {
		calculate();
		nbTour--;
	}
	
	public void interruptTSP() {
		tsp.setInterruptTSP(true);
	}
	
	private void calculate() {
		int nbVertices = graph.getDeliveryTime().length;
		tsp.searchSolution(limitTime, nbVertices, graph.getCost(), graph.getDeliveryTime());
	}
	
	public void buildTour() {
		int nbVertices = graph.getDeliveryTime().length;
		tour = new Tour();
		
		for(int j = 1; j < nbVertices ; j++) {
			int nextIndex = tsp.getBestSolution(j);
			Node nextVertex = graph.getDeliverySpotFromIndex(nextIndex); 
			tour.addVertex(nextVertex);
		}
		
		double duration = 0;
		for (double dur : graph.getDeliveryTime()) {
			duration += dur;
		}
		tour.setDuration(duration);
	}
	
	public void registerTSPListener(TSPListener tspListener) {
		tsp.registerTSPListener(tspListener);
	}
	
	public int getIndex() {
		return index;
	}
	

	public Tour getTour() {
		return this.tour;
	}

}
