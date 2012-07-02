package com.ysaito.euclid;

public class ShapeIntersection {
	public ShapeIntersection(double x, double y) {
		mSize = 1;
		mX1 = x;
		mY1 = y;
		mX2 = -1;
		mY2 = -1;
	}
	public ShapeIntersection(double x0, double y0, double x1, double y1) {
		mSize = 2;
		mX1 = x0;
		mY1 = y0;
		mX2 = x1;
		mY2 = y1;
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
		double d1 = Util.distance(x, y, mX1, mY1);
		double d2 = (mSize > 1) ? Util.distance(x, y, mX2, mY2) : 9999;
		return Math.min(d1, d2);
	}
	
	public int size() { return mSize; }
	public double x(int index) { 
		if (index == 0) return mX1;
		return mX2;
	}
	public double y(int index) { 
		if (index == 0) return mY1;
		return mY2;
	}
	
	private final int mSize;
	private final double mX1, mY1;
	private final double mX2, mY2; // < 0 if unused
}
