package com.voice.common.util.nlp;

import java.util.ArrayList;

import com.iii360.base.common.utl.LogManager;

public class SentenceObject {
	private ArrayList<WordObj> mTextObjects;
	private String mContent;
	private String mOrder;
	private String mTarget;
	private String mClearContent;
	private int mResultDistance;

	public static final String TYPE_ROOM = "房间";
	public static final String TYPE_OPERITE = "父动作";
	public static final String TYPE_SUB_OPERITE = "子动作";
	public static final String TYPE_DEVICES = "设备";
	public static final String TYPE_PARAM = "属性";
	public static final String TYPE_CHAANL = "频道";
	public static final String TYPE_MODIFICATION = "描述";
	public static final String TYPE_OTHER = "其他";
	public static final String TYPE_MODE = "model";

	public static final String TYPE_SENTENCE = TYPE_OPERITE + TYPE_MODIFICATION + TYPE_ROOM + TYPE_DEVICES
			+ TYPE_SUB_OPERITE + TYPE_PARAM + TYPE_CHAANL;

	public static ArrayList<String> BASE_ALL_TYPE = new ArrayList<String>();
	static {
		BASE_ALL_TYPE.add(TYPE_OPERITE);
		BASE_ALL_TYPE.add(TYPE_ROOM);
		BASE_ALL_TYPE.add(TYPE_DEVICES);
		BASE_ALL_TYPE.add(TYPE_SUB_OPERITE);
		BASE_ALL_TYPE.add(TYPE_PARAM);
		BASE_ALL_TYPE.add(TYPE_CHAANL);
		BASE_ALL_TYPE.add(TYPE_MODIFICATION);
	}

	public SentenceObject(ArrayList<WordObj> textObjects, String content) {
		mTextObjects = textObjects;
		mContent = content;
		mClearContent = TYPE_SENTENCE;
		for (WordObj w : textObjects) {

			if (mClearContent.contains(w.getType())) {
				mClearContent = mClearContent.replace(w.getType(), w.getType() + w.getWord());
			} else {
				mClearContent += w.getWord();
			}
		}

		for (String type : BASE_ALL_TYPE) {
			mClearContent = mClearContent.replace(type, "");
		}
	}

	public ArrayList<WordObj> getTextObjects() {
		return mTextObjects;
	}

	public String getClearContent() {

		return mClearContent;
	}

	public String getOrder() {
		return mOrder;
	}

	public void setOrder(String mOrder) {
		this.mOrder = mOrder;
	}

	public String getTarget() {
		return mTarget;
	}

	public void setTarget(String mTarget) {
		this.mTarget = mTarget;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		for (WordObj w : mTextObjects) {
			sb.append(w.getWord());
		}
		return sb.toString();
	}

	public int getResultDistance() {
		return mResultDistance;
	}

	public void setResultDistance(int ResultDistance) {
		mResultDistance = ResultDistance;
	}

}
