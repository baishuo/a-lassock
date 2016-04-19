package com.aleiye.lassock.live.hill.source.text;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.lang.StringUtils;

/**
 * Project：collector
 * Created:2014年8月30日下午4:22:04
 * Copyright (c) 2014 aleiye
 * 根据有限正则，进行文件递归查找；目前不支持文件路径的正则查找，即输入的filePath不能包含正则的符号在内；
 * 但是支持对文件名的正则匹配；
 * 
 * @Author ywt
 */
public class FileFinder extends DirectoryWalker<File> {

	private List<Pattern> filterPatternList = new ArrayList<Pattern>();

	// 需要过滤的文件正则
	public FileFinder(String[] filterRegxs) {
		super();
		if (filterRegxs != null && filterRegxs.length > 0) {
			Pattern filterPattern = null;
			for (String filterRegx : filterRegxs) {
				if (StringUtils.isNotBlank(filterRegx) && !"null".equals(filterRegx)) {
					filterPattern = Pattern.compile(filterRegx);
					filterPatternList.add(filterPattern);
				}
			}
		}
	}

	@Override
	protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
		if (filterPatternList.size() > 0) {
			for (Pattern filterPattern : filterPatternList) {
				Matcher matcher = filterPattern.matcher(file.getCanonicalPath());
				if (matcher.find()) {
					return;
				}
			}
		}
		results.add(file);
	}

	public List<File> getFiles(String filePath) {
		String path = filePath.trim();
		List<File> result = new ArrayList<File>();
		try {
			File file = new File(path);
			// 如果为目录，直接进行递归查找
			if (file.isDirectory()) {
				walk(new File(path), result);
			} else {// 如果为文件，则需要考虑文件名的正则匹配
				// 解决windows路径斜杠问题
				// 构造正则表达式 将站位符替换为.*
				final Pattern p = Pattern.compile(file.getName().replaceAll("\\*", "[@@]").replaceAll("[@@]", ".*"));
				File[] files = file.getParentFile().listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						// 解决windows路径斜杠问题
						String name = pathname.getPath().replaceAll("\\\\", "/");
						Matcher matcher = p.matcher(name);
						return matcher.find() && pathname.isFile() && pathname.canRead();
					}
				});
				if (files == null) {
					return new ArrayList<File>();
				} else {
					return Arrays.asList(files);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
