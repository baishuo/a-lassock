package com.aleiye.lassock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by weiwentao on 16/10/11.
 */
public class StatusUtils {

    private static Logger logger = LoggerFactory.getLogger(StatusUtils.class);


    public static final String filePath = StatusUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    public static void markStatusChange() {
        markStatusChange(false);
    }

    public static void markStatusChange(boolean shutDown) {
        try {
            File jarPath = new File(filePath);

            String fileName;
            if (shutDown) {
                fileName = "shutdown.aleiye";
            } else {
                fileName = "statuechange.aleiye";
            }

            File file = new File(jarPath.getParent(), fileName);
            file.createNewFile();
            logger.warn("the lassock ip has change,so the lassock will restart");
        } catch (IOException e) {
            logger.error("create ipChange.aleiye file error", e);
        }
    }
}
