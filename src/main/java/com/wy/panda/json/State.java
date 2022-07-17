package com.wy.panda.json;

public enum State {
	
	SUCCESS(0),
	PUSH(1),
	FAIL(2),
	ERROR(3)
	;
	
	private int state;
	
	private State(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
