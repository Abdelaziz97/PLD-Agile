package controller;

/**
 * classe permettant de faire le traitement inverse d'une commande
 * @author H4202
 *
 */
public class CommandReverse implements Command {

	private Command command;

	/**
	 * cree la commande qui fait l'inverse de la commande passee en parametre
	 * @param command la commande dont on veut faire l'inverse
	 */
	public CommandReverse(Command command) {
		this.command = command;
	}

	@Override
	public void doCde() {
		this.command.undoCde();
	}

	@Override
	public void undoCde() {
		this.command.doCde();
	}

}
