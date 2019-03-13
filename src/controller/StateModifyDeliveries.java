package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;
import util.Pair;
import view.Window;
/**
 * etat atteint lorsque l'on passe en mode edition (apres un clic sur "Modifier")
 * @author hexanome H4202
 *
 */
public class StateModifyDeliveries implements State {
	private State currentState;
	private boolean notUndo = false;
	private boolean notRedo = false;
	
	@Override
	public void updateFlags(CommandList commandList) {
		notRedo = commandList.notRedo();
		notUndo = commandList.notUndo();
	}
	
	@Override
	public void addDelivery(Controller controller, ModelInterface modelInterface, Window window,
			CommandList commandList, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
		if (tour == null) {
			commandList.add(new CommandAddDeliverySpot(modelInterface, deliverySpot));
		} else {
			commandList.add(new CommandAddDeliverySpotInTour(modelInterface, deliverySpot, previousNode, tour));
		}
		currentState.updateFlags(commandList);
		this.updateFlags(commandList);
	}
	
	@Override
	public void deleteDelivery(Controller controller, ModelInterface modelInterface, Window window,
			CommandList commandList, DeliverySpot deliverySpot) {
		if (currentState instanceof StateLoadedDelivery) {
			commandList.add(new CommandReverse(new CommandAddDeliverySpot(modelInterface, deliverySpot)));
		} else if (currentState instanceof StateCalculatedTour) {
			Tour currentTour=null;
			Node previousNodeSupp=null;
			Pair<Tour,Node> a = modelInterface.getDeliveryPosition(deliverySpot);
			currentTour = a.first;
			previousNodeSupp = a.second;
			commandList.add(new CommandReverse(new CommandAddDeliverySpotInTour(modelInterface, deliverySpot, previousNodeSupp, currentTour)));
		}
		currentState.updateFlags(commandList);
		this.updateFlags(commandList);
	}
	
	@Override
	public void moveNodeInTour(Controller controller, ModelInterface modelInterface, Window window,
			CommandList commandList, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
		commandList.add(new CommandMoveDeliveryInTour(modelInterface, previousNode, deliverySpot, tour));
		currentState.updateFlags(commandList);
		this.updateFlags(commandList);
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
	public void cancel(Controller controller, Window window) {
		controller.setCurrentState(currentState, window);
		System.out.println("Retour etat precedent ");
	}

	public void setState(State state) {
		this.currentState = state;
	}

	@Override
	public void updateWindowButtons(Window window) {
		window.allowOnlyCancelButton(notUndo,notRedo);
	}
}
