package com.aleiye.lassock.live.hill.source.text;

/**
 * 文件路径的解析类
 * Created by weiwentao on 16/9/5.
 */
public class FilePathParseInfo {

    private String basePath;

    private String includefile;

    public FilePathParseInfo(String basePath, String includefile) {
        this.basePath = basePath;
        this.includefile = includefile;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getIncludefile() {
        return includefile;
    }
}
