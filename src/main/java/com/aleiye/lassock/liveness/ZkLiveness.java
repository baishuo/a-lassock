package com.aleiye.lassock.liveness;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.LassockState.RunState;
import com.aleiye.lassock.api.conf.Context;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.util.JsonProvider;
import com.aleiye.zkclient.standard.CuratorClient;
import com.aleiye.zkclient.standard.CuratorFactory;
import com.aleiye.zkpath.constants.ZKPathConstants;

/**
 * ZK 课程配置活动监测
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public class ZkLiveness extends AbstractLiveness {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkLiveness.class);
	ObjectMapper mapper = JsonProvider.adaptMapper;
	// ZK连接串
	private String zkurl;
	// ZK framework
	private CuratorFramework framework;

	// 监视节点 该节点为lassock 注册节点 用于控制lassock 启停状态控制
	private NodeCache nodeCache;
	// 监视节点
	private PathChildrenCache courseCache;

	@Override
	public void doConfigure(Context context) {
		zkurl = context.getString("zkurl");
	}

	@Override
	public void doStart() {
		try {
			framework = CuratorFactory.createFramework(zkurl);
			framework.start();
			framework.blockUntilConnected();
			final CuratorClient client = CuratorFactory.create(framework);
			final String nodePath = String.format(ZKPathConstants.collector.COLLECTOR_REG_PATH, Sistem.getMac());

			// 监测该采集状态
			nodeCache = new NodeCache(framework, nodePath);
			NodeCacheListener nodeListener = new NodeCacheListener() {
				@SuppressWarnings({
					"unchecked"
				})
				@Override
				public void nodeChanged() throws Exception {
					String payload = client.getDataString(nodePath);
					if (StringUtils.isNotBlank(payload)) {
						Map<String, Object> map = client.getData(nodePath, HashMap.class);
						if (map.containsKey(ZKPathConstants.collector.KEY_STATUS)) {
							try {
								int sts = Integer.parseInt(map.get(ZKPathConstants.collector.KEY_STATUS).toString());
								switch (sts) {
								case 0:
									eventBus.post(RunState.PAUSED);
									break;
								case 1:
									eventBus.post(RunState.RUNNING);
									break;
								default:
									break;
								}
							} catch (Exception e) {
								LOGGER.error("Repetitive operation!", e);
							}
						}
					}
				}
			};
			nodeCache.getListenable().addListener(nodeListener);
			nodeCache.start();

			// 监测该采集器配置
			final String resourcePath = String.format(ZKPathConstants.collector.RESOURCE_PATH, Sistem.getMac());
			courseCache = new PathChildrenCache(framework, resourcePath, true);
			PathChildrenCacheListener listener = new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					try {
						byte[] data = event.getData().getData();
						if (data == null) {
							return;
						}
						Course course = mapper.readValue(event.getData().getData(), Course.class);
						switch (event.getType()) {
						case CHILD_ADDED:
						case CHILD_UPDATED: {
							eventBus.post(course);
							break;
						}

						case CHILD_REMOVED: {
							eventBus.post(course.getName());
							break;
						}
						default:
							break;
						}
					} catch (Exception e) {
						LOGGER.error("采集课程操作异常", e);
					}
				}
			};
			courseCache.getListenable().addListener(listener);

			courseCache.start();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void doStop() {
		CloseableUtils.closeQuietly(nodeCache);
		CloseableUtils.closeQuietly(courseCache);
		CloseableUtils.closeQuietly(framework);
	}
}
