package controller;

import model.ModelInterface;
import xml.DeserializerXML;
import view.Window;
/**
 * etat initial de l'application
 * @author hexanome H4202
 *
 */
public class StateInit implements State {

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
	public void changeDeliveryMenNumber(Window w, ModelInterface modelInterface, int number) {
		modelInterface.setDeliveryMenNumber(number);
	}

	@Override
	public void updateWindowButtons(Window window) {
		window.allowOnlyLoadMapButton();
	}

}
