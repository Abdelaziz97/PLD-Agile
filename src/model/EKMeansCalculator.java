package model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cette classe permet de faire le clustering des points de livraisons  
 * @author Hexanome H4202
 * @version 1.0
 */
public class EKMeansCalculator extends Thread {
	
	protected  Point2D[] centroids;
    protected  Point2D[] points;

    protected  double[][] distances;
    protected  int[] assignments;
    protected  boolean[] changed;
    protected  int[] counts;
    protected  boolean[] done;
    protected int idealCount;
    
    private final int nbVertex;
    private final int nbTour;
    private final List<DeliverySpot> deliverySpots;
    private List<Integer>[]  clusterList;
    
    
    private final int nbIterMax;
    
    public EKMeansCalculator(int nbTour, List<DeliverySpot> deliverySpots) {
    	this.nbTour = nbTour;
    	this.deliverySpots = deliverySpots;
    	this.nbVertex = deliverySpots.size();
    	if(nbVertex > 0) {
    		this.idealCount = (nbVertex/nbTour);
    	}
    	this.nbIterMax= 100;
    }
    
	/**
	 * Méthode permettant de débuter le calcul dans le thread
	 */
    public void run() {
    	try {
			kmeans();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
	 * Méthode globale permettant de débuter le calcul
	 */
    public void kmeans () throws InterruptedException {
    	long chrono1 = java.lang.System.currentTimeMillis() ; 
    	calculateKMeans(deliverySpots);
    	if( ! isKMeansBalanced()) {
    		recalculateKMeans(deliverySpots);
    	}
        clusterList = buildClusters();
        long chrono2 = java.lang.System.currentTimeMillis() ; 
        long temps = chrono2 - chrono1 ; 
        System.out.println("Temps ecoule pour KMeans = " + temps + " ms") ; 
    }
    
    
    public List<Integer>[] getClusterList() {
    	return this.clusterList;
    }
    
    /**
	 * Méthode permettant d'initialiser les clusters
	 */
    private void initClustering() {
        centroids = new Point2D[nbTour];
        if(nbTour <= points.length) {
        	for (int i = 0; i < nbTour; ++i) {
        		centroids[i] = points[i];
        	}
        }
    }
    
    /**
	 * Méthode permettant de réaliser un clustering des points
	 * de livraisons de l'algorithme de calcul KMeans
	 * @param deliverySpots : Liste des points de livraisons que l'on souhaite clusteriser
	 */
    private void calculateKMeans(List<DeliverySpot> deliverySpots) {
    	initPoints(deliverySpots);
        initClustering();
        initDistances();
        
        calculateDistances();
        int move = makeAssignments();
        int counter= 0;
        while (move > 0 && counter++ < nbIterMax) {
            if (points.length >= centroids.length) {
                move = fillEmptyCentroids();
            }
            moveCentroids();
            calculateDistances();
            move += makeAssignments();
        }
    }
    
    private void recalculateKMeans(List<DeliverySpot> deliverySpots) {
    	idealCount++;
    	calculateKMeans(deliverySpots);
    }
    
    /**
	 * Méthode permettant de déterminer si les clusters formés sont équilibrés
	 * @return VRAI si les clusters sont équilibrés (de tailles égales à + ou - 1 près), sinon faux
	 */
    private boolean isKMeansBalanced() {
    	boolean correctSize = true;
    	for (int i = 0; i < counts.length ; ++i) {
    		if(((nbVertex % nbTour != 0) && (counts [i] > (idealCount+1)))
    				|| ((nbVertex % nbTour == 0) && ((counts[i])> (idealCount)))) {
    			correctSize = false;
    			break;
    		}
    		System.out.println(i + " "+ counts[i]);
    	}
    	return correctSize;
    }
    
    /**
	 * Méthode permettant de construire les clusters suite aux calculs
	 * @return Tableau de tournées contenant une liste de points contenus dans chaques clusters
	 */
    private List<Integer>[] buildClusters() {
        List<Integer>[] clusterList = new List[nbTour];
       
        for  (int i = 0; i < nbTour; ++i ) {
        	clusterList[i]= new ArrayList<Integer>();
        	clusterList[i].add(0);
        }
        
        for  (int i = 0; i < points.length; ++i ) {
        	int cluster = assignments[i];
        	clusterList[cluster].add(i+1);
        }
        return clusterList;
    }
    
    /**
	 * Méthode permettant d'initialiser les points de livraisons
	 * @param deliverySpots
	 */
    private void initPoints(List<DeliverySpot> deliverySpots) {
    	points = new Point2D[nbVertex];
    	int i = 0;
    	for(DeliverySpot ds: deliverySpots) {
    		double x = ds.getAddress().getLatitude();
    		double y = ds.getAddress().getLongitude();
    		points[i] = new Point2D.Double(x,y);
    		i++;
    	}
    }
    
    /**
	 * Initialise les tableaux intermédiaires permettants de réaliser 
	 * les calculs pour l'algorithme KMeans
	 */
    private void initDistances() {
    	distances = new double[nbTour][nbVertex];
    	assignments = new int[nbVertex];
    	Arrays.fill(assignments, -1);
    	changed = new boolean[nbTour];
        Arrays.fill(changed, true);
        counts = new int[nbTour];
        done = new boolean[nbTour];
    }
    
    /**
     * Méthode permettant de calculer les distances de chaque point
     * de livraison à chaque centroid
	 */
    protected void calculateDistances() {
        for (int c = 0; c < centroids.length; c++) {
            if (!changed[c]) continue;
            Point2D centroid = centroids[c];
            for (int p = 0; p < points.length; p++) {
            	Point2D point = points[p];
                distances[c][p] = centroid.distance(point);
            }
        }
        Arrays.fill(changed, false);
    }
    
    /**
     * Méthode permettant d'assigner à chaque itération un
     * point à un cluster puis à recalculer les centroids du
     * cluster
	 */
    protected int makeAssignments() {
        int move = 0;
        Arrays.fill(counts, 0);
        for (int p = 0; p < points.length; p++) {
            int nc = nearestCentroid(p);
            if (nc == -1) {
                continue;
            }
            if (assignments[p] != nc) {
                if (assignments[p] != -1) {
                    changed[assignments[p]] = true;
                }
                changed[nc] = true;
                assignments[p] = nc;
                move++;
            }
            counts[nc]++;
            if ( counts[nc] > idealCount) {
                move += remakeAssignments(nc);
            }
        }
        return move;
    }
    
    /**
     * Méthode permettant de réassigner  des points à un cluster dans le cas
     * ou la distribution précédente était déséquilibrée
     * @param cc : cluster pour lequel il faudra ajouter des points de livraison
     * @return le nombre de mouvement réalisé pour rééquilibrer la taille des clusters
	 */
    protected int remakeAssignments(int cc) {
        int move = 0;
        double md = Double.POSITIVE_INFINITY;
        int nc = -1;
        int np = -1;
        for (int p = 0; p < points.length; p++) {
            if (assignments[p] != cc) {
                continue;
            }
            for (int c = 0; c < centroids.length; c++) {
                if (c == cc || done[c]) {
                    continue;
                }
                double d = distances[c][p];
                if (d < md) {
                    md = d;
                    nc = c;
                    np = p;
                }
            }
        }
        if (nc != -1 && np != -1) {
            if (assignments[np] != nc) {
                if (assignments[np] != -1) {
                    changed[assignments[np]] = true;
                }
                changed[nc] = true;
                assignments[np] = nc;
                move++;
            }
            counts[cc]--;
            counts[nc]++;
            if (counts[nc] > idealCount) {
                done[cc] = true;
                move += remakeAssignments(nc);
                done[cc] = false;
            }
        }
        return move;
    }
    
    /**
     * Méthode permettant de déterminer le centroid le plus proche pour un point donné
     * @param pt : point pour lequel on souhaite connaitre le centroid le plus proche
     * @return l'indice du centroid le plus proche
	 */
    private int nearestCentroid(int pt) {
        double distance = Double.POSITIVE_INFINITY;
        int nearestCent = -1;
        for (int c = 0; c < centroids.length; c++) {
            double d = distances[c][pt];
            if (d < distance) {
            	distance = d;
                nearestCent = c;
            }
        }
        return nearestCent;
    }
    
    protected int nearestPoint(int inc, int fromc) {
        double md = Double.POSITIVE_INFINITY;
        int np = -1;
        for (int p = 0; p < points.length; p++) {
            if (assignments[p] != inc) {
                continue;
            }
            double d = distances[fromc][p];
            if (d < md) {
                md = d;
                np = p;
            }
        }
        return np;
    }

    /**
     * Méthode permettant de déterminer le cluster le plus grand, excepté le cluster dont
     * l'indice est donné en paramètre 
     * @param except: indice du cluster qu'on ne souhaite pas considérer
     * @return l'indice du cluster le plus grand
	 */
    protected int largestCentroid(int except) {
        int lc = -1;
        int mc = 0;
        for (int c = 0; c < centroids.length; c++) {
            if (c == except) {
                continue;
            }
            if (counts[c] > mc) {
                lc = c;
            }
        }
        return lc;
    }

    /**
     * Méthode permettant de remplir les clusters qui sont vides
     * @return le nombre de mouvements réalisés pour équilibrer les clusters
	 */
    protected int fillEmptyCentroids() {
        int move = 0;
        for (int c = 0; c < centroids.length; c++) {
            if (counts[c] == 0) {
                int lc = largestCentroid(c);
                int np = nearestPoint(lc, c);
                assignments[np] = c;
                counts[c]++;
                counts[lc]--;
                changed[c] = true;
                changed[lc] = true;
                move++;
            }
        }
        return move;
    }
    
    /**
     * Méthode recalculer les coordonées des centroids des clusters après avoir
     * assigné les points associés
	 */
    protected void moveCentroids() {
    	for (int c = 0; c < centroids.length; c++) {
            if (!changed[c]) continue;
            Point2D centroid = centroids[c];
            int n = 0;
            for (int p = 0; p < points.length; p++) {
                if (assignments[p] != c) continue;
                Point2D point = points[p];
                if (n++ == 0) centroid = new Point2D.Double(0,0);
                double x = centroid.getX() + point.getX();
                double y = centroid.getY() + point.getY();
                centroid = new Point2D.Double(x,y);
                
            }
            if (n > 0) {
                double x = centroid.getX()/n;
                double y = centroid.getY()/n;
                centroid = new Point2D.Double(x,y);
                centroids[c] = centroid;
            }
            
        }
    }
    
}
