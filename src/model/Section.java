package model;

/**
 * Cette classe represente un troncon c'est a dire un noeud de depart et
 * d'arrivee, la distance entre les deux, et un nom de rue.
 * @author Hexanome H4202
 * @version 1.0
 */

public class Section {

	private String streetName;
	private double length;
	private Node source;
	private Node destination;

	public Section(String streetName, double length, Node source, Node destination) {
		super();
		this.streetName = streetName;
		this.length = length;
		this.source = source;
		this.destination = destination;
	}

	public String getStreetName() {
		return streetName;
	}

	public double getLength() {
		return length;
	}

	public Node getSource() {
		return source;
	}

	public Node getDestination() {
		return destination;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Section) {
			Section sectionTmp = (Section) obj;
			return (this.destination.getId() == sectionTmp.getDestination().getId()
					&& this.source.getId() == sectionTmp.getSource().getId());
		}
		return false;

	}

}
