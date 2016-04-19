package com.aleiye.lassock.live.hill.source.text.cluser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.IOUtils;

import com.aleiye.event.constants.EventKey;
import com.aleiye.lassock.api.Intelligence.ShadeState;
import com.aleiye.lassock.live.hills1.text.FileGeter;
import com.aleiye.lassock.live.model.GeneralMushroom;
import com.aleiye.lassock.util.MarkUtil;

/**
 * 文件采集块
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.2
 */
public class FileCluser extends TextCluser {

	public FileCluser(File file, CluserListener listener, BlockingQueue<TextCluser> normals,
			BlockingQueue<TextCluser> errors, List<TextCluser> emergency) {
		super(file, listener, normals, errors, emergency);
	}

	/** 文件 */
	// 读取文件 ds
	RandomAccessFile readFile;
	// 文件频道es
	FileChannel channel;
	// 栈读取偏移si
	long readOffset = 0;
	// 结尾偏移di
	long eofIndex = 0;

	// 当前行开始位置bx
	long lineStartIndex = 0;
	// 当前行结束位dx
	long lineEndIndex = 0;

	/** 读取栈 */
	// 栈
	ByteBuffer readBuf;
	// 重置位
	int resetIndex = 0;
	// 栈读取位
	int bufReadIndex = 0;

	// 累加缓存器,用于两次读取之间行头行尾合并 ax
	ByteBuffer addBuf = null;

	// 计数缓存器 cx
	long count = 0;

	// long ip = 0;
	// RandomAccessFile cs;

	// int flag;

	private void makeMushroom(byte[] content) throws InterruptedException {
		GeneralMushroom mr = new GeneralMushroom();
		mr.setIntelligence(this.intelligence);
		mr.setBody(content);
		mr.getHeaders().put(EventKey.FILEPATH, this.file.getPath());
		mr.getHeaders().put("soffset", String.valueOf(this.lineStartIndex));
		mr.getHeaders().put("eoffset", String.valueOf(this.lineEndIndex));
		listener.picked(mr);
		this.intelligence.put("offset", this.readOffset);
		MarkUtil.mark(this.file.getPath(), this.lineEndIndex);
	}

	/**
	 * 移动偏读取偏移
	 */
	public void seek(long offset) {
		if (offset > 0) {
			this.readOffset = offset;
			this.lineEndIndex = readOffset;
			this.intelligence.put("offset", this.readOffset);
		}
	}

	public void doOpen() throws IOException {
		selfCheck();
		readFile.seek(this.readOffset);
		// 初始缓存器
		readBuf = ByteBuffer.allocate(2048);
		readBuf.clear();

	}

	// 读入栈
	@Override
	public void next() throws IOException, InterruptedException {
		if (!canPick()) {
			return;
		}
		// 重置读取行数（相对于栈）
		count = 0;
		// 可用空间移动最大，但保留上次未读完数据
		// ss.limit(ss.capacity());
		int rl;
		// 读取
		if ((rl = channel.read(readBuf)) > 0) {
			this.readOffset += rl;
			// 反转缓冲区
			this.readBuf.flip();
			this.readLines();
			return;
		}
		return;
	}

	// 读入行
	private void readLines() throws IOException, InterruptedException {
		// 行开始的位置、结束位置
		resetIndex = readBuf.position();
		bufReadIndex = readBuf.limit();
		// 标记为0
		readBuf.mark();
		boolean eol = false;
		while (readBuf.hasRemaining()) {
			switch (readBuf.get()) {
			case '\n':
				eol = true;
				break;
			case '\r':
				// 判断下一个字符是不是换行
				if ((readBuf.get()) == '\n') {
					eol = true;
				}
				break;
			}
			if (eol) {
				// 回朔一位
				readBuf.position(readBuf.position() - 1);
				this.readLine();
				// 跳过换行符
				readBuf.get();
				// 标记行起始位
				readBuf.mark();
				bufReadIndex = readBuf.position();
				// 新行开始位
				resetIndex = bufReadIndex;
				lineEndIndex++;
				// 重新找行
				eol = false;
			}
		}
		// 没有读到行
		if (count == 0) {
			// 读到尾
			if (this.readOffset == this.eofIndex) {
				this.readLine();
			} else {
				// 一次读取读不完一行
				// 将当前读取数据寄存在AX
				if (addBuf == null) {
					addBuf = ByteBuffer.allocate(readBuf.capacity() + readBuf.limit());
					addBuf.clear();
				}
				// 当缓存可用部分小于当前读取数据时 ，增加缓存容量
				if (addBuf.remaining() < readBuf.limit()) {
					ByteBuffer tax = ByteBuffer.allocate(addBuf.capacity() + readBuf.limit());
					tax.clear();
					addBuf.flip();
					tax.put(addBuf);
					addBuf = tax;
				}
				readBuf.flip();
				addBuf.put(readBuf);
				readBuf.mark();
			}
		}
		// 结尾
		else {
			if (this.readOffset == this.eofIndex) {
				// 标记行开始位
				// ss.mark();
				this.readLine();
			}
		}
		// 重置到标记位
		readBuf.reset();
		readBuf.compact();
	}

	// 读行
	private void readLine() throws InterruptedException {
		// 换行位置
		bufReadIndex = readBuf.position();
		// 计算该行开始移至上行结束位的下一位
		if (lineEndIndex != 0) {
			lineStartIndex = lineEndIndex + 1;
		}

		int size = 0;
		// 如果有缓存数据，将加上缓存长度
		if (addBuf != null) {
			int offset = 0;
			size = bufReadIndex - resetIndex + addBuf.position();
			byte[] line = new byte[size];
			addBuf.flip();
			// 读取缓存长度数据
			addBuf.get(line, offset, addBuf.limit());
			offset = addBuf.limit();
			// 续读 SS;
			// 重置到标记位
			readBuf.reset();
			// 读行数据（换行符前一位）
			readBuf.get(line, offset, bufReadIndex - resetIndex);
			// 释放AX
			addBuf = null;
			lineEndIndex += size;
			// 传输
			makeMushroom(line);
		} else {
			size = bufReadIndex - resetIndex;
			if (size > 0) {
				byte[] line = new byte[size];
				// 重置到标记位
				readBuf.reset();
				// 读行数据（换行符前一位）
				readBuf.get(line);
				lineEndIndex += size;
				// 传输
				makeMushroom(line);
			}
		}
		count++;
	}

	public synchronized void setState(CluserState state) {
		this.state = state;
	}

	// 是否可以采集
	public synchronized boolean canPick() {
		// 是否移除
		if (this.state == CluserState.REMOVED) {
			return false;
		}
		if (this.state == CluserState.END) {
			return false;
		}
		// 是否读到尾
		if (this.readOffset == this.eofIndex) {
			this.setState(CluserState.END);
			this.intelligence.setState(ShadeState.END);
			return false;
		}
		return true;
	}

	/**
	 * 自检是否可续读
	 */
	public CluserState selfCheck() {
		if (!this.isOpen()) {
			throw new IllegalStateException("File shade not open!");
		}
		CluserState stat = CluserState.NORMAL;
		if (this.state == CluserState.REMOVED) {
			return CluserState.REMOVED;
		} else {
			// 先初始化文件
			try {
				if (channel != null) {
					IOUtils.closeQuietly(channel);
				}
				if (readFile != null) {
					IOUtils.closeQuietly(channel);
				}
				readFile = new RandomAccessFile(file, "r");
				// 再验证是否是同一文件
				String key = FileGeter.getFileKey(file);
				if (this.fileKey.equals(key)) {
					// 是同一文件
					long length = file.length();
					if (this.eofIndex < length) {
						this.eofIndex = length;
						channel = readFile.getChannel();
						readFile.seek(this.readOffset);
					} else {
						if (this.eofIndex > length) {
							this.readOffset = this.eofIndex = length;
							readFile.seek(this.readOffset);
						}
						stat = CluserState.END;
					}
				} else {
					stat = CluserState.ERR;
				}
			} catch (IOException e) {
				stat = CluserState.ERR;
			}
		}
		this.state = stat;
		switch (stat) {
		case NORMAL:
			this.intelligence.setState(ShadeState.NORMAL);
			break;
		case END:
			this.intelligence.setState(ShadeState.END);
			break;
		case ERR:
			this.intelligence.setState(ShadeState.ERROR);
			break;
		default:
			break;
		}
		return this.getStat();
	}

	@Override
	public void doClose() {
		if (channel != null) {
			IOUtils.closeQuietly(channel);
		}
		if (readFile != null) {
			IOUtils.closeQuietly(readFile);
		}
		channel = null;
		readFile = null;
	}
}
