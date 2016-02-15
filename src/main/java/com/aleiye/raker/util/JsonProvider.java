package com.aleiye.raker.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Json mappers 提供类
 * 
 * @author ruibing.zhao
 * @since 2015年4月20日
 * @version 2.2.1
 */
public class JsonProvider {

	// 自适应 Mapper 
	// 表现在于1:忽略在JSON字符串中存在但Java对象实际没有的属性
	//        2:空值"" 和 NULL值都不参与序列化
	public final static ObjectMapper adaptMapper;

	// 标准 Mapper
	public final static ObjectMapper mapper;

	static {
		//		StdSerializerProvider sp = new StdSerializerProvider();
		//		sp.setNullValueSerializer(NullSerializer.instance);
		//		adaptMapper = new ObjectMapper(null, sp, null);
		adaptMapper = new ObjectMapper();
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		adaptMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		adaptMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		// 空值"" 和 NULL值都不参与序列化
		adaptMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);

		mapper = new ObjectMapper();
	}
}
