package tsp;

import java.util.ArrayList;

/**
 * TSP2 est une classe réalisant les calcul pour un TSP par separation et evaluation 
 * (branch and bound) avec un iterateur sequentiel et une surcharge de la fonction
 * bound avec une heuristique
 *     
 * @author Hexanome H4202
 * @version 1.0
 */
public class TSP2 extends TSP1{

	@Override
	protected double bound(Integer currentVertex, ArrayList<Integer> nonVus, double[][] cost, double[] duration) {
		
		double sum = 0;
		
		double minPtCrt = Double.MAX_VALUE;
		for(Integer i : nonVus) {
			//Pour le sommet courant
			if(cost[currentVertex][i] < minPtCrt) {
				minPtCrt = cost[currentVertex][i];
			}
		}
		sum += minPtCrt;
		
		for(Integer i : nonVus) {
			double min = cost[i][0];
			for(Integer j : nonVus) {
				if( (cost[i][j] < min) && (i != j)  ) {
					min = cost[i][j];
				}
			}
			sum = sum + min + duration[i];
		}
		
		return sum;
	}
}
