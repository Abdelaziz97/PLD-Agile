package tsp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TSP1 est une classe réalisant les calcul pour un TSP par separation et evaluation 
 * (branch and bound) avec un iterateur sequentiel et une heuristique naive pour la 
 * fonction bound
 *     
 * @author Hexanome H4202
 * @version 1.0
 */
public class TSP1 extends TemplateTSP {

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, ArrayList<Integer> nonVus, double[][] cost, double[] duration) {
		return new IteratorSeq(nonVus, currentVertex);
	}
	
    @Override
	protected double bound(Integer sommetCourant, ArrayList<Integer> nonVus, double[][] cout, double[] duree) {
		return 0;
	}
	
}
