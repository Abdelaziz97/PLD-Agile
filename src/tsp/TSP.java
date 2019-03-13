package tsp;

/**
 * TSP est l'interface qui definit les methodes à surcharger afin de réaliser le calcul du TSP  
 * @author Hexanome H4202
 * @version 1.0
 */
public interface TSP {
	
	/**
	 * @return true si chercheSolution() s'est terminee parce que la limite de temps avait ete atteinte, 
	 * avant d'avoir pu explorer tout l'espace de recherche,
	 */
	public Boolean isTimeOutReached();
	
	/**
	 * Cherche un circuit de duree minimale passant par chaque sommet (compris entre 0 et nbSommets-1)
	 * @param limitTime : limite (en millisecondes) sur le temps d'execution de chercheSolution
	 * @param nbVertices : nombre de sommet du graphe a visiter
	 * @param cost : cost[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duration : duration[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 */
	public void searchSolution(int limitTime, int nbVertices, double[][] cost, double[] duration);
	
	/**
	 * @param i
	 * @return le sommet visite en i-eme position dans la solution calculee par searchSolution
	 */
	public Integer getBestSolution(int i);
	
	/** 
	 * @return la duree de la solution calculee par chercheSolution
	 */
	public double getCostBestSolution();
}
