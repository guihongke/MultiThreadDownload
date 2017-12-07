package com.kgh.interfaces;

/**
 * Created by keguihong on 2017/11/6.
 */

public interface TaskListener {

    void notifyProgress(String taskId, int pro, long downloadedLength, long fileLength);

    void notifyProgressInBackThread(String taskId, int pro, long downloadedLength, long fileLength);

    void notifyFinish(String taskId, String filePath, String fileName, long fileSize);

    void notifyError(String taskId, Exception e);

    void notifyStart(String taskId, String filePath, String fileName, long fileSize, long downloadSize, int pro);
}
