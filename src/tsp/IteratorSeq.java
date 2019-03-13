package tsp;

import java.util.Collection;
import java.util.Iterator;

/**
 * IteratorSeq est la classe qui permet d'iterer sequentiellement sur l'ensemble des sommets de nonVus
 * 
 * @author Hexanome H4202
 * @version 1.0
 */
public class IteratorSeq implements Iterator<Integer> {

	
	private Integer[] candidates;
	
	
	private int nbCandidates;

	/**
	 * Cree un iterateur pour iterer sequentiellement sur l'ensemble des sommets de nonVus
	 * @param nonVus
	 * @param currentVertex
	 */
	public IteratorSeq(Collection<Integer> nonVus, int currentVertex){
		this.candidates = new Integer[nonVus.size()];
		nbCandidates = 0;
		for (Integer s : nonVus){
			candidates[nbCandidates++] = s;
		}
	}
	
	@Override
	public boolean hasNext() {
		return nbCandidates > 0;
	}

	@Override
	public Integer next() {
		return candidates[--nbCandidates];
	}

	@Override
	public void remove() {}

}
