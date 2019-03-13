package model.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import model.ModelInterface;
import xml.DeserializerXML;


public class test_xml {

	@Test
	void testNegative() {
		ModelInterface mi = new ModelInterface();
		assertThrows(Exception.class,
	            ()->{
	            	DeserializerXML.loadMap(mi);
	            	DeserializerXML.loadDeliverySpots(mi);
	            });
	}
	
	void testNotExist() {
		ModelInterface mi = new ModelInterface();
		assertThrows(Exception.class,
	            ()->{
	            	DeserializerXML.loadMap(mi);
	            	DeserializerXML.loadDeliverySpots(mi);
	            });
	}
	
	void testNull() {
		ModelInterface mi = new ModelInterface();
		assertThrows(Exception.class,
	            ()->{
	            	DeserializerXML.loadMap(mi);
	            	DeserializerXML.loadDeliverySpots(mi);
	            });
	}
	
	void testNormal() {
		ModelInterface mi = new ModelInterface();
		try {
			DeserializerXML.loadMap(mi);
			DeserializerXML.loadDeliverySpots(mi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(mi.getMap().getNodeList().size()>0);
	}
	
}
