package controller;

import java.util.LinkedList;

/**
 * liste des commandes executes
 * @author H4202
 *
 */
public class CommandList {
	private LinkedList<Command> list;
	private int indiceCrt;

	/**
	 * cree la liste des commandes executes
	 */
	public CommandList() {
		indiceCrt = -1;
		list = new LinkedList<Command>();
	}

	/**
	 * Ajout de la commande c a la liste this
	 * 
	 * @param c
	 */
	public void add(Command c) {
		System.out.println("ajout commande");
		int i = indiceCrt + 1;
		while (i < list.size()) {
			list.remove(i);
		}
		indiceCrt++;
		list.add(indiceCrt, c);
		c.doCde();
	}

	/**
	 * Annule temporairement la derniere commande ajoutee (cette commande pourra
	 * etre remise dans la liste avec redo)
	 * 
	 * @return
	 */
	public void undo() {
		System.out.println("undo");
		try {
		if (indiceCrt >= 0) {
			Command cde = list.get(indiceCrt);
			indiceCrt--;
			cde.undoCde();
		    }
		}
		catch (Exception e) {
			System.out.println("pas de commande disponible");
		}
	}

	/**
	 * Supprime definitivement la derniere commande ajoutee (cette commande ne
	 * pourra pas etre remise dans la liste avec redo)
	 */
	public void annule() {
		try {
		if (indiceCrt >= 0) {
			Command cde = list.get(indiceCrt);
			list.remove(indiceCrt);
			indiceCrt--;
			cde.undoCde();
		    }
		}
		catch (Exception e) {
			System.out.println("pas de commande disponible");
		}
	}

	/**
	 * Remet dans la liste la derniere commande annulee avec undo
	 */
	public void redo() {
		System.out.println("redo");
		try {
		if (indiceCrt < list.size() - 1) {
			indiceCrt++;
			Command cde = list.get(indiceCrt);
			cde.doCde();
		    }
		}
		catch (Exception e) {
			System.out.println("pas de commande disponible");
		}
	}

	/**
	 * Supprime definitivement toutes les commandes de liste
	 */
	public void reset() {
		indiceCrt = -1;
		list.clear();
	}

	public boolean notRedo() {
		return (indiceCrt >= list.size() - 1);
	}
	
	public boolean notUndo() {
		return (indiceCrt < 0);
	}
}
