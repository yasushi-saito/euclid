package com.ysaito.euclid.test;

import com.ysaito.euclid.Point;
import com.ysaito.euclid.Shape;
import com.ysaito.euclid.ShapeIntersection;

import android.test.AndroidTestCase;

public class ShapeTest extends AndroidTestCase {
	private Shape.Line newLine(double x0, double y0, double x1, double y1) {
		return new Shape.Line(new Point.Explicit(x0, y0), new Point.Explicit(x1, y1));
	}
	
	private Shape.Circle newCircle(double x, double y, double radius) {
		return new Shape.Circle(new Point.Explicit(x,  y), new Point.Explicit(x + radius, y));
	}
	
	private void assertNear(double expected, double actual) {
		String message = "expected=" + expected + ", d2=" + actual;
		assertTrue(message, actual >= expected - 0.001);
		assertTrue(message, actual <= expected + 0.001);
	}
	
	private void assertNear(ShapeIntersection si, int index, double x, double y) {
		String message = si.toString() + " expected=(" + x + "," + y + ")";
		assertTrue(message, si.x(index) >= x - 0.001);
		assertTrue(message, si.x(index) <= x + 0.001);		
		assertTrue(message, si.y(index) >= y - 0.001);
		assertTrue(message, si.y(index) <= y + 0.001);		
	}
	
	public void testLineLineIntersection() {
			Shape s1 = newLine(0, 0, 1, 1);
			ShapeIntersection si = Shape.intersection(s1, newLine(0, 0, 1, 2));
			assertEquals(1, si.size());
			assertEquals(0.0, si.x(0));
			assertEquals(0.0, si.y(0));
			
			si = Shape.intersection(s1, newLine(1, 0, 0, 1));
			assertEquals(0.5, si.x(0));
			assertEquals(0.5, si.y(0));
	}
	
	public void testLineCircleIntersection() {
		Shape s1 = newCircle(1, 1, 1);
		ShapeIntersection si = Shape.intersection(s1, newLine(0, 0, 1, 1));
		assertEquals(2, si.size());
		assertNear(si, 0, 1 + 1.0 / Math.sqrt(2), 1 + 1.0 / Math.sqrt(2));
		assertNear(si, 1, 1 - 1.0 / Math.sqrt(2), 1 - 1.0 / Math.sqrt(2));
	}
	
	public void testCircleCircleIntersection() {
		Shape s1 = newCircle(1, 1, 1);
		ShapeIntersection si = Shape.intersection(s1, newCircle(2, 1, 1));
		assertEquals(2, si.size());
		assertNear(si, 0, 1.5, 1 + Math.sqrt(0.75));
		assertNear(si, 1, 1.5, 1 - Math.sqrt(0.75));

		si = Shape.intersection(s1, newCircle(1, 2, 1));
		assertEquals(2, si.size());
		assertNear(si, 1, 1 + Math.sqrt(0.75), 1.5);
		assertNear(si, 0, 1 - Math.sqrt(0.75), 1.5);
		
		assertNull(Shape.intersection(s1, newCircle(3, 1, 0.95)));
	}
	
	public void testLineDistance() {
		Shape s1 = newLine(0, 0, 1, 1);
		assertNear(0.0, s1.distanceFrom(0.5,  0.5));
		assertNear(0.0, s1.distanceFrom(50.0,  50.0));		
		assertNear(1.0 / Math.sqrt(2.0), s1.distanceFrom(1.0, 0.0));
		assertNear(1.0 / Math.sqrt(2.0), s1.distanceFrom(0.0, 1.0));		
	}
	
	public void testCircleDistance() {
		Shape s1 = newCircle(1, 1, 1);
		assertNear(1.0, s1.distanceFrom(1, 1));
		assertNear(0.5, s1.distanceFrom(0.5, 1));
	}
	
}
