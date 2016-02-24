package com.aleiye.lassock.liveness;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.cache.NodeCache;
//import org.apache.curator.framework.recipes.cache.NodeCacheListener;
//import org.apache.curator.utils.CloseableUtils;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.JavaType;
//
//import com.aleiye.aleiye.zkclient.constants.ZKPathConstants;
//import com.aleiye.aleiye.zkclient.standard.CuratorClient;
//import com.aleiye.common.utils.EncrypDES;
//import com.aleiye.lassock.cache.FileloadLiveness.SimpleCourse;
//import com.aleiye.lassock.live.Live;
//import com.aleiye.lassock.live.hill.shade.QuartzGroupShadeRunner;
//import com.aleiye.lassock.live.scroll.Const;
//import com.aleiye.lassock.live.scroll.Course;
//import com.aleiye.lassock.live.scroll.Course.RunType;
//import com.aleiye.lassock.logging.Logging;
//import com.aleiye.lassock.ping2.CiscoParser;
//import com.aleiye.lassock.ping2.ExecPinger;
//import com.aleiye.lassock.ping2.JumpPinger;
//import com.aleiye.lassock.ping2.PingParser;
//import com.aleiye.lassock.ping2.Pinger.PingResult;
//import com.aleiye.lassock.util.ConfigUtils;
//import com.aleiye.lassock.util.JsonProvider;
//import com.aleiye.lassock.util.LogUtils;

/**
 * ZK 课程配置活动监测
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
//public class ZkLiveness extends Logging implements Liveness {
//	// private static final Logger LOGGER = LoggerFactory.getLogger(ZkLiveness.class);
//
//	ObjectMapper mapper = new ObjectMapper();
//	// ZK framework
//	private CuratorFramework framework;
//	private CuratorClient client;
//	// 起停监听节点
//	private NodeCache nodeCache;
//	// 定时监听节点
//	private NodeCache cronCache;
//	// command配置监听
//	private NodeCache commandConfig;
//	// snmp配置监听
//	private NodeCache snmpConfig;
//
//	// 配置监听
//	// private PathChildrenCache courseCache;
//
//	SimpleCourse sc = null;
//	// 正式配置
//	List<Course> addCources = null;
//	Map<String, Course> template = null;
//
//	public void initialize() throws Exception {
//		framework = ZkUtil.getFramework();
//		client = ZkUtil.getClient();
//		ObjectMapper mapper = JsonProvider.adaptMapper;
//		String strfile = this.getClass().getResource("/" + ConfigUtils.getConfig().getString("system.coursefile"))
//				.getFile();
//		File file = new File(strfile);
//		SimpleCourse sc = mapper.readValue(file, SimpleCourse.class);
//		template = sc.template;
//		// 正式配置
//		addCources = sc.courses;
//	}
//
//	@Override
//	public void lisen(final Live live) throws Exception {
//		// 添加固定配置
//		live.refresh(this.addCources);
//
//		// Ping
//		if (ConfigUtils.getConfig().getBoolean("remote.ping.enabled")) {
//			Collection<String> hosts = null;
//			if (client.exists(ZKPathConstants.BANK_OTHER_TYPE_DEVICE_IP)) {
//				hosts = client.getDataCollection(ZKPathConstants.BANK_OTHER_TYPE_DEVICE_IP, String.class);
//			} else {
//				hosts = new ArrayList<String>();
//			}
//			if (CollectionUtils.isNotEmpty(hosts)) {
//				PingParser pparser = new CiscoParser();
//				if (client.exists(ZKPathConstants.BANK_COREDEVICE_LOGIN)) {
//					Map<String, Object> jump = (Map<String, Object>) client.getData(
//							ZKPathConstants.BANK_COREDEVICE_LOGIN, Map.class);
//					if (MapUtils.isNotEmpty(jump)) {
//						StringBuffer juStr = new StringBuffer();
//						juStr.append(jump.get("host").toString());
//						if (jump.containsKey("port")) {
//							juStr.append("/");
//							juStr.append(jump.get("port").toString());
//						}
//						juStr.append(";");
//						if (jump.containsKey("account")) {
//							juStr.append(jump.get("account").toString());
//							juStr.append(";");
//						}
//						if (jump.containsKey("pwd")) {
//							juStr.append(EncrypDES.decrypt(jump.get("pwd").toString()));
//							juStr.append(";");
//						}
//						if (jump.containsKey("encode")) {
//							juStr.append("en;");
//							juStr.append(EncrypDES.decrypt(jump.get("encode").toString()));
//						}
//						JumpPinger pinger = new JumpPinger();
//						pinger.setParser(pparser);
//						String[] ju = { juStr.toString() };
//						try {
//							pinger.connect(ju);
//							for (Object host : hosts) {
//								PingResult count = pinger.ping(host.toString());
//								if (count != PingResult.SUCCESS) {
//									LogUtils.error("ping " + host + " failed");
//								}
//							}
//						} catch (Exception e) {
//							LogUtils.error(e.getMessage());
//							logError("ping error", e);
//						} finally {
//							pinger.distinct();
//						}
//					}
//				} else {
//					ExecPinger pinger = new ExecPinger();
//					pinger.setParser(new CiscoParser());
//					try {
//						for (Object host : hosts) {
//							PingResult r = pinger.ping(host.toString());
//							if (r != PingResult.SUCCESS) {
//								LogUtils.error("ping " + host + " failed");
//							}
//						}
//					} catch (Exception e) {
//						logError("ping error", e);
//					}
//				}
//			}
//		}
//
//		// *****************************************************************************
//		// 监测该采集器启停状态
//		// *****************************************************************************
//		nodeCache = new NodeCache(framework, ZKPathConstants.BANK_COLLECTOR_SNMP_STATUS);
//		NodeCacheListener nodeListener = new NodeCacheListener() {
//			@Override
//			public void nodeChanged() throws Exception {
//				String status = client.getDataString(ZKPathConstants.BANK_COLLECTOR_SNMP_STATUS);
//				if (StringUtils.isNotBlank(status) && status.equals("on")) {
//					// if (live.isPaused()) {
//					// live.resume();
//					// }
//					String payload = client.getDataString(ZKPathConstants.BANK_CONFIG_SNMP);
//					if (StringUtils.isNotBlank(payload)) {
//						JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
//						List<Map<String, Object>> list = mapper.readValue(payload, javaType);
//						List<Course> courses = covert(list, "snmp");
//						live.clean("snmp");
//						live.refresh(courses);
//					}
//				} else {
//					live.clean("snmp");
//				}
//			}
//		};
//		nodeCache.getListenable().addListener(nodeListener);
//		nodeCache.start();
//
//		// *****************************************************************************
//		// 定时任务监听
//		// *****************************************************************************
//		cronCache = new NodeCache(framework, ZKPathConstants.BANK_COLLECTOR_COMMAND_CRON);
//		NodeCacheListener cronListener = new NodeCacheListener() {
//			@Override
//			public void nodeChanged() throws Exception {
//				String payload = client.getDataString(ZKPathConstants.BANK_COLLECTOR_COMMAND_CRON);
//				if (StringUtils.isNotBlank(payload)) {
//					QuartzGroupShadeRunner.delTask("jump_command");
//					QuartzGroupShadeRunner.createTask("jump_command", payload);
//
//					String payload1 = client.getDataString(ZKPathConstants.BANK_COMMAND_CONFIG);
//					if (StringUtils.isNotBlank(payload1)) {
//						JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
//						List<Map<String, Object>> list = mapper.readValue(
//								client.getData(ZKPathConstants.BANK_COMMAND_CONFIG), javaType);
//						List<Course> courses = covert(list, "command");
//						live.clean("command");
//						for (Course course : courses) {
//							course.setCron(payload);
//						}
//						live.refresh(courses);
//					}
//
//				} else {
//					QuartzGroupShadeRunner.delTask("jump_command");
//					live.clean("command");
//				}
//			}
//		};
//		cronCache.getListenable().addListener(cronListener);
//		cronCache.start();
//
//		// 监测该采集器启停状态
//		commandConfig = new NodeCache(framework, ZKPathConstants.BANK_COMMAND_CONFIG);
//		NodeCacheListener commandNodeListener = new NodeCacheListener() {
//			@Override
//			public void nodeChanged() throws Exception {
//				String payload = client.getDataString(ZKPathConstants.BANK_COMMAND_CONFIG);
//				if (StringUtils.isNotBlank(payload)) {
//					JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
//					List<Map<String, Object>> list = mapper.readValue(
//							client.getData(ZKPathConstants.BANK_COMMAND_CONFIG), javaType);
//					List<Course> courses = covert(list, "command");
//					live.clean("command");
//					String cron = client.getDataString(ZKPathConstants.BANK_COLLECTOR_COMMAND_CRON);
//					if (StringUtils.isNotBlank(cron)) {
//						for (Course course : courses) {
//							course.setCron(cron);
//						}
//						live.refresh(courses);
//					}
//				} else {
//					live.clean("command");
//				}
//			}
//		};
//		commandConfig.getListenable().addListener(commandNodeListener);
//		commandConfig.start();
//
//		// 监测该采集器启停状态
//		snmpConfig = new NodeCache(framework, ZKPathConstants.BANK_CONFIG_SNMP);
//		NodeCacheListener snmpNodeListener = new NodeCacheListener() {
//			@Override
//			public void nodeChanged() throws Exception {
//				String payload = client.getDataString(ZKPathConstants.BANK_CONFIG_SNMP);
//				if (StringUtils.isNotBlank(payload)) {
//					JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
//					List<Map<String, Object>> list = mapper.readValue(payload, javaType);
//					List<Course> courses = covert(list, "snmp");
//					live.clean("snmp");
//					String status = client.getDataString(ZKPathConstants.BANK_COLLECTOR_SNMP_STATUS);
//					if (StringUtils.isNotBlank(status) && status.equals("on")) {
//						live.refresh(courses);
//					}
//				}
//			}
//		};
//		snmpConfig.getListenable().addListener(snmpNodeListener);
//		snmpConfig.start();
//	}
//
//	private List<Course> covert(List<Map<String, Object>> resources, String nodeName) {
//		List<Course> list = new ArrayList<Course>();
//		for (Map<String, Object> config : resources) {
//			if (nodeName.equals("command")) {
//				try {
//					if (MapUtils.isNotEmpty(config))
//						list.add(covertCommand(config));
//				} catch (Exception e) {
//					LogUtils.error("config lisen error : " + e.getMessage() + "/" + config.toString());
//				}
//			} else if (nodeName.equals("snmp")) {
//				list.addAll(covertSnmp(config));
//			}
//		}
//		return list;
//	}
//
//	private List<Course> covertSnmp(Map<String, Object> resource) {
//		List<Course> cs = new ArrayList<Course>();
//		if (resource.containsKey("flow")) {
//			Map<String, Object> sei = (Map<String, Object>) resource.get("flow");
//			Course c;
//			if (sei.containsKey("en") && Boolean.valueOf(sei.get("en").toString())) {
//				try {
//					c = template.get("flow").clone();
//				} catch (CloneNotSupportedException e) {
//					c = new Course();
//				}
//				if (sei.containsKey("oid")) {
//					c.put("oids", sei.get("oid"));
//				}
//				c.setId(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.setName(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.put(Const.snmp.HOST, resource.get(Const.snmp.HOST));
//				if (resource.containsKey("community")) {
//					c.put(Const.snmp.COMMUNITY, EncrypDES.decrypt(resource.get("community").toString()));
//				}
//				if (resource.containsKey(Const.snmp.PORT)) {
//					c.put(Const.snmp.PORT, Integer.parseInt(resource.get(Const.snmp.PORT).toString()));
//				}
//				if (resource.containsKey("attrs")) {
//					c.addAllAttributes((Map<String, Object>) resource.get("attrs"));
//				}
//				c.setUserId(Env.USERID);
//				cs.add(c);
//			}
//		}
//
//		if (resource.containsKey("cpu")) {
//			Map<String, Object> sei = (Map<String, Object>) resource.get("cpu");
//			Course c;
//			if (sei.containsKey("en") && Boolean.valueOf(sei.get("en").toString())) {
//				try {
//					c = template.get("cpu").clone();
//				} catch (CloneNotSupportedException e) {
//					c = new Course();
//				}
//				if (sei.containsKey("oid")) {
//					c.put("oids", sei.get("oid"));
//				}
//				c.setId(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.setName(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.put(Const.snmp.HOST, resource.get(Const.snmp.HOST));
//				if (resource.containsKey("community")) {
//					c.put(Const.snmp.COMMUNITY, EncrypDES.decrypt(resource.get("community").toString()));
//				}
//				if (resource.containsKey(Const.snmp.PORT)) {
//					c.put(Const.snmp.PORT, Integer.parseInt(resource.get(Const.snmp.PORT).toString()));
//				}
//				if (resource.containsKey("attrs")) {
//					c.addAllAttributes((Map<String, Object>) resource.get("attrs"));
//				}
//				c.setUserId(Env.USERID);
//				cs.add(c);
//			}
//		}
//
//		if (resource.containsKey("temperature")) {
//			Map<String, Object> sei = (Map<String, Object>) resource.get("temperature");
//			Course c;
//			if (sei.containsKey("en") && Boolean.valueOf(sei.get("en").toString())) {
//				try {
//					c = template.get("temperature").clone();
//				} catch (CloneNotSupportedException e) {
//					c = new Course();
//				}
//				if (sei.containsKey("oid")) {
//					c.put("oids", sei.get("oid"));
//				}
//				c.setId(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.setName(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.put(Const.snmp.HOST, resource.get(Const.snmp.HOST));
//				if (resource.containsKey("community")) {
//					c.put(Const.snmp.COMMUNITY, EncrypDES.decrypt(resource.get("community").toString()));
//				}
//				if (resource.containsKey(Const.snmp.PORT)) {
//					c.put(Const.snmp.PORT, Integer.parseInt(resource.get(Const.snmp.PORT).toString()));
//				}
//				if (resource.containsKey("attrs")) {
//					c.addAllAttributes((Map<String, Object>) resource.get("attrs"));
//				}
//				c.setUserId(Env.USERID);
//				cs.add(c);
//			}
//		}
//
//		if (resource.containsKey("memory")) {
//			Map<String, Object> sei = (Map<String, Object>) resource.get("flow");
//			Course c;
//			if (sei.containsKey("en") && Boolean.valueOf(sei.get("en").toString())) {
//				try {
//					c = template.get("memory").clone();
//				} catch (CloneNotSupportedException e) {
//					c = new Course();
//				}
//				if (sei.containsKey("oid")) {
//					c.put("oids", sei.get("oid"));
//				}
//				if (resource.containsKey("total")) {
//					c.put("total", Boolean.valueOf(sei.get("total").toString()));
//				}
//				c.setId(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.setName(resource.get(Const.snmp.HOST) + "-" + c.getType() + "-" + c.getSubType());
//				c.put(Const.snmp.HOST, resource.get(Const.snmp.HOST));
//				if (resource.containsKey("community")) {
//					c.put(Const.snmp.COMMUNITY, EncrypDES.decrypt(resource.get("community").toString()));
//				}
//				if (resource.containsKey(Const.snmp.PORT)) {
//					c.put(Const.snmp.PORT, Integer.parseInt(resource.get(Const.snmp.PORT).toString()));
//				}
//				if (resource.containsKey("attrs")) {
//					c.addAllAttributes((Map<String, Object>) resource.get("attrs"));
//				}
//				c.setUserId(Env.USERID);
//				cs.add(c);
//			}
//		}
//		return cs;
//	}
//
//	private Course covertCommand(Map<String, Object> resource) throws Exception {
//		Course c = null;
//		if (!resource.containsKey("ip")) {
//			throw new Exception("Host can not be empty:" + resource.toString());
//		}
//		try {
//			c = template.get("telnet").clone();
//		} catch (CloneNotSupportedException e) {
//			c = new Course();
//		}
//		c.setId(UUID.randomUUID().toString());
//		c.setName(resource.get("ip") + "-" + c.getType()
//				+ (StringUtils.isBlank(c.getSubType()) ? "" : "-" + c.getSubType()));
//		c.put(Const.command.HOST, resource.get("ip"));
//		if (resource.containsKey("port"))
//			c.put(Const.command.PORT, resource.get("port"));
//		if (resource.containsKey("account"))
//			c.put(Const.command.USERNAME, resource.get("account"));
//		if (resource.containsKey("pwd"))
//			c.put(Const.command.PASSWORD, EncrypDES.decrypt(resource.get("pwd").toString()));
//		if (resource.containsKey("encode")) {
//			c.put(Const.command.PREPARE_COMMAND, "en;" + EncrypDES.decrypt(resource.get("encode").toString()));
//		}
//		if (resource.containsKey("jumped")) {
//			c.put("jumped", convetJump((List<Map<Object, Object>>) resource.get("jumped")));
//			c.setRunType(RunType.GROUP);
//			c.setGroupName("jump_command");
//		}
//
//		if (resource.containsKey("attrs")) {
//			c.addAllAttributes((Map<String, Object>) resource.get("attrs"));
//		}
//		c.put(Const.command.COMMANDS, resource.get("commands").toString().split(";"));
//
//		c.setUserId(Env.USERID);
//		return c;
//	}
//
//	private List<String> convetJump(List<Map<Object, Object>> jumps) {
//		List<String> ju = new ArrayList<String>();
//		for (Map<Object, Object> map : jumps) {
//			if (map.containsKey("en") && Boolean.valueOf(map.get("en").toString())) {
//				String pc = map.get("ip") + ";";
//				if (map.containsKey("account")) {
//					pc += map.get("account") + ";";
//				}
//				if (map.containsKey("pwd")) {
//					pc += EncrypDES.decrypt(map.get("pwd").toString());
//				}
//				ju.add(pc);
//			}
//		}
//		return ju;
//	}
//
//	@Override
//	public void close() {
//		CloseableUtils.closeQuietly(nodeCache);
//		CloseableUtils.closeQuietly(commandConfig);
//		CloseableUtils.closeQuietly(snmpConfig);
//		CloseableUtils.closeQuietly(framework);
//	}
//}
