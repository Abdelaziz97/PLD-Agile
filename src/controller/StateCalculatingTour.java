package controller;

import model.ModelInterface;
import view.Window;
/**
 * etat atteint quand le calcul est en cours (apres un clic sur "calculer les tournees")
 * @author hexanome H4202
 *
 */
public class StateCalculatingTour implements State {

	@Override
	public void stopCalculatingTours(Controller controller, ModelInterface modelInterface, Window w) {
		try {
			modelInterface.interruptTSP();
			controller.setCurrentState(controller.calculatedTourState, w);
			w.switchView();
			System.out.println("changement d'etat : calcul terminé");
		} catch (Exception e) {
			w.printMessage(e);
		}
	}

	@Override
	public void updateWindowButtons(Window window) {
		window.allowOnlyStopButton();
	}

}
