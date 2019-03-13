package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;

/**
 * commande de deplacement d'un point de livraison dans une tournee
 * @author H4202
 *
 */
public class CommandMoveDeliveryInTour implements Command {
	
	private ModelInterface modelInterface;
	private Node previousNode;
	private DeliverySpot deliverySpot;
	private Tour tour;
	private Node firstPreviousNode;
	
	/**
	 * cree la commande qui deplace le point de livraison dans une tournee
	 * @param modelInterface
	 * @param previousNode l'intersection precedente
	 * @param deliverySpot le point de livraison a deplacer
	 * @param tour le tournee contenant le point de livraison
	 */
	public CommandMoveDeliveryInTour(ModelInterface modelInterface, Node previousNode, DeliverySpot deliverySpot,
			Tour tour) {
		super();
		this.modelInterface = modelInterface;
		this.previousNode = previousNode;
		this.deliverySpot = deliverySpot;
		this.tour = tour;
		this.firstPreviousNode = modelInterface.getDeliveryPosition(deliverySpot).second;
	}

	@Override
	public void doCde() {
		this.modelInterface.changePositionInTour(deliverySpot, previousNode, tour);
	}

	@Override
	public void undoCde() {
		this.modelInterface.changePositionInTour(deliverySpot, firstPreviousNode, tour);
	}
}
