package com.ysaito.euclid;

import java.util.Vector;

public class Circle extends Shape {
	public Point center, radiusControl;
		
	public Circle(Point pCenter, Point pRadius) {
		center = pCenter;
		radiusControl = pRadius;
	}
		
	@Override public boolean prepareLocationUpdate() {
		return true;
	}
	
	@Override public void commitLocationUpdate() {
	}
	
	@Override public void abortLocationUpdate() {
	}
	
	public double radius() {
		final double dx = center.x() - radiusControl.x();
		final double dy = center.y() - radiusControl.y();
		return Math.sqrt(dx * dx + dy * dy);
	}
		
	@Override public double distanceFrom(double x, double y) {
		return Math.abs(Util.distance(center.x(), center.y(), x, y) - radius());
	}
	@Override public String toString() {
		return "Circle center=" + center.toString() + " radius=" + radiusControl.toString();
	}
}
