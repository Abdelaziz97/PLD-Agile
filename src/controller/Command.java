package controller;

/**
 * classe de commande generique
 * @author H4202
 *
 */
public interface Command {

	/**
	 * execution de la commande
	 */
	void doCde();

	/**
	 * execution de l'inverse de la commande
	 */
	void undoCde();

}
