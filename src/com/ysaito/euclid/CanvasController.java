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

	PointAndDistance mStartPoint;
	ExplicitPoint mCurrentPoint;
	Shape mStartPointDep0, mStartPointDep1;
	
	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
		mMode = MOVE;
	}
	public void onTouchStart(float x, float y) {
		if (mMode == MOVE) {
			mStartPoint = findNearestUserDefinedPoint(x, y);
			if (mStartPoint != null) {
				mCurrentPoint = mStartPoint.point;
			}
		} else if (mMode == DRAW_LINE) {
			mStartPoint = pickNearbyPoint(x, y);
			if (mStartPoint == null) {
				mStartPoint = new PointAndDistance();
				mStartPoint.point = new ExplicitPoint(x,  y);
			}
			mCurrentPoint = new ExplicitPoint(x,  y);
		} else if (mMode == DRAW_CIRCLE) {
			mStartPoint = pickNearbyPoint(x, y);
			if (mStartPoint == null) {
				mStartPoint = new PointAndDistance();
				mStartPoint.point = new ExplicitPoint(x,  y);
			}
			mCurrentPoint = new ExplicitPoint(x,  y);
		}
		onTouchMove(x, y);
	}
	public void onTouchEnd(float x, float y) {
		if (mStartPoint == null) return;
		
		onTouchMove(x, y);
		if (mMode == DRAW_LINE) {
			mModel.addShape(mCurrentPoint);
			Point p0;
			if (mStartPoint.shape0 == null) {
				p0 = mStartPoint.point;
				mModel.addShape(mStartPoint.point);  // TODO: remove dup
			} else {
				p0 = new DerivedPoint(mStartPoint.shape0, mStartPoint.shape1, x, y);
				mStartPoint.shape0.addDependency(p0);
				mStartPoint.shape1.addDependency(p0);				
			}
			Line line = new Line(p0, mCurrentPoint); 
			mModel.addShape(line);
			if (p0 instanceof ExplicitPoint) {
				p0.addDependency(line);
			}
			mCurrentPoint.addDependency(line);
		} else if (mMode == DRAW_CIRCLE) {
			Point p0;
			if (mStartPoint.shape0 == null) {
				p0 = mStartPoint.point;
				mModel.addShape(p0);
			} else {
				p0 = new DerivedPoint(mStartPoint.shape0, mStartPoint.shape1, x, y);
				mStartPoint.shape0.addDependency(p0);
				mStartPoint.shape1.addDependency(p0);				
			}
			mModel.addShape(mCurrentPoint);
			Circle circle = new Circle(p0, mCurrentPoint);
			mModel.addShape(circle);
			if (p0 instanceof ExplicitPoint) {
				p0.addDependency(circle);
			}
			mCurrentPoint.addDependency(circle);
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
		if (mCurrentPoint != null) {
			mCurrentPoint.setTempLocation(x, y);
			if (mCurrentPoint.dependencies() != null) {
				LinkedList<Shape> queue = new LinkedList<Shape>();
				addDepsTo(mCurrentPoint, queue); 
				boolean ok = true;
				while (queue.size() > 0) {
					Shape shape = queue.getFirst();
					queue.removeFirst();
					if (!shape.prepareLocationUpdate()) {
						ok = false;
					}
					addDepsTo(shape, queue);
				}

				queue.addFirst(mCurrentPoint);
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
				mCurrentPoint.commitLocationUpdate();
			}
			if (mMode == DRAW_LINE) {
				mModel.setTempShape(new Line(mStartPoint.point, mCurrentPoint));
			} else if (mMode == DRAW_CIRCLE) {
				mModel.setTempShape(new Circle(mStartPoint.point, mCurrentPoint));
			}
		}
		mView.redraw();
	}

	public void onModeChange(int mode) {
		mMode = mode;
	}

	static final float MAX_SNAP_DISTANCE = 20;

	static private class PointAndDistance {
		ExplicitPoint point;
		Shape shape0, shape1;
		double distance;
	}

	private PointAndDistance pickNearbyPoint(float x, float y) {
		PointAndDistance nearestUserDefinedPoint = findNearestUserDefinedPoint(x, y);
		PointAndDistance nearestIntersection = findNearestIntersection(x, y);
		if (nearestUserDefinedPoint != null) {
			if (nearestIntersection == null || nearestUserDefinedPoint.distance <= nearestIntersection.distance) {
				return nearestUserDefinedPoint;
			}
		}
		return nearestIntersection;
	}

	private final String TAG = "CanvasController";

	private PointAndDistance findNearestIntersection(double x, double y) {
		Vector<Shape> shapes = mModel.shapes();
		Vector<PointAndDistance> nearbyShapes = null;
		int n = shapes.size();
		for (int i = 0; i < n; ++i) {
			Shape shape = shapes.get(i);
			double d = shape.distanceFrom(x, y);
			if (d < MAX_SNAP_DISTANCE) {
				if (nearbyShapes == null) nearbyShapes = new Vector<PointAndDistance>();
				PointAndDistance pd = new PointAndDistance();
				pd.shape0 = shape; // point and shape1 are unused
				pd.distance = d;
				nearbyShapes.add(pd);
			}
		}
		if (nearbyShapes == null) return null;
		n = nearbyShapes.size();

		PointAndDistance candidate = new PointAndDistance();
		candidate.distance = MAX_SNAP_DISTANCE;

		for (int i = 0; i < n; ++i) {
			PointAndDistance pd1 = nearbyShapes.get(i);
			for (int j = i + 1; j < n; ++j) {
				PointAndDistance pd2 = nearbyShapes.get(j);
				ShapeIntersection intersection = Shape.intersection(pd1.shape0,  pd2.shape0);
				if (intersection != null) {
					double d = intersection.minDistanceFrom(x, y);
					// Log.d(TAG, "Intersection: " + sd1.shape1.toString() + "," + sd2.shape1.toString() + ": " + intersection.toString() + " distance=" + d);
					if (d < candidate.distance) {
						candidate.distance = d;
						candidate.shape0 = pd1.shape0;
						candidate.shape1 = pd2.shape0;
						candidate.point = new ExplicitPoint(intersection.x(0), intersection.y(0));
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
