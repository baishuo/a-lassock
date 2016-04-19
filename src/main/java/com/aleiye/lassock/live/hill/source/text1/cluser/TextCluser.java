package com.aleiye.lassock.live.hill.source.text1.cluser;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.hill.source.text1.CluserSign;
import com.aleiye.lassock.live.hill.source.text1.PickPolicy;

/**
 * 事件驱动
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class TextCluser implements Cluser, Runnable {

	protected final AtomicBoolean done = new AtomicBoolean(false);
	protected final CluserSign sign;
	protected CluserListener listener;

	// 等待执行队列(单元对应Shade)
	private final BlockingQueue<TextCluser> normals;
	// 发生错误的Shade
	private final BlockingQueue<TextCluser> errors;
	// 应急备用通道(用于在关闭时保存唤醒阻塞无处存放的Shade)
	private final List<TextCluser> emergency;

	public TextCluser(CluserSign sign, BlockingQueue<TextCluser> normals, BlockingQueue<TextCluser> errors,
			List<TextCluser> emergency) {
		this.sign = sign;
		this.normals = normals;
		this.errors = errors;
		this.emergency = emergency;
	}

	// 状态
	private CluserState state = CluserState.NORMAL;
	// 结束状态保持次数
	private int endConut = 0;
	// 异常状态保持次数
	private int errConut = 0;

	// 采集策略 默认为行采集策略
	protected PickPolicy pickPolic = new LinesPickPolicy();

	@Override
	public void run() {
		try {
			this.pickPolic.pick(this);
		} catch (IOException e) {
			this.setState(CluserState.ERR);
			// TODO
			// this.intelligence.setErrorCount(this.intelligence.getErrorCount()
			// + 1);
		} catch (InterruptedException e) {
			;
		} catch (SignRemovedException e) {
			this.setState(CluserState.ERR);
		}
	}

	// 移动偏移
	public void seek(long offset) {

	}

	// 是否可采集信息
	public abstract boolean canPick();

	// 采集信息
	public abstract CluserState selfCheck();

	public int getEndConut() {
		return endConut;
	}

	public int getErrConut() {
		return errConut;
	}

	@Override
	public boolean isOpen() {
		return this.done.get();
	}

	// 状态设置 如果当前设置状态是保持状态,状态自增
	@Override
	public void setState(CluserState state) {
		this.state = state;
		switch (state) {
		case END:
			errConut = 0;
			endConut++;
			break;
		case ERR:
			errConut++;
			endConut = 0;
			break;

		default:
			errConut = 0;
			endConut = 0;
			break;
		}
	}

	@Override
	public CluserState getState() {
		return state;
	}

	@Override
	public void setListener(CluserListener listener) {
		this.listener = listener;
	}

	@Override
	public void returnQueue() {
		try {
			if (getState() == CluserState.ERR) {
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
		if (isOpen()) {
			throw new IllegalStateException("Cluser was opend!");
		}
		doOpen();
		done.set(true);
	}

	@Override
	public void close() throws IOException {
		if (!isOpen()) {
			throw new IllegalStateException("Cluser is not opend!");
		}
		doClose();
		done.set(false);
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

		public void pick(TextCluser shade) throws IOException, SignRemovedException, InterruptedException {
			if (shade.canPick()) {
				shade.next();
			}
		}
	}

	public CluserSign getSign() {
		return sign;
	}
}
