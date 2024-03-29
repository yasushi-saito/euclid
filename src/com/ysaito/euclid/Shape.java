package com.ysaito.euclid;

import java.util.Vector;

import android.util.Log;

public abstract class Shape {
	// public abstract StateId stateId();
	public abstract double distanceFrom(double x, double y);
	public abstract boolean prepareLocationUpdate(int txnId);
	public abstract void commitLocationUpdate(int txnId);
	public abstract void abortLocationUpdate(int txnId);
	public abstract int lastTransactionId();
	
	private Vector<Shape> mDeps;
	private final int mId;
	private static int mNextId = 0;

	public Shape() {
		mDeps = null;
		mId = mNextId++;
	}

	public final int id() { 
		return mId; 
	}
	
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("id=");
		b.append(mId);
		if (mDeps != null) {
			b.append(" deps={");
			for (int i = 0; i < mDeps.size(); ++i) {
				if (i > 0) b.append(",");
				b.append(mDeps.get(i).id());
			}
			b.append("}");
		}
		return b.toString();
	}
	public final void removeDependency(Shape child) {
		boolean removed = mDeps.remove(child);
		if (Util.debugMode) Util.assertTrue(removed, toString());
	}
	
	public final void addDependency(Shape child) {
		if (mDeps == null) mDeps = new Vector<Shape>();
		if (Util.debugMode) {
			Util.assertFalse(mDeps.contains(child), child.toString());
		}
		mDeps.add(child);
	}
	
	static Vector<Shape> emptyDeps;
	public final Vector<Shape> dependencies() {
		if (mDeps != null) return mDeps;
		if (emptyDeps == null) emptyDeps = new Vector<Shape>();
		return emptyDeps;
	}
	
	
	static private ShapeIntersection lineCircleIntersection(Line line, Circle circle) {
		final double radius = circle.radius();
		final double x0 = line.p0.x() - circle.center.x();
		final double y0 = line.p0.y() - circle.center.y();
		final double x1 = line.p1.x() - circle.center.x();
		final double y1 = line.p1.y() - circle.center.y();
		double dx = x1 - x0;
		double dy = y1 - y0;
		double dq = dx * dx + dy * dy;
		double det = x0 * y1 - x1 * y0;
		double disc = radius * radius * dq - det * det;
		if (disc < 0) return null;

		int sign = (dy < 0) ? -1 : 1;
		double xw = sign * dx * Math.sqrt(disc);
		double yw = Math.abs(dy) * Math.sqrt(disc);
		
		return new ShapeIntersection(
				circle.center.x() + (det * dy + xw) / dq, 
				circle.center.y() + (-det * dx + yw) / dq,
				circle.center.x() + (det * dy - xw) / dq, 
				circle.center.y() + (-det * dx - yw) / dq);
	}
	
	static private ShapeIntersection circleCircleIntersection(Circle c0, Circle c1) {
		/* Copied from http://local.wasp.uwa.edu.au/~pbourke/geometry/2circle/ */
		final double x0 = c0.center.x();
		final double y0 = c0.center.y();
		final double x1 = c1.center.x();	
		final double y1 = c1.center.y();	
		final double radius0 = c0.radius();
		final double radius1 = c1.radius();
		
		final double dx = x1 - x0;
		final double dy = y1 - y0;

		final double d = (double)Math.sqrt((dy*dy) + (dx*dx));

		if (d > (radius0 + radius1)) return null;  // no intersection
		if (d < Math.abs(radius0 - radius1)) return null; // one circle is inside the other

		/* Determine the distance from point 0 to point 2. */
		double a = ((radius0*radius0) - (radius1*radius1) + (d*d)) / (2 * d) ;

		/* Determine the coordinates of point 2. */
		double x2 = x0 + (dx * a/d);
		double y2 = y0 + (dy * a/d);

		/* Determine the distance from point 2 to either of the
		 * intersection points.
		 */
		double h = (double)Math.sqrt((radius0*radius0) - (a*a));

		/* Now determine the offsets of the intersection points from
		 * point 2.
		 */
		double rx = -dy * (h/d);
		double ry = dx * (h/d);

		/* Determine the absolute intersection points. */
		return new ShapeIntersection(x2 + rx, y2 + ry, x2 - rx, y2 - ry);
	}
	
	static private ShapeIntersection lineLineIntersection(Line lineA, Line lineB) {
		final double xa0 = lineA.p0.x();
		final double ya0 = lineA.p0.y();
		final double xa1 = lineA.p1.x();
		final double ya1 = lineA.p1.y();
		
		final double xb0 = lineB.p0.x();
		final double yb0 = lineB.p0.y();			
		final double xb1 = lineB.p1.x();
		final double yb1 = lineB.p1.y();			
		
		double det = (xa0-xa1)*(yb0-yb1) - (ya0-ya1)*(xb0-xb1);
		if (Math.abs(det) < 0.001) {
			return null;
		} else {
			double iX = ((xa0 * ya1 - ya0 * xa1) * (xb0 - xb1) - (xa0 - xa1) * (xb0 * yb1 - yb0 * xb1)) / det;
			double iY = ((xa0 * ya1 - ya0 * xa1) * (yb0 - yb1) - (ya0 - ya1) * (xb0 * yb1 - yb0 * xb1)) / det;
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
		if (Util.debugMode) Util.assertFalse(true, "shape1=" + shape1.toString() + " shape2=" + shape2.toString());
		return null;
	}
}
