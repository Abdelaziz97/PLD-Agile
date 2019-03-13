package model.test;

import java.util.Observable;
import java.util.Observer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.ModelInterface;
import model.Node;
import model.Path;
import model.Section;
import model.Tour;
import util.Pair;

class ModelInterfaceTestCase {

	static boolean updateCalled;
	static Observer observer;
	static ModelInterface modelInterface;
	static Node node0;
	static Node node1;
	
	@BeforeAll
	public static void init() {
		modelInterface = new ModelInterface();
		observer = new Observer(){public void update(Observable o, Object arg) {updateCalled=true;}};

		node0 = new Node(120, 150, 01);
		node1 = new Node(150, 120, 02);
		Node node2 = new Node(170, 180, 03);

		modelInterface.getMap().addNode(node0);
		modelInterface.getMap().addNode(node1);
		modelInterface.getMap().addNode(node2);

		modelInterface.getMap().addSection(new Section("streetName", 1d, node0, node1));
		modelInterface.getMap().addSection(new Section("streetName", 1d, node1, node0));

		modelInterface.getMap().addSection(new Section("streetName", 2d, node1, node2));
		modelInterface.getMap().addSection(new Section("streetName", 2d, node2, node1));

		modelInterface.getMap().addSection(new Section("streetName", 10d, node0, node2));
		modelInterface.getMap().addSection(new Section("streetName", 10d, node2, node0));

		modelInterface.setStore(node0);
		modelInterface.addDeliverySpot(new DeliverySpot(node1, 2));
		modelInterface.addDeliverySpot(new DeliverySpot(node2, 2));
	}
	
	/*@Test
	public void testCalculateKMeans() throws Exception {
		updateCalled = false;
		modelInterface.addObserver(observer);
		
		modelInterface.calculateDijkstra();
		modelInterface.calculateKMeans(30);
		
		assert(updateCalled);
	}
	
	
	//test de calculateKMeans avec plus de livreurs que de livraisons
	@Test
	void testCalculateKMeans2() throws Exception {
		updateCalled = false;
		modelInterface.addObserver(observer);
		
		modelInterface.setDeliveryMenNumber(7);
		
		modelInterface.calculateDijkstra();
		modelInterface.calculateKMeans(30);
		
		assert(updateCalled);
	}*/
	
	//test de removeDeliverySpot avant calcul des tournees
	@Test
	void testRemoveDeliverySpot(){
		updateCalled=false;
		modelInterface.addObserver(observer);
		DeliverySpot ds = new DeliverySpot(node1, 2);
		modelInterface.removeDeliverySpot(ds);
		
		boolean verified = !modelInterface.getDeliverySpots().contains(ds);
		assert(updateCalled);
		assert(verified);
		
	}
	
	//test de removeDeliverySpot apres calcul des tournees
		@Test
		void testRemoveDeliverySpot2() throws Exception{
			updateCalled=false;
			modelInterface.addObserver(observer);
			
			DeliverySpot toRemove = new DeliverySpot(node1, 2);
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			modelInterface.removeDeliverySpot(toRemove);
			
			boolean verified = !modelInterface.getDeliverySpots().contains(toRemove);
			assert(updateCalled);
			assert(verified);
		}
		
		//test d'ajout d'un deliverySpot unreachable a une tournee
		@Test
		void testAddDeliverySpotToTour() throws Exception {
			updateCalled=false;
			modelInterface.addObserver(observer);
			
			Node node3 = new Node(145,162,04);
			
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			Tour tour = modelInterface.getTourList().get(0);
			DeliverySpot toAdd = new DeliverySpot(node3,3);
			modelInterface.addDeliverySpotToTour(toAdd, node0, tour);
			
			boolean verified = modelInterface.getDeliverySpots().contains(toAdd);
			assert(updateCalled);
			assert(!verified);
		}
		
		//test d'ajout d'un deliverySpot a une tournee
		@Test
		void testAddDeliverySpotToTour2() throws Exception {
			updateCalled=false;
			modelInterface.addObserver(observer);
			
			Node node3 = new Node(145,162,04);
			modelInterface.getMap().addNode(node3);
			
			modelInterface.getMap().addSection(new Section("streeName", 10d, node0, node3));
			modelInterface.getMap().addSection(new Section("streeName", 10d, node3, node0));
			
			modelInterface.setDeliveryMenNumber(7);
			
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			Tour tour = modelInterface.getTourList().get(0);
			DeliverySpot toAdd = new DeliverySpot(node3,3);
			modelInterface.addDeliverySpotToTour(toAdd, node0, tour);
			
			boolean verified = modelInterface.getDeliverySpots().contains(toAdd);
			assert(updateCalled);
			assert(verified);
		}
		
		@Test
		void testKMeans() throws Exception {
			updateCalled = true;
			modelInterface.addObserver(observer);
			
			modelInterface.setDeliveryMenNumber(7);
			
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			Tour tour = modelInterface.getTourList().get(0);
			
			modelInterface.reinitializeCalcul();
			modelInterface.addObserver(observer);
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			Tour tour2 = modelInterface.getTourList().get(0);
			
			for (Path path: tour.getPathList()) {
			   for (Section section: path.sectionList()) {
				   for (Path path2: tour2.getPathList()) {
					   for (Section section2: path2.sectionList()) {
						   updateCalled &= (section.getDestination().getId() == section2.getDestination().getId()); 
						   updateCalled &= (section.getSource().getId() == section2.getSource().getId()); 
						   updateCalled &= (section.getLength() == section2.getLength());   
						   updateCalled &= (section.getStreetName() == section2.getStreetName());   
					   }
				   }
			   }
			}
			
			assert(updateCalled);
		}
		
		/*@Test
		void testChangePositionInTour() throws Exception{
			updateCalled = false;
			modelInterface.addObserver(observer);
			
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			
			Tour tour = modelInterface.getTourList().get(0);
			modelInterface.changePositionInTour(new DeliverySpot(node1, 2), node0, tour);
			
			assert(updateCalled);
		}*/
		
		//test de getDeliveryPosition pour un deliverySpot dont le noeud prec est le store
		@Test
		void testGetDeliveryPosition() throws Exception {
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			
			DeliverySpot ds = new DeliverySpot(node1, 2);
			
			Tour currentTour=null;
			Node previousNodeSupp=null;
			l1: for (Tour t: modelInterface.getTourList()) {
				 if (t.getVertexList().contains(ds.getAddress())) {
					currentTour = t;
					int i=t.getVertexList().lastIndexOf(ds.getAddress())-1;
					if (i>=0)
					previousNodeSupp = t.getVertexList().get(i);
					else
					previousNodeSupp = modelInterface.getStore();
					break l1;
				}
			}
			Pair<Tour,Node> toCompare = new Pair<Tour,Node>(currentTour,previousNodeSupp);
			Pair<Tour,Node> result = modelInterface.getDeliveryPosition(ds);
			boolean res = (toCompare.first==result.first && toCompare.second==result.second);
			assert(res);
		}
		
		//test de getDeliveryPosition pour un deliverySpot quelconque
		@Test
		void testGetDeliveryPosition2() throws Exception {
			Node node4 = new Node(670, 180, 7);
			Node node5 = new Node(595, 260, 9);
			modelInterface.getMap().addNode(node4);
			modelInterface.getMap().addNode(node5);
			modelInterface.getMap().addSection(new Section("streetName", 10d, node0, node4));
			modelInterface.getMap().addSection(new Section("streetName", 10d, node4, node0));
			modelInterface.getMap().addSection(new Section("streetName", 10d, node0, node5));
			modelInterface.getMap().addSection(new Section("streetName", 10d, node5, node0));
			modelInterface.addDeliverySpot(new DeliverySpot(node4, 3));
			DeliverySpot ds = new DeliverySpot(node5, 5);
			modelInterface.addDeliverySpot(ds);
			
			modelInterface.calculateDijkstra();
			modelInterface.calculateKMeans(30);
			
			Tour currentTour=null;
			Node previousNodeSupp=null;
			l1: for (Tour t: modelInterface.getTourList()) {
				 if (t.getVertexList().contains(ds.getAddress())) {
					currentTour = t;
					int i=t.getVertexList().lastIndexOf(ds.getAddress())-1;
					if (i>=0)
					previousNodeSupp = t.getVertexList().get(i);
					else
					previousNodeSupp = modelInterface.getStore();
					break l1;
				}
			}
			Pair<Tour,Node> toCompare = new Pair<Tour,Node>(currentTour,previousNodeSupp);
			Pair<Tour,Node> result = modelInterface.getDeliveryPosition(ds);
			boolean res = (toCompare.first==result.first && toCompare.second==result.second);
			assert(res);
		}

}
