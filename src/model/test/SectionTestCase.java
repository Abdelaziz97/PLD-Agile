package model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.Node;
import model.Section;

public class SectionTestCase {

	@Test
	void testAddSection() {
		Section section = new Section("street", 12.3, new Node(120, 150, 01), new Node(150, 120, 02));
		Section section2 = new Section("street1", 12.3, new Node(120, 150, 01), new Node(150, 120, 02));
		Section section3 = new Section("street1", 12.3, new Node(150, 120, 02), new Node(120, 150, 01));
		assertTrue(section.equals(section2));
		assertFalse(section.equals(section3));
		assertFalse(section.equals(new Node(120, 150, 01)));
	}
	
	@Test
	void testGetSection() {
		Node nodeSrc = new Node(120, 150, 01);
		Node nodeDest = new Node(150, 120, 02);
		Section section = new Section("street", 12.3, nodeSrc, nodeDest);
		boolean verify = section.getStreetName().equals("street");
		verify &= section.getDestination().equals(nodeDest);
		verify &= section.getSource().equals(nodeSrc);
		String tostring = "Section [streetName="+"street"+", length="+12.3+", source="+nodeSrc+", destination=" +nodeDest+"]";
		verify &= section.toString().equals(tostring);
		assertTrue(verify);
	}
	
	
}
