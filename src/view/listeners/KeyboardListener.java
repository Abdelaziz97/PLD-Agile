package view.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import controller.Controller;
import view.MapView;
import view.TourView;
import view.Window;
import view.model.ViewAction;
import view.model.ViewModel;

/**
 * Ecouteur de clavier pour gérer raccourcis claviers.
 * 
 * @author H4202
 */
public class KeyboardListener implements KeyListener {

	private MapView mapView;
	private TourView tourView;
	private ViewModel viewModel;
	private Controller controller;
	private Window window;

	public KeyboardListener(MapView mapView, TourView tourView, ViewModel viewModel, Controller controller, Window window) {
		this.mapView = mapView;
		this.tourView = tourView;
		this.viewModel = viewModel;
		this.controller = controller;
		this.window = window;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * Effectue l'action correspondant à l'appui d'une touche du clavier.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Appui sur " + e.getKeyChar());
		char keyChar = Character.toLowerCase(e.getKeyChar());
		if (keyChar == '+') {
			this.mapView.zoomIn();
		} else if (keyChar == '-') {
			this.mapView.zoomOut();
		} else if (keyChar == 'z' || e.getKeyCode() == KeyEvent.VK_UP) {
			this.viewModel.moveUp();
		} else if (keyChar == 's' || e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.viewModel.moveDown();
		} else if (keyChar == 'y') {
			this.viewModel.regenerateColors();
		} else if (keyChar == 'f') {
			this.mapView.switchArrowsMode();
			this.mapView.repaint();
		} else if (keyChar == 'd') {
			this.mapView.setDebugMode(! this.mapView.isDebugMode());
			this.mapView.repaint();
		} else if (e.isControlDown() && e.isShiftDown() == false && e.getKeyChar() != 'z' && e.getKeyCode() == 90) {
	        this.controller.undo(this.window);
		} else if (e.isControlDown() && e.isShiftDown() && e.getKeyChar() != 'z' && e.getKeyCode() == 90) {
			this.controller.redo(this.window);
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.controller.cancel(this.window);
			this.viewModel.setCurrentAction(ViewAction.MAIN);
		} else if (e.getKeyChar() == 'm') {
			this.controller.modifyDeliveries(this.window);
			this.viewModel.setCurrentAction(ViewAction.MODIFY);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
