package model.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.Graph;
import model.Map;
import model.ModelInterface;
import model.Node;
import model.Section;

class MapTestCase {

	@Test
	void testAddNode1() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		map.addNode(node1);
		HashMap<Long, Node> nodeList = map.getNodeList();
		assert (nodeList.containsValue(node1));
	}

	@Test
	void testAddNode2() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(150, 120, 02);
		map.addNode(node1);
		map.addNode(node2);
		HashMap<Long, Node> nodeList = map.getNodeList();
		assert (nodeList.containsValue(node1) && nodeList.containsValue(node2));
	}

	@Test
	void testAddNode3() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(120, 150, 02);
		map.addNode(node1);
		map.addNode(node2);
		HashMap<Long, Node> nodeList = map.getNodeList();
		assert (nodeList.containsValue(node1) && nodeList.containsValue(node2));
	}

	@Test
	void testNearestNode() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(150, 130, 02);
		Node node3 = new Node(170, 180, 03);
		map.addNode(node1);
		map.addNode(node2);
		map.addNode(node3);
		assertEquals(node1, map.nearestNode(100, 100));
	}

	@Test
	void testNearestNode2() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(150, 120, 02);
		Node node3 = new Node(170, 180, 03);
		map.addNode(node1);
		map.addNode(node2);
		map.addNode(node3);
		assertEquals(node1, map.nearestNode(100, 100));
	}

	@Test
	void testAddSection1() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(150, 120, 02);
		Node node3 = new Node(170, 180, 03);
		map.addNode(node1);
		map.addNode(node2);
		map.addNode(node3);

		Section section1 = new Section("streetName", 0d, node1, node2);
		Section section2 = new Section("streetName", 0d, node2, node3);
		map.addSection(section1);
		map.addSection(section2);
		assertEquals(section2, map.getSection(node2, node3));
		// Pas existant
		assertNull(map.getSection(node3, node3));
	}

	@Test
	void testNearestSection() {
		Map map = new Map();
		Node node1 = new Node(120, 150, 01);
		Node node2 = new Node(150, 120, 02);
		Node node3 = new Node(170, 180, 03);
		map.addNode(node1);
		map.addNode(node2);
		map.addNode(node3);

		Section section1 = new Section("streetName", 0d, node1, node2);
		Section section2 = new Section("streetName", 0d, node2, node3);
		map.addSection(section1);
		map.addSection(section2);

		assertEquals(map.getNearestSection(170, 180), section2);

	}

	@Test
	void testDijkstra1() throws Exception {
		ModelInterface mi = new ModelInterface();
		Node node0 = new Node(120, 150, 01);
		Node node1 = new Node(150, 120, 02);
		Node node2 = new Node(170, 180, 03);

		mi.getMap().addNode(node0);
		mi.getMap().addNode(node1);
		mi.getMap().addNode(node2);

		mi.getMap().addSection(new Section("streetName", 1d, node0, node1));
		mi.getMap().addSection(new Section("streetName", 1d, node1, node0));

		mi.getMap().addSection(new Section("streetName", 2d, node1, node2));
		mi.getMap().addSection(new Section("streetName", 2d, node2, node1));

		mi.getMap().addSection(new Section("streetName", 10d, node0, node2));
		mi.getMap().addSection(new Section("streetName", 10d, node2, node0));

		mi.setStore(node0);
		mi.addDeliverySpot(new DeliverySpot(node1, 2));
		mi.addDeliverySpot(new DeliverySpot(node2, 2));

		Graph graph = new Graph(mi);
		mi.getMap().dijkstra(graph, mi, node0);
		mi.getMap().dijkstra(graph, mi, node1);
		mi.getMap().dijkstra(graph, mi, node2);
		
		double[][] cost = { { 0d, 1d, 3d }, { 1d, 0d, 2d }, { 3d, 2d, 0d } };
		double[] deliveryTimes = { 1d, 2d };
		HashMap<Node, Integer> nodeToIndex = new HashMap<Node, Integer>();
		nodeToIndex.put(node0, 0);
		nodeToIndex.put(node2, 2);
		nodeToIndex.put(node1, 1);
		Graph graph2 = new Graph(cost, deliveryTimes, nodeToIndex);

		boolean verify = true;
		loop: for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
              verify = graph.getCost()[i][j] == graph2.getCost()[i][j];
              if (!verify) break loop;
			}
		}
		
		assertTrue(verify);

	}
	
	//Exception
	@Test
	void testDijkstra2(){
		ModelInterface mi = new ModelInterface();
		Node node0 = new Node(120, 150, 01);
		Node node1 = new Node(150, 120, 02);
		Node node2 = new Node(170, 180, 03);
		Node node3 = new Node(270, 280, 03);
		Node node4 = new Node(170, 180, 03);

		mi.getMap().addNode(node0);
		mi.getMap().addNode(node1);
		mi.getMap().addNode(node2);
		mi.getMap().addNode(node4);

		mi.getMap().addSection(new Section("streetName", 1d, node0, node1));
		mi.getMap().addSection(new Section("streetName", 1d, node1, node0));

		mi.getMap().addSection(new Section("streetName", 2d, node1, node2));
		mi.getMap().addSection(new Section("streetName", 2d, node2, node1));

		mi.getMap().addSection(new Section("streetName", 10d, node0, node2));
		mi.getMap().addSection(new Section("streetName", 10d, node2, node0));
		
		mi.getMap().addSection(new Section("streetName", 1d, node0, node4));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node0));
		
		mi.getMap().addSection(new Section("streetName", 1d, node1, node4));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node1));

		mi.getMap().addSection(new Section("streetName", 1d, node2, node4));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node2));
		
		mi.setStore(node0);
		mi.addDeliverySpot(new DeliverySpot(node1, 2));
		mi.addDeliverySpot(new DeliverySpot(node2, 2));

		Graph graph = new Graph(mi);
		
		assertThrows(Exception.class,
	            ()->{
	            	mi.getMap().dijkstra(graph, mi, node3);
	            });
		
		assertThrows(Exception.class,
	            ()->{
	            	mi.getMap().dijkstra(graph, mi, null);
	            });

	}
	
	@Test
	void testCheckReachable()
	{
		ModelInterface mi = new ModelInterface();
		Node node0 = new Node(120, 150, 01);
		Node node1 = new Node(150, 120, 02);
		Node node2 = new Node(170, 180, 03);
		Node node3 = new Node(270, 280, 04);
		Node node4 = new Node(170, 180, 05);
		Node nodeBlock  = new Node(170, 180, 06);

		mi.getMap().addNode(node0);
		mi.getMap().addNode(node1);
		mi.getMap().addNode(node2);
		mi.getMap().addNode(node4);
		mi.getMap().addNode(nodeBlock);

		mi.getMap().addSection(new Section("streetName", 1d, node0, node1));
		mi.getMap().addSection(new Section("streetName", 1d, node1, node0));

		mi.getMap().addSection(new Section("streetName", 2d, node1, node2));
		mi.getMap().addSection(new Section("streetName", 2d, node2, node1));

		mi.getMap().addSection(new Section("streetName", 10d, node0, node2));
		mi.getMap().addSection(new Section("streetName", 10d, node2, node0));
		
		mi.getMap().addSection(new Section("streetName", 1d, node0, node4));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node0));
		
		mi.getMap().addSection(new Section("streetName", 1d, node1, node4));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node1));

		mi.getMap().addSection(new Section("streetName", 1d, node2, node3));
		mi.getMap().addSection(new Section("streetName", 1d, node4, node3));
		
		mi.setStore(node0);
		mi.addDeliverySpot(new DeliverySpot(node1, 2));
		mi.addDeliverySpot(new DeliverySpot(node2, 2));
		
		mi.getMap().checkReachable();
		
		
		assert(node0.isReachable());
		assert(!nodeBlock.isReachable());
	}
	
	//Si tous les noeuds destinations ne sont pas atteigable
	@Test
	void testCheckReachable2()
	{
		ModelInterface mi = new ModelInterface();
		Node node0 = new Node(120, 150, 01);
		Node node1 = new Node(150, 120, 02);
		Node nodeBlock  = new Node(170, 180, 06);

		mi.getMap().addNode(node0);
		mi.getMap().addNode(node1);
		mi.getMap().addNode(nodeBlock);

		mi.getMap().addSection(new Section("streetName", 1d, nodeBlock, node0));
		mi.getMap().addSection(new Section("streetName", 1d, nodeBlock, node1));

		mi.setStore(node0);
		mi.addDeliverySpot(new DeliverySpot(node1, 2));
		
		mi.getMap().checkReachable();
		
		
		assert(!nodeBlock.isReachable());
	}
	
}