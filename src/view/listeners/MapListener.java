package view.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import view.MapView;
import view.model.ViewModel;

/**
 * Ecouteur de souris pour naviguer sur le plan
 * 
 * @author H4202
 */
public class MapListener implements MouseMotionListener, MouseWheelListener, MouseListener {

	private MapView mapView;
	private ViewModel viewModel;
	private int x;
	private int y;

	public MapListener(MapView mapView, ViewModel viewModel) {
		this.mapView = mapView;
		this.viewModel = viewModel;
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Permet d'effectuer une translation dans la vue graphique, dans le cas où la
	 * souris se déplace sur l'écran avec le bouton gauche enfoncé.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int deltaX = e.getX() - this.x;
		int deltaY = e.getY() - this.y;
		this.x = e.getX();
		this.y = e.getY();
		this.mapView.translate(deltaX, deltaY);
	}

	/**
	 * Permet de mettre en avant les intersections et les chemins au passage de la
	 * souris lorsqu'on est en mode de modification pour afficher ce qu'on est en
	 * train de sélectionner.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		this.x = e.getX();
		this.y = e.getY();
		this.mapView.highlightNearestNode(this.x, this.y);
		this.mapView.highlightNearestPath(this.x, this.y);
	}

	/**
	 * Permets de zoomer lorsqu'on tourne la molette de la souris.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.mapView.zoom(e.getWheelRotation());
	}

	/**
	 * Permets à la vue graphique d'avoir le focus dans la fenêtre, et permets de
	 * sélectionner un noeud ou un chemin lorsqu'on est en mode modification.
	 * Affiche également le nom du tronçon dans la vue d'aide.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		this.mapView.requestFocusInWindow();

		this.x = e.getX();
		this.y = e.getY();
		this.mapView.showNearestSection(this.x, this.y);
		if (this.viewModel.getHighlightedNode() != null) {
			this.viewModel.selectNode(this.viewModel.getHighlightedNode());
		}
		if (this.viewModel.getSelectedPathIndex() != null) {
			this.mapView.highlightNearestPath(this.x, this.y);
			this.viewModel.selectPath(this.viewModel.getSelectedTourIndex(), this.viewModel.getSelectedPathIndex());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
