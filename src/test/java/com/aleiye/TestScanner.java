package com.aleiye;

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
    public void testSanner() {

        String inpupath = "/Users/weiwentao/Downloads/log/";
        int index = inpupath.indexOf('*');
        String path = "";
        String include = "*";
        if (index != -1) {
            path = inpupath.substring(0, index);
            include = inpupath.substring(index);
            if (!path.endsWith("/")) {
                int lastIndex = path.lastIndexOf("/");
                path = inpupath.substring(0, lastIndex + 1);
                include = inpupath.substring(lastIndex + 1);
            }
        }else{
            path = inpupath.substring(0,inpupath.lastIndexOf("/"));
            include = inpupath.substring(inpupath.lastIndexOf("/"));
        }

        System.out.println(path + "===" + include);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(path);
        scanner.setIncludes(new String[]{include+"/*"});
        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        for (int i = 0; i < files.length; i++) {
            File file = new File(path,files[i]);
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
