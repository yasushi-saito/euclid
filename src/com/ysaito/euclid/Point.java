package com.ysaito.euclid;

public abstract class Point {
	public abstract double x();
	public abstract double y();
	public abstract StateId stateId();
	
	static public class Explicit extends Point {
		private double mX, mY;
		private StateId.Leaf mStateId;
		
		public Explicit(double x, double y) {
			mX = x;
			mY = y;
			mStateId = new StateId.Leaf();
		}
		
		public double x() { return mX; }
		public double y() { return mY; }
		public void moveTo(double x, double y) {
			mX = x;
			mY = y;
			mStateId.updated();
		}
		public StateId stateId() { return mStateId; }
	}
	
	static public class Derived extends Point {
		private int mN;
		public final Shape shape0, shape1;
		public StateId.NonLeaf mStateId;
		public double mX, mY;
		
		public Derived(Shape s0, Shape s1, double nearX, double nearY) {
			shape0 = s0;
			shape1 = s1;
			mStateId = new StateId.NonLeaf(s0.stateId(), s1.stateId());
			mStateId.maybeUpdateId();
			mN = 0;
		}
		
		private void maybeRefreshState() {
			if (mStateId.maybeUpdateId()) {
				ShapeIntersection intersection = Shape.intersection(shape0, shape1);
				mX = intersection.x(mN);
				mY = intersection.y(mN);				
			}
		}
		public double x() {
			maybeRefreshState();
			return mX;
		}
		
		public double y() {
			maybeRefreshState();
			return mY;
		}
		public StateId stateId() { return mStateId; }
	}
}

