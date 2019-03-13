package model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.Node;


public class DeliverySpotTestCase {

	@Test
	void testGetAddress() {
		Node node = new Node(120,130,1);
		DeliverySpot deliverySpot = new DeliverySpot(node,10);
		assertEquals(node , deliverySpot.getAddress());
	}
	
	@Test
	void testGetUnloadingTime() {
		DeliverySpot deliverySpot = new DeliverySpot(new Node(120,130,1),10);
		assertEquals(10 , deliverySpot.getUnloadingTime());
	}
	
	@Test
	void testToString() {
		Node node = new Node(120,130,1);
		DeliverySpot deliverySpot = new DeliverySpot(node,10);
		assertEquals("DeliverySpot [address=" + node + ", unloadingTime=" + 10 + "]" , deliverySpot.toString());
	}
	
}
