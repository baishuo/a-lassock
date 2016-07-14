package com.aleiye.lassock.live.hill.source.text.cluser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.aleiye.event.constants.EventKey;
import com.aleiye.lassock.live.exception.SignRemovedException;
import com.aleiye.lassock.live.hill.source.text.CluserSign;
import com.aleiye.lassock.live.hill.source.text.FileAttributes;
import com.aleiye.lassock.live.model.GeneralMushroom;
import com.aleiye.lassock.util.MarkUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 文件采集块
 * 
 * @author ruibing.zhao
 * @since 2015年5月22日
 * @version 2.1.2
 */
public class FileCluser extends TextCluser {

	public FileCluser(CluserSign sign, BlockingQueue<TextCluser> normals, BlockingQueue<TextCluser> errors,
			List<TextCluser> emergency) {
		super(sign, normals, errors, emergency);
	}

	/** 文件 */
	// 读取文件
	RandomAccessFile ds;
	// 文件频道
	FileChannel es;
	// 栈读取偏移
	long si = 0;
	// 结尾偏移
	long di = 0;

	// 当前行开始位置
	long bx = 0;
	// 当前行结束位
	long dx = 0;

	/** 读取栈 */
	// 栈
	ByteBuffer ss;
	// 重置位
	int bp = 0;
	// 栈读取位
	int sp = 0;
	//ss中读取的位置
	int bbp = 0;

	// 累加缓存器,用于两次读取之间行头行尾合并
	ByteBuffer ax = null;

	// 计数缓存器
	long cx = 0;

	//数据开头匹配正则
	String regular;

	//是否匹配正则
	boolean isRegular;

	//缓存行数据，用于将一段内容合并
	ByteBuffer arx = null;

	//保存当前读取到的一行
	byte[] linesByte ;

	//当前读取行是否匹配到正则
	boolean isRegularTrue = false;

	//初始化正则表达式
	Pattern p = null;

	//存放放入缓存中的大小在读入流中的位置
	int bpx = 0 ;

	//存储字节位置
	int byp = 0;

	// long ip = 0;
	// RandomAccessFile cs;

	// int flag;

	private void makeMushroom(byte[] content) throws SignRemovedException, InterruptedException {
		GeneralMushroom mr = new GeneralMushroom();
		mr.setBody(content);
		mr.getHeaders().put(EventKey.FILEPATH, this.sign.getPath());
		mr.getHeaders().put("soffset", String.valueOf(this.bx));
		mr.getHeaders().put("eoffset", String.valueOf(this.dx));
		this.listener.picked(mr);
		MarkUtil.mark(this.sign.getKey(), this.dx);
	}

	/**
	 * 移动偏读取偏移
	 */
	public void seek(long offset) {
		if (offset > 0) {
			this.si = offset;
			this.dx = si;
			// this.intelligence.put("offset", this.si);
		}
	}

	@Override
	public void doOpen() throws IOException {
		if (selfCheck() == CluserState.NORMAL) {
			// 初始缓存器
			this.regular = this.sign.getRegular();
			if(!StringUtils.isEmpty(this.regular)){
				isRegular = true;
				p = Pattern.compile(regular);
			}
			ss = ByteBuffer.allocate(this.sign.getReadLength());
			ss.clear();
			ds.seek(this.si);
		}
	}

	// 读入栈
	public boolean next() throws IOException, SignRemovedException, InterruptedException {
		if (!canPick()) {
			return false;
		}
		// 重置读取行数（相对于栈）
		cx = 0;
		// 可用空间移动最大，但保留上次未读完数据
		// ss.limit(ss.capacity());
		int rl;
		// 读取
		if ((rl = es.read(ss)) > 0) {
			this.si += rl;
			// 反转缓冲区
			this.ss.flip();
			//位置初始化
			bbp = 0;
			bpx = 0;
			if(isRegular){
				this.readLinesByRegular();
			}else{
				this.readLines();
			}
			return true;
		}
		return false;
	}

	// 读入行
	private void readLines() throws IOException, SignRemovedException, InterruptedException {
		// 行开始的位置、结束位置
		bp = ss.position();
		sp = ss.limit();
		// 标记为0
		ss.mark();
		boolean eol = false;
		while (ss.hasRemaining()) {
			switch (ss.get()) {
			case '\n':
				eol = true;
				break;
			case '\r':
				// 判断下一个字符是不是换行
				if ((ss.get()) == '\n') {
					eol = true;
				}
				break;
			}
			if (eol) {
				// 回朔一位
				ss.position(ss.position() - 1);
				this.readLine();
				// 跳过换行符
				ss.get();
				// 标记行起始位
				ss.mark();
				sp = ss.position();
				// 新行开始位
				bp = sp;
				dx++;
				// 重新找行
				eol = false;
			}
		}
		// 没有读到行
		if (cx == 0) {
			// 读到尾
			if (this.si == this.di) {
				this.readLine();
			} else {
				// 一次读取读不完一行
				// 将当前读取数据寄存在AX
				if (ax == null) {
					ax = ByteBuffer.allocate(ss.capacity() + ss.limit());
					ax.clear();
				}
				// 当缓存可用部分小于当前读取数据时 ，增加缓存容量
				if (ax.remaining() < ss.limit()) {
					ByteBuffer tax = ByteBuffer.allocate(ax.capacity() + ss.limit());
					tax.clear();
					ax.flip();
					tax.put(ax);
					ax = tax;
				}
				ss.flip();
				ax.put(ss);
				ss.mark();
			}
		}
		// 结尾
		else {
			if (this.si == this.di) {
				// 标记行开始位
				// ss.mark();
				this.readLine();
			}
		}
		// 重置到标记位
		ss.reset();
		ss.compact();
	}

	private void readLinesByRegular() throws IOException, SignRemovedException, InterruptedException {
		// 行开始的位置、结束位置
		bp = ss.position();
		sp = ss.limit();
		// 标记为0
		ss.mark();
		boolean eol = false;
		while (ss.hasRemaining()) {
			switch (ss.get()) {
				case '\n':
					isRegularTrue = false;
					eol = writeBuffer();
					break;
				case '\r':
					// 判断下一个字符是不是换行
					if ((ss.get()) == '\n') {
						isRegularTrue = false;
						eol = writeBuffer();
					}					break;
			}
			if (eol) {
				// 回朔一位
				ss.position(ss.position() - 1);//				this.readLine();
				this.readLineByRegular();
				// 跳过换行符
				ss.get();
				// 标记行起始位
				ss.mark();
				sp = ss.position();
				// 新行开始位
				bp = sp;
				dx++;
				// 重新找行
				eol = false;
			}
		}
		// 没有读到行
		if (cx == 0) {
			// 读到尾
			if (this.si == this.di) {
				this.readLineByRegular();
			} else {
				// 一次读取读不完一行
				// 将当前读取数据寄存在AX
				if (arx == null) {
					arx = ByteBuffer.allocate(ss.capacity() + ss.limit());
					arx.clear();
				}else{
					arx.position(arx.position() - 1);
					byte lineLast = arx.get();
					int po = ss.position();
					ss.reset();
					int size = po - ss.position();
					byte[] line = new byte[size];
					ss.get(line);
					String linesString = new String(line);
					//匹配当前读取的行的内容是否匹配正则
					Matcher m = p.matcher(linesString);
					m.matches();
					if((lineLast == '\n' || lineLast == '\r') && m.matches()){
						size = arx.limit();
						arx.flip();
						line = new byte[size];
						arx.get(line);
						dx += size;
						// 传输
						makeMushroom(line);
						cx ++;
						arx = null;
						arx = ByteBuffer.allocate(ss.capacity() + ss.limit());
						arx.clear();
					}
				}

				// 当缓存可用部分小于当前读取数据时 ，增加缓存容量
				if (arx.remaining() < ss.limit()) {
					ByteBuffer tax = ByteBuffer.allocate(arx.capacity() + ss.limit());
					tax.clear();
					arx.flip();
					tax.put(arx);
					arx = tax;
				}
//				ss.flip();
				ss.reset();
				arx.put(ss);
				ss.mark();
			}
		}
		// 结尾
		else {
			if (this.si == this.di) {
				// 标记行开始位
				this.readLineByRegular();
			}
		}
		// 重置到标记位
		ss.reset();
		ss.compact();
	}

	// 读行
	private void readLine() throws SignRemovedException, InterruptedException {
		// 换行位置
		sp = ss.position();
		// 计算该行开始移至上行结束位的下一位
		if (dx != 0) {
			bx = dx + 1;
		}

		int size = 0;
		// 如果有缓存数据，将加上缓存长度
		if (ax != null) {
			int offset = 0;
			size = sp - bp + ax.position();
			byte[] line = new byte[size];
			ax.flip();
			// 读取缓存长度数据
			ax.get(line, offset, ax.limit());
			offset = ax.limit();
			// 续读 SS;
			// 重置到标记位
			ss.reset();
			// 读行数据（换行符前一位）
			ss.get(line, offset, sp - bp);
			// 释放AX
			ax = null;
			dx += size;
			// 传输
			makeMushroom(line);
		} else {
			size = sp - bp;
			if (size > 0) {
				byte[] line = new byte[size];
				// 重置到标记位
				ss.reset();
				// 读行数据（换行符前一位）
				ss.get(line);
				dx += size;
				// 传输
				makeMushroom(line);
			}
		}
		cx++;
	}

	// 读行
	private void readLineByRegular() throws SignRemovedException, InterruptedException {
		if (dx != 0) {
			bx = dx + 1;
		}
		int size = 0;
		byte[] line;
		//如果已经读完，把缓存内容采集然后再把剩下的数据采集了
		if(ss.limit() == ss.position()){
			// 换行位置
			sp = ss.limit();
			//先采集缓存内容
			if(arx != null){
				//判断是否\n结尾
				ss.position(ss.position() - 1);
				byte lastByte = ss.get();
				boolean isLF = (lastByte == '\n' || lastByte == '\r');
				if(isLF && isRegularTrue){//如果此次匹配到则分两次采集
					bothTransmission();
				}else if (isLF && !isRegularTrue){//没有匹配到则跟缓存同时采集
					leaveTransmission();
				}else{//未换行则进行匹配
					ss.reset();
					size = ss.limit() - ss.position();
					line = new byte[size];
					byp = ss.position();
					ss.get(line);
					String linesString = new String(line);
					//匹配当前读取的行的内容是否匹配正则
					Matcher m = p.matcher(linesString);
					if(m.matches()){
						leaveTransmission();
					}else{
						bothTransmission();
					}
				}
			}else{
				size = ss.limit();
				line = new byte[size];
				ss.position(0);
				ss.get(line, 0, size);
				ss.mark();
				dx += size;
				// 传输
				makeMushroom(line);
				cx++;
			}
		}else{
			// 换行位置
			sp = linesByte.length;
			// 计算该行开始移至上行结束位的下一位

			size = arx.position();
			//判断缓存中是否有完整内容，有就采集
			if(size > 0 && isRegularTrue){
				line = new byte[size];
				arx.flip();
				arx.get(line);
				dx += size;
				// 传输
				makeMushroom(line);
				arx = null;
				cx++;
				//缓存采集完之后将当前行放入缓存
				//当前行的位置
				int curP = ss.position();
				//回到之前行的位置
				ss.reset();
				arx = ByteBuffer.allocate(curP - ss.position());
				line = new byte[arx.limit()];
				ss.get(line);
				//把当前行放入缓存
				arx.put(line);
			}else if(size > 0 && !isRegularTrue){
				int curP = ss.position();
				//回到之前行的位置
				ss.reset();
				int beforP = ss.position();
				if(arx.remaining() < (curP - beforP)){
					ByteBuffer tarx = ByteBuffer.allocate(arx.limit());
					arx.flip();
					tarx.put(arx);
					arx = ByteBuffer.allocate(tarx.limit() + (curP - beforP));
					arx.put(tarx);
					line = new byte[curP - beforP];
					ss.get(line);
					arx.put(line);
				}
			}

		}
	}

	private void bothTransmission() throws SignRemovedException, InterruptedException {
		int size = 0;
		byte[] line;
		ss.position(bpx);
		size = arx.position() + ss.limit() - bpx;
		line = new byte[size];
		arx.flip();
		arx.get(line,0,arx.limit());
		ss.get(line, arx.position(), size - arx.position());
		ss.mark();
		dx += size;

		//释放缓存
		arx = null;
		//传输
		makeMushroom(line);
		bpx = 0;
		cx ++;
	}

	private void leaveTransmission()throws SignRemovedException, InterruptedException {
		int size = 0;
		byte[] line;
		size = arx.limit();
		line = new byte[size];
		arx.position(0);
		arx.get(line,0,size);
		dx += size;
		makeMushroom(line);
		cx++;

		//再采集剩下的数据,先判断是否被标记，没有就采集全部
		if(byp > 0){
			ss.reset();
		}

		size = ss.limit() - ss.position();
		line = new byte[size];
		ss.get(line);
		ss.mark();
		dx += size;
		// 传输
		makeMushroom(line);
		cx++;

		//释放缓存
		arx = null;
	}

	//缓存数据
	private boolean writeBuffer() throws IOException, SignRemovedException, InterruptedException {
		if(arx == null){//如果缓存为空则把一行数据直接放入缓存
			arx = ByteBuffer.allocate(ss.position());
			arx.clear();
			linesByte = new byte[arx.limit()];
			ss.reset();
			ss.get(linesByte);
			ss.mark();
			arx.put(linesByte);
			//写出到缓存的位置
			bbp = ss.position();
			return false;
		}else{

			//当次读取一行的大小
			int size = ss.position() - bbp;
			linesByte = new byte[size];
			ss.reset();
			ss.get(linesByte);
			bpx = ss.position();
//			ss.mark();
			bbp = ss.position();
			String linesString = new String(linesByte);
			//匹配当前读取的行的内容是否匹配正则
			Matcher m = p.matcher(linesString);
			isRegularTrue = m.matches();
			if(isRegularTrue){
				return true;
			}else{
				ss.mark();
				//当缓存可用部分小于当前读取数据时 ，增加缓存容量
				if(arx.remaining() < size){
					ByteBuffer tarx = ByteBuffer.allocate(arx.limit());
					arx.flip();
					tarx.put(arx);
					arx = ByteBuffer.allocate(tarx.limit() + size);
					arx.clear();
					tarx.flip();
					arx.put(tarx);
				}
				arx.put(linesByte);
				return false;
			}
		}
	}

	// 是否可以采集
	@Override
	public boolean canPick() {
		// 是否打开
		if (!this.isOpen()) {
			return false;
		}
		// 是否移除
		if (this.sign.isRemoved()) {
			this.setState(CluserState.REMOVED);
			return false;
		}
		// 是否读到尾
		if (this.si == this.di) {
			this.setState(CluserState.END);
			return false;
		}
		return true;
	}

	/**
	 * 自检是否可续读
	 */
	@Override
	public CluserState selfCheck() {
		if (!this.isOpen()) {
			throw new IllegalStateException("File shade not open!");
		}
		CluserState state = CluserState.NORMAL;
		if (this.sign.isRemoved()) {
			state = CluserState.REMOVED;
		} else {
			File file = new File(this.sign.getPath());
			// 先初始化文件
			try {
				if (es != null) {
					IOUtils.closeQuietly(es);
				}
				if (ds != null) {
					IOUtils.closeQuietly(ds);
				}
				FileAttributes fa = new FileAttributes(file);
				// 再验证是否是同一文件
				String key = fa.getFileKey();
				if (this.sign.getKey().equals(key)) {
					ds = new RandomAccessFile(file, "r");
					// 是同一文件
					long length = file.length();
					if (this.di < length) {
						this.di = length;
						es = ds.getChannel();
						ds.seek(this.si);
					} else {
						if (this.di > length) {
							this.si = this.di = length;
							ds.seek(this.si);
						}
						state = CluserState.END;
					}
				} else {
					state = CluserState.ERR;
				}
			} catch (IOException e) {
				state = CluserState.ERR;
			}
		}
		this.setState(state);
		// switch (state) {
		// case NORMAL:
		// this.intelligence.setState(ShadeState.NORMAL);
		// break;
		// case END:
		// this.intelligence.setState(ShadeState.END);
		// break;
		// case ERR:
		// this.intelligence.setState(ShadeState.ERROR);
		// break;
		// default:
		// break;
		// }
		return this.getState();
	}

	@Override
	protected void doClose() {
		if (done.compareAndSet(true, false)) {
			if (es != null) {
				IOUtils.closeQuietly(es);
			}
			if (ds != null) {
				IOUtils.closeQuietly(ds);
			}
			es = null;
			ds = null;
		}
	}
}
