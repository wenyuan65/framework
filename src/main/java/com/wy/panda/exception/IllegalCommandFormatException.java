package com.wy.panda.exception;

public class IllegalCommandFormatException extends BaseException{

	private static final long serialVersionUID = 1L;
	
	public IllegalCommandFormatException(String detailMsg) {
		super(ErrorCode.ILLEGAL_COMMAND, detailMsg);
	}
	
	public IllegalCommandFormatException(String detailMsgFormat, Object... args) {
		this(String.format(detailMsgFormat, args));
	}
}
