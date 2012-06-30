package com.ysaito.euclid;

public class Util {
	/** Compute planar distance between two points (x1, y1) and (x2, y2) */
	public static float distance(float x1, float y1, float x2, float y2) {
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
}
