package com.ysaito.euclid;

public class Line extends Shape {
	public final Point p0, p1;
		
	public Line(Point pp1, Point pp2) {
		p0 = pp1;
		p1 = pp2;
	}

	@Override public boolean prepareLocationUpdate() {
		return true;
	}
	
	@Override public void commitLocationUpdate() {
	}
	
	@Override public void abortLocationUpdate() {
	}
	
	@Override public double distanceFrom(double x, double y) {
		final double x0 = p0.x();
		final double y0 = p0.y();
		final double x1 = p1.x();
		final double y1 = p1.y();
		return Math.abs(((x1 - x0) * (y0 - y) - (x0 - x) * (y1 - y0)) / Util.distance(x0, y0, x1, y1));
	}
	@Override public String toString() {
		return "Point " + p0.toString() + "-" + p1.toString();
	}
}
