package com.ysaito.euclid;

import android.util.Log;

public class DerivedPoint extends Point {
	private int mN;
	public final Shape shape0, shape1;
	
	public double mX, mY;
	public double mTempX, mTempY;
	public int mNumPendingTxns;
	
	public DerivedPoint(Shape s0, Shape s1, double nearX, double nearY) {
		shape0 = s0;
		shape1 = s1;
		mN = 0;
		ShapeIntersection intersection = Shape.intersection(shape0, shape1);
		mX = intersection.x(mN);
		mY = intersection.y(mN);
		mNumPendingTxns = 0;
	}
		
	private static String TAG = "DerivedPoint";
	
	@Override public boolean prepareLocationUpdate() {
		mNumPendingTxns++;
		if (mNumPendingTxns == 1) {
			ShapeIntersection intersection = Shape.intersection(shape0, shape1);
			if (intersection == null) return false;
			mTempX = intersection.x(mN);
			mTempY = intersection.y(mN);				
			return true;
		} else {
			// If the previous invocation returned false, the whole txn will abort anyway.
			// so it's safe to return true always here.
			return true;
		}
	}

	@Override public void commitLocationUpdate() {
		Util.assertTrue(mNumPendingTxns > 0);
		mX = mTempX;
		mY = mTempY;
		--mNumPendingTxns;
	}
	
	@Override public void abortLocationUpdate() {
		Util.assertTrue(mNumPendingTxns > 0);
		--mNumPendingTxns;
	}
	
	@Override public double distanceFrom(double px, double py) {
		return Util.distance(px, py, x(), y());
	}
	
	@Override public double x() {
		return (mNumPendingTxns > 0 ? mTempX : mX);
	}
	
	@Override public double y() {
		return (mNumPendingTxns > 0 ? mTempY : mY);
	}
	
	@Override public String toString() {
		return "derived(" + x() + "," + y() + ")";
	}
}	

