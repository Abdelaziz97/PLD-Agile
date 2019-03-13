package controller;

import model.DeliverySpot;
import model.ModelInterface;

/**
 * commande d'ajout du point de livraison
 * @author H4202
 *
 */
public class CommandAddDeliverySpot implements Command {

	private ModelInterface modelInterface;
	private DeliverySpot deliverySpot;

	/**
	 * cree la commande qui ajoute un point de livraison
	 * @param modelInterface
	 * @param deliverySpot point de livraison
	 */
	public CommandAddDeliverySpot(ModelInterface modelInterface, DeliverySpot deliverySpot) {
		this.modelInterface = modelInterface;
		this.deliverySpot = deliverySpot;
	}

	@Override
	public void doCde() {
		modelInterface.addDeliverySpot(deliverySpot);

	}

	@Override
	public void undoCde() {
		modelInterface.removeDeliverySpot(deliverySpot);
	}

}
