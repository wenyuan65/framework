package com.wy.panda.exception;

public enum ErrorCode {

	ILLEGAL_COMMAND("1","1001","ILLEGAL COMMAND"),
	COMMAND_NOT_FOUND("2","1002","COMMAND NOT FOUND"),
	ILLEGAL_PARAMETER("3","1003","ILLEGAL PARAMETER"),
	error3("9999","9999","");
	
	private String innerCode;
	
	private String outCode;
	/** 说明错误类型  */
	private String errorType;
	
	private ErrorCode(String innerCode, String outCode,String errorType){
		this.innerCode = innerCode;
		this.outCode = outCode;
		this.errorType = errorType;
	}

	public String getInnerCode() {
		return innerCode;
	}

	public String getOutCode() {
		return outCode;
	}

	public String getErrorType() {
		return errorType;
	}

}
