package controller;

import model.ModelInterface;
import view.Window;
import xml.DeserializerXML;
/**
 * etat atteint quand le fichier de demandes de livraisons est charge
 * @author hexanome H4202
 *
 */
public class StateLoadedDelivery implements State {
	
	private boolean notUndo = false;
	private boolean notRedo = false;

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
	public void calculateTours(Controller controller, ModelInterface modelInterface, Window w, int duration) {
		try {
			modelInterface.reinitializeCalcul();
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(duration*1000);
			controller.setCurrentState(controller.calculatingTourState, w);
			
			System.out.println("changement d'etat : calcul en cours");
		} catch (Exception e) {
			w.printMessage(e);
		}
	}

	@Override
	public void modifyDeliveries(Controller controller, Window window) {
		try {
			controller.modifyDeliveriesState.setState(this);
			controller.setCurrentState(controller.modifyDeliveriesState, window);
			System.out.println("changement d'etat : modification");
		} catch (Exception e) {
			window.printMessage(e);
		}
	}

	@Override
	public void undo(CommandList commandList) {
		commandList.undo();
		this.updateFlags(commandList);
	}

	@Override
	public void redo(CommandList commandList) {
		commandList.redo();
		this.updateFlags(commandList);
	}
	
	@Override
	public void updateFlags(CommandList commandList) {
		notRedo = commandList.notRedo();
		notUndo = commandList.notUndo();
	}

	@Override
	public void updateWindowButtons(Window window) {
		window.allowAllButtonsExceptCancelAndStop(notUndo,notRedo);
	}

}
