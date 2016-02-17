package com.aleiye.lassock.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.lang.Sistem.OSType;

/**
 * 系统信息
 * @author ruibing.zhao
 * @since 2015年5月19日
 * @version 2.2.1
 */
public class SystemInfo {
	private static final Logger logger = Logger.getLogger(SystemInfo.class);

	// 针对Unix系统 主文件系统
	public static FileSystem main;
	// Unix挂载文件系统 ,window 扩展文件系统
	public static List<FileSystem> extended;
	static {
		extended = new ArrayList<FileSystem>();
		Sigar sigar = null;
		try {
			sigar = SigarUtils.getSigar();
			FileSystem fslist[] = sigar.getFileSystemList();
			for (int i = fslist.length - 1; i >= 0; i--) {
				FileSystem fs = fslist[i];
				if (Sistem.getSysType() != OSType.WINDOWS) {
					if (fs.getDirName().equals("/")) {
						main = fs;
						continue;
					}
				}
				extended.add(fs);
			}
		} catch (SigarException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} finally {
			sigar.close();
		}
	}

	/**
	 * 通过路径获取所在磁盘分区
	 * @param path
	 * @return
	 */
	public static FileSystem getFSbyPath(String path) {
		if (extended.size() == 0) {
			return main;
		} else {
			for (FileSystem fs : extended) {
				if (path.startsWith(fs.getDirName())) {
					return fs;
				}
			}
			return main;
		}
	}
}
