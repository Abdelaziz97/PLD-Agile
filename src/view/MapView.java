package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import model.DeliverySpot;
import model.Map;
import model.Node;
import model.Path;
import model.Section;
import model.Tour;
import view.listeners.MapListener;
import view.model.ViewAction;
import view.model.ViewModel;

/**
 * Représente la vue graphique, qui affiche le plan, les points de livraison,
 * l'entrepôt et les tournées. Permets aussi de déselectionner des
 * intersections, des points de livraison et des chemins de tournée pour
 * effectuer les différentes modifications possibles (ajout, suppression de
 * points de livraison
 * 
 * @author H4202).
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class MapView extends JPanel implements Observer {

	private static final Color COLOR_BACKGROUND = new Color(0.7f, 0.7f, 0.7f);
	private static final Color COLOR_SECTION = new Color(0.95f, 0.95f, 0.95f);
	private static final Color COLOR_DELIVERY_SPOT = new Color(0.1f, 0.4f, 0.9f);
	private static final Color COLOR_DELIVERY_SPOT_BOUNDS = new Color(1f, 1f, 1f);
	private static final Color COLOR_STORE = new Color(0.9f, 0.1f, 0.1f);
	private static final Color COLOR_STORE_BOUNDS = new Color(1f, 1f, 1f);

	private static final double STREET_WIDTH = 0.00015;
	private static final double HIGHLIGHT_TOUR_WIDTH = 0.00023;
	private static final double DELIVERY_SPOT_RADIUS = 0.0002; // lat
	private static final int DELIVERY_SPOT_MIN_RADIUS = 3; // pix
	private static final double DELIVERY_SPOT_BOUNDS_RADIUS = 0.0003;
	private static final int DELIVERY_SPOT_BOUNDS_MIN_RADIUS = 5;
	private static final double STORE_RADIUS = 0.00029;
	private static final double STORE_BOUNDS_RADIUS = 0.00034;
	private static final int STORE_BOUNDS_MIN_RADIUS = 6;
	private static final int STORE_MIN_RADIUS = 4; // px
	private static final int ARROWS_NONE = 0;
	private static final int ARROWS_MIDDLE = 1;
	private static final int ARROWS_ALL = 2;

	private final CoordsLongLat INSA_LOGO_CORNER1 = new CoordsLongLat(4.87153339170311, 45.78251878217893);
	private final CoordsLongLat INSA_LOGO_CORNER2 = new CoordsLongLat(4.87265723728035, 45.78228309501116);
	private BufferedImage insaLogo;
	private int arrowsMode;
	private boolean debugMode;

	private ViewModel viewModel;
	private Frame frame;

	/**
	 * Crée la vue graphique et ajoute un MapListener. La vue s'ajoute comme
	 * observeur du ViewModel.
	 * 
	 * @param viewModel Le ViewModel que va observer la vue.
	 */
	public MapView(ViewModel viewModel) {
		this.viewModel = viewModel;

		this.viewModel.addObserver(this);

		this.setFocusable(true);

		this.arrowsMode = ARROWS_MIDDLE;
		this.debugMode = false;

		this.frame = new Frame();
		MapListener mapListener = new MapListener(this, this.viewModel);
		this.addMouseMotionListener(mapListener);
		this.addMouseWheelListener(mapListener);
		this.addMouseListener(mapListener);

		try {
			this.insaLogo = ImageIO.read(new File("img/logo.png"));
			System.out.println("Image chargée");
		} catch (IOException e) {
			System.out.println("Image non chargée");
		}
	}

	/**
	 * A la mise à jour du ViewModel, repeint la vue. Si c'est la première fois
	 * qu'on charge un plan (avec un repère non initialisé donc), on calcule la
	 * position du repère.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (this.frame.isInitialized() == false) {
			this.calculateFrameLocation();
		}
		this.repaint();
	}

	/**
	 * Effectue le rendu de la vue. Dans l'ordre, les opérations sont : - Affichage
	 * du fond, uniforme gris - Affichage de chaque tronçon, en blanc - Si une
	 * tournée est mise en avant (highlight), affichage de celle-ci en gras -
	 * Affichage des points de livraisons en blanc - Si un point de livraison est
	 * mis en avant, affichage de celui-ci en plus épais - Affichage de l'entrepôt
	 * en rouge - Affichage d'un cercle rouge autour de l'intersection sélectionnée
	 * - Affichage du logo de l'INSA
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = this.getWidth();
		int height = this.getHeight();
		CoordsPixels frameCenter = new CoordsPixels(width / 2, height / 2);

		g.setColor(COLOR_BACKGROUND);
		g.fillRect(0, 0, width, height);

		g.setColor(COLOR_SECTION);

		Map map = this.viewModel.getMap();
		if (map == null) {
			return;
		}
		HashMap<Long, Node> nodesList = map.getNodeList();

		int streetWidth = (int) (STREET_WIDTH / this.frame.getScale());
		int highlightTourWidth = (int) (HIGHLIGHT_TOUR_WIDTH / this.frame.getScale());
		g2.setStroke(new BasicStroke(streetWidth));

		for (Entry<Long, Node> entry : nodesList.entrySet()) {
			HashSet<Section> sections = entry.getValue().getSectionsList();
			for (Section section : sections) {
				CoordsPixels source = this.frame.longLatToPixels(new CoordsLongLat(section.getSource()), frameCenter);
				CoordsPixels destination = this.frame.longLatToPixels(new CoordsLongLat(section.getDestination()),
						frameCenter);

				g2.drawLine(source.x, source.y, destination.x, destination.y);
			}
		}

		List<Tour> tours = this.viewModel.getTours();
		Integer tourHighlight = this.viewModel.getSelectedTourIndex();
		Integer pathHighlight = this.viewModel.getSelectedPathIndex();
		if (tours.isEmpty() == false) {
			int i = 0;
			for (Tour tour : tours) {
				if (tourHighlight == null || tourHighlight != i) {
					g2.setColor(ViewModel.getTourColor(i));
					for (Path path : tour.getPathList()) {
						int sectionIndex = 0;
						for (Section section : path.sectionList()) {
							CoordsPixels source = this.frame.longLatToPixels(new CoordsLongLat(section.getSource()),
									frameCenter);
							CoordsPixels destination = this.frame
									.longLatToPixels(new CoordsLongLat(section.getDestination()), frameCenter);

							g2.setStroke(new BasicStroke(streetWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
							g2.drawLine(source.x, source.y, destination.x, destination.y);
							if (this.arrowsMode == ARROWS_ALL || (this.arrowsMode == ARROWS_MIDDLE
									&& sectionIndex == (path.sectionList().size() / 2))) {
								this.paintTriangle(g2, frameCenter, section, 0.0003);
							}
							sectionIndex++;
						}
					}
				}
				++i;
			}

			g2.setStroke(new BasicStroke(highlightTourWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			if (tourHighlight != null) {
				Tour tour = tours.get(tourHighlight);
				g2.setColor(ViewModel.getTourColor(tourHighlight));

				int pathIndex = 0;
				for (Path path : tour.getPathList()) {
					int sectionIndex = 0;
					for (Section section : path.sectionList()) {
						if (pathHighlight != null && pathHighlight == pathIndex) {
							g2.setStroke(new BasicStroke(2 * highlightTourWidth, BasicStroke.CAP_ROUND,
									BasicStroke.JOIN_ROUND));
						} else {
							g2.setStroke(
									new BasicStroke(highlightTourWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						}
						CoordsPixels source = this.frame.longLatToPixels(new CoordsLongLat(section.getSource()),
								frameCenter);
						CoordsPixels destination = this.frame
								.longLatToPixels(new CoordsLongLat(section.getDestination()), frameCenter);

						g2.drawLine(source.x, source.y, destination.x, destination.y);
						if (this.arrowsMode == ARROWS_ALL || (this.arrowsMode == ARROWS_MIDDLE
								&& sectionIndex == (path.sectionList().size() / 2))) {
							this.paintTriangle(g2, frameCenter, section, 0.0004);
						}
						sectionIndex++;
					}
					++pathIndex;
				}
			}
		}

		int dlRadius = (int) (DELIVERY_SPOT_RADIUS / this.frame.getScale());
		if (dlRadius < DELIVERY_SPOT_MIN_RADIUS) {
			dlRadius = DELIVERY_SPOT_MIN_RADIUS;
		}
		int dlBoundsRadius = (int) (DELIVERY_SPOT_BOUNDS_RADIUS / this.frame.getScale());
		if (dlBoundsRadius < DELIVERY_SPOT_BOUNDS_MIN_RADIUS) {
			dlBoundsRadius = DELIVERY_SPOT_BOUNDS_MIN_RADIUS;
		}
		for (DeliverySpot deliverySpot : this.viewModel.getDeliverySpots()) {
			if (deliverySpot.getAddress() != null) {
				CoordsPixels coords = this.frame.longLatToPixels(new CoordsLongLat(deliverySpot.getAddress()),
						frameCenter);

				g2.setColor(COLOR_DELIVERY_SPOT_BOUNDS);
				g2.fillOval(coords.x - dlBoundsRadius, coords.y - dlBoundsRadius, 2 * dlBoundsRadius,
						2 * dlBoundsRadius);
				g2.setColor(COLOR_DELIVERY_SPOT);
				g2.fillOval(coords.x - dlRadius, coords.y - dlRadius, 2 * dlRadius, 2 * dlRadius);
			}
		}

		if (this.viewModel.getStore() != null) {
			CoordsPixels store = this.frame.longLatToPixels(new CoordsLongLat(this.viewModel.getStore()), frameCenter);
			int radius = (int) (STORE_RADIUS / this.frame.getScale());
			if (radius < STORE_MIN_RADIUS) {
				radius = STORE_MIN_RADIUS;
			}
			int boundsRadius = (int) (STORE_BOUNDS_RADIUS / this.frame.getScale());
			if (boundsRadius < STORE_BOUNDS_MIN_RADIUS) {
				boundsRadius = STORE_BOUNDS_MIN_RADIUS;
			}
			g2.setColor(COLOR_STORE_BOUNDS);
			g2.fillOval(store.x - boundsRadius, store.y - boundsRadius, 2 * boundsRadius, 2 * boundsRadius);
			g2.setColor(COLOR_STORE);
			g2.fillOval(store.x - radius, store.y - radius, 2 * radius, 2 * radius);
		}

		Node highlightedNode = this.viewModel.getHighlightedNode();
		if (highlightedNode != null) {
			if (this.viewModel.getCurrentAction() == ViewAction.MODIFY) {
				g2.setColor(Color.RED);
			} else if (this.viewModel.getCurrentAction() == ViewAction.MODIFY_SELECT_PATH) {
				g2.setColor(Color.GREEN);
			}
			CoordsPixels coords = this.frame.longLatToPixels(new CoordsLongLat(highlightedNode), frameCenter);
			int radius = (int) (STORE_RADIUS / this.frame.getScale());
			if (radius < STORE_MIN_RADIUS) {
				radius = STORE_MIN_RADIUS;
			}
			g2.drawOval(coords.x - radius, coords.y - radius, 2 * radius, 2 * radius);
		}

		DeliverySpot selectedDeliverySpot = this.viewModel.getSelectedDeliverySpot();
		if (selectedDeliverySpot != null) {
			CoordsPixels coords = this.frame.longLatToPixels(new CoordsLongLat(selectedDeliverySpot.getAddress()),
					frameCenter);
			g2.setColor(COLOR_DELIVERY_SPOT_BOUNDS);
			g2.fillOval(coords.x - 2 * dlBoundsRadius, coords.y - 2 * dlBoundsRadius, 4 * dlBoundsRadius,
					4 * dlBoundsRadius);
			g2.setColor(COLOR_DELIVERY_SPOT);
			g2.fillOval(coords.x - 2 * dlRadius, coords.y - 2 * dlRadius, 4 * dlRadius, 4 * dlRadius);
		}

		this.paintInsaLogo(g2, frameCenter);

		if (this.debugMode) {
			g.setColor(Color.MAGENTA);
			for (Node node : this.viewModel.getMap().getUnreachableNode()) {
				CoordsPixels coords = this.frame.longLatToPixels(new CoordsLongLat(node), frameCenter);
				g2.fillOval(coords.x - 3, coords.y - 3, 2 * 3, 2 * 3);
			}
		}
	}

	/**
	 * Méthode d'aide pour afficher un triangle au milieu d'un tronçon.
	 * 
	 * @param g2          L'objet Graphics2D qui permet le dessin
	 * @param frameCenter Le centre de la vue graphique
	 * @param section     Le tronçon au milieu duquel le triangle sera dessiné
	 * @param d           La distance du centre de gravité du triangle à chacun de
	 *                    ses sommets (triangle équilatéral), soit 2 tiers de la
	 *                    hauteur
	 */
	private void paintTriangle(Graphics2D g2, CoordsPixels frameCenter, Section section, double d) {
		double centerLat = (section.getSource().getLatitude() + section.getDestination().getLatitude()) / 2;
		double centerLong = (section.getSource().getLongitude() + section.getDestination().getLongitude()) / 2;
		CoordsLongLat c = new CoordsLongLat(centerLong, centerLat);

		double destLat = section.getDestination().getLatitude();
		double destLong = section.getDestination().getLongitude();
		CoordsLongLat a = new CoordsLongLat(destLong, destLat);
		double dLat = d;
		double dLong = dLat * this.frame.getScaleLong() / this.frame.getScaleLat();
		double ca = Math.sqrt((a.latitude - c.latitude) * (a.latitude - c.latitude)
				+ (a.longitude - c.longitude) * (a.longitude - c.longitude));
		double sqrt3 = Math.sqrt(3d);

		double s1Long = c.longitude + (dLong / ca) * (a.longitude - c.longitude);
		double s1Lat = c.latitude + (dLat / ca) * (a.latitude - c.latitude);
		double s2Long = c.longitude - (dLong / (2 * ca)) * (a.longitude - c.longitude)
				+ (sqrt3 * dLong / (2 * ca)) * (c.latitude - a.latitude);
		double s2Lat = c.latitude - (dLat / (2 * ca)) * (a.latitude - c.latitude)
				+ (sqrt3 * dLat / (2 * ca)) * (a.longitude - c.longitude);
		double s3Long = c.longitude - (dLong / (2 * ca)) * (a.longitude - c.longitude)
				- (sqrt3 * dLong / (2 * ca)) * (c.latitude - a.latitude);
		double s3Lat = c.latitude - (dLat / (2 * ca)) * (a.latitude - c.latitude)
				- (sqrt3 * dLat / (2 * ca)) * (a.longitude - c.longitude);

		CoordsPixels s1 = this.frame.longLatToPixels(new CoordsLongLat(s1Long, s1Lat), frameCenter);
		System.out.println("/Point S1 : " + s1Long + "; " + s1Lat);
		System.out.println("/Point S1 : " + s1.x + "; " + s1.y);
		CoordsPixels s2 = this.frame.longLatToPixels(new CoordsLongLat(s2Long, s2Lat), frameCenter);
		System.out.println("/Point S1 : " + s2Long + "; " + s2Lat);
		System.out.println("/Point S2 : " + s2.x + "; " + s2.y);
		CoordsPixels s3 = this.frame.longLatToPixels(new CoordsLongLat(s3Long, s3Lat), frameCenter);
		System.out.println("/Point S1 : " + s3Long + "; " + s3Lat);
		System.out.println("/Point S3 : " + s3.x + "; " + s3.y);

		g2.setStroke(new BasicStroke(1));
		g2.fillPolygon(new int[] { s1.x, s2.x, s3.x }, new int[] { s1.y, s2.y, s3.y }, 3);
	}

	/**
	 * Méthode d'aide pour afficher le logo de l'INSA
	 * 
	 * @param g2          L'objet Graphics2D pour le dessin
	 * @param frameCenter Le centre de la vue graphique
	 */
	private void paintInsaLogo(Graphics2D g2, CoordsPixels frameCenter) {
		if (this.insaLogo != null) {
			CoordsPixels insaCorner1 = this.frame.longLatToPixels(this.INSA_LOGO_CORNER1, frameCenter);
			CoordsPixels insaCorner2 = this.frame.longLatToPixels(this.INSA_LOGO_CORNER2, frameCenter);
			g2.drawImage(this.insaLogo, insaCorner1.x, insaCorner1.y, insaCorner2.x, insaCorner2.y, 0, 0,
					this.insaLogo.getWidth(), this.insaLogo.getHeight(), this);
		}
	}

	/**
	 * Calcule la longitude et la latitude correspondants aux coordonnées en pixels,
	 * puis demande au ViewModel d'afficher le nom de la rue sélectionnée.
	 * 
	 * @param x La coordonnée X du point, en pixels
	 * @param y La coordonnée Y du point, en pixels
	 */
	public void showNearestSection(int x, int y) {
		int width = this.getWidth();
		int height = this.getHeight();
		CoordsPixels frameCenter = new CoordsPixels(width / 2, height / 2);
		CoordsLongLat coords = this.frame.pixelsToLongLat(new CoordsPixels(x, y), frameCenter);
		this.viewModel.nearestSection(coords.longitude, coords.latitude);
	}

	/**
	 * Change le mode d'affichage des flèches. La rotation s'effectue dans cet ordre
	 * : - Flèches au milieu de chaque chemin (mode par défaut) - Flèches au milieu
	 * de chaque tronçon (peut poser des problèmes de performance) - Pas de flèche
	 */
	public void switchArrowsMode() {
		switch (this.arrowsMode) {
		case ARROWS_NONE:
			this.arrowsMode = ARROWS_MIDDLE;
			break;
		case ARROWS_MIDDLE:
			this.arrowsMode = ARROWS_ALL;
			break;
		case ARROWS_ALL:
			this.arrowsMode = ARROWS_NONE;
			break;
		default:
			this.arrowsMode = ARROWS_MIDDLE;
			break;
		}
	}

	/**
	 * Indique si le mode de Debug est activé. Le mode de Debug correspond à
	 * l'affichage des points non atteignables depuis l'entrepôt.
	 * 
	 * @return true si le mode de Debug est activé, false sinon
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Activer / désactiver le mode de Debug. Le mode de Debug correspond à
	 * l'affichage des points non atteignables depuis l'entrepôt.
	 * 
	 * @param debugMode true pour activer le mode de Debug, false pour le désactiver
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Effectue un zoom sur le plan et redessine la vue.
	 */
	public void zoomIn() {
		this.frame.zoomIn();
		this.repaint();
	}

	/**
	 * Effectue un dezoom sur le plan et redessine la vue.
	 */
	public void zoomOut() {
		this.frame.zoomOut();
		this.repaint();
	}

	/**
	 * Effectue un zoom d'un certain nombres de clicks sur le plan et redessine la
	 * vue.
	 * 
	 * @param clicks Le nombre de rotations de la molette effectués. Si la valeur
	 *               est négative, effectue un dezoom au lieu d'un zoom.
	 */
	public void zoom(int clicks) {
		this.frame.zoom(clicks);
		this.repaint();
	}

	/**
	 * Effectue une translation sur le plan, et redessine la vue.
	 * 
	 * @param deltaX La coordonnée X de la translation, en pixels.
	 * @param deltaY La coordonnée Y de la translation, en pixels.
	 */
	public void translate(int deltaX, int deltaY) {
		this.frame.translateCenter(new CoordsPixels(deltaX, deltaY));
		this.repaint();
	}

	/**
	 * Calcule les coordonnées en latitude / longitude des coordonnées
	 * sélectionnées, puis récupère le noeud le plus proche. Ensuite, appelle le
	 * ViewModel pour mettre en avant le noeud en question.
	 * 
	 * @param x La coordonnée X du point, en pixels.
	 * @param y La coordonnée Y du point, en pixels.
	 */
	public void highlightNearestNode(int x, int y) {
		int width = this.getWidth();
		int height = this.getHeight();
		CoordsPixels frameCenter = new CoordsPixels(width / 2, height / 2);
		if (this.viewModel.getCurrentAction() == ViewAction.MODIFY) {
			CoordsLongLat coords = this.frame.pixelsToLongLat(new CoordsPixels(x, y), frameCenter);
			Node nearestNode = this.viewModel.getMap().nearestNode(coords.longitude, coords.latitude);
			this.viewModel.highlightNode(nearestNode);
		}
	}

	/**
	 * Calcule les coordonnées en latitude / longitude des coordonnées
	 * sélectionnées, puis récupère le tronçon le plus proche. Ensuite, appelle le
	 * ViewModel pour mettre en avant le tronçon en question.
	 * 
	 * @param x La coordonnée X du point, en pixels.
	 * @param y La coordonnée Y du point, en pixels.
	 */
	public void highlightNearestPath(int x, int y) {
		int width = this.getWidth();
		int height = this.getHeight();
		CoordsPixels frameCenter = new CoordsPixels(width / 2, height / 2);
		if (this.viewModel.getCurrentAction() == ViewAction.MODIFY_SELECT_PATH) {
			CoordsLongLat coords = this.frame.pixelsToLongLat(new CoordsPixels(x, y), frameCenter);
			Section section = this.viewModel.getMap().getNearestSection(coords.longitude, coords.latitude);
			this.viewModel.highlightPath(section);
		}
	}

	/**
	 * Méthode d'aide pour calculer les coordonnées d'un repère permettant
	 * d'englober tous les points du plan. Elle est appelée une fois au chargement
	 * du plan, puis l'utilisateur est libre de se déplacer comme il veut.
	 */
	private void calculateFrameLocation() {
		CoordsLongLat min = new CoordsLongLat(Double.MAX_VALUE, Double.MAX_VALUE);
		CoordsLongLat max = new CoordsLongLat(Double.MIN_VALUE, Double.MIN_VALUE);

		HashMap<Long, Node> nodesList = this.viewModel.getMap().getNodeList();

		if (nodesList.isEmpty()) {
			return;
		}

		for (Entry<Long, Node> entry : nodesList.entrySet()) {
			HashSet<Section> sections = entry.getValue().getSectionsList();
			for (Section section : sections) {
				min.latitude = Math.min(section.getSource().getLatitude(), min.latitude);
				max.latitude = Math.max(section.getSource().getLatitude(), max.latitude);
				min.longitude = Math.min(section.getSource().getLongitude(), min.longitude);
				max.longitude = Math.max(section.getSource().getLongitude(), max.longitude);
				min.latitude = Math.min(section.getDestination().getLatitude(), min.latitude);
				max.latitude = Math.max(section.getDestination().getLatitude(), max.latitude);
				min.longitude = Math.min(section.getDestination().getLongitude(), min.longitude);
				max.longitude = Math.max(section.getDestination().getLongitude(), max.longitude);
			}
		}
		double centerLong = (max.longitude + min.longitude) / 2.;
		double centerLat = (max.latitude + min.latitude) / 2.;
		double height = max.latitude - min.latitude;
		double width = max.longitude - min.longitude;
		double scaleLat = height / this.getHeight();
		double scaleLong = width / this.getHeight();

		this.frame.setLocation(new CoordsLongLat(centerLong, centerLat));
		this.frame.setScaleLat(scaleLat);
		this.frame.setScaleLong(scaleLong);
	}

	/**
	 * Classe d'aide qui représente des coordonnées en terme de latitude / longitude
	 * (exprimées en double).
	 */
	class CoordsLongLat {
		public double longitude;
		public double latitude;

		public CoordsLongLat(double longitude, double latitude) {
			this.longitude = longitude;
			this.latitude = latitude;
		}

		public CoordsLongLat(Node node) {
			this.longitude = node.getLongitude();
			this.latitude = node.getLatitude();
		}
	}

	/**
	 * Classe d'aide qui représente des coordonnées en terme de pixels sur la vue
	 * (en int)
	 */
	class CoordsPixels {
		public int x;
		public int y;

		public CoordsPixels(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Classe qui représente un repère dans un système de coordonnées en latitude /
	 * longitude, et qui permets de faire le lien entre les coordonnées en pixels
	 * affichées dans la vue graphique avec les coordonnées en latitude / longitude
	 * utilisées dans le modèle.
	 */
	class Frame {

		private static final double ZOOM_RATIO = 1.1d;

		private CoordsLongLat center;
		private double scaleLong; // LatLong par pixel
		private double scaleLat;
		private boolean initialized;

		/**
		 * Crée un repère centré en (0; 0) d'échelle latitude / longitude par pixel
		 * valant 1. Inutilisable en l'état, doit être initialisé avec les méthodes
		 * setLocation(), setScaleLat() et setScaleLong().
		 */
		public Frame() {
			this.center = new CoordsLongLat(0, 0);
			this.scaleLong = 1;
			this.scaleLat = 1;
			this.initialized = false;
		}

		/**
		 * Méthode de conversion de coordonnées longitudes / latitudes vers des
		 * coordonnées de l'écran (en pixels).
		 * 
		 * @param real        Les coordonnées réelles (longitude / latitude) d'un point
		 * @param frameCenter Le centre, en pixels, de la vue graphique
		 * @return Les coordonnées, en pixels, du point dans la vue graphique
		 */
		public CoordsPixels longLatToPixels(CoordsLongLat real, CoordsPixels frameCenter) {
			int x = frameCenter.x + (int) ((real.longitude - center.longitude) / this.scaleLong);
			int y = frameCenter.y - (int) ((real.latitude - center.latitude) / this.scaleLat);

			return new CoordsPixels(x, y);
		}

		/**
		 * Méthode de conversion de coordonnées pixels vers des coordonnées réelles (en
		 * latitude / longitude).
		 * 
		 * @param frame       Les coordonnées dans la vue graphique (en pixels) d'un
		 *                    point
		 * @param frameCenter Le centre, en pixels, de la vue graphique
		 * @return Les coordonnées, en latitude / longitude, du point dans des
		 *         coordonnées réelles (latitude / longitude)
		 */
		public CoordsLongLat pixelsToLongLat(CoordsPixels frame, CoordsPixels frameCenter) {
			double realLong = center.longitude + this.scaleLong * (frame.x - frameCenter.x);
			double realLat = center.latitude + this.scaleLat * (frameCenter.y - frame.y);

			return new CoordsLongLat(realLong, realLat);
		}

		/**
		 * Définit la position du centre du repère dans un système de coordonnées réel.
		 * 
		 * @param center La position du centre, en latitude / longitude
		 */
		public void setLocation(CoordsLongLat center) {
			this.center.latitude = center.latitude;
			this.center.longitude = center.longitude;
			this.initialized = true;
		}

		/**
		 * Effectue un zoom.
		 */
		public void zoomIn() {
			this.scaleLong /= ZOOM_RATIO;
			this.scaleLat /= ZOOM_RATIO;
		}

		/**
		 * Effectue un dezoom.
		 */
		public void zoomOut() {
			this.scaleLong *= ZOOM_RATIO;
			this.scaleLat *= ZOOM_RATIO;
		}

		/**
		 * Effectue un zoom d'un certain nombre de clicks (rotation de molette de
		 * souris, par exemple). Equivalent à utiliser plusieurs fois les méthodes
		 * zoomIn() ou zoomOut().
		 * 
		 * @param clicks Le nombre de roations de la molette de la souris. Si la valeur
		 *               est négative, effectue un dezoom.
		 */
		public void zoom(int clicks) {
			double ratio = Math.pow(ZOOM_RATIO, (double) clicks);
			this.scaleLong *= ratio;
			this.scaleLat *= ratio;
		}

		/**
		 * Effectue une translation du repère. Les coordonnées passées en pixels sont
		 * convertis en latitude / longitude pour être ajoutées ou soustraites aux
		 * coordonnées du centre.
		 * 
		 * @param delta La valeur de la translation, en pixels.
		 */
		public void translateCenter(CoordsPixels delta) {
			this.center.longitude -= delta.x * this.scaleLong;
			this.center.latitude += delta.y * this.scaleLat;
		}

		/**
		 * Définit l'échelle pour la latitude.
		 * 
		 * @param scale L'échelle, exprimée en latitude par pixel.
		 */
		public void setScaleLat(double scale) {
			this.scaleLat = scale;
		}

		/**
		 * Définit l'échelle pour la longitude.
		 * 
		 * @param scale L'échelle, exprimée en longitude par pixel.
		 */
		public void setScaleLong(double scale) {
			this.scaleLong = scale;
		}

		/**
		 * Equivalent à getScaleLat()
		 * 
		 * @return la valeur de getScaleLat()
		 */
		public double getScale() {
			return this.scaleLat;
		}

		/**
		 * Retourne la valeur de l'échelle en latitude du repère.
		 * 
		 * @return L'échelle exprimée en latitude par pixel.
		 */
		public double getScaleLat() {
			return this.scaleLat;
		}

		/**
		 * Retourne la valeur de l'échelle en longitude du repère.
		 * 
		 * @return L'échelle exprimée en longitude par pixel.
		 */
		public double getScaleLong() {
			return this.scaleLong;
		}

		/**
		 * Indique si le repère a été initialisé. Le repère est considéré comme
		 * initialisé si la méthode setLocation() a été appelée au moins une fois, mais
		 * il est fortement conseillé d'utiliser également les méthodes setScaleLat() et
		 * setScaleLong() avant d'utiliser l'objet.
		 * 
		 * @return true si le repère est initialisé, false sinon.
		 */
		public boolean isInitialized() {
			return this.initialized;
		}
	}
}
