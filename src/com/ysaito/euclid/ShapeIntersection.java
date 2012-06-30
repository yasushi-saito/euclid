package com.ysaito.euclid;

public class ShapeIntersection {
	public ShapeIntersection(float x, float y) {
		mSize = 1;
		mX1 = x;
		mY1 = y;
		mX2 = -1;
		mY2 = -1;
	}
	public ShapeIntersection(float x1, float y1, float x2, float y2) {
		mSize = 2;
		mX1 = x1;
		mY1 = y1;
		mX2 = y2;
		mY2 = y2;
	}
	
	public int size() { return mSize; }
	public float x(int index) { 
		if (index == 0) return mX1;
		return mX2;
	}
	public float y(int index) { 
		if (index == 0) return mY1;
		return mY2;
	}
	
	private final int mSize;
	private final float mX1, mY1;
	private final float mX2, mY2; // < 0 if unused
}
