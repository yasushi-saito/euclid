package com.ysaito.euclid;

import java.util.Vector;

public class CanvasModel {
	public CanvasModel() {
		mShapes = new Vector<Shape>();
	}
	
	public void addShape(Shape s) {
		mShapes.add(s);
	}
	
	Vector<Shape> shapes() { return mShapes; } 
	private final Vector<Shape> mShapes;
}
