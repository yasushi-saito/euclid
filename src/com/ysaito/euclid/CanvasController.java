package com.ysaito.euclid;

import java.util.LinkedList;
import java.util.Vector;

import android.util.Log;

public class CanvasController {
	final CanvasModel mModel;
	final CanvasView mView;

	static final int MOVE = 1;
	static final int DRAW_LINE = 2;
	static final int DRAW_CIRCLE = 3;
	int mMode;

	static private class PointAndDistance {
		Point point;
		Shape shape0, shape1;
		double distance;
		
		public PointAndDistance clone() {
			PointAndDistance p = new PointAndDistance();
			p.point = point;
			p.shape0 = shape0;
			p.shape1 = shape1;
			p.distance = distance;
			return p;
		}
	}

	PointAndDistance mStartPoint;
	PointAndDistance mCurrentPoint;
	
	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
		mMode = MOVE;
	}
	public void onTouchStart(float x, float y) {
		if (mMode == MOVE) {
			mStartPoint = findNearestUserDefinedPoint(x, y);
			if (mStartPoint != null) {
				mCurrentPoint = mStartPoint.clone();
			}
		} else if (mMode == DRAW_LINE || mMode == DRAW_CIRCLE) {
			mStartPoint = pickNearbyPoint(x, y);
			mCurrentPoint = mStartPoint.clone();
		}
		onTouchMove(x, y);
	}
	public void onTouchEnd(float x, float y) {
		if (mStartPoint == null) return;
		
		onTouchMove(x, y);
		if (mMode == DRAW_LINE || mMode == DRAW_CIRCLE) {
			mModel.addShape(mCurrentPoint.point);
			Point p0;
			if (mStartPoint.shape0 == null) {
				p0 = mStartPoint.point;
				mModel.addShape(mStartPoint.point);  // TODO: remove dup
			} else {
				// p0 = new DerivedPoint(mStartPoint.shape0, mStartPoint.shape1, x, y);
				p0 = mStartPoint.point;
				mStartPoint.shape0.addDependency(p0);
				mStartPoint.shape1.addDependency(p0);				
			}
			Point p1;
			if (mCurrentPoint.shape0 == null) {
				p1 = mCurrentPoint.point;
				mModel.addShape(mCurrentPoint.point);
			} else {
				p1 = mCurrentPoint.point;				
				// p1 = new DerivedPoint(mCurrentPoint.shape0, mCurrentPoint.shape1, x, y);
			}
			Shape shape = (mMode == DRAW_LINE) ? new Line(p0, p1) : new Circle(p0, p1);
			
			mModel.addShape(shape);
			if (p0 instanceof ExplicitPoint) {
				p0.addDependency(shape);
			}
			if (p1 instanceof ExplicitPoint) {
				p1.addDependency(shape);
			}
		}
		mStartPoint = null;
		mCurrentPoint = null;
		mModel.setTempShape(null);
	}
	
	static private void addDepsTo(Shape shape, LinkedList<Shape> deps) {
		Vector<Shape> d = shape.dependencies();
		if (d != null) {
			for (int i = 0; i < d.size(); ++i) {
				deps.addLast(d.get(i));
			}
		}
	}
	public void onTouchMove(float x, float y) {
		if (mCurrentPoint == null) return;
		if (mMode == MOVE) {
			((ExplicitPoint)mCurrentPoint.point).setTempLocation(x, y);
			if (mCurrentPoint.point.dependencies() != null) {
				LinkedList<Shape> queue = new LinkedList<Shape>();
				addDepsTo(mCurrentPoint.point, queue); 
				boolean ok = true;
				while (queue.size() > 0) {
					Shape shape = queue.getFirst();
					queue.removeFirst();
					if (!shape.prepareLocationUpdate()) {
						ok = false;
					}
					addDepsTo(shape, queue);
				}

				queue.addFirst(mCurrentPoint.point);
				while (queue.size() > 0) {
					Shape shape = queue.getFirst();
					queue.removeFirst();
					if (ok) {
						shape.commitLocationUpdate();
					} else {
						shape.abortLocationUpdate();
					}
					addDepsTo(shape, queue);
				}
			} else {
				mCurrentPoint.point.commitLocationUpdate();
			}
		} else  {
			mCurrentPoint = pickNearbyPoint(x, y);
			if (mMode == DRAW_LINE) {
				mModel.setTempShape(new Line(mStartPoint.point, mCurrentPoint.point));
			} else if (mMode == DRAW_CIRCLE) {
				mModel.setTempShape(new Circle(mStartPoint.point, mCurrentPoint.point));
			}
		}
		mView.redraw();
	}

	public void onModeChange(int mode) {
		mMode = mode;
	}

	public void onReset() {
		mModel.clear();
		mView.redraw();
	}
	
	static final float MAX_SNAP_DISTANCE = 40;

	private PointAndDistance pickNearbyPoint(float x, float y) {
		PointAndDistance nearestUserDefinedPoint = findNearestUserDefinedPoint(x, y);
		PointAndDistance nearestIntersection = findNearestIntersection(x, y);
		if (nearestUserDefinedPoint != null) {
			if (nearestIntersection == null || nearestUserDefinedPoint.distance <= nearestIntersection.distance) {
				return nearestUserDefinedPoint;
			}
		}
		if (nearestIntersection != null) {
			return nearestIntersection;
		}
		PointAndDistance pd = new PointAndDistance();
		pd.point = new ExplicitPoint(x,  y);
		return pd;
	}

	private final String TAG = "CanvasController";

	private PointAndDistance findNearestIntersection(double x, double y) {
		Vector<Shape> allShapes = mModel.shapes();
		Vector<Shape> nearbyShapes = null;
		int n = allShapes.size();
		for (int i = 0; i < n; ++i) {
			Shape shape = allShapes.get(i);
			double d = shape.distanceFrom(x, y);
			if (d < MAX_SNAP_DISTANCE) {
				if (nearbyShapes == null) nearbyShapes = new Vector<Shape>();
				nearbyShapes.add(shape);
			}
		}
		if (nearbyShapes == null) return null;
		n = nearbyShapes.size();

		PointAndDistance candidate = new PointAndDistance();
		candidate.distance = MAX_SNAP_DISTANCE;

		for (int i = 0; i < n; ++i) {
			Shape shape0 = nearbyShapes.get(i);
			for (int j = i + 1; j < n; ++j) {
				Shape shape1 = nearbyShapes.get(j);
				ShapeIntersection intersection = Shape.intersection(shape0, shape1);
				if (intersection != null) {
					double d = intersection.minDistanceFrom(x, y);
					// Log.d(TAG, "Intersection: " + sd1.shape1.toString() + "," + sd2.shape1.toString() + ": " + intersection.toString() + " distance=" + d);
					if (d < candidate.distance) {
						candidate.distance = d;
						candidate.shape0 = shape0;
						candidate.shape1 = shape1;
						candidate.point = new DerivedPoint(shape0, shape1, x, y);
					}
				}
			}
		}
		if (candidate.shape0 != null) {
			return candidate;
		}
		return null;
	}

	private PointAndDistance findNearestUserDefinedPoint(float x, float y) {
		Vector<Shape> shapes = mModel.shapes();
		ExplicitPoint candidate = null;
		final int n = shapes.size();
		double minDistance = MAX_SNAP_DISTANCE;
		
		for (int i = 0; i < n; ++i) {
			Shape shape = shapes.get(i);
			if (shape instanceof ExplicitPoint) {
				ExplicitPoint point = (ExplicitPoint)shape;
				double d = Util.distance(point.x(), point.y(), x, y);
				if (d < minDistance) {
					minDistance = d;
					candidate = point;
				}
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
