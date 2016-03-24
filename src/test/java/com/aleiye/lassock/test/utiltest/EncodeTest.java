package com.aleiye.lassock.test.utiltest;

import java.io.File;
import java.net.URL;

import com.aleiye.lassock.util.EncodeDetect;

public class EncodeTest {
	public static void main(String argc[]) {
		EncodeDetect sinodetector;
		int result = EncodeDetect.OTHER;

		argc = new String[1];
		// argc[0] = "c:\\chinesedata\\codeconvert\\voaunit.txt";
		argc[0] = "中文";
		sinodetector = new EncodeDetect();
		if (argc[0].startsWith("http://") == true) {
			try {
				result = sinodetector.detectEncoding(new URL(argc[0]));
			} catch (Exception e) {
				System.err.println("Bad URL " + e.toString());
			}
		} else {
			result = sinodetector.detectEncoding(new File(argc[0]));
			result = sinodetector.detectEncoding(argc[0].getBytes());
		}
		System.out.println(EncodeDetect.nicename[result]);
	}
}
