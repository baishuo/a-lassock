package com.aleiye.lassock.common.able;

/**
 * 可初始化的组件
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public interface Initializable {
	/**
	 * 任何继承该类接口都将通过引入一个初始化
	 * 
	 * @throws Exception
	 */
	void initialize() throws Exception;
}
