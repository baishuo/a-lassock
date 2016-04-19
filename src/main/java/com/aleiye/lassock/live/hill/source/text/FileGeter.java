package com.aleiye.lassock.live.hill.source.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;

/**
 * 文件获取
 * 
 * @author ruibing.zhao
 * @since 2015年6月6日
 * @version 2.1.2
 */
public class FileGeter {

	public static String getFileByexec(String path, String inode) {
		String command = "find {0} -inum {1}";
		command = MessageFormat.format(command, path, inode);
		// 获取当前系统的环境。
		Runtime rt = Runtime.getRuntime();
		// 执行
		Process p = null;
		BufferedReader br = null;
		try {
			p = rt.exec(command);
			// 获取执行后的数据
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String msg = null;
			// 输出。
			while ((msg = br.readLine()) != null) {
				if (msg.startsWith(path)) {
					return msg;
				}
			}
		} catch (IOException e) {
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return null;
	}

	public static String getFile(String path, String key) {
		return getFile(new File(path), key);
	}

	// 扫描INODE 文件路径
	public static String getFile(File file, String key) {
		if (file.isFile()) {
			String r = getFileKey(file);
			if (r.equals(key)) {
				return file.getPath();
			}
		} else {
			for (File f : file.listFiles()) {
				String r = getFileKey(f);
				if (r.equals(key)) {
					return f.getPath();
				}
			}
			for (File dir : file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			})) {
				String p = getFile(dir, key);
				if (p != null) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileKey(File file, BasicFileAttributes bfa) {
		String ret = null;
		if (file.exists()) {
			String fileKey = bfa.fileKey().toString();
			ret = fileKey.substring(fileKey.lastIndexOf('=') + 1, fileKey.lastIndexOf(')'));
			return ret;

		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileKey(File file) {
		String ret = null;
		if (file.exists()) {
			Path path = file.toPath();
			BasicFileAttributes bfa;
			try {
				bfa = Files.readAttributes(path, BasicFileAttributes.class);
				String fileKey = bfa.fileKey().toString();
				ret = fileKey.substring(fileKey.lastIndexOf('=') + 1, fileKey.lastIndexOf(')'));
				return ret;
			} catch (IOException e) {
				;
			}
		}
		return null;
	}

	public static BasicFileAttributes getFileAttributes(String path) {
		File file = new File(path);
		return getFileAttributes(file);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static BasicFileAttributes getFileAttributes(File file) {
		if (file.exists()) {
			Path path = file.toPath();
			try {
				return Files.readAttributes(path, BasicFileAttributes.class);
			} catch (IOException e) {
				;
			}

		}
		return null;
	}
}
