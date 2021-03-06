package com.aleiye.lassock.live.hills1.text;

import com.aleiye.lassock.live.hills1.Sign;


/**
 * 文档采集标识
 * 
 * @author ruibing.zhao
 * @since 2015年6月6日
 * @version 2.1.2
 */
public class TextSign extends Sign {
	/** FILE */
	// 文件
	private String path;
	// unix系统nodeid
	private String nodeId;
	// 文件是否变更
	private boolean changed = false;

	private int changedReadCount = 0;

	// 创建时间
	private long ct = 0;
	// 最后修改时间
	private long lmt = 0;

	private String fs;
	// 续读偏移量
	private long offset = 0;
	// 每次读取大小
	private int readLength = 2048;

	@Override
	public String getDescription() {
		return path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public int getChangedReadCount() {
		return changedReadCount;
	}

	public void setChangedReadCount(int changedReadCount) {
		this.changedReadCount = changedReadCount;
	}

	public long getCt() {
		return ct;
	}

	public void setCt(long ct) {
		this.ct = ct;
	}

	public long getLmt() {
		return lmt;
	}

	public void setLmt(long lmt) {
		this.lmt = lmt;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getReadLength() {
		return readLength;
	}

	public void setReadLength(int readLength) {
		this.readLength = readLength;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
