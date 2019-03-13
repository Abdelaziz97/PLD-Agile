package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.DeliverySpot;
import view.listeners.DeliverySpotListener;
import view.model.ViewModel;

/**
 * Paneau latéral qui permet d'afficher les points de livraisons avec leurs
 * durées de déchargement respectives. Les points de livraisons sont cliquables
 * pour mettre en gras leur position sur la vue graphique.
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class DeliverySpotView extends JPanel implements Observer {

	private static final Color HIGHLIGHT_COLOR = Color.red;
	private static final Color NORMAL_COLOR = Color.black;

	private JScrollPane scrollPane;
	private JPanel listPanel;

	private ViewModel viewModel;
	private Window window;

	public DeliverySpotView(Window window, ViewModel viewModel) {
		this.window = window;
		this.viewModel = viewModel;

		this.viewModel.addObserver(this);

		this.listPanel = new JPanel();
		this.listPanel.setLayout(new BoxLayout(this.listPanel, BoxLayout.Y_AXIS));
		this.scrollPane = new JScrollPane(this.listPanel);

		this.listPanel.setBackground(Color.white);
		this.listPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				DeliverySpotView.this.viewModel.highlightDeliverySpot(null);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		this.setLayout(new BorderLayout());
		this.add(this.scrollPane, BorderLayout.CENTER);
	}

	public ViewModel getViewModel() {
		return this.viewModel;
	}

	/**
	 * Lors de la mise à jour de la liste des livraison, affiche sous forme de
	 * Labels chaque point de livraison.
	 */
	@Override
	public void update(Observable obs, Object obj) {
		if (this.viewModel.hasModelChanged()) {
			Component[] comps = this.listPanel.getComponents();
			for (Component comp : comps) {
				this.listPanel.remove(comp);
			}

			DeliverySpotListener listener = new DeliverySpotListener(this.viewModel);

			List<DeliverySpot> deliverySpots = this.viewModel.getDeliverySpots();
			System.out.println("Update : on a " + deliverySpots.size() + " delivery spots");
			int i = 1;
			for (DeliverySpot spot : deliverySpots) {
				int spotIndex = i - 1;
				int minutes = (int) Math.ceil((double) spot.getUnloadingTime() / 60d);
				String time = (minutes >= 1) ? minutes + " min" : spot.getUnloadingTime() + " sec";
				String spotDesc = "Livraison " + i + " (" + time + ")";

				Color color = NORMAL_COLOR;

				DeliverySpotLabel spotLabel = new DeliverySpotLabel(spotDesc, spotIndex);
				spotLabel.setForeground(color);
				spotLabel.addMouseListener(listener);
				this.listPanel.add(spotLabel);
				++i;
			}

			this.window.revalidate();
			this.window.repaint();
		}
	}
}