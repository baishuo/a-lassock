package com.aleiye;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Scanner;
import org.junit.Test;

import java.io.File;

/**
 * Created by weiwentao on 16/7/19.
 */
public class TestScanner {

    @Test
    public void testSanner() {

        String inpupath = "/Users/weiwentao/Downloads/yiyang/syslog/";
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
            path = inpupath;
        }

        System.out.println(path + "===" + include);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(path);
        scanner.setIncludes(new String[]{include,""});
        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        for (int i = 0; i < files.length; i++) {
            File file = new File(path,files[i]);
            System.out.println(file.exists());
        }
    }
}
