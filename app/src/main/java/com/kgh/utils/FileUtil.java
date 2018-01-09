package com.kgh.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by keguihong on 2017/11/9.
 */

public class FileUtil {

    public static String getSDCard() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static File createSDDir(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getDownloadFilePath(){
        String path = getSDCard()+"/mulit_thread_download";
        createSDDir(path);
        return path;
    }
}
