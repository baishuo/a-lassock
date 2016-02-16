package com.aleiye.lassock.live.hill.shade.text;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.model.Mushroom;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.MarkUtil;

/**
 * 文件采集块
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.2
 */
//public class FileShade extends TextShade {
//	/** 文件 */
//	// 读取文件
//	RandomAccessFile ds;
//	// 文件频道
//	FileChannel es;
//	// 栈读取偏移
//	long si = 0;
//	// 结尾偏移
//	long di = 0;
//
//	// 当前行开始位置
//	long bx = 0;
//	// 当前行结束位
//	long dx = 0;
//
//	/** 读取栈 */
//	// 栈
//	ByteBuffer ss;
//	// 重置位
//	int bp = 0;
//	// 栈读取位
//	int sp = 0;
//
//	// 累加缓存器,用于两次读取之间行头行尾合并
//	ByteBuffer ax = null;
//
//	// 计数缓存器
//	long cx = 0;
//
//	// long ip = 0;
//	// RandomAccessFile cs;
//
//	// int flag;
//
//	public FileShade(TextSign unit, Basket basket) {
////		super(unit, basket);
//	}
//
//	private void makeMushroom(byte[] content) throws SignRemovedException, InterruptedException {
//		Mushroom mr = new Mushroom();
//		mr.setContent(content);
//		mr.setPath(this.sign.getPath());
//		mr.setSoffset(this.bx);
//		mr.setEoffset(this.dx);
//		putMushroom(ti,mr);
//		MarkUtil.mark(this.sign.getKey(), this.dx);
//	}
//
//	/**
//	 * 移动偏读取偏移
//	 */
//	public void seek(long offset) {
//		if (offset > 0) {
//			this.si = offset;
//			this.dx = si;
//		}
//	}
//
//	@Override
//	public void open() throws IOException {
//		if (done.compareAndSet(false, true)) {
//			selfCheck();
//			// 初始缓存器
//			ss = ByteBuffer.allocate(this.sign.getReadLength());
//			ss.clear();
//			ds.seek(this.si);
//		}
//	}
//
//	// 读入栈
//	public boolean pick() throws IOException, SignRemovedException, InterruptedException {
//		if (!canPick()) {
//			return false;
//		}
//		// 重置读取行数（相对于栈）
//		cx = 0;
//		// 可用空间移动最大，但保留上次未读完数据
//		// ss.limit(ss.capacity());
//		int rl;
//		// 读取
//		if ((rl = es.read(ss)) > 0) {
//			this.si += rl;
//			// 反转缓冲区
//			this.ss.flip();
//			this.readLines();
//			return true;
//		}
//		return false;
//	}
//
//	// 读入行
//	private void readLines() throws IOException, SignRemovedException, InterruptedException {
//		// 行开始的位置、结束位置
//		bp = ss.position();
//		sp = ss.limit();
//		// 标记为0
//		ss.mark();
//		boolean eol = false;
//		while (ss.hasRemaining()) {
//			switch (ss.get()) {
//			case '\n':
//				eol = true;
//				break;
//			case '\r':
//				// 判断下一个字符是不是换行
//				if ((ss.get()) == '\n') {
//					eol = true;
//				}
//				break;
//			}
//			if (eol) {
//				// 回朔一位
//				ss.position(ss.position() - 1);
//				this.readLine();
//				// 跳过换行符
//				ss.get();
//				// 标记行起始位
//				ss.mark();
//				sp = ss.position();
//				// 新行开始位
//				bp = sp;
//				dx++;
//				// 重新找行
//				eol = false;
//			}
//		}
//		// 没有读到行
//		if (cx == 0) {
//			// 读到尾
//			if (this.si == this.di) {
//				this.readLine();
//			} else {
//				// 一次读取读不完一行
//				// 将当前读取数据寄存在AX
//				if (ax == null) {
//					ax = ByteBuffer.allocate(ss.capacity() + ss.limit());
//					ax.clear();
//				}
//				// 当缓存可用部分小于当前读取数据时 ，增加缓存容量
//				if (ax.remaining() < ss.limit()) {
//					ByteBuffer tax = ByteBuffer.allocate(ax.capacity() + ss.limit());
//					tax.clear();
//					ax.flip();
//					tax.put(ax);
//					ax = tax;
//				}
//				ss.flip();
//				ax.put(ss);
//				ss.mark();
//			}
//		}
//		// 结尾
//		else {
//			if (this.si == this.di) {
//				// 标记行开始位
//				// ss.mark();
//				this.readLine();
//			}
//		}
//		// 重置到标记位
//		ss.reset();
//		ss.compact();
//	}
//
//	// 读行
//	private void readLine() throws SignRemovedException, InterruptedException {
//		// 换行位置
//		sp = ss.position();
//		// 计算该行开始移至上行结束位的下一位
//		if (dx != 0) {
//			bx = dx + 1;
//		}
//
//		int size = 0;
//		// 如果有缓存数据，将加上缓存长度
//		if (ax != null) {
//			int offset = 0;
//			size = sp - bp + ax.position();
//			byte[] line = new byte[size];
//			ax.flip();
//			// 读取缓存长度数据
//			ax.get(line, offset, ax.limit());
//			offset = ax.limit();
//			// 续读 SS;
//			// 重置到标记位
//			ss.reset();
//			// 读行数据（换行符前一位）
//			ss.get(line, offset, sp - bp);
//			// 释放AX
//			ax = null;
//			dx += size;
//			// 传输
//			makeMushroom(line);
//		} else {
//			size = sp - bp;
//			if (size > 0) {
//				byte[] line = new byte[size];
//				// 重置到标记位
//				ss.reset();
//				// 读行数据（换行符前一位）
//				ss.get(line);
//				dx += size;
//				// 传输
//				makeMushroom(line);
//			}
//		}
//		cx++;
//	}
//
//	// 是否可以采集
//	@Override
//	public boolean canPick() {
//		// 是否打开
//		if (!this.isOpen()) {
//			return false;
//		}
//		// 是否移除
//		if (this.sign.isRemoved()) {
//			this.setStat(Stat.REMOVED);
//			return false;
//		}
//		// 是否读到尾
//		if (this.si == this.di) {
//			this.setStat(Stat.END);
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 自检是否可续读
//	 */
//	@Override
//	public Stat selfCheck() {
//		if (!this.isOpen()) {
//			throw new IllegalStateException("File shade not open!");
//		}
//		Stat stat = Stat.NORMAL;
//		if (this.sign.isRemoved()) {
//			stat = Stat.REMOVED;
//		} else {
//			File file = new File(this.sign.getPath());
//			// 先初始化文件
//			try {
//				if (es != null) {
//					CloseableUtils.closeQuietly(es);
//				}
//				if (ds != null) {
//					CloseableUtils.closeQuietly(es);
//				}
//				ds = new RandomAccessFile(file, "r");
//				// 再验证是否是同一文件
//				String key = FileGeter.getFileKey(file);
//				if (this.sign.getNodeId().equals(key)) {
//					// 是同一文件
//					long length = file.length();
//					if (this.di < length) {
//						this.di = length;
//						es = ds.getChannel();
//						ds.seek(this.si);
//					} else {
//						stat = Stat.END;
//					}
//				} else {
//					stat = Stat.ERR;
//				}
//			} catch (IOException e) {
//				stat = Stat.ERR;
//			}
//		}
//		this.setStat(stat);
//		return this.getStat();
//	}
//
//	@Override
//	public void close() {
//		if (done.compareAndSet(true, false)) {
//			if (es != null) {
//				CloseableUtils.closeQuietly(es);
//			}
//			if (ds != null) {
//				CloseableUtils.closeQuietly(ds);
//			}
//			es = null;
//			ds = null;
//		}
//	}
//}
