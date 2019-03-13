package xml;

import java.io.File;
import java.time.LocalTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Section;
import util.Util;

/**
 * classe de creation du plan et de la demande de livraisons a partir de fichiers xml
 * @author H4202
 *
 */
public class DeserializerXML {
	
	private final static String nomRootMap = "reseau";
	private final static String nomRootDelivery = "demandeDeLivraisons";

	/**
	 * recuperation, verification et construction du plan a partir d'un fichier xml
	 * @param mi
	 * @throws Exception
	 */
	public static void loadMap(ModelInterface mi) throws Exception {
		File xml;
		try {
			xml = XMLFileOpener.getInstance().open();
		} catch (Exception e) {
			throw new Exception("Veuillez sélectionner un fichier");
		}
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = docBuilder.parse(xml);
		Element root = document.getDocumentElement();
		if (root.getNodeName().equals(nomRootMap)) {
			mapConstruction(root, mi);
		} else {
			throw new Exception("Document non conforme");
		}
		mi.getMap().checkReachable();
		mi.notifyMap();
	}

	/**
	 * construction du plan a partir du fichier charge
	 * @param domRootNode l'element racine du fichier xml
	 * @param mi
	 * @throws Exception
	 */
	private static void mapConstruction(Element domRootNode, ModelInterface mi) throws Exception {
		
		mi.getMap().getNodeList().clear();
        mi.setStore(null);
		mi.getDeliverySpots().clear();
		mi.getTourList().clear();
		mi.getMap().getUnreachableNode().clear();

		Element e;

		NodeList domlistNode = domRootNode.getElementsByTagName("noeud");
		double longitude;
		double latitude;
		long id;

		for (int i = 0; i < domlistNode.getLength(); i++) {
			e = (Element) domlistNode.item(i);
			longitude = Double.parseDouble(e.getAttribute("longitude"));
			latitude = Double.parseDouble(e.getAttribute("latitude"));
			id = Long.parseLong(e.getAttribute("id"));
			if((longitude < 0)||(latitude < 0)||(id < 0)) {
				throw new Exception();
			}
			mi.getMap().addNode(new Node(longitude, latitude, id));
		}

		NodeList domlistSection = domRootNode.getElementsByTagName("troncon");

		long idSource;
		long idDestination;
		double length;
		String streetName;

		for (int i = 0; i < domlistSection.getLength(); i++) {
			e = (Element) domlistSection.item(i);
			idSource = Long.parseLong(e.getAttribute("origine"));
			idDestination = Long.parseLong(e.getAttribute("destination"));
			length = Double.parseDouble(e.getAttribute("longueur"));
			streetName = e.getAttribute("nomRue");
			if((idSource < 0)||(idDestination < 0)||(length < 0)||(mi.getMap().getNodeList().get(idSource) == null)||(mi.getMap().getNodeList().get(idDestination) == null)) {
				throw new Exception();
			}
			mi.getMap().addSection(new Section(streetName, length, mi.getMap().getNodeList().get(idSource),
					mi.getMap().getNodeList().get(idDestination)));
		}
	}

	/**
	 * recuperation, verification et construction de la demande de livraisons a partir d'un fichier xml
	 * @param mi
	 * @throws Exception
	 */
	public static void loadDeliverySpots(ModelInterface mi) throws Exception {
		File xml;
		try {
			xml = XMLFileOpener.getInstance().open();
		} catch (Exception e) {
			throw new Exception("Veuillez sélectionner un fichier");
		}
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = docBuilder.parse(xml);
		Element root = document.getDocumentElement();
		if (root.getNodeName().equals(nomRootDelivery)) {
			deliverySpotConstruction(root, mi);
		} else {
			throw new Exception("Document non conforme");
		}
	}

	/**
	 * construction de la demande de livraisons a partir du fichier charge
	 * @param domRootNode l'element racine du fichier xml
	 * @param mi
	 * @throws Exception
	 */
	private static void deliverySpotConstruction(Element domRootNode, ModelInterface mi) throws Exception {
		
		mi.getDeliverySpots().clear();
		mi.getTourList().clear();

		Element e;

		NodeList domStore = domRootNode.getElementsByTagName("entrepot");
		e = (Element) domStore.item(0);
		Long storeAddress = Long.parseLong(e.getAttribute("adresse"));
		LocalTime hourStart = LocalTime.parse(Util.parseTime(e.getAttribute("heureDepart")));
		if((storeAddress < 0)||(mi.getMap().getNodeList().get(storeAddress) == null)) {
			throw new Exception();
		}
		mi.setStore(mi.getMap().getNodeList().get(storeAddress));
		mi.setStartHour(hourStart);

		NodeList domlistDeliverySpot = domRootNode.getElementsByTagName("livraison");

		long idAddress;
		Node address;
		int unloadingTime;

		for (int i = 0; i < domlistDeliverySpot.getLength(); i++) {
			e = (Element) domlistDeliverySpot.item(i);
			idAddress = Long.parseLong(e.getAttribute("adresse"));
			address = mi.getMap().getNodeList().get(idAddress);
			unloadingTime = Integer.parseInt(e.getAttribute("duree"));
			if((idAddress < 0)||(unloadingTime < 0)||(address == null)) {
				throw new Exception();
			}
			mi.addDeliverySpot(new DeliverySpot(address, unloadingTime));
		}
		mi.getMap().DFS(mi.getStore());
	}
}