package com.aleiye.lassock.util;

import com.aleiye.lassock.live.hill.source.text.FilePathParseInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by weiwentao on 16/9/5.
 */
public class DirectorScannerUtils {

    private static final Logger logger = LoggerFactory.getLogger(DirectorScannerUtils.class);

    public static FilePathParseInfo parseFilePath(String inputPath) {

        int index = inputPath.indexOf('*');
        String path;
        String pathInclude;
        if (index != -1) {
            path = inputPath.substring(0, index);
            pathInclude = inputPath.substring(index);
            if (!path.endsWith("/")) {
                int lastIndex = path.lastIndexOf("/");
                path = inputPath.substring(0, lastIndex + 1);
                pathInclude = inputPath.substring(lastIndex + 1);
            }
        } else {
            path = inputPath.substring(0, inputPath.lastIndexOf("/"));
            pathInclude = inputPath.substring(inputPath.lastIndexOf("/"));
        }
        return new FilePathParseInfo(path, pathInclude);
    }

    public static String[] scannerFiles(String fileIncludesJson, String filesExcludesJson, FilePathParseInfo filePathParseInfo) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(filePathParseInfo.getBasePath());

        ObjectMapper objectMapper = new ObjectMapper();
        String[] includes = new String[]{filePathParseInfo.getIncludefile() + "/*"};
        if (fileIncludesJson != null && fileIncludesJson.length() > 0) {
            try {
                List<String> fileIncludes = objectMapper.readValue(fileIncludesJson, List.class);
                if (fileIncludes.size() > 0) {
                    includes = new String[fileIncludes.size()];
                    for (int i = 0; i < fileIncludes.size(); i++) {
                        includes[i] = filePathParseInfo.getIncludefile() + "/" + fileIncludes.get(i);
                    }
                }
            } catch (IOException e) {
                logger.warn("parse includes error", e);
            }
        }
        if (filesExcludesJson != null && filesExcludesJson.length() > 0) {
            try {
                List<String> fileExclude = objectMapper.readValue(filesExcludesJson, List.class);
                String[] excludes = new String[fileExclude.size()];
                for (int i = 0; i < fileExclude.size(); i++) {
                    excludes[i] = fileExclude.get(i);
                }
                scanner.setExcludes(excludes);
            } catch (IOException e) {
                logger.warn("parse excludes error", e);
            }
        }
        scanner.setIncludes(includes);
        scanner.scan();

        return scanner.getIncludedFiles();
    }
}
