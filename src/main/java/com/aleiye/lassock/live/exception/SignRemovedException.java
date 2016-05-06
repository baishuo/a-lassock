package com.aleiye.lassock.live.exception;

/**
 * 标识已删除异常
 * 
 * @author ruibing.zhao
 * @since 2015年6月6日
 * @version 2.1.2
 */
public class SignRemovedException extends Exception {

	private static final long serialVersionUID = -896717934363068825L;

	public SignRemovedException(String msg) {
		super(msg);
	}
}
