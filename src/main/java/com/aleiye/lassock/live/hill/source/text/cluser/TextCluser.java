package com.aleiye.lassock.live.hill.source.text.cluser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.lassock.api.Intelligence;
import com.aleiye.lassock.live.hills1.text.FileGeter;

/**
 * 事件驱动
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class TextCluser implements Cluser, Runnable {
	protected final File file;
	protected final String fileKey;
	protected Intelligence intelligence;
	protected CluserListener listener;
	// 状态
	protected CluserState state = CluserState.NORMAL;

	// 采集策略 默认为行采集策略
	protected PickPolicy pickPolic = new LinesPickPolicy();

	// 等待执行队列(单元对应Shade)
	private final BlockingQueue<TextCluser> normals;
	// 发生错误的Shade
	private final BlockingQueue<TextCluser> errors;
	// 应急备用通道(用于在关闭时保存唤醒阻塞无处存放的Shade)
	private final List<TextCluser> emergency;

	AtomicBoolean opend = new AtomicBoolean();

	public TextCluser(File file, CluserListener listener, BlockingQueue<TextCluser> normals,
			BlockingQueue<TextCluser> errors, List<TextCluser> emergency) {
		this.file = file;
		String key = FileGeter.getFileKey(file);
		fileKey = key;
		this.normals = normals;
		this.errors = errors;
		this.emergency = emergency;
	}

	@Override
	public void run() {
		try {
			this.pickPolic.pick(this);
		} catch (IOException e) {
			this.state = CluserState.ERR;
		} catch (InterruptedException e) {
			;
		}
	}

	public CluserState getStat() {
		return state;
	}

	public void returnQueue() {
		try {
			if (getStat() == CluserState.ERR) {
				errors.put(this);
			} else {
				normals.put(this);
			}
		} catch (InterruptedException e) {
			emergency.add(this);
		}
	}

	@Override
	public void open() throws IOException {
		doOpen();
		opend.compareAndSet(false, true);
	}

	@Override
	public void close() throws IOException {
		doClose();
		opend.compareAndSet(true, false);
	}

	@Override
	public boolean isOpen() {
		return opend.get();
	}

	@Override
	public CluserState getState() {
		return this.state;

	}

	@Override
	public void setListener(CluserListener listener) {
		this.listener = listener;
	}

	protected abstract void doOpen() throws IOException;

	protected abstract void doClose();

	/**
	 * 块数采集策略
	 * 
	 * @author ruibing.zhao
	 * @since 2015年5月25日
	 * @version 2.1.2
	 */
	public static class LinesPickPolicy implements PickPolicy {

		public void pick(Cluser shade) throws IOException, InterruptedException {
			shade.next();
		}
	}

	public Intelligence getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	public String getFileKey() {
		return fileKey;
	}
}
