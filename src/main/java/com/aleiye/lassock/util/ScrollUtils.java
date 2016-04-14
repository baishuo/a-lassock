package com.aleiye.lassock.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.annotation.Required;

public class ScrollUtils {
	// public static Sign forSign(Course course, Class<? extends Sign> clazz)
	// throws Exception {
	// Sign sign = clazz.newInstance();
	// JSONObject jo = JSONObject.fromObject(course);
	// jo.putAll(course.getParameters());
	// sign = JsonProvider.adaptMapper.readValue(jo.toString(), clazz);
	// sign.setId(course.getName());
	// sign.associate(((Course) course).getName());
	// return sign;
	// }

	public static <T> T forParam(Course course, Class<T> clazz) throws Exception {
		JSONObject jo = JSONObject.fromObject(course.getParameters());
		T t = JsonProvider.adaptMapper.readValue(jo.toString(), clazz);
		validate(t);
		return t;
	}

	public static void validate(Object obj) throws Exception {
		Class<?> cls = obj.getClass();
		for (Field f : obj.getClass().getDeclaredFields()) {
			Required req = f.getAnnotation(Required.class);
			if (req != null) {
				String getMetName = pareGetName(f.getName());
				Method method = cls.getMethod(getMetName);
				Object value = method.invoke(obj, new Object[] {});
				if (value == null) {
					throw new Exception(obj.getClass().getSimpleName() + "-" + f.getName() + " is required!");
				}
				if (value.getClass().isAssignableFrom(String.class)) {
					if (StringUtils.isBlank((String) value)) {
						throw new Exception(obj.getClass().getSimpleName() + "-" + f.getName() + "is required!");
					}
				}
			}
		}
	}

	/**  
	 * 拼接某属性get 方法  
	 * @param fldname  
	 * @return  
	 */
	public static String pareGetName(String fldname) {
		if (null == fldname || "".equals(fldname)) {
			return null;
		}
		String pro = "get" + fldname.substring(0, 1).toUpperCase() + fldname.substring(1);
		return pro;
	}
}
