package com.aleiye.lassock.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.live.scroll.Const;
import com.aleiye.lassock.live.scroll.Course;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.JsonProvider;

public class FilePickLiveness implements Liveness {

	@Override
	public void close() throws IOException {

	}

	@Override
	public void initialize() throws Exception {

	}

	public static class SimpleCourse {
		public List<Course> courses;
	}

	@Override
	public void lisen(Live live) throws Exception {
		ObjectMapper mapper = JsonProvider.adaptMapper;
		String strfile = this.getClass().getResource("/" + ConfigUtils.getConfig().getString("system.coursefile"))
				.getFile();
		File file = new File(strfile);
		SimpleCourse sc = mapper.readValue(file, SimpleCourse.class);
		// 正式配置
		List<Course> addCources = sc.courses;

		live.refresh(addCources);
	}
}
