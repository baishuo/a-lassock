package com.aleiye.lassock.live.hill.shade.text;

import java.io.IOException;

import com.aleiye.lassock.live.exception.SignRemovedException;

/**
 * 事件驱动
 * 
 * @author ruibing.zhao
 * @since 2015年5月18日
 * @version 2.1.2
 */
public abstract class TextShade implements Runnable {

	// 状态
	private Stat stat = Stat.NORMAL;
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
			this.setStat(Stat.ERR);
		} catch (InterruptedException e) {
			;
		} catch (SignRemovedException e) {
			this.setStat(Stat.ERR);
		}
	}

	// 移动偏移
	public void seek(long offset) {

	}

	// 是否可采集信息
	public abstract boolean canPick();

	// 采集信息
	public abstract boolean pick() throws IOException, SignRemovedException, InterruptedException;

	// 采集信息
	public abstract Stat selfCheck();

	public Stat getStat() {
		return stat;
	}

	// 状态设置 如果当前设置状态是保持状态,状态自增
	public void setStat(Stat stat) {
		this.stat = stat;
		switch (stat) {
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

	public int getEndConut() {
		return endConut;
	}

	public int getErrConut() {
		return errConut;
	}

	/**
	 * 采集状态
	 * <li>NORMAL: 正常</li> 
	 * <li>ERR: 异常</li> 
	 * <li>REMOVED: 已移除</li> 
	 * <li>END: 采集结束</li> 
	 * 
	 * @author ruibing.zhao
	 * @since 2015年6月6日
	 * @version 2.1.2
	 */
	public static enum Stat {
		NORMAL, ERR, REMOVED, END
	}

	/**
	 * 块数采集策略
	 * 
	 * @author ruibing.zhao
	 * @since 2015年5月25日
	 * @version 2.1.2
	 */
	public static class LinesPickPolicy implements PickPolicy {

		public void pick(TextShade shade) throws IOException, SignRemovedException, InterruptedException {
			if (shade.canPick()) {
				shade.pick();
			}
		}
	}
}
