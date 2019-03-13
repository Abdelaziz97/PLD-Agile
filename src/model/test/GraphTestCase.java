package model.test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.Graph;
import model.Map;
import model.ModelInterface;
import model.Node;
import model.Section;

public class GraphTestCase {
	
	private static ModelInterface mi;
	private static Graph graph;
	private static Node node0;
	
	@BeforeAll
	public static void setup() {
		
		mi = new ModelInterface();
		node0 = new Node(120, 150, 01);
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

		graph = new Graph(mi);
	}
	
	@Test
	void testAddDeliverySpot() {
		Node node = new Node(120d,110d,5);
		graph.addNode(node);
		graph.addNode(node0);
		Integer i = graph.getIndexFromDeliverySpot(node);
		assert(i != null);
	}
	
	@Test
	void testRetourNull() {
		Node nodeNull = graph.getDeliverySpotFromIndex(999999);
		assert(nodeNull == null);
	}
}
	
	

