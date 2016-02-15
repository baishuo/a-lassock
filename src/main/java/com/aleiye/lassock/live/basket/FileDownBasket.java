package com.aleiye.lassock.live.basket;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.util.ConfigUtils;

/**
 * 将数据内容写入文件Basket
 * 
 * @author ruibing.zhao
 * @since 2015年8月26日
 * @version 2.1.2
 */
public class FileDownBasket extends AbstractBasket {
	private static Logger LOGGER = LoggerFactory.getLogger(FileDownBasket.class);
	private final String path = ConfigUtils.getConfig().getString("live.custom.filepath");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	BlockingQueue<Mushroom> queue = new LinkedBlockingQueue<Mushroom>();

	@Override
	public void push(Mushroom mushroom) throws InterruptedException {
		@SuppressWarnings("unchecked")
		// 内容
		List<Map<String, String>> contents = (List<Map<String, String>>) mushroom.getContent();
		// 主机
		String hostDir = cutIP(mushroom.getString("host"));
		// 日期
		String dateStr = sdf.format(new Date());
		// 保存目录
		String filePath = path + File.separator + dateStr + File.separator + hostDir + File.separator;
		mushroom.put("path", filePath);
		for (int i = 0; i < contents.size(); i++) {
			Map<String, String> content = contents.get(i);
			String command = content.get("command");
			String fileName = command.replaceAll("[\\s]", ".") + ".txt";
			String result = content.get("result");
			File writeFile = new File(filePath + fileName);
			try {
				FileUtils.writeStringToFile(writeFile, result, false);
				content.put("filePath", writeFile.getAbsolutePath());
			} catch (IOException e) {
				LOGGER.error("File:" + writeFile.getAbsolutePath() + " white failed!" + e.getMessage());
				LOGGER.debug(e.getMessage(), e);
			}
		}
		queue.put(mushroom);
	}

	@Override
	public Mushroom take() throws InterruptedException {
		return queue.take();
	}

	/**
	 * 载取字符串中第一个IP
	 * 
	 * @param s
	 * @return
	 */
	private static String cutIP(String s) {
		Matcher m = Pattern.compile("((\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}))").matcher(s);
		while (m.find()) {
			return m.group(1);
		}
		return s;
	}

}
