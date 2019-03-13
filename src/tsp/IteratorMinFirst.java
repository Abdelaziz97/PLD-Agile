package tsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * IteratorMinFirst est la classe qui permet d'iterer sur l'ensemble des sommets de nonVus avec 
 * la particularité de réaliser cela par ordre de cout croissant entre le sommet courant et les 
 * autres sommets
 * 
 * @author Hexanome H4202
 * @version 1.0
 */
public class IteratorMinFirst implements Iterator<Integer> {

	private ArrayList<Integer> candidats;
	private double[][] cost;
	private Integer currentVertex;

	/**
	 * Cree un iterateur pour iterer sur l'ensemble des sommets de nonVus avec la particularité de
	 * réaliser cela par ordre de cout croissant entre le sommet courant et les autres sommets
	 * @param nonVus
	 * @param currentVertex
	 * @param cost
	 */
	public IteratorMinFirst(Collection<Integer> nonVus, int currentVertex, double[][] cost){
		this.candidats = new ArrayList<Integer>();
		this.cost = cost;
		this.currentVertex = currentVertex;
		for (Integer s : nonVus){
			candidats.add(s);
		}
	}
	
	@Override
	public boolean hasNext() {
		return candidats.size() > 0;
	}

	@Override
	public Integer next() {
		Integer min = candidats.get(0);
		int index = 0;

		for(int i = 1; i < candidats.size(); i++) {
			if(cost[currentVertex][candidats.get(i)] < cost[currentVertex][min]) {
				min = candidats.get(i);
				index = i;
			}
		}
		candidats.remove(index);
		return min;
	}

	@Override
	public void remove() {}

}