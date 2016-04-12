package com.aleiye.lassock.conf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aleiye.lassock.api.conf.Context;

/**
 * 采集生涯配置
 * 
 * @author ruibing.zhao
 * @since 2016年4月12日
 * @version 1.0
 */
public class LiveConfiguration {
	// private static final Logger logger =
	// LoggerFactory.getLogger(LiveConfiguration.class);

	private Map<String, Context> basketContextMap;
	private Map<String, Context> bazaarContextMap;

	private Set<String> basketSet;
	private Set<String> bazaarSet;

	private LiveConfiguration() {
		basketContextMap = new HashMap<String, Context>();
		bazaarContextMap = new HashMap<String, Context>();
	}

	/**
	 * Creates a populated Live Configuration object.
	 */
	public LiveConfiguration(Context properties) {
		this();
		// 获取对列配置
		Map<String, Object> basketConfig = properties.getSubProperties(ConfigurationConstants.CONFIG_BASKETS_PREFIX);
		Context basketContext = new Context(basketConfig);
		basketSet = getObjectConfig(basketConfig);
		for (String basketName : basketSet)
			basketContextMap.put(basketName, new Context(basketContext.getSubProperties(basketName + ".")));
		// 获取消费端配置
		Map<String, Object> bazaarConfig = properties.getSubProperties(ConfigurationConstants.CONFIG_BAZAARS_PREFIX);
		Context bazaarContext = new Context(bazaarConfig);
		// 获取对列配置名
		bazaarSet = getObjectConfig(bazaarConfig);
		for (String bazaarName : bazaarSet)
			bazaarContextMap.put(bazaarName, new Context(bazaarContext.getSubProperties(bazaarName + ".")));
	}

	/**
	 * 获取对像名
	 * 
	 * @param properties
	 * @return
	 */
	private Set<String> getObjectConfig(Map<String, Object> properties) {
		Set<String> sets = new HashSet<String>();
		// Construct the in-memory component hierarchy
		for (String name : properties.keySet()) {
			// Remove leading and trailing spaces
			name = name.trim();
			// value = value.trim();

			int index = name.indexOf('.');

			if (index == -1) {
				// errors.add(new FlumeConfigurationError(name, "",
				// FlumeConfigurationErrorType.AGENT_NAME_MISSING,
				// ErrorOrWarning.ERROR));
				// return false;
				continue;
			}

			String objName = name.substring(0, index);

			if (objName.length() == 0) {
				// errors.add(new FlumeConfigurationError(name, "",
				// FlumeConfigurationErrorType.AGENT_NAME_MISSING,
				// ErrorOrWarning.ERROR));
				// return false;
				continue;
			}
			sets.add(objName);
			// String configKey = name.substring(index + 1);
		}
		return sets;
	}

	public Map<String, Context> getBazaarContextMap() {
		return bazaarContextMap;
	}

	public void setBazaarContextMap(Map<String, Context> bazaarContextMap) {
		this.bazaarContextMap = bazaarContextMap;
	}

	public Set<String> getBasketSet() {
		return basketSet;
	}

	public void setBasketSet(Set<String> basketSet) {
		this.basketSet = basketSet;
	}

	public Set<String> getBazaarSet() {
		return bazaarSet;
	}

	public void setBazaarSet(Set<String> bazaarSet) {
		this.bazaarSet = bazaarSet;
	}

	public Map<String, Context> getBasketContextMap() {
		return basketContextMap;
	}

	public void setBasketContextMap(Map<String, Context> basketContextMap) {
		this.basketContextMap = basketContextMap;
	}
}
