package model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import model.Node;
import model.Section;

class NodeTestCase {

	@Test
	void testAddSection1() {
		Node nodeSrc = new Node(120, 150, 01);
		Node nodeDest = new Node(150, 120, 02);
		Section section = new Section("street", 12.3, nodeSrc, nodeDest);
		nodeSrc.addSection(section);
		HashSet<Section> sectionsList = nodeSrc.getSectionsList();
		assert(sectionsList.contains(section));
	}
	
	@Test
	void testAddSection2() {
		Node nodeSrc = new Node(120, 150, 01);
		Section section = new Section("street", 12.3, nodeSrc, nodeSrc);
		nodeSrc.addSection(section);
		HashSet<Section> sectionsList = nodeSrc.getSectionsList();
		assert(sectionsList.contains(section));
	}
	
	@Test
	void testAddSection3() {
		Node nodeSrc = new Node(120, 150, 01);
		Node nodeDest = new Node(150, 120, 02);
		Node nodeDest2 = new Node (125, 125, 03);
		Section section = new Section("street", 12.3, nodeSrc, nodeDest);
		Section section2 = new Section("street1", 10, nodeSrc, nodeDest2);
		nodeSrc.addSection(section);
		nodeSrc.addSection(section2);
		HashSet<Section> sectionsList = nodeSrc.getSectionsList();
		assert(sectionsList.contains(section)&&sectionsList.contains(section2));
	}
	

	

}
