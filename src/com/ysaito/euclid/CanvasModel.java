package com.ysaito.euclid;

import java.util.Vector;

import android.util.Log;

public class CanvasModel {
	private final String TAG = "CanvasModel";
	public CanvasModel() {
		mShapes = new Vector<Shape>();
	}
	
	public void addShape(Shape s) {
		Log.d(TAG, "AddShape:" + s.toString());
		mShapes.add(s);
	}
	
	public void setTempShape(Shape s) {
		mTempShape = s;
	}
	
	Vector<Shape> shapes() { return mShapes; }
	Shape tempShape() { return mTempShape; }
	
	private final Vector<Shape> mShapes;
	private Shape mTempShape;
}
