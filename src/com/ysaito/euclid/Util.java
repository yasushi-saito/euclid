package com.ysaito.euclid;

import android.util.Log;

public class Util {
	/** Compute planar distance between two points (x0, y0) and (x1, y1) */
	public static double distance(double x0, double y0, double x1, double y1) {
		final double dx = x0 - x1;
		final double dy = y0 - y1;
		return (double)Math.sqrt(dx * dx + dy * dy);
	}
	
	static Integer nullint;
	static final boolean debugMode = false;
	static private final String TAG = "Util";

	public static void assertTrue(boolean value, String message) {
		if (!value) {
			Log.wtf(TAG, "assertion failed: " + message);
			nullint += 10;  // will crash the process
		}
	}
	
	public static void assertFalse(boolean value, String message) {
		assertTrue(!value, message);
	}
	
	public static void assertNear(double v0, double v1, double maxDelta, String message) {
		message += ": Value=" + v0 + "," + v1;
		assertTrue(v0 >= v1 - maxDelta, message);
		assertTrue(v0 <= v1 + maxDelta, message);
	}
}
