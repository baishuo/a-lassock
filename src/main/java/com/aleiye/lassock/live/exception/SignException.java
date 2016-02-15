package com.aleiye.lassock.live.exception;

/**
 * 标识异常
 * <br>
 * 该异常发生表示在Shade创建过程中发生的异常
 * 
 * @author ruibing.zhao
 * @since 2015年6月18日
 * @version 2.1.2
 */
public class SignException extends Exception {
	private static final long serialVersionUID = 1L;

	public SignException(String msg) {
		super(msg);
	}

}
