package com.ysaito.euclid;

import java.util.Vector;

public class CanvasController {
	final CanvasModel mModel;
	final CanvasView mView;
	
	static final int MOVE = 1;
	static final int DRAW_LINE = 2;
	static final int DRAW_CIRCLE = 3;
	int mMode;
	
	Point.UserDefined mPointToMove;
	
	Point.UserDefined mLineAnchor;
	Point.UserDefined mLineTarget;
	
	Point.UserDefined mCircleAnchor;
	Point.UserDefined mCircleTarget;	
	
	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
		mMode = MOVE;
	}
	public void onTouchStart(float x, float y) {
		if (mMode == MOVE) {
			mPointToMove = pickNearbyPoint(x, y);
		} else if (mMode == DRAW_LINE) {
			mLineAnchor = pickNearbyPoint(x, y);
			if (mLineAnchor == null) {
				mLineAnchor = new Point.UserDefined(x,  y);
			}
			mLineTarget = new Point.UserDefined(x,  y);
		} else if (mMode == DRAW_CIRCLE) {
			mCircleAnchor = pickNearbyPoint(x, y);
			if (mCircleAnchor == null) {
				mCircleAnchor = new Point.UserDefined(x,  y);
			}
			mCircleTarget = new Point.UserDefined(x,  y);
		}
		onTouchMove(x, y);
	}
	public void onTouchEnd(float x, float y) {
		onTouchMove(x, y);
		if (mLineAnchor != null) {
			mModel.addShape(new Shape.Line(mLineAnchor, mLineTarget));
		} else if (mCircleAnchor != null) {
			mModel.addShape(new Shape.Circle(mCircleAnchor, mCircleTarget));
		}
		mPointToMove = null;
		mLineAnchor = null;
		mLineTarget = null;
		mCircleAnchor = null;
		mCircleTarget = null;
		mModel.setTempShape(null);
	}
	public void onTouchMove(float x, float y) {
		if (mPointToMove != null) {
			mPointToMove.moveTo(x, y);
		} else if (mLineAnchor != null) {
			mLineTarget.moveTo(x,  y);
			mModel.setTempShape(new Shape.Line(mLineAnchor, mLineTarget));
		} else if (mCircleAnchor != null) {
			mCircleTarget.moveTo(x,  y);
			mModel.setTempShape(new Shape.Circle(mCircleAnchor, mCircleTarget));
		}
		mView.redraw();
	}
	
	public void onModeChange(int mode) {
		mMode = mode;
	}

	private Point.UserDefined pickNearbyPoint(float x, float y) {
		Vector<Point.UserDefined> points = mModel.userDefinedPoints();
		Point.UserDefined candidate = null;
		final int n = points.size();
		float minDistance = 20;
		for (int i = 0; i < n; ++i) {
			Point.UserDefined point = points.get(i);
			float d = Point.distanceFrom(point, x, y);
			if (d < minDistance) {
				minDistance = d;
				candidate = point;
			}
		}
		return candidate;
	}
}
