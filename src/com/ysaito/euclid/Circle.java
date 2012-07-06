package com.ysaito.euclid;

import java.util.Vector;

public class Circle extends Shape {
	public Point center, radiusControl;
		
	public Circle(Point pCenter, Point pRadius) {
		center = pCenter;
		radiusControl = pRadius;
	}
	
	private int mLastTransactionId;
	
	@Override public void commitLocationUpdate(int txnId) {
		if (Util.debugMode) Util.assertTrue(txnId == mLastTransactionId, toString());
	}
	
	@Override public void abortLocationUpdate(int txnId) {
		if (Util.debugMode) Util.assertTrue(txnId == mLastTransactionId, toString());
	}
	@Override public boolean prepareLocationUpdate(int txnId) {
		//Util.assertTrue(txnId == center.lastTransactionId());
		//Util.assertTrue(txnId == radiusControl.lastTransactionId());		
		mLastTransactionId = txnId;
		return true;
	}
	@Override public int lastTransactionId() { return mLastTransactionId; }
	
	public double radius() {
		final double dx = center.x() - radiusControl.x();
		final double dy = center.y() - radiusControl.y();
		return Math.sqrt(dx * dx + dy * dy);
	}
		
	@Override public double distanceFrom(double x, double y) {
		return Math.abs(Util.distance(center.x(), center.y(), x, y) - radius());
	}
	@Override public String toString() {
		StringBuilder b = new StringBuilder("Circle ");
		b.append(super.toString());
		b.append(" center=");
		b.append(center.toString());
		b.append(" radius=");
		b.append(radiusControl.toString());
		return b.toString();
	}
}
