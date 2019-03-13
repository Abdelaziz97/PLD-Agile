package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import controller.Controller;
import view.Window;
import view.model.ViewAction;
import view.model.ViewModel;

/**
 * Ecouteur pour les boutons de la fenêtre. Fait appel au contrôleur et au ViewModel.
 * 
 * @author H4202
 */
public class ButtonListener implements ActionListener {

	private Controller controller;
	private Window window;
	private ViewModel viewModel;

	public ButtonListener(Controller controller, Window window, ViewModel viewModel) {
		this.controller = controller;
		this.window = window;
		this.viewModel = viewModel;
	}

	/**
	 * En fonction du bouton sélectionné, appele les bons services du contrôleur et / ou du ViewModel
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case Window.BUTTON_LOAD_MAP:
			if (this.viewModel.getTours().isEmpty() == false) {
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Si vous chargez un nouveau plan, vous tournées calculées seront perdues. Continuer ?",
						"Attention", JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					controller.loadMap(window);
				}
			} else {
				controller.loadMap(window);
			}
			break;
		case Window.BUTTON_LOAD_DELIVERIES:
			if (this.viewModel.getTours().isEmpty() == false) {
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Si vous chargez de nouvelles livraisons, vous tournées calculées seront perdues. Continuer ?",
						"Attention", JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					controller.loadDelivery(window);
				}
			} else {
				controller.loadDelivery(window);
			}
			break;
		case Window.BUTTON_CALCULATE:
			this.viewModel.highlightDeliverySpot(null);
			int duration = -1;
			try {
				duration = Integer.parseInt(JOptionPane.showInputDialog("Rentrez une durée maximale de calcul (secondes)", 30));	
			} catch(Exception exception) {
			}
			if (duration <= 0) {
				JOptionPane.showMessageDialog(this.window,
						"Vous devais saisir un nombre strictement positif",
						"Erreur de saisie", JOptionPane.WARNING_MESSAGE);
				}else {
                controller.calculateTours(window, duration);	
			}
			controller.calculateTours(window, this.window.getComputingDuration());
			break;
		case Window.BUTTON_STOP_CALCUL:
			controller.stopCalculatingTours(window);
			break;
		case Window.BUTTON_MODIFY:
			this.viewModel.setCurrentAction(ViewAction.MODIFY);
			controller.modifyDeliveries(window);
			break;
		case Window.BUTTON_UNDO:
			controller.undo(window);
			break;
		case Window.BUTTON_REDO:
			controller.redo(window);
			break;
		case Window.BUTTON_CANCEL:
			this.viewModel.setCurrentAction(ViewAction.MAIN);
			controller.cancel(window);
			break;
		case Window.BUTTON_HELP:
			String text = "[Y] Changer couleurs des tournées\n" + "[F] Changer affichage des flèches\n"
					+ "[Ctrl + Z] Annuler\n" + "[Ctrl + Shift + Z] Rétablir\n" + "[M] Modifier\n"
					+ "[Echap] Terminer la modification\n" + "[D] Afficher / masquer les points non atteignables";
			JOptionPane.showMessageDialog(this.window, text, "Raccourcis claviers", JOptionPane.INFORMATION_MESSAGE);
			break;
		}

	}

}
