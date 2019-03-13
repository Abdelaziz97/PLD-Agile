package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;

/**
 * commande d'ajout de point de livraison dans une tournee
 * @author Hexanome 4202
 *
 */
public class CommandAddDeliverySpotInTour implements Command {
	private ModelInterface modelInterface;
	private DeliverySpot deliverySpot;
	private Node previousNode;
	private Tour tour;
	
	/**
	 * cree la commande qui ajoute un point de livraison dans une tournee, apres le calcul
	 * @param modelInterface
	 * @param deliverySpot point de livraison a ajouter
	 * @param previousNode l'intersection precedente
	 * @param tour la tournee dans laquelle on ajoute le point de livraison
	 */
	public CommandAddDeliverySpotInTour(ModelInterface modelInterface, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
		this.modelInterface = modelInterface;
		this.deliverySpot = deliverySpot;
		if (tour != null && previousNode != null) {
		this.previousNode = previousNode;
		this.tour = tour;
		}else {
			
		}
	}

	@Override
	public void doCde() {
		try {
			modelInterface.addDeliverySpotToTour(deliverySpot, previousNode, tour);
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public void undoCde() {
		modelInterface.removeDeliverySpot(deliverySpot);
	}
}
