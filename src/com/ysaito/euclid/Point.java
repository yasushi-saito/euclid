package com.ysaito.euclid;

public abstract class Point {
	public abstract float x();
	public abstract float y();
	public abstract StateId stateId();
	
	static public float distanceFrom(Point p, float x, float y) {
		final float dx = p.x() - x;
		final float dy = p.y() - y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
	
	static public class UserDefined extends Point {
		private float mX, mY;
		private StateId.Leaf mStateId;
		
		public UserDefined(float x, float y) {
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
	
	static public class LineLineIntersection extends Point {
		public Shape.Line line1, line2;
		public StateId.NonLeaf mStateId;
		public float mX, mY;
		
		public LineLineIntersection(Shape.Line p1, Shape.Line p2) {
			line1 = p1;
			line2 = p2;
			mStateId = new StateId.NonLeaf(p1.stateId(), p2.stateId());
		}
		
		private void maybeRefreshState() {
			if (mStateId.maybeUpdateId()) {
				final float x1 = line1.p1.x();
				final float y1 = line1.p1.y();
				final float x2 = line1.p2.x();
				final float y2 = line1.p2.y();
			
				final float x3 = line2.p1.x();
				final float y3 = line2.p1.y();			
				final float x4 = line2.p2.x();
				final float y4 = line2.p2.y();			
				
				float det = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
				if (Math.abs(det) < 0.001) {
					mX = mY = -1;
				} else {
					mX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / det;
					mY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))/det;
				}
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

