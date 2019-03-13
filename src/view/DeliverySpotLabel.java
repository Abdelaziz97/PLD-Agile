package view;

import javax.swing.JLabel;

/**
 * Un JLabel qui contient l'indice d'un point de livraison.
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class DeliverySpotLabel extends JLabel {
	private int deliverySpotIndex;
	
	public DeliverySpotLabel(String text, int index) {
		super(text);
		this.deliverySpotIndex = index;
	}

	public int getDeliverySpotIndex() {
		return deliverySpotIndex;
	}
}


