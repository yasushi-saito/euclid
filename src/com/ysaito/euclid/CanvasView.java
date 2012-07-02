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
		if (shape instanceof Shape.Circle) {
			drawCircle(canvas, (Shape.Circle)shape);
			return;
		}
		if (shape instanceof Shape.Line) {
			drawLine(canvas, (Shape.Line)shape); 
			return;
		}
	}

	private void drawPoint(Canvas canvas, double x, double y) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setColor(0xff40e838);
		canvas.drawRect((float)x - 3, (float)y - 3, (float)x + 3, (float)y + 3, p);
	}
	
	private void drawLine(Canvas canvas, Shape.Line line) {
		final double dX = line.p0.x() - line.p1.x();
		final double dY = line.p0.y() - line.p1.y();
		if (Math.abs(dX) > Math.abs(dY)) {
			drawLineH(canvas, line);
		} else {
			drawLineV(canvas, line);
		}
		drawPoint(canvas, line.p0.x(), line.p0.y());
		drawPoint(canvas, line.p1.x(), line.p1.y());
	}
	
	private void drawLineH(Canvas canvas, Shape.Line line) {
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

	private void drawLineV(Canvas canvas, Shape.Line line) {
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
	
	private void drawCircle(Canvas canvas, Shape.Circle circle) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawCircle((float)circle.center.x(), (float)circle.center.y(), (float)circle.radius(), p);
		drawPoint(canvas, circle.center.x(), circle.center.y());
	}
	
	CanvasController mController;
	CanvasModel mModel;
}
