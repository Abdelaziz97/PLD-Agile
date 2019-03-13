package view.model;

/**
 * Les différents états dans lesquels peut se trouver la fenêtre, correspondant
 * à des ensembles d'action possibles de la part de l'utilisateur.
 * 
 * @author H4202
 */
public enum ViewAction {
	/**
	 * L'état principal dans lequel on a accès aux différents boutons (dépendemment
	 * de l'état du contrôleur)
	 */
	MAIN,
	/**
	 * L'état de modification dans lequel l'utilisateur peut sélectionner un point
	 * de livraison ou une intersection sur la vue graphique
	 */
	MODIFY,
	/**
	 * L'état dans lequel l'utilisateur doit sélectionner un chemin d'une tournée
	 * pour y ajouter un nouveau point de livraison
	 */
	MODIFY_SELECT_PATH,
}
