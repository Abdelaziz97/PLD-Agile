package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;

public class Util {
	
	private static final int COLOR_MIN_DIFF = 80;

	public static String parseTime(String time) {
		String[] splitedTime = time.split(":");

		if (splitedTime[0].length() == 1) {
			String temp = "0" + splitedTime[0];
			splitedTime[0] = temp;
		}
		if (splitedTime[1].length() == 1) {
			String temp = "0" + splitedTime[1];
			splitedTime[1] = temp;
		}
		if (splitedTime[2].length() == 1) {
			String temp = "0" + splitedTime[2];
			splitedTime[2] = temp;
		}

		return splitedTime[0] + ":" + splitedTime[1] + ":" + splitedTime[2];
	}

	// Compute the dot product AB . BC
	private static double DotProduct(double[] pointA, double[] pointB, double[] pointC) {
		double[] AB = new double[2];
		double[] BC = new double[2];
		AB[0] = pointB[0] - pointA[0];
		AB[1] = pointB[1] - pointA[1];
		BC[0] = pointC[0] - pointB[0];
		BC[1] = pointC[1] - pointB[1];
		double dot = AB[0] * BC[0] + AB[1] * BC[1];

		return dot;
	}

	// Compute the cross product AB x AC
	private static double CrossProduct(double[] pointA, double[] pointB, double[] pointC) {
		double[] AB = new double[2];
		double[] AC = new double[2];
		AB[0] = pointB[0] - pointA[0];
		AB[1] = pointB[1] - pointA[1];
		AC[0] = pointC[0] - pointA[0];
		AC[1] = pointC[1] - pointA[1];
		double cross = AB[0] * AC[1] - AB[1] * AC[0];

		return cross;
	}

	// Compute the distance from A to B
	private static double Distance(double[] pointA, double[] pointB) {
		double d1 = pointA[0] - pointB[0];
		double d2 = pointA[1] - pointB[1];

		return Math.sqrt(d1 * d1 + d2 * d2);
	}

	// Compute the distance from AB to C
	// if isSegment is true, AB is a segment, not a line.
	public static double LineToPointDistance2D(double[] pointA, double[] pointB, double[] pointC, boolean isSegment) {
		double dist = CrossProduct(pointA, pointB, pointC) / Distance(pointA, pointB);
		if (isSegment) {
			double dot1 = DotProduct(pointA, pointB, pointC);
			if (dot1 > 0)
				return Distance(pointB, pointC);

			double dot2 = DotProduct(pointB, pointA, pointC);
			if (dot2 > 0)
				return Distance(pointA, pointC);
		}
		return Math.abs(dist);
	}
	
	public static void newRandomColors(ArrayList<Color> colors, int nbColors) {
		colors.clear();
		colors.add(new Color(0.8f, 0.8f, 0.8f));
		colors.add(new Color(1f, 1f, 1f));
		for (int i = 0; i < nbColors; ++i) {
			newRandomColor(colors);
		}
		colors.remove(0);
		colors.remove(0);
	}

	public static void newRandomColor(ArrayList<Color> colors) {
		Color c = null;
		do {
			c = new Color((int) (Math.random() * 0x1000000));
		} while (check(c, colors) == false);
		colors.add(c);
	}

	public static boolean check(Color color, ArrayList<Color> colors) {
		for (int i = 0; i < colors.size(); ++i) {
			double euclideanDistance = Math.sqrt(Math.pow(color.getGreen() - colors.get(i).getGreen(), 2)
					+ Math.pow(color.getBlue() - colors.get(i).getBlue(), 2)
					+ Math.pow(color.getRed() - colors.get(i).getRed(), 2));
			if (euclideanDistance < COLOR_MIN_DIFF) {
				return false;
			}
		}
		return true;
	}
	
	public static Calendar dateDiff(Calendar date1, Calendar date2) {
		long stamp1 = date1.getTimeInMillis();
		long stamp2 = date2.getTimeInMillis();
		long diff = Math.abs(stamp1 - stamp2);
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis(diff);
		return result;
	}
}
