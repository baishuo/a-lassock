package com.aleiye.lassock.liveness;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.conf.Context;
import com.aleiye.lassock.util.JsonProvider;

public class FilePickLiveness extends AbstractLiveness {

	private String configFile;

	@Override
	public void doStart() throws Exception {
		ObjectMapper mapper = JsonProvider.adaptMapper;
		String strfile = this.getClass().getResource("/" + configFile).getFile();
		File file = new File(strfile);
		SimpleCourse sc;
		sc = mapper.readValue(file, SimpleCourse.class);
		// 正式配置
		List<Course> addCources = sc.courses;
		eventBus.post(addCources);
	}

	@Override
	public void doConfigure(Context context) throws Exception {
		configFile = context.getString("coursefile");
	}

	public static class SimpleCourse {
		public List<Course> courses;
	}

	@Override
	protected void doStop() throws Exception {
		;
	}
}
