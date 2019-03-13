package controller;

import model.ModelInterface;
import view.Window;
import xml.DeserializerXML;
/**
 * etat atteint lorsque le fichier contenant le plan est charge
 * @author hexanome H4202
 *
 */
public class StateLoadedMap implements State {

	@Override
	public void loadDelivery(Controller controller, ModelInterface modelInterface, Window w) {
		try {
			DeserializerXML.loadDeliverySpots(modelInterface);
			controller.setCurrentState(controller.loadedDeliveryState, w);
			System.out.println("changement d'etat : livraisons chargees");
		} catch (Exception e) {
			w.printMessage(e);
		}
	}

	@Override
	public void changeDeliveryMenNumber(Window w, ModelInterface modelInterface, int number) {
		modelInterface.setDeliveryMenNumber(number);
	}

	@Override
	public void loadMap(Controller controller, ModelInterface modelInterface, Window w) {
		try {
			DeserializerXML.loadMap(modelInterface);
			controller.setCurrentState(controller.loadMapState, w);
			System.out.println("changement d'etat : plan charge");
		} catch (Exception e) {
			w.printMessage(e);
		}
	}

	@Override
	public void updateWindowButtons(Window window) {
		window.allowLoadMapAndDeliveryButtons();
	}
}
