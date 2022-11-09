package com.panda.framework.exception;

public class IllegalParametersException extends BaseException{

	private static final long serialVersionUID = 1L;
	
	public IllegalParametersException(String detailMsg) {
		super(ErrorCode.ILLEGAL_PARAMETER, detailMsg);
	}
	
	public IllegalParametersException(String detailMsgFormat, Object... args) {
		this(String.format(detailMsgFormat, args));
	}
}
