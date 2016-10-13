package com.aleiye.lassock.util;

import com.aleiye.lassock.live.hill.source.text.FilePathParseInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiwentao on 16/9/5.
 */
public class DirectorScannerUtils {

    private static final Logger logger = LoggerFactory.getLogger(DirectorScannerUtils.class);

    public static FilePathParseInfo parseFilePath(String inputPath) {

        String path = inputPath.substring(0, inputPath.lastIndexOf("/"));
        String pathInclude = inputPath.substring(inputPath.lastIndexOf("/"));
        return new FilePathParseInfo(path, pathInclude);
    }

    public static String[] scannerFiles(String fileIncludesJson, String filesExcludesJson, FilePathParseInfo filePathParseInfo) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(filePathParseInfo.getBasePath());

        ObjectMapper objectMapper = new ObjectMapper();
        String[] includes = new String[]{filePathParseInfo.getIncludefile() + "/*", filePathParseInfo.getIncludefile()};
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
                List<String> excludes = new ArrayList<String>();
                for (int i = 0; i < fileExclude.size(); i++) {
                    if (!fileExclude.get(i).trim().isEmpty()) {
                        excludes.add(filePathParseInfo.getIncludefile() + "/" + fileExclude.get(i));
                    }
                }
                scanner.setExcludes(excludes.toArray(new String[]{}));
            } catch (IOException e) {
                logger.warn("parse excludes error", e);
            }
        }
        scanner.setIncludes(includes);
        scanner.scan();

        return scanner.getIncludedFiles();
    }
}
