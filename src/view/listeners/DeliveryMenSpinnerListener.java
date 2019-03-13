package view.listeners;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import view.Window;

/**
 * Ecouteur pour le spinner de sélection du nombre de livreurs
 * 
 * @author H4202
 */
public class DeliveryMenSpinnerListener implements ChangeListener {
	private Controller controller;
	private Window window;

	public DeliveryMenSpinnerListener(Controller controller, Window window) {
		this.controller = controller;
		this.window = window;
	}

	/**
	 * Lorsque la valeur du spinner est changée, mets à jour via le contrôleur cette
	 * valeur dans le modèle.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) e.getSource();
		this.controller.changeDeliverMenNumber(this.window, (Integer) spinner.getValue());
	}
}
