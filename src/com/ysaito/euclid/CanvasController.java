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

	private Vector<UndoEntry> mUndos;
	PointAndDistance mStartPoint;
	PointAndDistance mCurrentPoint;
	UndoEntry mCurrentUndo;
	
	static private abstract class UndoEntry {
		public abstract void apply(CanvasModel model);
	}
	
	static private class UndoMoveEntry extends UndoEntry {
		public UndoMoveEntry(ExplicitPoint p) {
			point = p;
			oldX = p.x();
			oldY = p.y();
		}
		@Override public void apply(CanvasModel model) {
			tryMovePoint(point, oldX, oldY);
		}
		
		public final ExplicitPoint point;
		public final double oldX, oldY; 
	}
	
	static private class UndoDrawShapeEntry extends UndoEntry {
		UndoDrawShapeEntry() { 
			mShapes = null;
			mDeps = null;
		}
		@Override public void apply(CanvasModel model) {
			for (int i = mDeps.size() - 1; i >= 0; --i) {
				Dep d = mDeps.get(i);
				d.parent.removeDependency(d.child);
			}
			for (int i = mShapes.size() - 1; i >= 0; --i) {
				model.removeShape(mShapes.get(i));
			}
		}
		public final void addShapeToRemove(Shape s) {
			if (mShapes == null) mShapes = new Vector<Shape>();
			mShapes.add(s);
		}
		public final void addDepToRemove(Shape parent, Shape child) {
			if (mDeps == null) mDeps = new Vector<Dep>();
			Dep d = new Dep();
			d.parent = parent;
			d.child = child;
			mDeps.add(d);
		}
		static private class Dep {
			Shape parent, child;
		}
		private Vector<Shape> mShapes;
		private Vector<Dep> mDeps;	
	}
	
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

	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
		mMode = MOVE;
		mUndos = new Vector<UndoEntry>();
	}
	
	public void onTouchStart(float x, float y) {
		if (mMode == MOVE) {
			mCurrentPoint = findNearestUserDefinedPoint(x, y);
			if (mCurrentPoint != null) {
				mCurrentUndo = new UndoMoveEntry((ExplicitPoint)mCurrentPoint.point);
				if (false) {
					Log.d(TAG, "Move: " + mCurrentPoint.point.toString());
					Vector<Shape> shapes = mModel.shapes();
					for (int i = 0; i < shapes.size(); ++i) {
						Log.d(TAG, "Shape " + i + ": " + shapes.get(i).toString());
					}
				}
			}
		} else if (mMode == DRAW_LINE || mMode == DRAW_CIRCLE) {
			mStartPoint = pickNearbyPoint(x, y);
			mCurrentPoint = mStartPoint.clone();
			// mCurrentUndo is filled later
		}
		onTouchMove(x, y);
	}
	public void onTouchEnd(float x, float y) {
		if (mCurrentPoint == null) return;
		
		onTouchMove(x, y);
		if (mMode == MOVE) {
			mUndos.add(mCurrentUndo);
		} else if (mMode == DRAW_LINE || mMode == DRAW_CIRCLE) {
			UndoDrawShapeEntry u = new UndoDrawShapeEntry(); // the fields are filled later
			Point p0;
			if (mStartPoint.shape0 == null) {
				p0 = mStartPoint.point;
				if (!mModel.shapes().contains(p0)) mModel.addShape(p0);
				u.addShapeToRemove(p0);
			} else {
				p0 = mStartPoint.point;
				mStartPoint.shape0.addDependency(p0);
				mStartPoint.shape1.addDependency(p0);				
				u.addDepToRemove(mStartPoint.shape0, p0);				
				u.addDepToRemove(mStartPoint.shape1, p0);								
			}
			Point p1;
			if (mCurrentPoint.shape0 == null) {
				p1 = mCurrentPoint.point;
				if (!mModel.shapes().contains(p1)) mModel.addShape(p1);
				u.addShapeToRemove(p1);				
			} else {
				p1 = mCurrentPoint.point;				
				mCurrentPoint.shape0.addDependency(p1);
				mCurrentPoint.shape1.addDependency(p1);				
				u.addDepToRemove(mCurrentPoint.shape0, p1);				
				u.addDepToRemove(mCurrentPoint.shape1, p1);								
			}
			Shape shape = (mMode == DRAW_LINE) ? new Line(p0, p1) : new Circle(p0, p1);
			
			mModel.addShape(shape);
			u.addShapeToRemove(shape);
			if (true || p0 instanceof ExplicitPoint) {
				p0.addDependency(shape);
				u.addDepToRemove(p0, shape);
			}
			if (true || p1 instanceof ExplicitPoint) {
				p1.addDependency(shape);
				u.addDepToRemove(p1, shape);
			}
			mUndos.add(u);
		}
		mStartPoint = null;
		mCurrentPoint = null;
		mModel.setTempShape(null);
	}
	
	static private void addDepsTo(Shape shape, LinkedList<Shape> deps) {
		Vector<Shape> d = shape.dependencies();
		if (d != null) {
			for (int i = 0; i < d.size(); ++i) {
				final Shape s = d.get(i);
				deps.remove(s);
				deps.addLast(s);
			}
		}
	}
	
	private static int mNextTransactionId = 0;
	
	private static boolean tryMovePoint(ExplicitPoint point, double x, double y) {
		int txnId = mNextTransactionId++;
		point.setTempLocation(txnId, x, y);
		if (point.dependencies() != null) {
			LinkedList<Shape> queue = new LinkedList<Shape>();
			addDepsTo(point, queue); 
			boolean ok = true;
			while (queue.size() > 0) {
				Shape shape = queue.removeFirst();
				if (!shape.prepareLocationUpdate(txnId)) {
					ok = false;
				}
				addDepsTo(shape, queue);
			}
			
			queue.addFirst(point);
			while (queue.size() > 0) {
				Shape shape = queue.removeFirst();
				if (ok) {
					shape.commitLocationUpdate(txnId);
				} else {
					shape.abortLocationUpdate(txnId);
				}
				addDepsTo(shape, queue);
			}
			return ok;
		} else {
			point.commitLocationUpdate(txnId);
			return true;
		}
	}
	public void onTouchMove(float x, float y) {
		if (mCurrentPoint == null) return;
		if (mMode == MOVE) {
			tryMovePoint(((ExplicitPoint)mCurrentPoint.point), x, y);
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
	
	public void onUndo() {
		if (mUndos.size() > 0) {
			UndoEntry u = mUndos.remove(mUndos.size() - 1);
			u.apply(mModel);
		}
		mView.redraw();
	}
	
	static final float MAX_SNAP_DISTANCE = 60;

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
			if (!(shape instanceof Point)) {
				double d = shape.distanceFrom(x, y);
				if (d < MAX_SNAP_DISTANCE) {
					if (nearbyShapes == null) nearbyShapes = new Vector<Shape>();
					nearbyShapes.add(shape);
				}
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
