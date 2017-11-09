package com.kgh.manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by keguihong on 2017/11/2.
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
}
