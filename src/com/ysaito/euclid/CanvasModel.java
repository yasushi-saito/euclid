package com.ysaito.euclid;

import java.util.HashSet;
import java.util.Vector;

import android.util.Log;

public class CanvasModel {
	private final String TAG = "CanvasModel";
	public CanvasModel() {
		mShapes = new Vector<Shape>();
	}
	
	public final void addShape(Shape s) {
		// Log.d(TAG, "AddShape:" + s.toString());
		if (Util.debugMode) Util.assertFalse(mShapes.contains(s), "Shape: " + s.toString());
		mShapes.add(s);
	}
	
	public final void removeShape(Shape s) {
		boolean removed = mShapes.remove(s);
		if (Util.debugMode) Util.assertTrue(removed, s.toString());
	}
	
	public final void setTempShape(Shape s) {
		mTempShape = s;
	}
	
	public final void clear() {
		mShapes.clear();
		mTempShape = null;
	}
	
	public final void checkInvariants() {
		HashSet<Shape> done = new HashSet<Shape>();
	}
	
	Vector<Shape> shapes() { return mShapes; }
	Shape tempShape() { return mTempShape; }
	
	private final Vector<Shape> mShapes;
	private Shape mTempShape;
}
