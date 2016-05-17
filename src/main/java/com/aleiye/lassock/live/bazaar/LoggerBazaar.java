package com.aleiye.lassock.live.bazaar;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.model.Mushroom;

import java.io.FileWriter;

/**
 * Logger输出消费端，采集数据将在Logger 中输出主要用于测试
 * 
 * @author ruibing.zhao
 * @since 2016年4月14日
 * @version 1.0
 */
public class LoggerBazaar extends AbstractBazaar {
	private static final Logger logger = LoggerFactory.getLogger(LoggerBazaar.class);

	@Override
	public void process() throws Exception {
		Basket channel = getBasket();
		Mushroom event = null;

		try {
			event = channel.take();

		if (event != null) {
				if (logger.isDebugEnabled()) {
					String body =  new String( event.getBody());
					//取出后删除空白
				String ps = "Event: " + body;
					FileWriter writer = new FileWriter("F:\\result.txt", true);
					writer.write(body+"\r\n");
					writer.close();
					logger.debug(ps.trim());
					logger.debug(JSONObject.fromObject(event.getHeaders()).toString());
					event.incrementCompleteCount();
				}
			}
		} catch (Exception ex) {}
	}

	@Override
	public void configure(Context context) {
		//
	}
}
