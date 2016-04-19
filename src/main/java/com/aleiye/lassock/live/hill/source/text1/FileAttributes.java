package com.aleiye.lassock.live.hill.source.text1;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;

import com.aleiye.lassock.lang.Sistem;

/**
 * 文件信息
 * 
 * @author ruibing.zhao
 * @since 2016年4月18日
 * @version 1.0
 */
public class FileAttributes {
	// 文件路径
	private final File file;
	// 文件唯一标识
	private final String fileKey;
	// 文件信息
	private final BasicFileAttributes attributes;

	public FileAttributes(String path) {
		this(new File(path));
	}

	public FileAttributes(File file) {
		this.file = file;
		this.attributes = FileGeter.getFileAttributes(file);
		switch (Sistem.getSysType()) {
		case WINDOWS:
			fileKey = this.file.getName();
			break;
		default:
			fileKey = FileGeter.getFileKey(file, attributes);
			break;
		}
	}

	public String getFileKey() {
		return fileKey;
	}

	public BasicFileAttributes getAttributes() {
		return attributes;
	}

	public File getFile() {
		return file;
	}
}
