package xml;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

/**
 * classe d'ouverture de fichier xml
 * @author H4202
 *
 */
public class XMLFileOpener extends FileFilter {// Singleton

	private static XMLFileOpener instance = null;

	private XMLFileOpener() {
	}

	protected static XMLFileOpener getInstance() {
		if (instance == null)
			instance = new XMLFileOpener();
		return instance;
	}

	/**
	 * ouverture d'un fichier xml et verification de l'extension
	 * @return
	 * @throws Exception erreur d'extension
	 */
	public File open() throws Exception {
		JFileChooser jFileChooserXML = new JFileChooser();
		jFileChooserXML.setFileFilter(this);
		jFileChooserXML.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooserXML.showOpenDialog(null);
		File f = new File(jFileChooserXML.getSelectedFile().getAbsolutePath());
		
		if(this.accept(f))
		{
			return f;
		}
		else
		{
			throw new Exception("Probleme d'extension du fichier");
		}
	}

	@Override
	public boolean accept(File f) {
		if (f == null)
			return false;
		if (f.isDirectory())
			return true;
		String extension = getExtension(f);
		if (extension == null)
			return false;
		return extension.toLowerCase().contentEquals("xml");
	}

	@Override
	public String getDescription() {
		return "Fichier XML";
	}

	private String getExtension(File f) {
		String filename = f.getName();
		int i = filename.lastIndexOf('.');
		if (i > 0 && i < filename.length() - 1)
			return filename.substring(i + 1).toLowerCase();
		return null;
	}
	
	public static void main(String[] args)
	{
		try
		{
			XMLFileOpener xfp = XMLFileOpener.getInstance();
			File f = xfp.open();
			System.out.println(f.getName());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
