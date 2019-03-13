package view.model;

import java.awt.Color;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import controller.Controller;
import model.DeliverySpot;
import model.Map;
import model.ModelInterface;
import model.Node;
import model.Path;
import model.Section;
import model.Tour;
import util.Util;
import view.Window;

/**
 * Réprésente l'interface entre la vue et le modèle. Lorsque le modèle est mis à
 * jour, le ViewModel se met également à jour avec les nouvelles données, et il
 * se charge de mettre à son tour à jour les différentes vues de la fenêtre. Il
 * contient également des données spécifiques à la vue concernant des objets du
 * modèle (par exemple, la tournée actuellement sélectionnée par l'utilisateur),
 * et centralise ces informations pour qu'elles soient accessibles depuis chaque
 * classe de la vue. Cela permet de réaliser la synchronisation entre les
 * différentes vues. Cette classe traite également certaines interactions
 * utilisateurs et fait appel au contrôleur lorsque c'est nécessaire.
 * 
 * @author H4202
 */
public class ViewModel extends Observable implements Observer {

	protected static final int NB_MAX_DELIVERIES = 30;
	private static ArrayList<Color> tourColors = new ArrayList<Color>(NB_MAX_DELIVERIES);

	public static final int TOUR_OVERCHARGED = -1;
	public static final int TOUR_UNDERCHARGED = 1;
	public static final int TOUR_CORRECTLY_CHARGED = 0;

	private Map map;

	private Node store;
	private LocalTime startHour;
	private ArrayList<DeliverySpot> deliverySpots;
	private ArrayList<Tour> tours;
	private ArrayList<Node> unreachableNodes;
	private ArrayList<Selection> possibleSelections;
	private Integer selectedTour;
	private Integer selectedPath;
	private Integer selectedSection;
	private Node highlightedNode;
	private ModelInterface modelInterface;
	private ViewAction currentAction;

	private boolean hasModelChanged;

	private Controller controller;
	private Window window;
	private Integer selectedDeliverySpot;

	public ViewModel(Window window, ModelInterface modelInterface, Controller controller) {
		this.map = null;
		this.deliverySpots = new ArrayList<DeliverySpot>();
		this.tours = new ArrayList<Tour>();
		this.unreachableNodes = new ArrayList<Node>();
		this.possibleSelections = new ArrayList<Selection>();
		this.selectedTour = null;
		this.selectedPath = null;
		this.selectedSection = null;
		this.highlightedNode = null;
		this.modelInterface = modelInterface;
		this.startHour = null;
		this.currentAction = ViewAction.MAIN;
		this.window = window;
		this.controller = controller;
		this.hasModelChanged = false;

		Util.newRandomColors(tourColors, NB_MAX_DELIVERIES);

		modelInterface.addObserver(this);
	}

	/**
	 * Permet de déplacer une livraison au sein d'une tournée, plus précisément en
	 * intervertissant la livraison avec celle se trouvant avant dans la tournée.
	 */
	public void moveUp() {
		if (this.getSelectedPath() != null && this.getSelectedPathIndex() > 0) {
			DeliverySpot deliverySpot = this
					.getDeliverySpot(this.getSelectedPath().sectionList().getLast().getDestination());
			Node previousNode = this.getSelectedTour().getPathList().get(this.getSelectedPathIndex() - 1).sectionList()
					.getFirst().getSource();
			Tour tour = this.getSelectedTour();
			this.controller.moveNodeInTour(this.window, deliverySpot, previousNode, tour);
			this.setCurrentAction(ViewAction.MODIFY);
		}
	}

	/**
	 * Permet de déplacer une livraison au sein d'une tournée, plus précisément en
	 * intervertissant la livraison avec celle se trouvant après dans la tournée.
	 */
	public void moveDown() {
		if (this.getSelectedPath() != null
				&& this.getSelectedPathIndex() < this.getSelectedTour().getPathList().size()) {
			DeliverySpot deliverySpot = this
					.getDeliverySpot(this.getSelectedPath().sectionList().getLast().getDestination());
			Node previousNode = this.getSelectedTour().getPathList().get(this.getSelectedPathIndex() + 1).sectionList()
					.getLast().getDestination();
			Tour tour = this.getSelectedTour();
			this.controller.moveNodeInTour(this.window, deliverySpot, previousNode, tour);
			this.setCurrentAction(ViewAction.MODIFY);
		}
	}

	/**
	 * Dans l'ordre : - Si les calculs des TSP sont terminés, appelle le contrôleur
	 * pour changer d'état - Mets à jour les références vers les objets du modèle
	 * (le plan, l'entrepôt, les points de livraisons, les tournées, l'heure de
	 * départ) - Mets à jour les indices des tournées, chemins et tronçons
	 * sélectionnés
	 */
	@Override
	public void update(Observable o, Object obj) {
		if (this.modelInterface.areTSPFinished()) {
			this.controller.stopCalculatingTours(this.window);
		}

		this.map = this.modelInterface.getMap();
		this.store = this.modelInterface.getStore();
		this.deliverySpots = new ArrayList<DeliverySpot>(this.modelInterface.getDeliverySpots());
		this.tours = new ArrayList<Tour>(this.modelInterface.getTourList());

		if (this.tours.isEmpty()) {
			this.selectedTour = null;
			this.selectedPath = null;
			this.selectedSection = null;
		}

		this.startHour = this.modelInterface.getStartHour();

		this.sendNotificationToView(true);
	}

	/**
	 * Mets à jour l'intersection actuellement sélectionnée et notifie la vue
	 * 
	 * @param node L'intersection à mettre en valeur
	 */
	public void highlightNode(Node node) {
		this.highlightedNode = node;
		this.sendNotificationToView(false);
	}

	/**
	 * Effectue l'action de sélectionner une intersection, c'est à dire lors d'une
	 * modification, de choisir un point sur lequel ajouter ou supprimer une
	 * livraison. Cela correspond au clic de souris sur le plan en mode
	 * modification. Si on a sélectionné une intersection vide, on ajoute un point
	 * de livraison, et si on a sélectionné un point de livraison, on le supprime.
	 * 
	 * @param node L'intersection concernée
	 */
	public void selectNode(Node node) {
		if (this.currentAction == ViewAction.MODIFY) {
			System.out.println("On a sélectionné : " + node);
			Integer spotIndex = this.getDeliverySpotIndex(node);

			if (node.equals(this.store)) {
				JOptionPane.showMessageDialog(this.window, "Vous ne pouvez pas modifier l'entrepôt.",
						"Modification entrepôt", JOptionPane.WARNING_MESSAGE);
				this.setCurrentAction(ViewAction.MAIN);
				return;
			}

			if (spotIndex == null) { // On a sélectionné un noeud normal
				if (node.isReachable() == false) {
					JOptionPane.showMessageDialog(this.window,
							"Vous ne pouvez pas ajouter de point de livraison ici, car ce noeud n'est pas atteignable depuis l'entrepôt.",
							"Livraison impossible", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Voulez-vous ajouter un point de livraison ici ?", "Ajout", JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					if (!this.tours.isEmpty()) {
						this.setCurrentAction(ViewAction.MODIFY_SELECT_PATH);
						this.window.setHelperText(Window.HELPER_SELECT_PATH);
						this.sendNotificationToView(false);
					} else {
						int duration = -1;
						try {
							duration = Integer.parseInt(JOptionPane.showInputDialog("Rentrez une durée", 0));
						} catch (Exception exception) {
						}
						if (duration < 0) {
							JOptionPane.showMessageDialog(this.window, "Vous devez saisir un nombre positif",
									"Erreur de saisie", JOptionPane.WARNING_MESSAGE);
						} else {
							DeliverySpot toAdd = new DeliverySpot(this.highlightedNode, duration);
							this.controller.addDelivery(window, toAdd, null, null);
							this.setCurrentAction(ViewAction.MODIFY);
							this.sendNotificationToView(false);
						}
					}
				}
			} else { // On a sélectionné un point de livraison
				System.out.println("On a sélectionné le DS : " + spotIndex);
				if (this.currentAction == ViewAction.MODIFY) {
					int dialogResult = JOptionPane.showConfirmDialog(this.window,
							"Etes-vous sûr de vouloir supprimer ce point de livraison ?", "Attention",
							JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						this.controller.deleteDelivery(window, this.deliverySpots.get(spotIndex));
						this.setCurrentAction(ViewAction.MODIFY);
						this.sendNotificationToView(false);
					}
				}
			}
		}
	}

	/**
	 * Méthode d'aide qui permet de vérifier si une intersection correspond à un
	 * point de livraison, et de renvoyer son indice le cas échéant.
	 * 
	 * @param node L'intersection à tester
	 * @return null si l'intersection n'est pas un point de livraison, l'indice de
	 *         la livraison sinon
	 */
	private Integer getDeliverySpotIndex(Node node) {
		int i = 0;
		for (DeliverySpot spot : this.deliverySpots) {
			if (spot.getAddress().equals(node)) {
				return i;
			}
			++i;
		}
		return null;
	}

	/**
	 * Méthode d'aide qui permet de vérifier si une intersection correspond à un
	 * point de livraison, et de le renvoyer le cas échéant.
	 * 
	 * @param node L'intersection à tester
	 * @return null si l'intersection n'est pas un point de livraison, le point de
	 *         livraison sinon
	 */
	private DeliverySpot getDeliverySpot(Node node) {
		for (DeliverySpot spot : this.deliverySpots) {
			if (spot.getAddress().equals(node)) {
				return spot;
			}
		}
		return null;
	}

	/**
	 * Retourne l'intersection actuellement sélectionnée par l'utilisateur
	 * 
	 * @return L'intersection actuellement sous la souris de l'utilisateur en mode
	 *         modification
	 */
	public Node getHighlightedNode() {
		return this.highlightedNode;
	}

	/**
	 * Effectue l'action de sélectionner un chemin dans une tournée, dans lequel on
	 * ajoute une intersection précédemment sélectionnée en mode modification.
	 * 
	 * @param tourIndex L'indice de la tournée dans laquelle on ajoute
	 *                  l'intersection
	 * @param pathIndex L'indice du chemin dans lequel on ajoute l'intersection
	 */
	public void selectPath(Integer tourIndex, Integer pathIndex) {
		System.out.println("Selection de : " + tourIndex + "; " + pathIndex);
		if (this.currentAction == ViewAction.MODIFY_SELECT_PATH) {
			Tour tour = this.tours.get(tourIndex);
			Path path = tour.getPathList().get(pathIndex);
			Node origin = path.sectionList().getFirst().getSource();
			int duration = -1;
			try {
				duration = Integer.parseInt(JOptionPane.showInputDialog("Rentrez une durée", 0));
			} catch (Exception exception) {
			}
			if (duration < 0) {
				JOptionPane.showMessageDialog(this.window, "Vous devez saisir un nombre positif", "Erreur de saisie",
						JOptionPane.WARNING_MESSAGE);
			} else {

				this.controller.addDelivery(this.window, new DeliverySpot(this.highlightedNode, duration), origin,
						tour);
				this.setCurrentAction(ViewAction.MODIFY);
			}
		}
	}

	/***
	 * Retourne une valeur indiquant si une tournée est surchargée, sous-chargée ou
	 * correctement chargée.
	 * 
	 * @param tour La tournée à tester
	 * @return TOUR_UNDERCHARGED si la tournée est surchargée, TOUR_OVERCHARGED si
	 *         la tournée est sous-chargée, TOUR_CORRECTLY_CHARGED sinon
	 */
	public int isCorrectlyCharged(Tour tour) {
		double meanTourCharge = ((double) this.modelInterface.getDeliverySpots().size())
				/ ((double) this.modelInterface.getDeliveryMenNumber());
		int min = (int) Math.floor(meanTourCharge);
		int max = (int) Math.ceil(meanTourCharge);
		int deliveries = tour.getPathList().size() - 1;

		return (deliveries < min) ? ViewModel.TOUR_UNDERCHARGED
				: ((deliveries > max) ? ViewModel.TOUR_OVERCHARGED : ViewModel.TOUR_CORRECTLY_CHARGED);
	}

	public Map getMap() {
		return map;
	}

	public Node getStore() {
		return store;
	}

	public ArrayList<DeliverySpot> getDeliverySpots() {
		return deliverySpots;
	}

	public ArrayList<Tour> getTours() {
		return tours;
	}

	public Tour getSelectedTour() {
		if (this.selectedTour != null) {
			return this.tours.get(this.selectedTour);
		}
		return null;
	}

	public Integer getSelectedTourIndex() {
		return this.selectedTour;
	}

	public Path getSelectedPath() {
		if (this.selectedTour != null && this.selectedPath != null) {
			return this.getSelectedTour().getPathList().get(this.selectedPath);
		}
		return null;
	}

	public Integer getSelectedPathIndex() {
		return this.selectedPath;
	}

	public Section getSelectedSection() {
		if (this.selectedTour != null && this.selectedPath != null && this.selectedSection != null) {
			return this.getSelectedPath().sectionList().get(this.selectedSection);
		}
		return null;
	}

	/**
	 * Mets en avant un point de livraison et notifie la vue
	 * 
	 * @param deliverySpotIndex Le point de livraison
	 */
	public void highlightDeliverySpot(Integer deliverySpotIndex) {
		this.selectedDeliverySpot = deliverySpotIndex;
		this.sendNotificationToView(false);
	}

	public DeliverySpot getSelectedDeliverySpot() {
		if (this.selectedDeliverySpot != null) {
			return this.deliverySpots.get(this.selectedDeliverySpot);
		}
		return null;
	}

	public Integer getSelectedSectionIndex() {
		return this.selectedSection;
	}

	public ModelInterface getModelInterface() {
		return modelInterface;
	}

	public static List<Color> getTourColors() {
		return tourColors;
	}

	public static Color getTourColor(int tourIndex) {
		return tourColors.get(tourIndex);
	}

	public ViewAction getCurrentAction() {
		return this.currentAction;
	}

	/**
	 * Retourne l'heure de début des livraisons
	 * 
	 * @return L'heure de début, sous forme d'un objet Calendar
	 */
	public Calendar getStartHour() {
		if (this.startHour != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.set(Calendar.HOUR, this.startHour.getHour());
			c.set(Calendar.MINUTE, this.startHour.getMinute());
			c.set(Calendar.SECOND, this.startHour.getSecond());
			return c;
		} else {
			return null;
		}
	}

	/**
	 * Retourne l'heure d'arrivée à une livraison au sein d'une tournée
	 * 
	 * @param tourIndex L'indice de la tournée
	 * @param pathIndex L'indice du chemin dans la tournée
	 * @return L'heure d'arrivée à la destination du chemin, sous forme d'un objet
	 *         Calendar
	 */
	public Calendar getHour(int tourIndex, int pathIndex) {
		Tour tour = this.tours.get(tourIndex);
		Calendar c = this.getStartHour();
		if (c == null) {
			return null;
		}

		int i = 0;
		for (Path path : tour.getPathList()) {
			double minutes = path.getDuration();
			c.add(Calendar.MINUTE, (int) Math.ceil(minutes));
			++i;
			if (i > pathIndex) {
				break;
			}
		}
		return c;
	}

	/**
	 * Sélectionne une tournée, un chemin dans cette tournée et une section. Mets
	 * ensuite à jour la vue.
	 * 
	 * @param tour    L'indice de la tournée à mettre en valeur. Null signifie
	 *                qu'aucune tournée n'est mise en avant.
	 * @param path    L'indice du chemin à mettre en avant dans la tournée. S'il est
	 *                null, aucun chemin n'est mis en avant.
	 * @param section L'indice du tronçon à mettre en valeur. Une valeur null
	 *                signifie qu'aucun tronçon n'est sélecitonné.
	 */
	public void setHighlight(Integer tour, Integer path, Integer section) {
		this.selectedTour = tour;
		this.selectedPath = path;
		this.selectedSection = section;
		this.sendNotificationToView(false);
	}

	/**
	 * Change l'action en cours de la vue. Si l'action passe à MAIN ou MODIFY, on ne
	 * sélectionne aucune tournée, ni chemin, ni tronçon, ni noeud.
	 * 
	 * @param newAction La nouvelle action effectuée par la vue
	 */
	public void setCurrentAction(ViewAction newAction) {
		this.currentAction = newAction;
		if (newAction == ViewAction.MAIN || newAction == ViewAction.MODIFY) {
			this.selectedPath = null;
			this.selectedSection = null;
			this.selectedTour = null;
			this.highlightedNode = null;
			this.sendNotificationToView(false);
		}
	}

	/**
	 * Génère un nouvel ensemble de couleurs pour les tournées.
	 */
	public void regenerateColors() {
		Util.newRandomColors(tourColors, NB_MAX_DELIVERIES);
		this.sendNotificationToView(false);
	}

	/**
	 * Permets de savoir si la dernière mise à jour de l'observeur concerne des
	 * objets du modèle (plan, livraisons, etc.) ou s'il s'agit d'un changement
	 * interne à la vue (changement de noeud sélectionné, etc.)
	 * 
	 * @return true si les objets du modèle ont changés, false sinon
	 */
	public boolean hasModelChanged() {
		return this.hasModelChanged;
	}

	/**
	 * Méthode d'aide pour notifier les observeurs (la vue)
	 * 
	 * @param hasModelChanged indique si le modèle à changé
	 */
	private void sendNotificationToView(boolean hasModelChanged) {
		this.hasModelChanged = hasModelChanged;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Permets d'afficher le nom de la rue actuellement survolée dans la zone d'aide
	 * de la fenêtre.
	 * 
	 * @param longitude La longitude du tronçon
	 * @param latitude  La latitude du tronçon
	 */
	public void nearestSection(double longitude, double latitude) {
		try {
			Section section = this.getMap().getNearestSection(longitude, latitude);
			String nomRue = section.getStreetName().isEmpty() ? "Rue sans nom" : section.getStreetName();
			this.window.setHelperText(nomRue);
		} catch (Exception e) {

		}
	}

	/**
	 * Mets en avant le chemin actuellement survolé par l'utilateur pour le
	 * sélectionner
	 * 
	 * @param section Le tronçon actuellement survolé par l'utilisateur
	 */
	public void highlightPath(Section section) {
		ArrayList<Selection> possiblePaths = new ArrayList<Selection>();
		int tourIndex = 0;
		for (Tour tour : this.tours) {
			int pathIndex = 0;
			for (Path path : tour.getPathList()) {
				if (path.sectionList().contains(section)) {
					possiblePaths.add(new Selection(tourIndex, pathIndex, null));
				}
				pathIndex++;
			}
			tourIndex++;
		}

		if (possiblePaths.isEmpty()) {
			this.selectedTour = null;
			this.selectedPath = null;
			this.selectedSection = null;
			this.possibleSelections.clear();
			this.sendNotificationToView(false);
			return;
		}

		Selection selection = possiblePaths.get(0);
		this.selectedTour = selection.tour;
		this.selectedPath = selection.path;
		this.selectedSection = null;
		this.possibleSelections.clear();
		this.sendNotificationToView(false);
	}

	/**
	 * Classe d'aide qui permet de représenter une sélection d'une tournée, d'un
	 * chemin et d'un tronçon
	 * 
	 * @author H4202
	 */
	class Selection {
		public Integer tour;
		public Integer path;
		public Integer section;

		public Selection(Integer tour) {
			this(tour, null, null);
		}

		public Selection(Integer tour, Integer path) {
			this(tour, path, null);
		}

		public Selection(Integer tour, Integer path, Integer section) {
			this.tour = tour;
			this.path = path;
			this.section = section;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof Selection))
				return false;
			Selection otherSelection = (Selection) other;
			return (this.tour.equals(otherSelection.tour) && this.path.equals(otherSelection.path));
		}
	}
}
