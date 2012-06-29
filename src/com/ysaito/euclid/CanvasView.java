package com.ysaito.euclid;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

class CanvasView extends View {
	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void initialize(CanvasModel model) {
		mModel = model;
	}
	
	@Override public void onDraw(Canvas canvas) {
		Vector<Shape> shapes = mModel.shapes();
		final int n = shapes.size();
		for (int i = 0; i < n; ++i) {
			Shape shape = shapes.get(i);
			if (shape instanceof Shape.Circle) {
				drawCircle(canvas, (Shape.Circle)shape);
				continue;
			}
			if (shape instanceof Shape.Line) {
				drawLine(canvas, (Shape.Line)shape); 
				continue;
			}
		}
	}
	
	private void drawPoint(Canvas canvas, float x, float y) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setColor(0xff40e838);
		canvas.drawRect(x - 3, y - 3, x + 3, y + 3, p);
	}
	
	private void drawLine(Canvas canvas, Shape.Line line) {
		final float dX = line.x1 - line.x2;
		final float dY = line.y1 - line.y2;
		if (Math.abs(dX) > Math.abs(dY)) {
			drawLineH(canvas, line);
		} else {
			drawLineV(canvas, line);
		}
		drawPoint(canvas, line.x1, line.y1);
		drawPoint(canvas, line.x2, line.y2);
	}
	
	private void drawLineH(Canvas canvas, Shape.Line line) {
		// Solve equation ax + y = c
		final float dX = line.x1 - line.x2;
		final float dY = line.y1 - line.y2;
		final float a = -dY / dX;
		final float c = a * line.x1 + line.y1;
		final float width = getWidth();
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawLine(0, c, width, c - a * width, p);
	}

	private void drawLineV(Canvas canvas, Shape.Line line) {
		// Solve equation x + ay = c
		final float dX = line.x1 - line.x2;
		final float dY = line.y1 - line.y2;
		final float a = -dX / dY;
		final float c = line.x1 + a * line.y1;
		final float height = getHeight();
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawLine(c, 0, c - a * height, height, p);
	}
	
	private void drawCircle(Canvas canvas, Shape.Circle circle) {
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(0xff505050);
		canvas.drawCircle(circle.x, circle.y, circle.radius, p);
		drawPoint(canvas, circle.x, circle.y);
	}
	
	CanvasModel mModel;
}
