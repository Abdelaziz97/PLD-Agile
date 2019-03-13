package model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.Node;
import model.Path;
import model.Section;
import model.Tour;
import util.Pair;

public class TourTestCase {
	
	@Test
	void testAddVertex1() {
		Tour tour = new Tour();
		Node node1 = new Node(150,120,1);
		tour.addVertex(node1);
		assertEquals(tour.getVertexList().get(0) , node1);
	}
	
	@Test
	void testDuration() {
		Tour tour = new Tour();
		tour.setDuration(10d);
		assertEquals(tour.getDuration() , 10d);
	}
	
	@Test
	void testBuildPath() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    List<Path> pathList = tour.getPathList();
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path2);
	    verify &= pathList.get(2).equals(path3);
	    verify &= pathList.get(3).equals(path4);
	    
	    assertTrue(verify);
	}
	
	@Test
	void testAddEnd() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    Node node4 = new Node(180,200,5);
	    DeliverySpot deliveryspot = new DeliverySpot(node4,0);
	    
	    Section section5 = new Section("street4", 1d, node3, node4);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,1d);
	    
	    Section section6 = new Section("street4", 2d, node4, store);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,2d);
	    
	    paths.put(new Pair(node3,node4), path5);
	    paths.put(new Pair(node4,store), path6);
	    
	    tour.addDeliverySpot(deliveryspot, node3, paths, store);
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path2);
	    verify &= pathList.get(2).equals(path3);
	    verify &= pathList.get(3).equals(path5);
	    verify &= pathList.get(4).equals(path6);
	    assertTrue(verify);
	    
	}
	
	@Test
	void testAddBegin() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    Node node4 = new Node(180,200,5);
	    DeliverySpot deliveryspot = new DeliverySpot(node4,0);
	    
	    Section section5 = new Section("street4", 1d, store, node4);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,1d);
	    
	    Section section6 = new Section("street4", 2d, node4, node1);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,2d);
	    
	    paths.put(new Pair(store,node4), path5);
	    paths.put(new Pair(node4,node1), path6);
	    
	    tour.addDeliverySpot(deliveryspot, store, paths, store);;
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path5);
	    verify &= pathList.get(1).equals(path6);
	    verify &= pathList.get(2).equals(path2);
	    verify &= pathList.get(3).equals(path3);
	    verify &= pathList.get(4).equals(path4);
	    assertTrue(verify);
	    
	}
	
	@Test
	void testAddFirstDeliverySpot() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node store = new Node(150,100,2);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,store), path2);
		
	    DeliverySpot deliveryspot = new DeliverySpot(node1,0);
	    
	    tour.addDeliverySpot(deliveryspot, store, paths, store);
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path2);
	    assertTrue(verify);
	    
	}
	
	@Test
	void testAddMiddle() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    Node node4 = new Node(180,200,5);
	    DeliverySpot deliveryspot = new DeliverySpot(node4,0);
	    
	    Section section5 = new Section("street4", 1d, node2, node4);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,1d);
	    
	    Section section6 = new Section("street4", 2d, node4, node3);
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,2d);
	    
	    paths.put(new Pair(node2,node4), path5);
	    paths.put(new Pair(node4,node3), path6);
	    
	    tour.addDeliverySpot(deliveryspot, node2, paths, store);;
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path2);
	    verify &= pathList.get(2).equals(path5);
	    verify &= pathList.get(3).equals(path6);
	    verify &= pathList.get(4).equals(path4);
	    assertTrue(verify);
	    
	}
	
	@Test
	void testRemoveFirst() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    List<Path> pathList = tour.getPathList();
	    tour.removeDeliverySpot(node1, paths, store);
	    boolean verify = !pathList.contains(path1);
	    verify &= !pathList.contains(path2);
	    
	    assertTrue(verify);
	}
	
	@Test
	void testRemoveLast() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    List<Path> pathList = tour.getPathList();
	    tour.removeDeliverySpot(node3, paths, store);
	    boolean verify = !pathList.contains(path3);
	    verify &= !pathList.contains(path4);
	    
	    assertTrue(verify);
	}
	
	@Test
	void testRemoveMiddle() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(130,150,3);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 5d, store, node1);
		Section section2 = new Section("street2", 4d, node1, node2);
		Section section3 = new Section("street3", 6d, node2, node3);
		Section section4 = new Section("street4", 3d, node3, store);
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,3d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,node2), path2);
		paths.put(new Pair(node2,node3), path3);
		paths.put(new Pair(node3,store), path4);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		
	    tour.buildPaths(store, paths);
	    
	    List<Path> pathList = tour.getPathList();
	    tour.removeDeliverySpot(node2, paths, store);
	    boolean verify = !pathList.contains(path2);
	    verify &= !pathList.contains(path3);
	    
	    assertTrue(verify);
	}
	
	@Test
	void testChangePositionInTourFirst() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 1d, store, node1);
		Section section2 = new Section("street4", 2d, node1, store);
		Section section3 = new Section("street4", 3d, store, node2);
		Section section4 = new Section("street4", 4d, node2, store);
		Section section5 = new Section("street2", 5d, node1, node2);
		Section section6 = new Section("street3", 6d, node2, node1);
		
		
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,1d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,2d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,3d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,6d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,store), path2);
		paths.put(new Pair(store,node2), path3);
		paths.put(new Pair(node2,store), path4);
		paths.put(new Pair(node1,node2), path5);
		paths.put(new Pair(node2,node1), path6);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		
	    tour.buildPaths(store, paths);
	    DeliverySpot deliverySpot = new DeliverySpot(node1,0);
	    tour.changePositionInTour(deliverySpot, node2, store, paths);
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path3);
	    verify &= pathList.get(1).equals(path6);
	    verify &= pathList.get(2).equals(path2);
	    
	    assertTrue(verify);	
	}
	
	@Test
	void testChangePositionInTourLast() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node store = new Node(150,100,4);
		
		Section section1 = new Section("street1", 1d, store, node1);
		Section section2 = new Section("street4", 2d, node1, store);
		Section section3 = new Section("street4", 3d, store, node2);
		Section section4 = new Section("street4", 4d, node2, store);
		Section section5 = new Section("street2", 5d, node1, node2);
		Section section6 = new Section("street3", 6d, node2, node1);
		
		
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,1d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,2d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,3d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,6d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(node1,store), path2);
		paths.put(new Pair(store,node2), path3);
		paths.put(new Pair(node2,store), path4);
		paths.put(new Pair(node1,node2), path5);
		paths.put(new Pair(node2,node1), path6);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		
	    tour.buildPaths(store, paths);
	    DeliverySpot deliverySpot = new DeliverySpot(node2,0);
	    tour.changePositionInTour(deliverySpot, node1, store, paths);
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path5);
	    verify &= pathList.get(2).equals(path4);
	    
	    assertTrue(verify);	
	}
	
	@Test
	void testChangePositionInTourMiddle() {
		Tour tour = new Tour();
		Node node1 = new Node(110,120,1);
		Node node2 = new Node(120,130,2);
		Node node3 = new Node(120,130,3);
		Node node4 = new Node(120,130,4);
		Node store = new Node(150,100,5);
		
		Section section1 = new Section("street1", 1d, store, node1);
		Section section2 = new Section("street4", 2d, store, node2);
		Section section3 = new Section("street4", 3d, store, node3);
		Section section4 = new Section("street4", 4d, store, node4);
		Section section5 = new Section("street1", 1d, node1, store);
		Section section6 = new Section("street4", 2d, node2, store);
		Section section7 = new Section("street4", 3d, node3, store);
		Section section8 = new Section("street4", 4d, node4, store);
		Section section9 = new Section("street2", 5d, node1, node2);
		Section section10 = new Section("street3", 6d, node1, node3);
		Section section11 = new Section("street3", 6d, node1, node4);
		Section section12 = new Section("street4", 3d, node2, node1);
		Section section13 = new Section("street4", 4d, node2, node3);
		Section section14 = new Section("street2", 5d, node2, node4);
		Section section15 = new Section("street4", 3d, node3, node1);
		Section section16 = new Section("street4", 4d, node3, node2);
		Section section17 = new Section("street2", 5d, node3, node4);
		Section section18 = new Section("street4", 3d, node4, node1);
		Section section19 = new Section("street4", 4d, node4, node2);
		Section section20 = new Section("street2", 5d, node4, node3);
		
		
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
	    Path path1 = new Path(sectionsList,1d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section2);
	    Path path2 = new Path(sectionsList,2d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section3);
	    Path path3 = new Path(sectionsList,3d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section4);
	    Path path4 = new Path(sectionsList,4d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section5);
	    Path path5 = new Path(sectionsList,5d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section6);
	    Path path6 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section7);
	    Path path7 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section8);
	    Path path8 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section9);
	    Path path9 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section10);
	    Path path10 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section11);
	    Path path11 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section12);
	    Path path12 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section13);
	    Path path13 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section14);
	    Path path14 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section15);
	    Path path15 = new Path(sectionsList,6d);
	    
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section16);
	    Path path16 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section17);
	    Path path17 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section18);
	    Path path18 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section19);
	    Path path19 = new Path(sectionsList,6d);
	    
	    sectionsList = new LinkedList<Section>();
		sectionsList.add(section20);
	    Path path20 = new Path(sectionsList,6d);
	    
	    HashMap<Pair<Node, Node>, Path> paths = new HashMap<Pair<Node, Node>, Path>();
		paths.put(new Pair(store,node1), path1);
		paths.put(new Pair(store,node2), path2);
		paths.put(new Pair(store,node3), path3);
		paths.put(new Pair(store,node4), path4);
		paths.put(new Pair(node1,store), path5);
		paths.put(new Pair(node2,store), path6);
		paths.put(new Pair(node3,store), path7);
		paths.put(new Pair(node4,store), path8);
		paths.put(new Pair(node1,node2), path9);
		paths.put(new Pair(node1,node3), path10);
		paths.put(new Pair(node1,node4), path11);
		paths.put(new Pair(node2,node1), path12);
		paths.put(new Pair(node2,node3), path13);
		paths.put(new Pair(node2,node4), path14);
		paths.put(new Pair(node3,node1), path15);
		paths.put(new Pair(node3,node2), path16);
		paths.put(new Pair(node3,node4), path17);
		paths.put(new Pair(node4,node1), path18);
		paths.put(new Pair(node4,node2), path19);
		paths.put(new Pair(node4,node3), path20);
		
		tour.addVertex(node1);
		tour.addVertex(node2);
		tour.addVertex(node3);
		tour.addVertex(node4);
		
	    tour.buildPaths(store, paths);
	    DeliverySpot deliverySpot = new DeliverySpot(node2,0);
	    tour.changePositionInTour(deliverySpot, node3, store, paths);
	    List<Path> pathList = tour.getPathList();
	    
	    boolean verify = pathList.get(0).equals(path1);
	    verify &= pathList.get(1).equals(path10);
	    verify &= pathList.get(2).equals(path16);
	    verify &= pathList.get(3).equals(path14);
	    verify &= pathList.get(4).equals(path8);
	    
	    assertTrue(verify);	
	}

}
