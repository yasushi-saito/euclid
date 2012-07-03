package com.ysaito.euclid;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class CanvasView extends View implements View.OnTouchListener {
	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}
	
	public void initialize(CanvasController controller, CanvasModel model) {
		mController = controller;
		mModel = model;
	}
	
	public void redraw() {
		invalidate();
	}
	
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    if (action == MotionEvent.ACTION_DOWN) {
	    	mController.onTouchStart(event.getX(), event.getY());
		} else if (action == MotionEvent.ACTION_MOVE) {
	    	mController.onTouchMove(event.getX(), event.getY());
		} else if (action == MotionEvent.ACTION_UP) {
	    	mController.onTouchEnd(event.getX(), event.getY());
		}
	    return true;
	}
	
	@Override public void onDraw(Canvas canvas) {
		Vector<Shape> shapes = mModel.shapes();
		final int n = shapes.size();
		for (int i = 0; i < n; ++i) {
			drawShape(canvas, shapes.get(i));
		}
		Shape temp = mModel.tempShape();
		if (temp != null) drawShape(canvas, temp);
	}

	private void drawShape(Canvas canvas, Shape shape) {
		if (shape instanceof Circle) {
			drawCircle(canvas, (Circle)shape);
			return;
		}
		if (shape instanceof Line) {
			drawLine(canvas, (Line)shape); 
			return;
		}
		if (shape instanceof Point) {
			// do nothing.
			return;
		}
	}

	private void drawPoint(Canvas canvas, Point point) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		if (point instanceof ExplicitPoint) { 
			p.setColor(0xff40e838);
		} else {
			p.setColor(0xff50c0c0);
		}
		float x = (float)point.x();
		float y = (float)point.y();
		canvas.drawCircle((float)x, (float)y, 10, p);
	}
	
	private void drawLine(Canvas canvas, Line line) {
		final double dX = line.p0.x() - line.p1.x();
		final double dY = line.p0.y() - line.p1.y();
		if (Math.abs(dX) > Math.abs(dY)) {
			drawLineH(canvas, line);
		} else {
			drawLineV(canvas, line);
		}
		drawPoint(canvas, line.p0);
		drawPoint(canvas, line.p1);
	}
	
	private void drawLineH(Canvas canvas, Line line) {
		// Solve equation ax + y = c
		final double dX = line.p0.x() - line.p1.x();
		final double dY = line.p0.y() - line.p1.y();
		final double a = -dY / dX;
		final double c = a * line.p0.x() + line.p0.y();
		final double width = getWidth();
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawLine(0, (float)c, (float)width, (float)(c - a * width), p);
	}

	private void drawLineV(Canvas canvas, Line line) {
		// Solve equation x + ay = c
		final double dX = line.p0.x() - line.p1.x();
		final double dY = line.p0.y() - line.p1.y();
		final double a = -dX / dY;
		final double c = line.p0.x() + a * line.p0.y();
		final double height = getHeight();
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawLine((float)c, 0, (float)(c - a * height), (float)height, p);
	}
	
	private void drawCircle(Canvas canvas, Circle circle) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawCircle((float)circle.center.x(), (float)circle.center.y(), (float)circle.radius(), p);
		drawPoint(canvas, circle.center);
		drawPoint(canvas, circle.radiusControl);		
	}
	
	CanvasController mController;
	CanvasModel mModel;
}
