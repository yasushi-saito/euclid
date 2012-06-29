package com.ysaito.euclid;

public abstract class Point {
	public abstract float x();
	public abstract float y();
	
	static public float distanceFrom(Point p, float x, float y) {
		final float dx = p.x() - x;
		final float dy = p.y() - y;
		return (float)Math.sqrt(dx * dx - dy * dy);
	}
	
	static public class UserDefined extends Point {
		private float mX, mY;
		public UserDefined(float x, float y) {
			mX = x;
			mY = y;
		}
		
		public float x() { return mX; }
		public float y() { return mY; }
		public void moveTo(float x, float y) {
			mX = x;
			mY = y;
		}
	}
}

