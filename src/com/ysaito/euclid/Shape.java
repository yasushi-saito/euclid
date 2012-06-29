package com.ysaito.euclid;

public abstract class Shape {
	static public class Line extends Shape {
		public float x1, y1, x2, y2;
		public Line(float pX1, float pY1, float pX2, float pY2) {
			x1 = pX1;
			y1 = pY1;
			x2 = pX2;
			y2 = pY2;
		}
	}
	static public class Circle extends Shape {
		public float x, y, radius;
		public Circle(float pX, float pY, float pRadius) {
			x = pX;
			y = pY;
			radius = pRadius;
		}
	}
}
