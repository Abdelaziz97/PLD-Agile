package model.test;

import org.junit.jupiter.api.Test;

import model.Node;
import model.Path;
import model.Section;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

public class PathTestCase {

	@Test
	void testGetPath() {
		Node node1 = new Node(150,130,1);
		Node node2 = new Node(120,120,2);
		Node node3 = new Node(140,110,3);
		Section section1 = new Section("street1",10d,node1,node2);
		Section section2 = new Section("street2",8d,node2,node3);
		LinkedList<Section> sectionsList = new LinkedList<Section>();
		sectionsList.add(section1);
		sectionsList.add(section2);
		Path path = new Path(sectionsList, 18d);
		double duration = 18d/250;
		boolean verify = path.getDuration() == duration;
		verify &= path.sectionList().get(0).equals(section1);
		verify &= path.sectionList().get(1).equals(section2);
		assertTrue (verify);
	}
	
}
