package tsp;

/**
 * TSPListener est l'interface qui permet de définir de méthodes qui trigger les couches
 * du dessus afin de prévenir la fin des calculs ou la découverte d'une nouvelle solution
 * optimale lors du calcul
 * 
 * @author Hexanome H4202
 * @version 1.0
 */
public interface TSPListener {
	
	/**
	 * Methode invoquee lorsque une nouvelle solution optimale a ete trouve lors du calcul du TSP
	 * @param tsp
	 */
	public void onNewBestSolutionFound(TemplateTSP tsp);
	
	public void onTSPFinished();

}
