package com.ysaito.euclid;

import android.util.Log;

public abstract class StateId {
	private static int mCurrentId = 1;

	abstract public int id();
	
	static public class Leaf extends StateId {
		private int mId;
		private int mUid;
		
		public Leaf() { 
			mCurrentId++;
			mId = mCurrentId;
			mCurrentId++;
			mUid = mCurrentId;
		}
		
		public void updated() {
			mCurrentId++;
			mId = mCurrentId;
		}
		
		public int id() { return mId; }
		@Override public String toString() { return "Leaf:(" + mUid + "," + mId + ")"; }
	}
	
	static public class NonLeaf extends StateId {
		private StateId mChild1, mChild2;
		private int mCachedChildStateId1, mCachedChildStateId2; 
		private int mLastIdUpdate;
		private int mCachedId;
		
		public NonLeaf(StateId child1, StateId child2) {
			mChild1 = child1;
			mChild2 = child2;
			mLastIdUpdate = -1;
			mCachedChildStateId1 = -1;
			mCachedChildStateId2 = -1;	
		}
	
		private final String TAG = "StateId";
		public boolean maybeUpdateId() {
			if (mLastIdUpdate >= mCurrentId) {
				return false;
			}
			if (mChild1.id() == mCachedChildStateId1 && mChild2.id() == mCachedChildStateId2) {
				Log.d(TAG, "NonLeaf: nonupdate1: " + toString());
				mLastIdUpdate = mCurrentId;
				return true;
			}
			Log.d(TAG, "NonLeaf: update");
			mCachedChildStateId1 = mChild1.id();
			mCachedChildStateId2 = mChild2.id();
			mCachedId++;
			mLastIdUpdate = mCurrentId;
			return true;
		}
		
		public int id() {
			assert(mLastIdUpdate == mCurrentId);
			return mCachedId;
		}
		@Override public String toString() { 
			return "NonLeaf:(" + mChild1.toString() + "," + mChild2.toString() + ":" + mCachedId + ")"; 
		} 
	}
}
