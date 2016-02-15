package com.aleiye.raker.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.aleiye.raker.live.Live;
import com.aleiye.raker.live.scroll.Const;
import com.aleiye.raker.live.scroll.Course;
import com.aleiye.raker.util.ConfigUtils;
import com.aleiye.raker.util.JsonProvider;

public class FileloadLiveness implements Liveness {

	String[] hosts = {

	};

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() throws Exception {
	}

	public static class SimpleCourse {
		public List<Course> courses;

		public Map<String, Course> template;

		public List<TagetServer> snmpServers;
		public List<TagetServer> commServers;
	}

	public static class TagetServer {
		public List<String> host;
		public String template;
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

		// router or switch
		if (sc.commServers != null) {
			for (TagetServer ss : sc.commServers) {
				for (String hostdisc : ss.host) {
					// 属性
					String attrs[] = hostdisc.split(",");
					// 端口
					String[] str = attrs[1].split("/");
					String host = str[0];
					int port = 23;
					if (str.length == 2) {
						port = Integer.parseInt(str[1]);
					}

					// 模版
					//				String template = ss.template;
					//				String[] templateArray = template.split(",");
					//1类采集 真接
					if (attrs[5].trim().toLowerCase().equals("yes")) {
						Course c = sc.template.get("telnet").clone();
						c.setId(UUID.randomUUID().toString());
						c.setName(attrs[1] + "-" + c.getType()
								+ (StringUtils.isBlank(c.getSubType()) ? "" : "-" + c.getSubType()));
						c.put(Const.command.HOST, host);
						c.put(Const.command.PORT, port);
						c.put(Const.command.USERNAME, attrs[2]);
						c.put(Const.command.PASSWORD, attrs[3]);
						c.put(Const.command.PREPARE_COMMAND, "en;" + attrs[4]);
						c.put(Const.command.COMMANDS, attrs[8].split(";"));
						addCources.add(c);
					}
					// 2类采集 跳转
					else if (attrs[6].trim().toLowerCase().equals("yes")) {
						Course c = sc.template.get("jump1").clone();
						c.setId(UUID.randomUUID().toString());
						c.setName(attrs[1] + "-" + c.getType()
								+ (StringUtils.isBlank(c.getSubType()) ? "" : "-" + c.getSubType()));
						c.put(Const.command.HOST, host);
						c.put(Const.command.PORT, port);
						c.put(Const.command.USERNAME, attrs[2]);
						c.put(Const.command.PASSWORD, attrs[3]);
						c.put(Const.command.PREPARE_COMMAND, "en;" + attrs[4]);
						c.put(Const.command.COMMANDS, attrs[8].split(";"));
						addCources.add(c);
					}
					// 3类采集 跳转
					else if (attrs[7].trim().toLowerCase().equals("yes")) {
						Course c = sc.template.get("jump2").clone();
						c.setId(UUID.randomUUID().toString());
						c.setName(attrs[1] + "-" + c.getType()
								+ (StringUtils.isBlank(c.getSubType()) ? "" : "-" + c.getSubType()));
						c.put(Const.command.HOST, host);
						c.put(Const.command.PORT, port);
						c.put(Const.command.USERNAME, attrs[2]);
						c.put(Const.command.PASSWORD, attrs[3]);
						c.put(Const.command.PREPARE_COMMAND, "en;" + attrs[4]);
						c.put(Const.command.COMMANDS, attrs[8].split(";"));
						addCources.add(c);
					}
				}
			}
		}
		if (sc.snmpServers != null) {
			for (TagetServer ss : sc.snmpServers) {
				for (String hostStr : ss.host) {
					String[] args = hostStr.split(",");
					String[] str = args[0].split("/");
					String host = str[0];
					String community = "";
					if (args.length > 1) {
						community = args[1];
					}
					int port = 161;
					if (str.length == 2) {
						port = Integer.parseInt(str[1]);
					}
					// 模版
					String template = ss.template;
					String[] templateArray = template.split(",");
					for (String t : templateArray) {
						Course c = sc.template.get(t.trim()).clone();
						c.setId(args[0] + "-" + c.getType() + "-" + c.getSubType());
						c.setName(args[0] + "-" + c.getType() + "-" + c.getSubType());
						c.put(Const.snmp.HOST, host);
						c.put(Const.snmp.PORT, port);
						if (StringUtils.isNotBlank(community))
							c.put(Const.snmp.COMMUNITY, community);
						addCources.add(c);
					}
				}
			}
		}
		live.refresh(addCources);
	}
}
