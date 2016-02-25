package com.aleiye.lassock.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (配置表达式)Config Expression
 * 
 * @author ruibing.zhao
 * @since 2016年2月25日
 * @version 1.0
 */
public class CEUtil {
	private static final String EXPRESSION_REGEX = "^@\\<[\\w.\\s]+\\>$";
	private static final Pattern PATTERN = Pattern.compile(EXPRESSION_REGEX);

	public static boolean match(String expression) {
		Matcher m = PATTERN.matcher(expression);
		return m.matches();
	}

	public static String getKey(String expression) {
		return expression.substring(expression.indexOf('<') + 1, expression.lastIndexOf('>'));
	}
}
