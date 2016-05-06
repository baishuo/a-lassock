package com.aleiye.lassock.common;

/**
 * 允许一个组件以一个名称为标记，
 * 以便它可以被称为唯一的配置系统中的。
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public interface NamedComponent {
	/**
	 * 设置组件名称
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * 获取组件名称
	 * 
	 * @return
	 */
	public String getName();
}
