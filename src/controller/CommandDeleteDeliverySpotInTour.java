package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;

/**
 * commande de suppression du point de livraison dans une tournee
 * @author H4202
 *
 */
public class CommandDeleteDeliverySpotInTour implements Command {
	private ModelInterface modelInterface;
	private DeliverySpot deliverySpot;
	private Node previousNode;
	private Tour tour;
	
	/**
	 * cree la commande qui supprime un point de livraison dans une tournee, apres le calcul
	 * @param modelInterface
	 * @param deliverySpot point de livraison a supprimer
	 * @param previousNode l'intersection precedente
	 * @param tour la tournee dans laquelle on supprime le point de livraison
	 */
	public CommandDeleteDeliverySpotInTour(ModelInterface modelInterface, DeliverySpot deliverySpot, Node previousNode,
			Tour tour) {
		super();
		this.modelInterface = modelInterface;
		this.deliverySpot = deliverySpot;
		this.previousNode = previousNode;
		this.tour = tour;
	}

	@Override
	public void doCde() {
		modelInterface.removeDeliverySpot(deliverySpot);
	}

	@Override
	public void undoCde() {
		try {
			modelInterface.addDeliverySpotToTour(deliverySpot, previousNode, tour);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

	
}
