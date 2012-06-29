package com.ysaito.euclid;

import com.ysaito.euclid.StateId.NonLeaf;

public abstract class Shape {
	public abstract StateId stateId();
	
	static public class Line extends Shape {
		public final Point p1, p2;
		public StateId mStateId;
		
		public Line(Point pp1, Point pp2) {
			p1 = pp1;
			p2 = pp2;
			mStateId = new StateId.NonLeaf(p1.stateId(), p2.stateId());
		}
		
		public StateId stateId() { return mStateId; }
	}
	
	static public class Circle extends Shape {
		public Point center, radiusControl;
		public StateId mStateId;
		public Circle(Point pCenter, Point pRadius) {
			center = pCenter;
			radiusControl = pRadius;
			mStateId = new StateId.NonLeaf(center.stateId(), radiusControl.stateId());
		}
		
		public float radius() {
			final float dx = center.x() - radiusControl.x();
			final float dy = center.y() - radiusControl.y();
			return (float)Math.sqrt(dx * dx + dy * dy);
		}
		
		public StateId stateId() { return mStateId; }
	}
}
