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
	
	public void setTempLocation(double x, double y) {
		mTempX = x;
		mTempY = y;
		mHasTempLocation = true;
	}
	
	@Override public boolean prepareLocationUpdate() {
		Log.wtf(TAG, "??? ExplicitPoint");
		return false;
	}

	@Override public void commitLocationUpdate() {
		Util.assertTrue(mHasTempLocation);
		mX = mTempX;
		mY = mTempY;
		mHasTempLocation = false;
	}
	
	@Override public void abortLocationUpdate() {
		Util.assertTrue(mHasTempLocation);
		mHasTempLocation = false;
	}
	
	@Override public String toString() {
		return "explicit(" + mX + "," + mY + ")";
	}
}
