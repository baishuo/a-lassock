package com.aleiye.lassock.liveness;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.live.Live;
import com.aleiye.lassock.util.ConfigUtils;
import com.aleiye.lassock.util.JsonProvider;
import com.aleiye.zkclient.standard.CuratorFactory;

/**
 * ZK 课程配置活动监测
 * 
 * @author ruibing.zhao
 * @since 2015年5月12日
 * @version 2.1.2
 */
public class ZkLiveness implements Liveness {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkLiveness.class);

	ObjectMapper mapper = JsonProvider.adaptMapper;
	// ZK framework
	private CuratorFramework framework;

	// // 监视节点
	// private NodeCache nodeCache;

	// 监视节点
	private PathChildrenCache cache;

	public void initialize() throws Exception {
		framework = CuratorFactory.createFramework(ConfigUtils.getConfig().getString("remote. zookeeper.url"));
		framework.start();
		framework.blockUntilConnected();
	}

	@Override
	public void lisen(final Live live) throws Exception {
		// final String path = ZKPaths.makePath("/aleiye/lassock",
		// Sistem.getMac());
		// 监测该采集器配置
		final String resourcePath = ZKPaths.makePath("/aleiye/lassock", Sistem.getMac());

		// // 监测该采集状态
		// nodeCache = new NodeCache(framework, path);
		// NodeCacheListener nodeListener = new NodeCacheListener() {
		// @SuppressWarnings({
		// "unchecked"
		// })
		// @Override
		// public void nodeChanged() throws Exception {
		// String payload = client.getDataString(path);
		// if (StringUtils.isNotBlank(payload)) {
		// Map<String, Object> map = client.getData(path, HashMap.class);
		// if (map.containsKey(ZKPathConstants.collector.KEY_STATUS)) {
		// try {
		// int sts =
		// Integer.parseInt(map.get(ZKPathConstants.collector.KEY_STATUS).toString());
		// switch (sts) {
		// case 0:
		// if (!live.isPaused()) {
		// live.pause();
		// }
		// break;
		// case 1:
		// if (live.isPaused()) {
		// live.resume();
		// }
		// break;
		// default:
		// break;
		// }
		// } catch (Exception e) {
		// LOGGER.error("Repetitive operation!", e);
		// }
		// }
		// }
		// }
		// };
		// nodeCache.getListenable().addListener(nodeListener);
		// nodeCache.start();

		cache = new PathChildrenCache(framework, resourcePath, true);
		PathChildrenCacheListener listener = new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				try {
					Course course = mapper.readValue(event.getData().getData(), Course.class);
					switch (event.getType()) {
					case CHILD_ADDED: {
						live.add(course);
						break;
					}

					case CHILD_UPDATED: {
						live.modify(course);
						break;
					}

					case CHILD_REMOVED: {
						live.remove(course);
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
		cache.getListenable().addListener(listener);
		cache.start();
	}

	@Override
	public void close() {
		CloseableUtils.closeQuietly(cache);
		CloseableUtils.closeQuietly(framework);
	}
}
