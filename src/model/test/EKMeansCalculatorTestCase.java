package model.test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.DeliverySpot;
import model.EKMeansCalculator;
import model.Node;

public class EKMeansCalculatorTestCase {
	
	private static EKMeansCalculator EKMc;
	private static ArrayList<DeliverySpot> deliverySpots = new ArrayList<DeliverySpot>();
	private static int nbTour;
	private static int idealCount;
	
	@BeforeAll
	public static void setUp(){
		initPoints();
	}
	
	@Test
	void testEquilibratedEven() {
		nbTour = 5;
		idealCount = deliverySpots.size()/nbTour;
		EKMc = new EKMeansCalculator(nbTour, deliverySpots);
		
		List<Integer>[] clusterList = null;
		try {
			EKMc.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Integer>[] c = EKMc.getClusterList();
		boolean one = ((c[0].size()-1 == idealCount) || (c[0].size()-1 == idealCount+1));
		boolean two = ((c[1].size()-1 == idealCount) || (c[1].size()-1 == idealCount+1));
		assert(one&two);
	}
	
	@Test
	void testEquilibratedOdd() {
		nbTour = 7;
		idealCount = deliverySpots.size()/nbTour;
		EKMc = new EKMeansCalculator(nbTour, deliverySpots);
		
		List<Integer>[] clusterList = null;
		try {
			EKMc.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Integer>[] c = EKMc.getClusterList();
		boolean one = ((c[0].size()-1 == idealCount) || (c[0].size()-1 == idealCount+1));
		boolean two = ((c[1].size()-1 == idealCount) || (c[1].size()-1 == idealCount+1));
		assert(one||two);
	}
	
    static double random(int t) {
        return Math.random() * t;
    }
    
    static ArrayList<DeliverySpot> initPoints() {
		int n = 1000;
		int t = 20000; 
        Point2D[] points = new Point2D[n];
        deliverySpots = new ArrayList<DeliverySpot>();
        
        for (int i = 0; i < n; ++i) {
            double x = random(t);
            double y = random(t);
            points[i] = new Point2D.Double(x, y);
            Node node = new Node(x,y,i);
            DeliverySpot ds = new DeliverySpot(node, 2);
            deliverySpots.add(ds);
        }
        
        return deliverySpots;
        
    }


}
