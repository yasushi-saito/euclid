package com.ysaito.euclid;

import java.util.Vector;

import android.util.Log;

public class CanvasController {
	final CanvasModel mModel;
	final CanvasView mView;
	
	static final int MOVE = 1;
	static final int DRAW_LINE = 2;
	static final int DRAW_CIRCLE = 3;
	int mMode;
	
	Point.Explicit mCurrentPoint;
	Point mLineAnchor;
	Point mCircleAnchor;
	
	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
		mMode = MOVE;
	}
	public void onTouchStart(float x, float y) {
		if (mMode == MOVE) {
			PointAndDistance pd = findNearestUserDefinedPoint(x, y);
			if (pd != null) {
				mCurrentPoint = pd.point;
			}
		} else if (mMode == DRAW_LINE) {
			mLineAnchor = pickNearbyPoint(x, y);
			if (mLineAnchor == null) {
				mLineAnchor = new Point.Explicit(x,  y);
			}
			mCurrentPoint = new Point.Explicit(x,  y);
		} else if (mMode == DRAW_CIRCLE) {
			mCircleAnchor = pickNearbyPoint(x, y);
			if (mCircleAnchor == null) {
				mCircleAnchor = new Point.Explicit(x,  y);
			}
			mCurrentPoint = new Point.Explicit(x,  y);
		}
		onTouchMove(x, y);
	}
	public void onTouchEnd(float x, float y) {
		onTouchMove(x, y);
		if (mLineAnchor != null) {
			if (mLineAnchor instanceof Point.Explicit){
				mModel.addUserDefinedPoint((Point.Explicit)mLineAnchor);
			}
			mModel.addUserDefinedPoint(mCurrentPoint);
			mModel.addShape(new Shape.Line(mLineAnchor, mCurrentPoint));
		} else if (mCircleAnchor != null) {
			if (mCircleAnchor instanceof Point.Explicit){
				mModel.addUserDefinedPoint((Point.Explicit)mCircleAnchor);
			}
			mModel.addUserDefinedPoint(mCurrentPoint);
			mModel.addShape(new Shape.Circle(mCircleAnchor, mCurrentPoint));
		}
		mCurrentPoint = null;
		mLineAnchor = null;
		mCircleAnchor = null;
		mModel.setTempShape(null);
	}
	public void onTouchMove(float x, float y) {
		if (mCurrentPoint != null) {
			mCurrentPoint.moveTo(x, y);
			if (mLineAnchor != null) {
				mModel.setTempShape(new Shape.Line(mLineAnchor, mCurrentPoint));
			} else if (mCircleAnchor != null) {
				mModel.setTempShape(new Shape.Circle(mCircleAnchor, mCurrentPoint));
			}
		}
		mView.redraw();
	}
	
	public void onModeChange(int mode) {
		mMode = mode;
	}

	static final float MAX_SNAP_DISTANCE = 20;
	
	static private class ShapesAndDistance {
		Shape shape1, shape2;
		double distance;
	}
	
	static private class PointAndDistance {
		Point.Explicit point;
		double distance;
	}
	
	private Point pickNearbyPoint(float x, float y) {
		PointAndDistance nearestPoint = findNearestUserDefinedPoint(x, y);
		ShapesAndDistance nearestShapes = findNearestShapeIntersection(x, y);
		if (nearestPoint != null) {
			if (nearestShapes == null || nearestPoint.distance <= nearestShapes.distance) {
				return nearestPoint.point;
			}
		}
		if (nearestShapes != null) {
			return new Point.Derived(nearestShapes.shape1, nearestShapes.shape2, x, y);
		}
		return null;
	}
	
	private final String TAG = "CanvasController";
	
	private ShapesAndDistance findNearestShapeIntersection(double x, double y) {
		Vector<Shape> shapes = mModel.shapes();
		Vector<ShapesAndDistance> nearbyShapes = null;
		int n = shapes.size();
		for (int i = 0; i < n; ++i) {
			Shape shape = shapes.get(i);
			double d = shape.distanceFrom(x, y);
			if (d < MAX_SNAP_DISTANCE) {
				if (nearbyShapes == null) nearbyShapes = new Vector<ShapesAndDistance>();
				ShapesAndDistance sd = new ShapesAndDistance();
				sd.shape1 = shape; // shape2 is unused
				sd.distance = d;
				nearbyShapes.add(sd);
				Log.d(TAG, "Add: " + shape.toString() + " distance=" + d);
			}
		}
		if (nearbyShapes == null) return null;
		n = nearbyShapes.size();
		
		ShapesAndDistance candidate = new ShapesAndDistance();
		candidate.distance = MAX_SNAP_DISTANCE;
		
		for (int i = 0; i < n; ++i) {
			ShapesAndDistance sd1 = nearbyShapes.get(i);
			for (int j = i + 1; j < n; ++j) {
				ShapesAndDistance sd2 = nearbyShapes.get(j);
				ShapeIntersection intersection = Shape.intersection(sd1.shape1,  sd2.shape1);
				if (intersection == null) {
					// Log.d(TAG, "Intersection: " + sd1.shape1.toString() + "," + sd2.shape1.toString() + ": null");
				}
				if (intersection != null) {
					double d = intersection.minDistanceFrom(x, y);
					// Log.d(TAG, "Intersection: " + sd1.shape1.toString() + "," + sd2.shape1.toString() + ": " + intersection.toString() + " distance=" + d);
					if (d < candidate.distance) {
						candidate.distance = d;
						candidate.shape1 = sd1.shape1;
						candidate.shape2 = sd2.shape1;
					}
				}
			}
		}
		if (candidate.shape1 != null) {
			return candidate;
		}
		return null;
	}

	private PointAndDistance findNearestUserDefinedPoint(float x, float y) {
		Vector<Point.Explicit> points = mModel.userDefinedPoints();
		Point.Explicit candidate = null;
		final int n = points.size();
		double minDistance = MAX_SNAP_DISTANCE;
		for (int i = 0; i < n; ++i) {
			Point.Explicit point = points.get(i);
			double d = Util.distance(point.x(), point.y(), x, y);
			if (d < minDistance) {
				minDistance = d;
				candidate = point;
			}
		}
		if (candidate != null) {
			PointAndDistance pd = new PointAndDistance();
			pd.distance = minDistance;
			pd.point = candidate;
			return pd;
		}
		return null;
	}
}
