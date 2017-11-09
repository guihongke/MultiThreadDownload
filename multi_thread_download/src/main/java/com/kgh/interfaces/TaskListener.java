package com.kgh.interfaces;

/**
 * Created by keguihong on 2017/11/6.
 */

public interface TaskListener {

    void notifyProgress(String taskId, int pro, long downloadedLength, long fileLength);

    void notifyFinish(String taskId, String filePath, String fileName);

    void notifyError(String taskId, Exception e);
}
