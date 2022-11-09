package com.panda.framework.exception;

public class CommandNotFoundException extends BaseException{

	private static final long serialVersionUID = 6424702324550529112L;

	public CommandNotFoundException(String detailMsg) {
		super(ErrorCode.COMMAND_NOT_FOUND, detailMsg);
	}
	
	public CommandNotFoundException(String detailMsgFormat, Object... args) {
		this(String.format(detailMsgFormat, args));
	}

}
