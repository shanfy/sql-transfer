package com.yang.exception;

/**
 * 封装错误信息类
 * @author shanfy
 * @date 2023-08-25 11:51
 */
public class ExceptionInfo {

	/**
	 * 状态信息
	 */
	private int errorCode;

    /**
     * 功能：错误消息
     */
    private String errorMsg;


    public ExceptionInfo() {}

	public ExceptionInfo(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}