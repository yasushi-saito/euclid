package com.ysaito.euclid;

public class Line extends Shape {
	public final Point p0, p1;
		
	public Line(Point pp1, Point pp2) {
		p0 = pp1;
		p1 = pp2;
	}

	private int mLastTransactionId;
	
	@Override public void commitLocationUpdate(int txnId) {
		Util.assertTrue(txnId == mLastTransactionId, toString());
	}
	
	@Override public void abortLocationUpdate(int txnId) {
		Util.assertTrue(txnId == mLastTransactionId, toString());
	}
	@Override public boolean prepareLocationUpdate(int txnId) {
		//Util.assertTrue(txnId == p0.lastTransactionId());
		//Util.assertTrue(txnId == p1.lastTransactionId());		
		mLastTransactionId = txnId;
		return true;
	}
	@Override public int lastTransactionId() { return mLastTransactionId; }
	
	@Override public double distanceFrom(double x, double y) {
		final double x0 = p0.x();
		final double y0 = p0.y();
		final double x1 = p1.x();
		final double y1 = p1.y();
		return Math.abs(((x1 - x0) * (y0 - y) - (x0 - x) * (y1 - y0)) / Util.distance(x0, y0, x1, y1));
	}
	
	@Override public String toString() {
		StringBuilder b = new StringBuilder("Line ");
		b.append(super.toString());
		b.append(" p0={");
		b.append(p0.toString());
		b.append("} p1={");
		b.append(p1.toString());
		b.append("}");
		return b.toString();
	}
}
