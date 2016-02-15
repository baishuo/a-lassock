package com.aleiye.raker.lang;

import com.aleiye.raker.util.SigarUtils;

/**
 * Lassock
 * 
 * @author ruibing.zhao
 * @since 2016年1月29日
 * @version 1.0
 */
public class Sistem {

	public static final String HOST;

	public static final String IP;

	public static final String MAC;

	static {
		HOST = SigarUtils.getHostsNameBySigar();
		IP = SigarUtils.getIPBySigar();
		MAC = SigarUtils.getMacBySigar();
	}
}
