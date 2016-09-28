package com.aleiye;

import com.aleiye.lassock.live.hill.source.text.FilePathParseInfo;
import com.aleiye.lassock.util.DirectorScannerUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Scanner;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiwentao on 16/7/19.
 */
public class TestScanner {

    @Test
    public void testSanner() throws IOException {

        String inpupath = "/Users/weiwentao/Downloads/log";
        FilePathParseInfo filePathParseInfo = DirectorScannerUtils.parseFilePath(inpupath);
        System.out.println(filePathParseInfo.getBasePath());
        System.out.println(filePathParseInfo.getIncludefile());


        ObjectMapper objectMapper = new ObjectMapper();
        List<String> iList = new ArrayList<>();
        iList.add("log.log");


        String[] files = DirectorScannerUtils.scannerFiles("",objectMapper.writeValueAsString(iList),filePathParseInfo);

        for (int i = 0; i < files.length; i++) {
            File file = new File(filePathParseInfo.getBasePath(),files[i]);
            System.out.println(file.getAbsoluteFile());
        }
    }

    @Test
    public void testJson() throws IOException {

        ObjectMapper object = new ObjectMapper();
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");

        String json = object.writeValueAsString(list);

        List<String> list2 = object.readValue(json,List.class);

        System.out.println(list2);
    }
}
