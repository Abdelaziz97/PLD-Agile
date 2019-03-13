package view.listeners;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import view.Window;

/**
 * Ecouteur pour le spinner qui permet de fixer la durée de calcul maximale des tournées.
 * 
 * @author H4202
 */
public class TimeSpinnerListener implements ChangeListener {
	private Window window;
	
	public TimeSpinnerListener(Window window) {
		this.window = window;
	}

	/**
	 * Lorsque la valeur du spinner change, mets à jour cette valeur dans la fenêtre.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) e.getSource();
		this.window.setComputingDuration((Integer) spinner.getValue()); 
	}
}
