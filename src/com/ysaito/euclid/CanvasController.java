package com.ysaito.euclid;

import java.util.Vector;

public class CanvasController {
	final CanvasModel mModel;
	final CanvasView mView;
	Point.UserDefined mSelectedPoint;
	public CanvasController(CanvasModel model, CanvasView view) {
		mModel = model;
		mView = view;
	}
	public void onMoveStart(float x, float y) {
		Vector<Point.UserDefined> points = mModel.userDefinedPoints();
		mSelectedPoint = null;
		final int n = points.size();
		float minDistance = 99999;
		for (int i = 0; i < n; ++i) {
			Point.UserDefined point = points.get(i);
			float d = Point.distanceFrom(point, x, y);
			if (d < 20.0 && d < minDistance) {
				minDistance = d;
				mSelectedPoint = point;
			}
		}
	}
	public void onMove(float x, float y) {
		if (mSelectedPoint == null) return;
		mSelectedPoint.moveTo(x, y);
		mView.redraw();
	}
	public void onMoveEnd(float x, float y) {
		mSelectedPoint = null;
	}
}
