package com.aleiye.lassock.live.hill.source.text;

import java.io.File;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

/**
 * 插件监听
 * 
 * @author ruibing.zhao
 * @since 2015年12月16日
 * @version 2.3
 */
public class FileMonitor {
	// 事件处理
	final FileListener fileLstener;
	DefaultFileMonitor fm;
	FileSystemManager fsManager = null;

	public FileMonitor(FileListener fileLstener) {
		this.fileLstener = fileLstener;
	}

	public void start() throws FileSystemException {
		fsManager = VFS.getManager();
		// 定义一个监视器及事件处理程序
		fm = new DefaultFileMonitor(fileLstener);
		fm.setRecursive(false); // 设置为级联监控
		fm.start(); // 启动监视器
	}

	public FileObject addFile(File file) throws FileSystemException {
		FileObject fo = fsManager.toFileObject(file);
		fm.addFile(fo);
		return fo;
	}

	public FileObject addFile(String path) throws FileSystemException {
		FileObject fo = fsManager.resolveFile(path);
		fm.addFile(fo);
		return fo;
	}

	public void remove(FileObject fo) {
		fm.removeFile(fo);
	}

	public static void main(String args[]) {
		FileMonitor listen = new FileMonitor(new FileListener() {
			@Override
			public void fileDeleted(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getURL().getPath());
			}

			@Override
			public void fileCreated(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getURL().getPath());
			}

			@Override
			public void fileChanged(FileChangeEvent event) throws Exception {
				System.out.println(event.getFile().getURL().getPath());
			}
		});
		try {
			listen.start();
			listen.addFile("/Users/asuroslove/data/text.txt");
		} catch (FileSystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		if (fm != null) {
			fm.stop();
		}
	}
}
