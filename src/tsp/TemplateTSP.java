package tsp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TemplateTSP est une classe definissant le patron (template) d'une 
 * resolution par separation et evaluation (branch and bound) du TSP  
 *   
 * @author Hexanome H4202
 * @version 1.0
 */
public abstract class TemplateTSP implements TSP {
	
	private Integer[] bestSolution;
	private double costBestSolution = 0;
	private Boolean limitTimeReached;
	private TSPListener listener;
	private boolean interruptTSP = false;
	
	/**
	 * Methode permettant d'enregistrer un listener permettant de notifier aux couches supérieurs de la
	 * découverte d'une nouvelle solution optimale pour le TSP
	 * @param listener
	 */	
	public void registerTSPListener(TSPListener listener) {
		this.listener = listener;
	}
	
	public void setInterruptTSP (boolean interruptTSP) {
		this.interruptTSP = interruptTSP;
	}
	
	/**
	 * Methode permettant notifier aux couches supérieurs de la découverte d'une nouvelle solution optimale 
	 * pour le TSP
	 */	
	protected void notifyListenerBestSolution() {
		this.listener.onNewBestSolutionFound(this);
    }
	
	protected void notifyListenerTSPFinished() {
		this.listener.onTSPFinished();
	}
	
	/**
	 * Methode qui permet de retourner "True" si le temps maximal de calcul a ete atteint, sinon "False" 
	 * pour le TSP
	 * @return true si searchSolution() s'est terminee parce que la limite de temps avait ete atteinte, 
	 * avant d'avoir pu explorer tout l'espace de recherche,
	 */
	public Boolean isTimeOutReached(){
		return limitTimeReached;
	}
	
	/**
	 * Methode qui permet de déclencher la resolution du probleme par separation et evaluation 
	 * (branch and bound) du TSP
	 * @param limitTime : limite (en millisecondes) sur le temps d'execution de chercheSolution
	 * @param nbVertices : nombre de sommet du graphe a visiter
	 * @param cost : cost[i][j] = duree pour aller de i a j, avec 0 <= i < nbVertex et 0 <= j < nbVertex
	 * @param duration : duration[i] = duree pour visiter le sommet i, avec 0 <= i < nbVertex
	 */
	public void searchSolution(int limitTime, int nbVertices, double[][] cost, double[] duration){
		limitTimeReached = false;
		costBestSolution = Integer.MAX_VALUE;
		bestSolution = new Integer[nbVertices];
		ArrayList<Integer> nonVus = new ArrayList<Integer>();
		for (int i=1; i<nbVertices; i++) nonVus.add(i);
		ArrayList<Integer> vus = new ArrayList<Integer>(nbVertices);
		vus.add(0); 
		branchAndBound(0, nonVus, vus, 0, cost, duration, System.currentTimeMillis(), limitTime);
		notifyListenerTSPFinished();
	}
	
	/**
	 * Methode qui retourne le numéro du i eme sommet a visiter de la solution optimale du probleme
	 * @param i
	 * @return le sommet visite en i-eme position dans la solution calculee par searchSolution
	 */
	public Integer getBestSolution(int i){
		if ((bestSolution == null) || (i<0) || (i>=bestSolution.length))
			return null;
		return bestSolution[i];
	}
	
	/**
	 * Methode qui retourne la solution optimale du probleme sous forme de tableau d'Integer représentant
	 * les numeros des sommets à visiter par ordre chronologique
	 * @return : tableau d'entiers représentant les numeros des sommets à visiter par ordre chronologique
	 */
	public Integer[] getBestSolution() {
		if ((bestSolution == null))
			return null;
		return bestSolution;
	}
	
	/** 
	 * @return la duree de la solution calculee par chercheSolution
	 */
	public double getCostBestSolution(){
		return costBestSolution;
	}
	
	/**
	 * Methode abstraite redefinie par les sous-classes de TemplateTSP
	 * @param currentVertex
	 * @param nonVus : tableau des sommets restant a visiter
	 * @param cost : cost[i][j] = duree pour aller de i a j, avec 0 <= i < nbVertex et 0 <= j < nbVertex
	 * @param duration : duration[i] = duree pour visiter le sommet i, avec 0 <= i < nbVertex
	 * @return une borne inferieure du cost des permutations commencant par currentVertex, 
	 * contenant chaque sommet de nonVus exactement une fois et terminant par le sommet 0
	 */
	protected abstract double bound(Integer currentVertex, ArrayList<Integer> nonVus, double[][] cost, double[] duration);
	
	/**
	 * Methode abstraite redefinie par les sous-classes de TemplateTSP
	 * @param currentVertex
	 * @param nonVus : tableau des sommets restant a visiter
	 * @param cost : cost[i][j] = duration pour aller de i a j, avec 0 <= i < nbVertex et 0 <= j < nbVertex
	 * @param duration : duration[i] = duration pour visiter le sommet i, avec 0 <= i < nbVertex
	 * @return un iterateur permettant d'iterer sur tous les sommets de nonVus
	 */
	protected abstract Iterator<Integer> iterator(Integer currentVertex, ArrayList<Integer> nonVus, double[][] cost, double[] duration);
	
	/**
	 * Methode definissant le patron (template) d'une resolution par separation et evaluation (branch and bound) du TSP
	 * @param currentVertex le dernier sommet visite
	 * @param nonVus la liste des sommets qui n'ont pas encore ete visites
	 * @param vus la liste des sommets visites (y compris currentVertex)
	 * @param costVus la somme des costs des arcs du chemin passant par tous les sommets de vus + la somme des duration des sommets de vus
	 * @param cost : cost[i][j] = duration pour aller de i a j, avec 0 <= i < nbVertex et 0 <= j < nbVertex
	 * @param duration : duration[i] = duration pour visiter le sommet i, avec 0 <= i < nbVertex
	 * @param beginTime : moment ou la resolution a commence
	 * @param limitTime : limite de temps pour la resolution
	 */	
	 public void branchAndBound(int currentVertex, ArrayList<Integer> nonVus, ArrayList<Integer> vus, double costVus, double[][] cost, double[] duration, long beginTime, int limitTime){

		if ((System.currentTimeMillis() - beginTime > limitTime) || (interruptTSP)){
			limitTimeReached = true;
			return;
		}
		if (nonVus.size() == 0){ // tous les sommets ont ete visites
			costVus += cost[currentVertex][0];
			if (costVus < costBestSolution){ // on a trouve une solution meilleure que bestSolution
					vus.toArray(bestSolution);
					costBestSolution = costVus;
					notifyListenerBestSolution();
			}
		} else if (costVus + bound(currentVertex, nonVus, cost, duration) < costBestSolution){
		    Iterator<Integer> it = iterator(currentVertex, nonVus, cost, duration);
		    while (it.hasNext()){
		    	Integer nextVertex = it.next();
		    	vus.add(nextVertex);
		    	nonVus.remove(nextVertex);
		    	branchAndBound(nextVertex, nonVus, vus, costVus + cost[currentVertex][nextVertex] + duration[nextVertex], cost, duration, beginTime, limitTime);
		    	vus.remove(nextVertex);
		    	nonVus.add(nextVertex);
		    }	    
		}
		
	 }
	 
	 
	
}
	 
