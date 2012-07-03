package com.ysaito.euclid;

public class ShapeIntersection {
	public ShapeIntersection(double x, double y) {
		mSize = 1;
		mX0 = x;
		mY0 = y;
		mX1 = -1;
		mY1 = -1;
	}
	public ShapeIntersection(double x0, double y0, double x1, double y1) {
		mSize = 2;
		mX0 = x0;
		mY0 = y0;
		mX1 = x1;
		mY1 = y1;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < size(); ++i) {
			b.append("(");
			b.append(x(i));
			b.append(",");
			b.append(y(i));
			b.append(")");
		}
		return b.toString();
	}
	
	public double minDistanceFrom(double x, double y) {
		double d = Util.distance(x, y, mX0, mY0);
		if (mSize > 1) {
			double d2 = Util.distance(x, y, mX1, mY1);
			if (d2 < d) d = d2;
		}
		return d;
	}
	
	public int size() { return mSize; }
	public double x(int index) { 
		if (index == 0) return mX0;
		return mX1;
	}
	public double y(int index) { 
		if (index == 0) return mY0;
		return mY1;
	}
	
	private final int mSize;
	private final double mX0, mY0;
	private final double mX1, mY1; // < 0 if unused
}
