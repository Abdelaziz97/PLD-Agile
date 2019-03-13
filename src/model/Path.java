package model;

import java.util.LinkedList;

/**
 * Cette classe represente un chemin c'est a dire une sucession de tronçon et le temps 
 * que l'on a mis à les traverser
 * @author Hexanome H4202
 * @version 1.0
 */

public class Path {
	
	private LinkedList<Section> sectionsList;
	private double duration; 
	
	public Path(LinkedList<Section> sectionsList, double duration) {
		this.sectionsList = sectionsList; 
		this.duration = duration; 
	}
	
	public LinkedList<Section> sectionList(){
		return this.sectionsList;
	}
	
	public double getDuration() {
		return this.duration/250;
	}
	
}
