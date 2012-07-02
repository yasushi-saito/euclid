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
		
		@Override public double x() { return mX; }
		@Override public double y() { return mY; }
		public void moveTo(double x, double y) {
			mX = x;
			mY = y;
			mStateId.updated();
		}
		@Override public StateId stateId() { return mStateId; }
		@Override public String toString() {
			return "explicit(" + mX + "," + mY + ")";
		}
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
			mN = 0;
			ShapeIntersection intersection = Shape.intersection(shape0, shape1);
			mX = intersection.x(mN);
			mY = intersection.y(mN);				
		}
		
		private void maybeRefreshState() {
			if (mStateId.maybeUpdateId()) {
				ShapeIntersection intersection = Shape.intersection(shape0, shape1);
				mX = intersection.x(mN);
				mY = intersection.y(mN);				
			}
		}
		@Override public double x() {
			maybeRefreshState();
			return mX;
		}
		
		@Override public double y() {
			maybeRefreshState();
			return mY;
		}
		@Override public StateId stateId() { return mStateId; }
		
		@Override public String toString() {
			return "derived(" + x() + "," + y() + ")";
		}
		
	}
}

