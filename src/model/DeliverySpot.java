package model;

/**
 * Cette classe représente un point de livraison qui est en réalité un noeud
 * avec un temps de déchargement
 */
public class DeliverySpot {
	private Node address;
	private int unloadingTime;
	
	public DeliverySpot(Node address, int unloadingTime) {
		this.address = address;
		this.unloadingTime = unloadingTime;
	}
	public Node getAddress() {
		return address;
	}
	public int getUnloadingTime() {
		return unloadingTime;
	}
	
}
