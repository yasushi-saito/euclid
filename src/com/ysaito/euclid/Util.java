package com.ysaito.euclid;

public class Util {
	/** Compute planar distance between two points (x0, y0) and (x1, y1) */
	public static double distance(double x0, double y0, double x1, double y1) {
		final double dx = x0 - x1;
		final double dy = y0 - y1;
		return (double)Math.sqrt(dx * dx + dy * dy);
	}
}
