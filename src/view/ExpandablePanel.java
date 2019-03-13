package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.model.ViewModel;

/**
 * Panneau extensible qui peut représenter une tournée, un chemin ou un tronçon.
 * Il s'agit d'un JPanel contenant un JLabel, ainsi que d'autres ExpandablePanel
 * qui peuvent s'afficher ou non (ce qui donne l'effet "extensible"). Ce panneau
 * contient également les indices potentiels d'une tournée, d'un chemin et d'une
 * section qui permet l'interactivité avec la vue graphique.
 * 
 * @author H4202
 */
@SuppressWarnings("serial")
public class ExpandablePanel extends JPanel {

	protected static final Color BACKGROUND_COLOR = new Color(1f, 1f, 1f);
	protected static final Color SELECTION_COLOR = Color.CYAN;

	private JLabel title;
	private String titleString;
	private ExpandablePanel parent;
	private ArrayList<ExpandablePanel> children;
	private Integer tourIndex;
	private Integer pathIndex;
	private Integer sectionIndex;
	private boolean expanded;
	private int hierarchyLevel;

	public ExpandablePanel(String title) {
		this(title, null, null, null);
	}

	public ExpandablePanel(String title, Integer tourIndex) {
		this(title, tourIndex, null, null);
	}

	public ExpandablePanel(String title, Integer tourIndex, Integer pathIndex) {
		this(title, tourIndex, pathIndex, null);
	}

	public ExpandablePanel(String title, Integer tourIndex, Integer pathIndex, Integer sectionIndex) {
		this.titleString = new String(title);
		this.title = new JLabel(this.titleString);
		this.tourIndex = tourIndex;
		this.pathIndex = pathIndex;
		this.sectionIndex = sectionIndex;
		this.children = new ArrayList<ExpandablePanel>();
		this.expanded = false;
		this.parent = null;
		this.hierarchyLevel = 0;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(this.title);
		this.updateLabel();
	}

	/**
	 * Permet d'ajouter un panneau extensible (ExpandablePanel) à ce panneau actuel.
	 * 
	 * @param panel Le panneau à ajouter.
	 */
	public void addPanel(ExpandablePanel panel) {
		this.children.add(panel);
		panel.hierarchyLevel = this.hierarchyLevel + 1;
		panel.updateLabel();
		if (this.expanded) {
			panel.parent = this;
			this.add(panel);
		}
		this.updateLabel();
	}

	/**
	 * Mets à jour le label qui change si le panneau s'étend ou se rétracte (la
	 * flèche à gauche change, voire ne s'affiche pas).
	 */
	private void updateLabel() {
		StringBuffer labelText = new StringBuffer();
		for (int i = 0; i < this.hierarchyLevel; ++i) {
			labelText.append("  ");
		}
		if (this.children.size() > 0) {
			if (this.expanded) {
				labelText.append("▾  ");
			} else {
				labelText.append("▸  ");
			}
		} else {
			labelText.append("   ");
		}
		labelText.append(this.titleString);
		this.title.setText(labelText.toString());
		this.revalidate();
	}

	/**
	 * Etends ou rétracte le panneau, en ajoutant ou retirant du JComponent en
	 * lui-même les ExpandabePanel enfants.
	 */
	public void toggleExpanded() {
		this.expanded = !this.expanded;
		for (ExpandablePanel panel : this.children) {
			if (this.expanded) {
				this.add(panel);
			} else {
				this.remove(panel);
			}
		}
		this.updateLabel();
		this.revalidate();
	}

	/**
	 * Retire l'ensemble des panneaux extensibles enfants de celui-là, en vidant
	 * également chancun de ces panneaux enfants.
	 */
	public void clear() {
		if (this.expanded) {
			for (ExpandablePanel panel : this.children) {
				this.remove(panel);
				panel.clear();
			}
		}
		this.children.clear();
	}

	/**
	 * Permets de changer l'affichage de ce panneau ainsi que des panneaux enfants,
	 * en fonction des indices de la tournée, du chemin et du tronçon actuellement
	 * sélectionnée. Cette méthode récursive parcourt tous les panneaux enfants de
	 * celui-ci et mets à jour leur affichage.
	 * 
	 * @param tourIndex L'indice de la tournée.
	 * @param pathIndex L'indice de l'espace.
	 * @param sectionIndex L'indice de la section.
	 */
	public void select(int tourIndex, Integer pathIndex, Integer sectionIndex) {
		this.unhighlightTitle();
		if (this.tourIndex != null && tourIndex == this.tourIndex) {
			this.title.setForeground(ViewModel.getTourColor(tourIndex));
			if (pathIndex != null && sectionIndex == null && pathIndex == this.pathIndex) {
				this.highlightTitle();
			} else if (pathIndex != null && sectionIndex != null && pathIndex == this.pathIndex
					&& sectionIndex == this.sectionIndex) {
				this.highlightTitle();
			}
		} else {
			this.title.setForeground(Color.black);
		}

		for (ExpandablePanel panel : this.children) {
			panel.select(tourIndex, pathIndex, sectionIndex);
		}
	}

	/**
	 * Affiche le JLabel du panneau de manière plus visible.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void highlightTitle() {
		Font font = this.title.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		this.title.setFont(font.deriveFont(attributes));
	}

	/**
	 * Affiche le JLabel du panneau de manière normale.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void unhighlightTitle() {
		Font font = this.title.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, -1);
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
		this.title.setFont(font.deriveFont(attributes));
	}

	public boolean isExpanded() {
		return this.expanded;
	}

	public Integer getTourIndex() {
		return this.tourIndex;
	}

	public Integer getPathIndex() {
		return pathIndex;
	}

	public Integer getSectionIndex() {
		return sectionIndex;
	}
}
