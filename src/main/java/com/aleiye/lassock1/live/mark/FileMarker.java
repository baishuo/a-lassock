package com.aleiye.lassock1.live.mark;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.aleiye.lassock.util.ConfigUtils;

/**
 * 文件记录标记
 * 
 * @author ruibing.zhao
 * @since 2015年5月28日
 * @version 2.2.1
 */
public class FileMarker implements Marker<Long> {
	private static final Logger LOG = Logger.getLogger(FileMarker.class);
	private Map<String, Long> marks = Collections.synchronizedMap(new HashMap<String, Long>());
	//从当前系统中获取换行符，默认是"\n"  
	private String lineSeparator = System.getProperty("line.separator", "\n");

	private String path = ConfigUtils.getConfig().getString("marker.filePath");

	@Override
	public void load() {
		try {
			File pfile = new File(path);
			if (pfile.exists()) {
				List<String> lines = FileUtils.readLines(pfile);
				synchronized (marks) {
					for (String line : lines) {
						String[] posAry = line.split("=");
						if (posAry.length == 2) {
							marks.put(posAry[0], Long.valueOf(posAry[1]));
						}
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Get position error:", e);
		}
	}

	@Override
	public void mark(String key, Long t) {
		marks.put(key, t);
	}

	@Override
	public void save() throws Exception {
		File recordFile = new File(path);
		StringBuilder sb = new StringBuilder();
		synchronized (marks) {
			for (Entry<String, Long> entry : marks.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(lineSeparator);
			}
		}
		if (0 != sb.length()) {
			try {
				// 写入磁盘大约耗时10毫秒
				FileUtils.writeStringToFile(recordFile, sb.toString());
			} catch (IOException e) {
				LOG.error("Recor error:", e);
			}
		}
	}

	@Override
	public Long getMark(String key) {
		return marks.get(key);
	}

	@Override
	public void close() throws IOException {
		marks.clear();
		marks = null;
	}
}
