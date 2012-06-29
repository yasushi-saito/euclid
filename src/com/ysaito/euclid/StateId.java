package com.ysaito.euclid;

public abstract class StateId {
	private static int mCurrentId = 1;

	abstract public int id();
	
	static public class Leaf extends StateId {
		private int mId;
		
		public Leaf() { 
			mCurrentId++;
			mId = mCurrentId;
		}
		
		public void updated() {
			mCurrentId++;
			mId = mCurrentId;
		}
		
		public int id() { return mId; }
	}
	
	static public class NonLeaf extends StateId {
		private StateId mChild1, mChild2;
		private int mCachedChildStateId1, mCachedChildStateId2; 
		private int mLastIdUpdate;
		private int mCachedId;
		
		public NonLeaf(StateId child1, StateId child2) {
			mChild1 = child1;
			mChild2 = child2;
		}
	
		public boolean maybeUpdateId() {
			if (mLastIdUpdate >= mCurrentId) return false;
			if (mChild1.id() == mCachedChildStateId1 || mChild2.id() == mCachedChildStateId2) {
				mLastIdUpdate = mCurrentId;
				return false;
			}
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
	}
}
