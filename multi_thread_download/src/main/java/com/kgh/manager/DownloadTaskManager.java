package com.kgh.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by keguihong on 2017/11/3.
 */
public class DownloadTaskManager {

    private static final DownloadTaskManager ourInstance = new DownloadTaskManager();

    public static DownloadTaskManager getInstance() {
        return ourInstance;
    }

    private OkHttpClient okHttpClient;

    public static final long DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    public final static long DEFAULT_READ_TIMEOUT = 30 * 1000;
    public final static long DEFAULT_WRITE_TIMEOUT = 30 * 1000;

    private List<DownloadTask> downloadTaskList;


    private DownloadTaskManager() {
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    public DownloadTask addTask(String url, String filePath, Object tag) {
        DownloadTask task = new DownloadTask(getOkHttpClient(),url,tag);
        addDownloadTask(task);
        task.startDownload(url, filePath, tag);
        return task;
    }

    public void addDownloadTask(DownloadTask task) {
        if (task == null || task.getDownloadTaskBean() == null || task.getDownloadTaskBean().getTaskId() == null) {
            return;
        }
        if (downloadTaskList == null) {
            downloadTaskList = new ArrayList<DownloadTask>();
        }
        if (!isExistDownloadTask(task)) {
            downloadTaskList.add(task);
        }
    }

    public void removeDownloadTask(DownloadTask task) {
        if (task == null || task.getDownloadTaskBean() != null) {
            return;
        }
        if (downloadTaskList == null) {
            downloadTaskList = new ArrayList<DownloadTask>();
        }
        for (int i = 0; i < this.downloadTaskList.size(); i++) {
            DownloadTask l = this.downloadTaskList.get(i);
            if (l.getDownloadTaskBean().getTaskId().equals(task.getDownloadTaskBean().getTaskId())) {
                this.downloadTaskList.remove(i);
                break;
            }
        }
    }

    private boolean isExistDownloadTask(DownloadTask task) {
        if (task == null || task.getDownloadTaskBean() != null) {
            return false;
        }
        if (downloadTaskList == null) {
            downloadTaskList = new ArrayList<DownloadTask>();
        }
        for (int i = 0; i < this.downloadTaskList.size(); i++) {
            DownloadTask l = this.downloadTaskList.get(i);
            if (l.getDownloadTaskBean().getTaskId().equals(task.getDownloadTaskBean().getTaskId())) {
                return true;
            }
        }
        return false;
    }


}
