package com.aleiye.lassock.test.snmp;

import java.util.ArrayList;
import java.util.List;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseType;
import com.aleiye.lassock.live.basket.MemoryQueueBasket;
import com.aleiye.lassock.live.hill.source.snmp.SnmpStandardSource;

public class SNMPTest {

	public static void main(String[] args) {
		SnmpStandardSource sourse = new SnmpStandardSource();
		Course course = new Course();
		course.setName("aaaa");
		course.setType(CourseType.SNMP);
		course.put("host", "10.1.0.132");
		List<String> ois = new ArrayList<String>();
		ois.add("1.3.6.1.2.1.1.1.0");
		course.put("oids", ois);
		course.put("community", "aaaa");
		sourse.configure(course);
		try {
			sourse.setBasket(new MemoryQueueBasket());
			sourse.start();
			sourse.pick();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
