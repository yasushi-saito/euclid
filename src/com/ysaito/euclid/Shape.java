package com.ysaito.euclid;

public abstract class Shape {
	static public class Line extends Shape {
		public final Point p1, p2;
		public Line(Point pp1, Point pp2) {
			p1 = pp1;
			p2 = pp2;
		}
	}
	
	static public class Circle extends Shape {
		public Point center, radiusControl;
		public Circle(Point pCenter, Point pRadius) {
			center = pCenter;
			radiusControl = pRadius;
		}
		
		public float radius() {
			final float dx = center.x() - radiusControl.x();
			final float dy = center.y() - radiusControl.y();
			return (float)Math.sqrt(dx * dx + dy * dy);
		}
	}
}
