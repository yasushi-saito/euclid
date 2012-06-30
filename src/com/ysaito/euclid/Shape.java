package com.ysaito.euclid;

import android.util.Log;

public abstract class Shape {
	public abstract StateId stateId();
	public abstract float distanceFrom(float x, float y);
	
	static private ShapeIntersection lineCircleIntersection(Line line, Circle circle) {
		float radius = circle.radius();
		final float x1 = line.p1.x() - circle.center.x();
		final float y1 = line.p1.y() - circle.center.y();
		final float x2 = line.p2.x() - circle.center.x();
		final float y2 = line.p2.y() - circle.center.y();
		float dx = x1 - x2;
		float dy = y2 - y1;
		float dq = dx * dx + dy * dy;
		float det = x1 * y2 - x2 * y1;
		float disc = radius * radius * dq - det * det;
		if (disc < 0) return null;

		int sign = (dy < 0) ? -1 : 1;
		float xw = (float)(sign * dx * Math.sqrt(disc));
		float yw = (float)(Math.abs(dy) * Math.sqrt(disc));
		
		return new ShapeIntersection(
				circle.center.x() + det * dy + xw, 
				circle.center.y() + -det * dx + yw,
				circle.center.x() + det * dy - xw, 
				circle.center.y() + -det * dx - yw);
	}
	
	static private ShapeIntersection circleCircleIntersection(Circle c0, Circle c1) {
		/* Copied from http://local.wasp.uwa.edu.au/~pbourke/geometry/2circle/ */
		final float x0 = c0.center.x();
		final float y0 = c0.center.y();
		final float x1 = c1.center.x();	
		final float y1 = c1.center.y();	
		final float radius0 = c0.radius();
		final float radius1 = c1.radius();
		
		final float dx = x1 - x0;
		final float dy = y1 - y0;

		final float d = (float)Math.sqrt((dy*dy) + (dx*dx));

		if (d > (radius0 + radius1)) return null;  // no intersection
		if (d < Math.abs(radius0 - radius1)) return null; // one circle is inside the other

		/* Determine the distance from point 0 to point 2. */
		float a = ((radius0*radius0) - (radius1*radius1) + (d*d)) / (2 * d) ;

		/* Determine the coordinates of point 2. */
		float x2 = x0 + (dx * a/d);
		float y2 = y0 + (dy * a/d);

		/* Determine the distance from point 2 to either of the
		 * intersection points.
		 */
		float h = (float)Math.sqrt((radius0*radius0) - (a*a));

		/* Now determine the offsets of the intersection points from
		 * point 2.
		 */
		float rx = -dy * (h/d);
		float ry = dx * (h/d);

		/* Determine the absolute intersection points. */
		return new ShapeIntersection(x2 + rx, y2 + ry, x2 - rx, y2 - ry);
	}
	
	static private ShapeIntersection lineLineIntersection(Line line1, Line line2) {
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
			return null;
		} else {
			float iX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / det;
			float iY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / det;
			return new ShapeIntersection(iX, iY);
		}		
	}

	static public ShapeIntersection intersection(Shape shape1, Shape shape2) {
		if (shape1 instanceof Line) {
			if (shape2 instanceof Line) {
				return lineLineIntersection((Line)shape1, (Line)shape2);
			} else if (shape2 instanceof Circle) {
				return lineCircleIntersection((Line)shape1, (Circle)shape2);
			}
		} else if (shape1 instanceof Circle) {
			if (shape2 instanceof Line) {
				return lineCircleIntersection((Line)shape2, (Circle)shape1);
			} else if (shape2 instanceof Circle) {
				return circleCircleIntersection((Circle)shape1, (Circle)shape2);
			}
		}
		Log.wtf("Blah",  "Blah4");
		return null;
	}
	
	static public class Line extends Shape {
		public final Point p1, p2;
		public StateId mStateId;
		
		public Line(Point pp1, Point pp2) {
			p1 = pp1;
			p2 = pp2;
			mStateId = new StateId.NonLeaf(p1.stateId(), p2.stateId());
		}
		
		public StateId stateId() { return mStateId; }
		public float distanceFrom(float x, float y) {
			final float x1 = p1.x();
			final float y1 = p1.y();
			final float x2 = p2.x();
			final float y2 = p2.y();
			return (x2 - x1) * (y1 - y) - (x1 - x) * (y2 - y1) / Util.distance(x1, y1, x2, y2);
		}
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
		public float distanceFrom(float x, float y) {
			return Math.abs(Util.distance(center.x(), center.y(), x, y) - radius());
		}
	}
}
