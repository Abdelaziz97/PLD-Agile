package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.DeliverySpot;
import model.Node;
import model.Path;
import model.Section;
import model.Tour;
import util.Util;
import view.listeners.TourSelectionListener;
import view.model.ViewModel;

/**
 * Représente la vue des tournées. Contient des panneaux extensibles
 * (ExpandablePanel) qui représentent chaque tournée. Chaque panneau contient
 * d'autres panneaux représentant les chemins entre les points de livraisons.
 * Ces panneaux contiennent enfin les tronçons.
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class TourView extends JPanel implements Observer {

	private TourSelectionListener tourListener;
	private JScrollPane scrollPane;
	private ExpandablePanel tourPanel;
	private Window window;
	private ViewModel viewModel;

	public TourView(Window window, ViewModel viewModel) {
		this.window = window;
		this.viewModel = viewModel;

		this.viewModel.addObserver(this);

		this.tourListener = new TourSelectionListener(this, this.viewModel);

		this.tourPanel = new ExpandablePanel("Liste des tournées");
		this.tourPanel.addMouseListener(this.tourListener);
		this.tourPanel.toggleExpanded();
		this.tourPanel.setBackground(ExpandablePanel.BACKGROUND_COLOR);

		this.scrollPane = new JScrollPane(this.tourPanel);
		this.scrollPane.setPreferredSize(new Dimension(450, 110));
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.setLayout(new BorderLayout());
		this.add(this.scrollPane, BorderLayout.CENTER);
	}

	/**
	 * A la mise à jour du modèle, on vide la liste des panneaux actuellements
	 * affichés, puis on parcourt l'ensemble des tournées, des chemins dans ces
	 * tournées et enfin des tronçons dans ces chemins pour les ajouter dans la
	 * hiérarchie des panneaux extensibles. On ajoute également les écouteurs
	 * (TourSelectionListener) dans chaque panneaux qui serviront à synchroniser la
	 * vue des tournées avec la vue graphique.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (this.viewModel.hasModelChanged()) {
			this.tourPanel.clear();

			Calendar startHour = this.viewModel.getStartHour();
			DateFormat df = new SimpleDateFormat("HH:mm");

			List<Tour> tourList = this.viewModel.getTours();
			int i = 1;
			for (Tour tour : tourList) {
				Integer tourIndex = i - 1;
				int charge = this.viewModel.isCorrectlyCharged(tour);
				String chargeText = (charge == ViewModel.TOUR_UNDERCHARGED) ? "  ⚠ SOUS-CHARGE ⚠"
						: (charge == ViewModel.TOUR_OVERCHARGED) ? "  ⚠ SURCHARGE ⚠" : "";

				Calendar lastHour = this.viewModel.getHour(tourIndex,
						this.viewModel.getTours().get(tourIndex).getPathList().size() - 1);
				Calendar diff = Util.dateDiff(startHour, lastHour);
				String duration = Integer.toString((int) ((double) diff.getTimeInMillis() / (double) 60000));

				ExpandablePanel tourNode = new ExpandablePanel("Tournée " + i + " (Départ : "
						+ df.format(startHour.getTime()) + ", Durée totale : " + duration + " minutes)" + chargeText,
						tourIndex);
				tourNode.addMouseListener(this.tourListener);
				this.tourPanel.addPanel(tourNode);

				int j = 1;
				for (Path path : tour.getPathList()) {
					int pathIndex = j - 1;
					Calendar c = this.viewModel.getHour(tourIndex, pathIndex);
					String text = "Livraison " + j + " (" + df.format(c.getTime());
					if (j == tour.getPathList().size()) {
						text = "Retour à l'entrepôt (" + df.format(c.getTime()) + ")";
					} else {
						Node currentDestination = path.sectionList().getLast().getDestination();
						for (DeliverySpot spot : this.viewModel.getDeliverySpots()) {
							if (spot.getAddress().equals(currentDestination)) {
								text += ("; " + spot.getUnloadingTime() / 60 + " minutes)");
							}
						}
					}

					ExpandablePanel pathNode = new ExpandablePanel(text, tourIndex, pathIndex);
					pathNode.addMouseListener(this.tourListener);
					tourNode.addPanel(pathNode);

					String lastSection = null;
					int sectionIndex = 0;
					for (Section section : path.sectionList()) {
						if (!section.getStreetName().equals(lastSection)) {
							String name = section.getStreetName();
							if (name.equals("")) {
								name = "⚠ Rue sans nom ⚠";
							}
							ExpandablePanel sectionNode = new ExpandablePanel(name, tourIndex, pathIndex, sectionIndex);
							sectionNode.addMouseListener(this.tourListener);
							pathNode.addPanel(sectionNode);

							lastSection = section.getStreetName();
						}
						sectionIndex++;
					}
					j++;
				}
				i++;
			}
			this.tourPanel.revalidate();
			this.tourPanel.repaint();
		}
	}

	/**
	 * Permets de mettre en avant les panneaux extensibles correspondants aux
	 * indices de tournée, de chemin et de tronçon passés en paramètre.
	 * 
	 * @param tourIndex    L'indice de la tournée. S'il est null, aucun panneau
	 *                     n'est mis en avant.
	 * @param pathIndex    L'indice du chemin dans la tournée. Est utilisé au sein
	 *                     d'une même tournée pour sélectionner le bon chemin. S'il
	 *                     est null, tous les chemins de la tournée (non nulle) sont
	 *                     sélectionnés.
	 * @param sectionIndex L'indice du ronçon dans un chemin. Est utilisé au sein
	 *                     d'un chemin pour sélectionner le bon tronçon. S'il est
	 *                     null, tous les tronçons du chemin (non null) sont
	 *                     sélectionnés.
	 */
	public void selectOnly(Integer tourIndex, Integer pathIndex, Integer sectionIndex) {
		System.out.println("Select only : " + tourIndex + " - " + pathIndex + " - " + sectionIndex);
		if (tourIndex != null) {
			this.tourPanel.select(tourIndex, pathIndex, sectionIndex);
		}
		this.repaint();
	}
}
