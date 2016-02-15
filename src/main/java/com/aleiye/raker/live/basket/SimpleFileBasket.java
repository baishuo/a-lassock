package com.aleiye.raker.live.basket;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.aleiye.raker.model.Mushroom;
import com.aleiye.raker.util.ConfigUtils;

public class SimpleFileBasket extends AbstractBasket {
	long count = 0;

	String filePath = ConfigUtils.getConfig().getString("live.custom.simplefilepath") + "/data.txt";
	File file = new File(filePath);

	@Override
	public void push(Mushroom mushroom) {
		try {
			FileUtils.writeStringToFile(file, mushroom.getSignId() + "\n", true);
			if (mushroom.getContent().getClass().isAssignableFrom(byte[].class)) {
				FileUtils.writeStringToFile(file, new String((byte[]) mushroom.getContent()) + "\n", true);
			} else {
				FileUtils.writeStringToFile(file, mushroom.getContent().toString() + "\n", true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Mushroom take() {
		return null;
	}

}
