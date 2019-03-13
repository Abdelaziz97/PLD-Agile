package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.Controller;
import model.ModelInterface;
import model.Node;
import model.Section;
import view.listeners.ButtonListener;
import view.listeners.DeliveryMenSpinnerListener;
import view.listeners.KeyboardListener;
import view.listeners.TimeSpinnerListener;
import view.model.ViewAction;
import view.model.ViewModel;

/***
 * Représente la fenêtre du programme. A l'initialisation, l'objet mets en place
 * les différents éléments de la vue. L'objet sert également d'interface pour
 * activer / désactiver les boutons ainsi que pour mettre à jour le texte de la
 * zone d'informations.
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class Window extends JFrame implements Observer {

	public static final String BUTTON_LOAD_MAP = "Charger plan";
	public static final String BUTTON_LOAD_DELIVERIES = "Charger livraisons";
	public static final String BUTTON_CALCULATE = "Calculer tournées";
	public static final String BUTTON_STOP_CALCUL = "Arrêter";
	public static final String BUTTON_MODIFY = "Modifier";
	public static final String BUTTON_UNDO = "↩";
	public static final String BUTTON_REDO = "↪";
	public static final String BUTTON_HELP = "?";
	public static final String BUTTON_CANCEL = "Terminer";

	private static final String PANEL_COMMANDS_LOADING = "Chargement";
	private static final String PANEL_COMMANDS_COMPUTING = "Calcul";
	private static final String PANEL_COMMANDS_DELIVERIES = "Livraisons";

	public static final String HELPER_BLANK = "";
	public static final String HELPER_LOAD_MAP = "Veuillez charger un plan";
	public static final String HELPER_LOAD_DELIVERIES = "Veuillez charger des livraisons";
	public static final String HELPER_SELECT_NODE = "Veuillez sélectionner un point de la carte pour ajouter une livraison, ou un point de livraison pour le supprimer";
	public static final String HELPER_SELECT_PATH = "Veuillez cliquer sur une tournée pour insérer la nouvelle livraison";

	private JPanel mainPanel;
	private JSplitPane splitPane;
	private JPanel menuBar;
	private JPanel helperBar;
	private JLabel helperText;
	protected MapView mapView;
	protected TourView tourView;
	private Collection<JButton> buttons;
	private JSpinner deliveryMenSpinner;
	private JSpinner timeSpinner;
	private int computingDuration = 30;

	private Controller controller;
	private ModelInterface modelInterface;
	private ViewModel viewModel;
	private DeliverySpotView deliverySpotView;

	/***
	 * Initialise la fenêtre.
	 * 
	 * @param controller     Le contrôleur.
	 * @param modelInterface L'interface du modèle.
	 */
	public Window(Controller controller, ModelInterface modelInterface) {
		super("Optimod'Lyon");
		this.controller = controller;
		this.modelInterface = modelInterface;
		this.viewModel = new ViewModel(this, this.modelInterface, this.controller);

		this.viewModel.addObserver(this);

		this.initPanels();
		this.initButtons();
		this.initKeyboardListener();

		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(800, 600));
		this.setMinimumSize(new Dimension(800, 600));

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

		this.addComponentListener(new ResizeListener());

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Window.this.splitPane.setDividerLocation(0.7);
			}
		}, 200);
	}

	/***
	 * Initialise les quatre zones principales de la fenêtre (le menu en haut, la
	 * carte au milieu, la zone des tournés à droite et le texte d'aide en bas).
	 */
	private void initPanels() {
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());

		this.menuBar = new JPanel();
		this.menuBar.setLayout(new BoxLayout(this.menuBar, BoxLayout.X_AXIS));

		this.mapView = new MapView(this.viewModel);
		this.tourView = new TourView(this, this.viewModel);
		this.deliverySpotView = new DeliverySpotView(this, this.viewModel);

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.mapView, this.deliverySpotView);
		this.splitPane.setDividerLocation(0.7);

		this.helperBar = new JPanel(new BorderLayout());
		this.helperBar.setBorder(BorderFactory.createEtchedBorder());
		JPanel helperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.helperText = new JLabel("Ceci est un test");
		helperPanel.add(this.helperText);
		this.helperBar.add(helperPanel, BorderLayout.CENTER);

		this.mainPanel.add(this.menuBar, BorderLayout.NORTH);
		this.mainPanel.add(this.splitPane, BorderLayout.CENTER);
		this.mainPanel.add(this.helperBar, BorderLayout.SOUTH);
		this.setContentPane(this.mainPanel);
	}

	/**
	 * Affiche la vue des points de livraisons à la place de la vue des tournées.
	 */
	public void deliveryView() {
		this.splitPane.setRightComponent(this.deliverySpotView);
		this.splitPane.setDividerLocation(0.7);
	}

	/**
	 * Affiche la vue des tournées à la place de la vue des points de livraison.
	 */
	public void switchView() {
		this.viewModel.highlightDeliverySpot(null);
		this.splitPane.setRightComponent(this.tourView);
		this.splitPane.setDividerLocation(0.7);
	}

	/***
	 * Cette méthode crée les éléments du menus (les boutons organisés à l'intérieur
	 * de différentes zones, ainsi que le spinner pour sélectionner le nombre de
	 * livreurs). Elle leur assigne également les écouteurs correspondants.
	 */
	private void initButtons() {
		this.buttons = new ArrayList<JButton>();
		ButtonListener buttonListener = new ButtonListener(controller, this, this.viewModel);

		JPanel commandsLoading = new JPanel();
		commandsLoading.setLayout(new BoxLayout(commandsLoading, BoxLayout.X_AXIS));
		commandsLoading.setBorder(new TitledBorder(PANEL_COMMANDS_LOADING));
		createButton(BUTTON_LOAD_MAP, commandsLoading, buttonListener);
		createButton(BUTTON_LOAD_DELIVERIES, commandsLoading, buttonListener);
		this.menuBar.add(commandsLoading);

		JPanel commandsComputing = new JPanel();
		commandsComputing.setLayout(new BoxLayout(commandsComputing, BoxLayout.LINE_AXIS));
		commandsComputing.setBorder(
				new CompoundBorder(new TitledBorder(PANEL_COMMANDS_COMPUTING), new EmptyBorder(0, 0, 0, 10)));
		JLabel deliveryMenLabel = new JLabel("Livreurs : ");
		SpinnerNumberModel model = new SpinnerNumberModel(1, 1, ViewModel.getTourColors().size(), 1);
		this.deliveryMenSpinner = new JSpinner(model);
		this.deliveryMenSpinner.addChangeListener(new DeliveryMenSpinnerListener(this.controller, this));
		this.deliveryMenSpinner.setMaximumSize(new Dimension(60, 100));
		this.deliveryMenSpinner.setMinimumSize(new Dimension(60, 0));
		((JSpinner.NumberEditor) this.deliveryMenSpinner.getEditor()).getTextField().setEditable(false);
		commandsComputing.add(deliveryMenLabel);
		commandsComputing.add(this.deliveryMenSpinner);
		JLabel timeLabel = new JLabel("  Durée (s) : ");
		SpinnerNumberModel modelTime = new SpinnerNumberModel(this.computingDuration, 1, 60, 1);
		this.timeSpinner = new JSpinner(modelTime);
		this.timeSpinner.addChangeListener(new TimeSpinnerListener(this));
		this.timeSpinner.setMaximumSize(new Dimension(60, 100));
		this.timeSpinner.setMinimumSize(new Dimension(60, 0));
		commandsComputing.add(timeLabel);
		commandsComputing.add(this.timeSpinner);
		createButton(BUTTON_CALCULATE, commandsComputing, buttonListener);
		createButton(BUTTON_STOP_CALCUL, commandsComputing, buttonListener);
		this.menuBar.add(commandsComputing);

		JPanel commandsHelp = new JPanel(new BorderLayout());
		JButton button = new JButton(BUTTON_HELP);
		button.addActionListener(buttonListener);
		button.setActionCommand(BUTTON_HELP);
		this.buttons.add(button);
		commandsHelp.add(button, BorderLayout.CENTER);
		this.helperBar.add(commandsHelp, BorderLayout.EAST);

		JPanel commandsDeliveries = new JPanel();
		commandsDeliveries.setLayout(new BoxLayout(commandsDeliveries, BoxLayout.X_AXIS));
		commandsDeliveries.setBorder(new TitledBorder(PANEL_COMMANDS_DELIVERIES));
		createButton(BUTTON_UNDO, commandsDeliveries, buttonListener);
		createButton(BUTTON_REDO, commandsDeliveries, buttonListener);
		createButton(BUTTON_MODIFY, commandsDeliveries, buttonListener);
		createButton(BUTTON_CANCEL, commandsDeliveries, buttonListener);
		this.menuBar.add(commandsDeliveries);

		this.allowOnlyLoadMapButton();
	}

	/***
	 * Méthode d'aide pour créer un bouton.
	 * 
	 * @param buttonText     Le texte affiché dans le bouton.
	 * @param commandsPanel  La zone dans laquelle ajouter le bouton.
	 * @param buttonListener L'écouteurs de boutons.
	 */
	private void createButton(String buttonText, JPanel commandsPanel, ButtonListener buttonListener) {
		JButton button = new JButton(buttonText);
		button.addActionListener(buttonListener);
		button.setActionCommand(buttonText);
		this.buttons.add(button);
		commandsPanel.add(button);
	}

	/***
	 * Cette méthode permets de créer l'écouteur de clavier, afin qu'on puisse
	 * intercepter les appuis sur des touches quelque soit le composant qui a le
	 * focus.
	 */
	private void initKeyboardListener() {
		KeyboardListener keyboardListener = new KeyboardListener(this.mapView, this.tourView, this.viewModel,
				this.controller, this);
		this.addKeyListener(keyboardListener);
		this.mainPanel.addKeyListener(keyboardListener);
		this.mapView.addKeyListener(keyboardListener);
		this.tourView.addKeyListener(keyboardListener);
		for (JButton button : this.buttons) {
			button.addKeyListener(keyboardListener);
		}
	}

	/**
	 * 
	 * @return La durée, en secondes, maximale au bout de laquelle le calcul
	 *         s'arrête.
	 */
	public int getComputingDuration() {
		return computingDuration;
	}

	/**
	 * 
	 * @param computingDuration La durée, en secondes, maximale au bout de laquelle
	 *                          le calcul s'arrête.
	 */
	public void setComputingDuration(int computingDuration) {
		this.computingDuration = computingDuration;
	}

	/**
	 * Désactive tous les boutons, à l'exception du boutons de chargement du plan et
	 * du bouton d'aide.
	 */
	public void allowOnlyLoadMapButton() {
		System.out.println("Allow load map");
		this.setHelperText(Window.HELPER_LOAD_MAP);
		for (JButton button : this.buttons) {
			if (button.getText().equals(BUTTON_LOAD_MAP) || button.getText().equals(BUTTON_HELP)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
		this.deliveryMenSpinner.setEnabled(false);
		this.timeSpinner.setEnabled(false);
	}

	/**
	 * Désactive tous les boutons, à l'exception du boutons de chargement du plan,
	 * de chargement des livraisons et du bouton d'aide.
	 */
	public void allowLoadMapAndDeliveryButtons() {
		System.out.println("Allow load map and delivery");
		this.setHelperText(Window.HELPER_LOAD_DELIVERIES);
		for (JButton button : this.buttons) {
			if (button.getText().equals(BUTTON_LOAD_MAP) || button.getText().equals(BUTTON_LOAD_DELIVERIES)
					|| button.getText().equals(BUTTON_HELP)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
		this.deliveryMenSpinner.setEnabled(false);
		this.timeSpinner.setEnabled(false);
	}

	/**
	 * Active tous les boutons, exceptés les boutons "Terminer", "Arrêter le calcul"
	 * et le bouton d'aide. Selon les paramètres notUndo et notRedo, active
	 * éventuellement les boutons Undo et Redo.
	 * 
	 * @param notUndo
	 * @param notRedo
	 */
	public void allowAllButtonsExceptCancelAndStop(boolean notUndo, boolean notRedo) {
		System.out.println("Allow all");
		this.setHelperText(Window.HELPER_BLANK);
		for (JButton button : this.buttons) {
			if (button.getText().equals(BUTTON_UNDO))
				button.setEnabled(!notUndo);
			else if (button.getText().equals(BUTTON_REDO))
				button.setEnabled(!notRedo);
			else if (!button.getText().equals(BUTTON_CANCEL) && !button.getText().equals(BUTTON_STOP_CALCUL)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
		this.deliveryMenSpinner.setEnabled(true);
		this.timeSpinner.setEnabled(true);
	}

	/**
	 * Active uniquement les boutons "Terminer" et le bouton d'aide.
	 * 
	 * @param notUndo
	 * @param notRedo
	 */
	public void allowOnlyCancelButton(boolean notUndo, boolean notRedo) {
		System.out.println("Allow all");
		this.setHelperText(Window.HELPER_SELECT_NODE);
		for (JButton button : this.buttons) {
			if (button.getText().equals(BUTTON_UNDO))
				button.setEnabled(!notUndo);
			else if (button.getText().equals(BUTTON_REDO))
				button.setEnabled(!notRedo);
			else if (button.getText().equals(BUTTON_CANCEL) || button.getText().equals(BUTTON_HELP)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
		this.deliveryMenSpinner.setEnabled(false);
		this.timeSpinner.setEnabled(false);
	}

	/**
	 * Active uniquement le bouton "Arrêter le calcul"
	 */
	public void allowOnlyStopButton() {
		System.out.println("Allow all");
		this.setHelperText(Window.HELPER_BLANK);
		for (JButton button : this.buttons) {
			if (button.getText().equals(BUTTON_STOP_CALCUL) || button.getText().equals(BUTTON_HELP)) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
		}
		this.deliveryMenSpinner.setEnabled(false);
		this.timeSpinner.setEnabled(false);
	}

	/**
	 * Affiche le texte dans la vue d'aide.
	 * 
	 * @param text Le texte à afficher.
	 */
	public void setHelperText(String text) {
		this.helperText.setText(text);
	}

	/**
	 * Lorsqu'on bouge la souris en mode modification, affiche le nom des rues
	 * adjacentes à l'intersection survolée.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (this.viewModel.getCurrentAction() == ViewAction.MODIFY) {
			Node node = this.viewModel.getHighlightedNode();
			HashSet<String> streets = new HashSet<String>();

			for (Section section : node.getSectionsList()) {
				String name = section.getStreetName();
				if (name.equals("")) {
					name = "Rue sans nom";
				}
				streets.add(name);
			}

			StringBuffer streetNames = new StringBuffer();
			int last = streets.size() - 1;
			int sectionIndex = 0;
			for (String streetName : streets) {
				streetNames.append(streetName);
				if (sectionIndex != last) {
					streetNames.append("; ");
				}
				sectionIndex++;
			}
			this.setHelperText(streetNames.toString());
		}
	}

	/**
	 * Affiche le message de l'exception dans une fenêtre d'erreur. Affiche
	 * également la trace d'erreur.
	 * 
	 * @param e L'exception lancée.
	 */
	public void printMessage(Exception e) {
		e.printStackTrace();
		String msg = e.getMessage();
		JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Ecouteur qui s'active lorsque la fenêtre change de taille. Permet de corriger
	 * la taille du paneau latéral.
	 */
	class ResizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			Window window = (Window) e.getComponent();
			window.splitPane.setDividerLocation(0.7);
		}
	}
}
