package tsp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TSP3 est une classe réalisant les calcul pour un TSP par separation et evaluation 
 * (branch and bound) avec un iterateur MinFirst et une surcharge de la fonction
 * bound avec une heuristique
 *     
 * @author Hexanome H4202
 * @version 1.0
 */
public class TSP3 extends TSP2{
	
	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, ArrayList<Integer> nonVus, double[][] cost, double[] duration) {
		return new IteratorMinFirst(nonVus, currentVertex, cost);
	}

}
