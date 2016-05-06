package com.aleiye.lassock.live.hill.source.text.cluser;

import java.io.Closeable;
import java.io.IOException;

import com.aleiye.lassock.live.exception.SignRemovedException;

/**
 * 游标
 * 
 * @author ruibing.zhao
 * @since 2016年4月15日
 * @version 1.0
 */
public interface Cluser extends Closeable {
	/**
	 * 打开
	 * @throws IOException 
	 */
	void open() throws IOException;

	/**
	 * 是否打开
	 * @return
	 */
	boolean isOpen();

	/**
	 * 推进
	 * @return 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws SignRemovedException 
	 */
	boolean next() throws IOException, InterruptedException, SignRemovedException;

	/**
	 * 设置状态
	 * @param state
	 */
	void setState(CluserState state);

	/**
	 * 状态
	 */
	CluserState getState();

	/**
	 * 设置监听器
	 * @param listener
	 */
	void setListener(CluserListener listener);

	/**
	 * 偏移移动
	 * @param offset
	 */
	public void seek(long offset);

	/**
	 * 返回对列
	 */
	void returnQueue();
}
