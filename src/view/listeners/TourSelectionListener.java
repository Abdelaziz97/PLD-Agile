package view.listeners;

import java.awt.event.MouseEvent;

import view.ExpandablePanel;
import view.MapView;
import view.TourView;
import view.model.ViewModel;

/**
 * Ecouteur de souris pour gérer le dépliage / pliage des panneaux extensibles
 * de la vue des tournées. Il permet aussi de sélectionner les tournées, les
 * chemins et les tronçons à mettre en exergue sur la vue graphique.
 * 
 * @author H4202
 */
public class TourSelectionListener implements java.awt.event.MouseListener {

	private TourView tourView;
	private ViewModel viewModel;

	public TourSelectionListener(TourView tourView, ViewModel viewModel) {
		this.tourView = tourView;
		this.viewModel = viewModel;
	}

	/**
	 * Gère le clic sur les panneaux extensibles représentant les tournées, les
	 * chemins et les tronçons. Au simple clic, la méthode mets en surbrillance dans
	 * la vue graphique la tournée ou le chemin concerné. Au double clic, permets de
	 * déplier ou de replier un panneau extensible.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		ExpandablePanel panel = (ExpandablePanel) e.getSource();
		System.out.println("Clic sur " + panel);
		Integer selectedTour = panel.getTourIndex();
		System.out.println("Index : " + selectedTour);
		if (e.getClickCount() > 1) {
			System.out.println("Expand");
			panel.toggleExpanded();
		}
		this.tourView.selectOnly(selectedTour, panel.getPathIndex(), panel.getSectionIndex());
		if (panel.getPathIndex() != null) {
			this.viewModel.selectPath(selectedTour, panel.getPathIndex());
		}
		this.viewModel.setHighlight(selectedTour, panel.getPathIndex(), panel.getSectionIndex());
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
