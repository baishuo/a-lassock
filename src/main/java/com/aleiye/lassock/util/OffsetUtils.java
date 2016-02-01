package com.aleiye.lassock.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ywt on 15/5/15.
 */
public class OffsetUtils {

    private static final Logger _LOG = LoggerFactory.getLogger(OffsetUtils.class);


    /**
     * 将偏移量写入文件
     *
     * @param offset
     */
    public static void persist(Map<String, Long> offset, String fileName) {
        File recordFile = new File(fileName);
        StringBuilder sb = new StringBuilder();
        Map<String, Long> currentMap = new HashMap<String, Long>(offset);
        try {
            for (Map.Entry<String, Long> entry : currentMap.entrySet()) {
                sb.append(entry.getKey()).append("=")
                        .append(entry.getValue()).append("\r\n");
            }
            if (0 != sb.length()) {
                try {
                    // 写入磁盘大约耗时10毫秒
                    FileUtils.writeStringToFile(recordFile, sb.toString());
                } catch (IOException e) {
                    _LOG.error("Recor error" + e);
                }
            } else {
                FileUtils.deleteQuietly(recordFile);
            }
        } catch (Exception e1) {
            _LOG.error("Recor error" + e1);
        }
    }

    /**
     * 夺取偏移量信息
     *
     * @param fileName
     * @return
     */
    public static Map<String, Long> getOffset(String fileName) {
        File recordFile = new File(fileName);
        Map<String, Long> offsetMap = new HashMap<String, Long>();
        if (!recordFile.exists()) {
            return offsetMap;
        }
        try {
            List<String> infoList = FileUtils.readLines(recordFile);
            for (String ss : infoList) {
                offsetMap.put(StringUtils.substringBeforeLast(ss, "="), Long.parseLong(StringUtils.substringAfterLast(ss, "=")));
            }
        } catch (IOException e) {
            _LOG.error("read offset error", e);
        }
        return offsetMap;
    }
}
