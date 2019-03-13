package model;

import java.util.HashSet;

/**
 * Cette classe caracterise un noeud c'est un dire un point definit par une longitude, latitude,
 * si ce dernier est atteignable , et enfin la liste des noeuds qu'on peut rejoindre en partant
 * de lui
 * @author Hexanome H4202
 * @version 1.0
 */

public class Node {
	

	private double longitude;
	private double latitude;
	private long id;
	private HashSet<Section> sectionsList; 
	private boolean reachable;
	

	public Node(double longitude, double latitude, long id) {
		this.sectionsList = new HashSet<Section>();
		this.longitude = longitude;
		this.latitude = latitude;
		this.id = id;
		this.reachable = true;
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public boolean isReachable() {
		return reachable;
	}

	public void setReachable(boolean reachable) {
		this.reachable = reachable;
	}

	public void addSection(Section section) {
		this.sectionsList.add(section);
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public long getId() {
		return id;
	}
	
	public HashSet<Section> getSectionsList(){
		return this.sectionsList;
	}


}
