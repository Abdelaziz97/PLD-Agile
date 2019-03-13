package controller;

import model.DeliverySpot;
import model.ModelInterface;

/**
 * commande de suppression du point de livraison
 * @author H4202
 *
 */
public class CommandDeleteDeliverySpot implements Command {
	private ModelInterface modelInterface;
	private DeliverySpot deliverySpot;

	/**
	 * cree la commande qui supprime un point de livraison
	 * @param modelInterface
	 * @param deliverySpot point de livraison
	 */
	public CommandDeleteDeliverySpot(ModelInterface modelInterface, DeliverySpot deliverySpot) {
		this.modelInterface = modelInterface;
		this.deliverySpot = deliverySpot;
	}

	@Override
	public void doCde() {
		modelInterface.removeDeliverySpot(deliverySpot);
	}

	@Override
	public void undoCde() {
		modelInterface.addDeliverySpot(deliverySpot);
	}
}
