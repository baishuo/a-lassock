package com.aleiye.raker.util;

import java.lang.reflect.InvocationTargetException;

/**
 * Class反射工具类
 * 
 * @author ruibing.zhao
 * @since 2015年5月19日
 * @version 2.2.1
 */
public class ClassUtils {
	/**
	 * 获取Class
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	/**
	 * 创建实例
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		return (T) getClass(className).newInstance();
	}

	/**
	 * 根据参数创建实例
	 * 
	 * @param className
	 * @param parameters
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className, Object... parameters) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}
		return (T) getClass(className).getConstructor(parameterTypes).newInstance(parameters);
	}
}
