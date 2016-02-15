package com.aleiye.raker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.aleiye.raker.annotations.Required;
import com.aleiye.raker.common.Context;
import com.aleiye.raker.live.scroll.Course;
import com.aleiye.raker.live.scroll.Sign;

public class ScrollUtils {
	public static Sign forSign(Context course, Class<? extends Sign> clazz) throws Exception {
		Sign sign = clazz.newInstance();
		JSONObject jo = JSONObject.fromObject(course);
		jo.putAll(course.getParameters());
//		System.out.println(jo.toString());
		sign = JsonProvider.adaptMapper.readValue(jo.toString(), clazz);
		sign.associate(((Course) course).getId());
		return sign;
	}

	public static void validate(Object obj) throws Exception {
		Class<?> cls = obj.getClass();
		for (Field f : obj.getClass().getDeclaredFields()) {
			Required req = f.getAnnotation(Required.class);
			if (req != null) {
				String getMetName = pareGetName(f.getName());
				Method method = cls.getMethod(getMetName, null);
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

//	public static void main(String args[]) {
//		Course command = new Course();
//		command.setId("101");
//		command.setType("command");
//		command.put(Const.command.HOST, "10.0.1.1");
//		command.put(Const.command.PORT, "23");
//		command.put(Const.command.UESRNAME, "admin");
//		command.put(Const.command.PASSWORD, "yhxt@123");
//		String[] commands = {
//				"dis arp", "dis mac-address"
//		};
//		command.put(Const.command.PERIOD, "120000");
//		command.put(Const.command.COMMANDS, commands);
//		command.setRunType(RunType.TIMER);
//
//		try {
//			SnmpSign sign = (SnmpSign) forSign(command, SnmpSign.class);
//			System.out.print(sign);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
