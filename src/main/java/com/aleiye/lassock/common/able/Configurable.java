package com.aleiye.lassock.common.able;

import com.aleiye.lassock.conf.Context;

/**
 * 任何继承该类接口都将通过引入一个上下文来配置
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public interface Configurable {
	/**
	 * 配置
	 * 
	 * @param context
	 */
	public void configure(Context context);
}
