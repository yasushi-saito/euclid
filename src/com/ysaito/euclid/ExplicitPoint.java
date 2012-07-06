package com.ysaito.euclid;

import android.util.Log;

public class ExplicitPoint extends Point {
	private final String TAG = "ExplicitPoint";
	private double mX, mY;
	
	private boolean mHasTempLocation; 
	private double mTempX, mTempY;
	
	public ExplicitPoint(double x, double y) {
		mX = x;
		mY = y;
		mHasTempLocation = false;
	}
	
	@Override public double distanceFrom(double px, double py) {
		return Util.distance(px, py, x(), y());
	}
	
	@Override public double x() { return (mHasTempLocation ? mTempX : mX); }
	@Override public double y() { return (mHasTempLocation ? mTempY : mY); }
	
	public final void setTempLocation(int txnId, double x, double y) {
		mTempX = x;
		mTempY = y;
		mHasTempLocation = true;
		mLastTransactionId = txnId;
	}
	
	@Override public void commitLocationUpdate(int txnId) {
		if (Util.debugMode) {
			Util.assertTrue(mHasTempLocation, toString());
			Util.assertTrue(txnId == mLastTransactionId, toString());
		}
		mX = mTempX;
		mY = mTempY;
		mHasTempLocation = false;
	}
	
	@Override public void abortLocationUpdate(int txnId) {
		if (Util.debugMode) {
			Util.assertTrue(mHasTempLocation, toString());
			Util.assertTrue(txnId == mLastTransactionId, toString());
		}
		mHasTempLocation = false;
	}
	
	private int mLastTransactionId;
	
	@Override public boolean prepareLocationUpdate(int txnId) {
		if (Util.debugMode) Util.assertFalse(true, toString());
		return true;
	}
	@Override public int lastTransactionId() { return mLastTransactionId; }
	
	
	@Override public String toString() {
		StringBuilder b = new StringBuilder("ExplicitPoint ");
		b.append(super.toString());
		b.append("(");
		b.append(mX);
		b.append(",");
		b.append(mY);
		b.append(")");
		return b.toString();
	}
}
