package com.ysaito.euclid;

import android.util.Log;

public class DerivedPoint extends Point {
	private final int mN;
	public final Shape shape0, shape1;
	
	public double mX, mY;
	public double mTempX, mTempY;
	public int mNumPendingTxns;
	
	public DerivedPoint(Shape s0, Shape s1, double nearX, double nearY) {
		shape0 = s0;
		shape1 = s1;
		ShapeIntersection intersection = Shape.intersection(shape0, shape1);
		
		double minDistance = 99999.0;
		int candidate = -1;
		for (int i = 0; i < intersection.size(); ++i) {
			final double d = Util.distance(intersection.x(i), intersection.y(i), nearX, nearY);
			if (i == 0 || d < minDistance) {
				candidate = i;
				minDistance = d;
			}
		}
		mN = candidate;
		mX = intersection.x(mN);
		mY = intersection.y(mN);
		mNumPendingTxns = 0;
	}
		
	private static String TAG = "DerivedPoint";
	private int mLastTransactionId = -1;
	
	@Override public boolean prepareLocationUpdate(int txnId) {
		//Util.assertTrue(shape0.lastTransactionId() == txnId);
		//Util.assertTrue(shape1.lastTransactionId() == txnId);
		// Util.assertTrue(txnId > mLastTransactionId && mNumPendingTxns == 0, toString());
		mLastTransactionId = txnId;
		mNumPendingTxns++;
		ShapeIntersection intersection = Shape.intersection(shape0, shape1);
		if (intersection == null) return false;
		mTempX = intersection.x(mN);
		mTempY = intersection.y(mN);				
		return true;
	}

	@Override public void commitLocationUpdate(int txnId) {
		if (Util.debugMode) {
			Util.assertTrue(mNumPendingTxns > 0, toString());
			Util.assertTrue(txnId == mLastTransactionId, toString());
		}
		if (--mNumPendingTxns == 0) {
			if (Util.debugMode) {
				ShapeIntersection intersection = Shape.intersection(shape0, shape1);
				Util.assertTrue(intersection != null, toString());
				Util.assertNear(mTempX, intersection.x(mN), 3.0, toString());
				Util.assertNear(mTempY, intersection.y(mN), 3.0, toString());				
			}
			mX = mTempX;
			mY = mTempY;
		}
	}
	
	@Override public void abortLocationUpdate(int txnId) {
		if (Util.debugMode) {
			Util.assertTrue(mNumPendingTxns > 0, toString());
			Util.assertTrue(txnId == mLastTransactionId, toString());			
		}
		--mNumPendingTxns;
	}
	
	@Override public int lastTransactionId() { return mLastTransactionId; }
	
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
		StringBuilder b = new StringBuilder("DerivedPoint ");
		b.append(super.toString());
		b.append("(x=<");
		b.append(shape0.id());
		b.append(",");
		b.append(x());
		b.append(">,y=<");
		b.append(shape1.id());
		b.append(",");
		b.append(y());
		b.append(">)");
		return b.toString();
	}
}	

