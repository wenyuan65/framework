package com.wy.panda.exception;

public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;
	/** 错误类型 */
	private ErrorCode errorCode;
	/** 错误详细信息 */
	private String detailMsg;
	
	public BaseException(ErrorCode errorCode, String detailMsg) {
		this.errorCode = errorCode;
		this.detailMsg = detailMsg;
	}
	
	protected String getInnerCode(){
		return errorCode.getInnerCode();
	}
	
	public String getOutCode(){
		return errorCode.getOutCode();
	}
	
	public String getErrorType(){
		return errorCode.getErrorType();
	}
	/**
	 * 获取错误信息，服务于外部，例如记录程序执行信息，往数据库存储msg时使用
	 * @return
	 */
	public String getMsgForOutter(){
		StringBuilder msg = new StringBuilder();
		msg.append("error code:").append(errorCode.getOutCode());
		msg.append(" msg:").append(errorCode.getErrorType());
		return msg.toString();
	}
	/**
	 * 获取错误信息，服务于内部，通常在记录日志时使用，用于排查bug
	 * @return
	 */
	public String getMsgForInner(){
		StringBuilder msg = new StringBuilder();
		msg.append("error code:").append(errorCode.getOutCode()).append("---");
		msg.append(errorCode.getInnerCode()).append('.');
		msg.append(errorCode.getErrorType()).append(':').append(detailMsg);
		return msg.toString();
	}
	
}
