package controller;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Tour;
import view.Window;

/**
 * State
 * 
 * @author Hexanome H4202
 * @version 1.0
 */
public interface State {
	
	public void updateWindowButtons(Window window);
	
	/**
	* Methode appelee par controleur apres un clic sur le bouton "Charger un plan"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	*/
	public default void loadMap(Controller controller, ModelInterface modelInterface, Window w) {
	}

	/**
	* Methode appelee par controleur apres un clic sur le bouton "Charger les demandes de livraison"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	*/
	public default void loadDelivery(Controller controller, ModelInterface modelInterface, Window w) {
	}

	/**
	* Methode appelee par controleur pour changer le nombre de livreurs
	* @param window la fenetre
	* @param ModelInterface
	* @param number le nouveau nombre de livreurs
	*/
	public default void changeDeliveryMenNumber(Window w, ModelInterface modelInterface, int number) {
	}

	/**
	* Methode appelee par controleur apres un clic sur le bouton "Calculer les tournees"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	* @param duration la duree max autorisee pour le calcul
	*/
	public default void calculateTours(Controller controller, ModelInterface modelInterface, Window w, int duration) {
	}

	/**
	* Methode appelee par controleur apres un clic sur le bouton "Arrêter"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	*/
	public default void stopCalculatingTours(Controller controller, ModelInterface modelInterface, Window w) {
	}

	/**
	* Methode appelee par controleur apres un clic sur "Ajouter un point de livraison"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	* @param commandList la liste de commandes
	* @param deliverySpot le point de livraison que l'on veut ajouter
	* @param previousNode l'intersection precedant celui que l'on veut ajouter
	* @param tour la tournee dans laquelle on veut ajouter un point de livraison
	*/
	public default void addDelivery(Controller controller, ModelInterface modelInterface, Window w,
			CommandList commandList, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
	}

	/**
	* Methode appelee par controleur apres un clic sur le bouton "Modifier"
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	*/
	public default void editDelivery(Controller controller, ModelInterface modelInterface, Window w) {
	}

	/**
	* Methode appelee par controleur apres un clic sur les fleches du clavier
	* @param controller le controleur
	* @param ModelInterface
	* @param window la fenetre
	* @param commandList la liste de commandes
	* @param deliverySpot le point de livraison que l'on veut deplacer dans la tournee
	* @param previousNode l'intersection precedant celui que l'on veut ajouter
	* @param tour la tournee dans laquelle on veut deplacer un point de livraison
	*/
	public default void moveNodeInTour(Controller controller, ModelInterface modelInterface, Window w,
			CommandList commandList, DeliverySpot deliverySpot, Node previousNode, Tour tour) {
	}

	/**
	 * Methode appelee par le controleur apres un clic sur le bouton "undo"
	 * @param commandList la liste de commandes
	 */
	public default void undo(CommandList commandList) {
	}

	/**
	 * Methode appelee par le controleur apres un clic sur le bouton "redo"
	 * @param commandList la liste de commandes
	 */
	public default void redo(CommandList commandList) {
	}

	/**
	 * Methode appelee par le controleur apres un clic sur le bouton "annuler"
	 * @param controller le controleur
	 * @param window la fenetre
	 */
	public default void cancel(Controller controller, Window window) {
	}

	/**
	 * Methode appelee par le controleur apres un clic sur le bouton "Modifier"
	 * @param controller le controleur
	 * @param window la fenetre
	 */
	public default void modifyDeliveries(Controller controller, Window window) {
	}

	/**
	 * Methode verifiant si des commandes undo ou redo sont disponibles dans
	 * le nouvel etat
	 * @param commandList la liste de commandes
	 */
	public default void updateFlags(CommandList commandList) {
	}

	/**
	 * Methode appelee par le controleur pour changer le nombre de livreurs
	 * @param modelInterface
	 * @param w la fenetre
	 * @param newNumber le nouveau nombre de livreurs
	 */
	public default void changeDeliveryMenNumber(ModelInterface modelInterface, Window w, int newNumber){}
	
	/**
	 * Methode appelee par le controleur apres un clic sur "Supprimer un point de livraison"
	 * @param controller le controleur
	 * @param modelInterface
	 * @param w la fenetre
	 * @param commandList la liste de commandes
	 * @param deliverySpot le point de livraison a supprimer
	 */
	public default void deleteDelivery(Controller controller, ModelInterface modelInterface, Window w,
			CommandList commandList, DeliverySpot deliverySpot) {
	}


}
