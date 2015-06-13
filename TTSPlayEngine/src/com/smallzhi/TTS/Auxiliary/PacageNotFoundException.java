package com.smallzhi.TTS.Auxiliary;

public class PacageNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	String pacageName;

	public PacageNotFoundException(String info) {
		pacageName = info;
	}

	public String toString() {
		return pacageName;
	};
}
