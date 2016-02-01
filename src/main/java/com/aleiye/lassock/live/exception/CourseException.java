package com.aleiye.lassock.live.exception;

/**
 * 课程常异
 * <br>
 * 该异常发生在课程操作失败时
 * 该异常发生证明用户在配置该项采集任务得不到执行，
 * 须通过修正课程配置属性使该采集任务正常执行
 * 
 * @author ruibing.zhao
 * @since 2015年6月18日
 * @version 2.1.2
 */
public class CourseException extends Exception {
	private static final long serialVersionUID = 1L;

	public CourseException() {

	}

	public CourseException(String msg) {
		super(msg);
	}

}
