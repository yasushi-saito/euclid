package com.ysaito.euclid;

public abstract class Point {
	public abstract float x();
	public abstract float y();
	public abstract StateId stateId();
	
	static public class Explicit extends Point {
		private float mX, mY;
		private StateId.Leaf mStateId;
		
		public Explicit(float x, float y) {
			mX = x;
			mY = y;
			mStateId = new StateId.Leaf();
		}
		
		public float x() { return mX; }
		public float y() { return mY; }
		public void moveTo(float x, float y) {
			mX = x;
			mY = y;
			mStateId.updated();
		}
		public StateId stateId() { return mStateId; }
	}
	
	static public class Derived extends Point {
		public final Shape shape0, shape1;
		public StateId.NonLeaf mStateId;
		public float mX, mY;
		
		public Derived(Shape s0, Shape s1) {
			shape0 = s0;
			shape1 = s1;
			mStateId = new StateId.NonLeaf(s0.stateId(), s1.stateId());
		}
		
		private void maybeRefreshState() {
			if (mStateId.maybeUpdateId()) {
				ShapeIntersection intersection = Shape.intersection(shape0, shape1);
			}
		}
		public float x() {
			maybeRefreshState();
			return mX;
		}
		
		public float y() {
			maybeRefreshState();
			return mY;
		}
		public StateId stateId() { return mStateId; }
	}
}

