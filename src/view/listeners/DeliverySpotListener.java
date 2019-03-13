package view.listeners;

import java.awt.event.MouseEvent;

import view.DeliverySpotLabel;
import view.model.ViewModel;

/**
 * Ecouteur pour les DeliverySpotLabel afin de gérer la sélection et la mise en
 * avant d'un point de livraison sur la vue graphique.
 * 
 * @author H4202
 */
public class DeliverySpotListener implements java.awt.event.MouseListener {

	private ViewModel viewModel;

	public DeliverySpotListener(ViewModel viewModel) {
		this.viewModel = viewModel;
	}

	/**
	 * Sélectionne le point de livraison dans le ViewModel pour le mettre en avant
	 * dans la vue graphique.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		DeliverySpotLabel spotLabel = (DeliverySpotLabel) e.getSource();
		this.viewModel.highlightDeliverySpot(spotLabel.getDeliverySpotIndex());
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}