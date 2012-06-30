package com.ysaito.euclid;

import java.util.Vector;

public class CanvasModel {
	public CanvasModel() {
		mShapes = new Vector<Shape>();
		mUserDefinedPoints = new Vector<Point.Explicit>();
	}
	
	public void addShape(Shape s) {
		mShapes.add(s);
	}
	
	public void addUserDefinedPoint(Point.Explicit p) {
		mUserDefinedPoints.add(p);
	}
	
	public void setTempShape(Shape s) {
		mTempShape = s;
	}
	
	Vector<Shape> shapes() { return mShapes; }
	Shape tempShape() { return mTempShape; }
	Vector<Point.Explicit> userDefinedPoints() { return mUserDefinedPoints; }
	
	private final Vector<Shape> mShapes;
	private Shape mTempShape;
	private final Vector<Point.Explicit> mUserDefinedPoints;
}
