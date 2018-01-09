package com.kgh.utils;

import com.kgh.adapter.DownloadAdapter;
import com.kgh.bean.TaskItemViewBean;
import com.kgh.interfaces.TaskListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by keguihong on 2017/11/13.
 */

public class TaskManager implements TaskListener {
    private static final TaskManager ourInstance = new TaskManager();
    DecimalFormat df;

    public static TaskManager getInstance() {
        return ourInstance;
    }

    private TaskManager() {
        data = new HashMap<String, TaskItemViewBean>();
        df = new DecimalFormat("#.##");
    }

    private Map<String, TaskItemViewBean> data;

    public void addTask(String taskId, TaskItemViewBean bean) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(taskId, bean);
    }

    public void removeTask(String taskId) {
        if (data != null && data.size() > 0) {
            data.remove(taskId);
        }
    }

    @Override
    public void notifyProgress(String taskId, int pro, long downloadedLength, long fileLength) {
        for (Map.Entry<String, TaskItemViewBean> entry : data.entrySet()) {
            TaskItemViewBean bean = null;
            if (entry.getKey().equals(taskId)) {
                bean = entry.getValue();
            }
            if (bean == null) {
                continue;
            }
            if (pro > 0) {
                bean.getTvProgress().setText(pro + "%");
                bean.getProgressBar().setProgress(pro);
            } else {
                bean.getProgressBar().setProgress(50);
                bean.getTvProgress().setText("");
            }
            if (fileLength > 0) {
                bean.getTvDownloadSize().setText(df.format(downloadedLength * 1.00 / 1024 / 1024) + "m/" + df.format(fileLength * 1.00 / 1024 / 1024) + "m");
            } else {
                bean.getTvDownloadSize().setText(df.format(downloadedLength * 1.00 / 1024 / 1024) + "m");
            }
            break;
        }
    }

    @Override
    public void notifyFinish(String taskId, String filePath, String fileName,long fileSize) {
        for (Map.Entry<String, TaskItemViewBean> entry : data.entrySet()) {
            TaskItemViewBean bean = null;
            if (entry.getKey().equals(taskId)) {
                bean = entry.getValue();
            }
            if (bean == null) {
                continue;
            }
            bean.getTvName().setText(fileName == null ? "" : fileName);
            bean.getTvProgress().setText(100 + "%");
            bean.getProgressBar().setProgress(100);
            bean.getTvDownloadSize().setText(df.format(fileSize * 1.00 / 1024 / 1024) + "m");
            break;
        }
    }

    @Override
    public void notifyError(String taskId, Exception e) {

    }

    @Override
    public void notifyStart(String taskId, String filePath, String fileName, long fileSize, long downloadSize, int pro) {
        for (Map.Entry<String, TaskItemViewBean> entry : data.entrySet()) {
            TaskItemViewBean bean = null;
            if (entry.getKey().equals(taskId)) {
                bean = entry.getValue();
            }
            if (bean == null) {
                continue;
            }
            bean.getTvName().setText(0 + "%");
            if (pro > 0) {
                bean.getTvProgress().setText(pro + "%");
                bean.getProgressBar().setProgress(pro);
            } else {
                bean.getProgressBar().setProgress(50);
                bean.getTvProgress().setText("");
            }
            if (fileSize > 0) {
                bean.getTvDownloadSize().setText(df.format(downloadSize * 1.00 / 1024 / 1024) + "m/" + df.format(fileSize * 1.00 / 1024 / 1024) + "m");
            } else {
                bean.getTvDownloadSize().setText(df.format(downloadSize * 1.00 / 1024 / 1024) + "m");
            }
            bean.getTvName().setText(fileName == null ? "" : fileName);
            break;
        }
    }

    @Override
    public void notifyProgressInBackThread(String taskId, int pro, long downloadedLength, long fileLength) {

    }
}
