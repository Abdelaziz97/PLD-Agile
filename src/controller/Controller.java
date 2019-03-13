package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;
import view.Window;

/**
 * Controller est le controleur de l'application
 * 
 * @author Hexanome H4202
 * @version 1.0
 */
public class Controller {

	private ModelInterface modelInterface;
	private CommandList commandList;

	private State currentState;

	protected final StateInit initState = new StateInit();
	protected final StateLoadedMap loadMapState = new StateLoadedMap();
	protected final StateLoadedDelivery loadedDeliveryState = new StateLoadedDelivery();
	protected final StateCalculatingTour calculatingTourState = new StateCalculatingTour();
	protected final StateCalculatedTour calculatedTourState = new StateCalculatedTour();
	protected final StateModifyDeliveries modifyDeliveriesState = new StateModifyDeliveries();

	/**
	 * Cree le controleur de l'application
	 * @param modelInterface
	 */
	public Controller(ModelInterface modelInterface) {
		this.modelInterface = modelInterface;
		this.currentState = initState;
		this.commandList = new CommandList();
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Charger un plan"
	 * @param window la fenetre
	 */
	public void loadMap(Window window) {
		commandList.reset();
		currentState.loadMap(this, modelInterface, window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Charger des
	 * demandes de livraison"
	 * @param window la fenetre
	 */
	public void loadDelivery(Window window) {
		commandList.reset();
		currentState.loadDelivery(this, modelInterface, window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic pour changer le nombre de livreurs
	 * @param window la fenetre
	 * @param newNumber le nouveau nombre de livreurs
	 */
	public void changeDeliverMenNumber(Window window, int newNumber) {
		System.out.println("Controleur : changement nombre : " + newNumber);
		currentState.changeDeliveryMenNumber(window, modelInterface, newNumber);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Calculer les
	 * tournees"
	 * @param window la fenetre
	 * @param duration la duree max autorisee pour le calcul
	 */
	public void calculateTours(Window window, int duration) {
		commandList.reset();
		currentState.calculateTours(this, modelInterface, window, duration);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Arreter le calcul
	 * des tournees"
	 * @param window la fenetre
	 */
	public void stopCalculatingTours(Window window) {
		currentState.stopCalculatingTours(this, modelInterface, window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Supprimer un
	 * point de livraison"
	 * @param window la fenetre
	 * @param deliverySpot le point de livraison a supprimer
	 */
	public void deleteDelivery(Window window, DeliverySpot deliverySpot) {
		currentState.deleteDelivery(this, modelInterface, window, commandList, deliverySpot);
		this.currentState.updateWindowButtons(window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Ajouter un point de livraison"
	 * @param window la fenetre
	 * @param deliverySpot le point de livraison a ajouter
	 * @param previousNode l'intersection precedant le nouveau point
	 * @param tour la tournee dans laquelle le nouveau point va etre ajoute
	 */
	public void addDelivery(Window window, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
		currentState.addDelivery(this, modelInterface, window, commandList, deliverySpot, previousNode, tour);
		this.currentState.updateWindowButtons(window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Modifier"
	 * @param window la fenetre
	 */
	public void modifyDeliveries(Window window) {
		currentState.modifyDeliveries(this, window);
	}
	
	/**
	 * change l'etat courant du controleur
	 * @param state le nouvel etat courant
	 * @param window
	 */
	public void setCurrentState(State state, Window window) {
		this.currentState = state;
		this.currentState.updateWindowButtons(window);
	}

	
	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Undo"
	 * @param window la fenetre
	 */
	public void undo(Window window) {
		this.currentState.undo(commandList);
		this.currentState.updateWindowButtons(window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Redo"
	 * @param window la fenetre
	 */
	public void redo(Window window) {
		this.currentState.redo(commandList);
		this.currentState.updateWindowButtons(window);
	}

	/**
	 * Methode appelee par la fenetre apres un clic sur le bouton "Annuler"
	 * @param window la fenetre
	 */
	public void cancel(Window window) {
		this.currentState.cancel(this, window);
		this.currentState.updateWindowButtons(window);
	}

	/**
	 * Methode appelee par la fenetre apres un "clic" sur les fleches du clavier
	 * @param window la fenetre
	 */
	public void moveNodeInTour(Window window, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
		this.currentState.moveNodeInTour(this, modelInterface, window, commandList, deliverySpot, previousNode, tour);
	}

	public static void main(String[] args) {
		ModelInterface modelInterface = new ModelInterface();
		Controller controller = new Controller(modelInterface);
		new Window(controller, modelInterface);
	}
}