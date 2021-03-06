package com.kgh.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kgh.bean.DownloadTaskBean;
import com.kgh.interfaces.TaskListener;
import com.kgh.request.RequestCallback;
import com.kgh.request.RequestUitl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载任务
 * Created by keguihong on 2017/11/3.
 */

public class DownloadTask {
    private DownloadTaskBean taskBean;
    public final int DEFAULT_THREAD_COUNT = 3;//默认分块大小
    private int threadCount = DEFAULT_THREAD_COUNT;
    private long MAX_LITMIT_SIZE_SINGLE = 1 * 1024 * 1024;//大于此值则开始分块
    private long[] downloadProgress ;//每块下载的大小
    private long downloadTotle ;//下载的总大小
    private int pro ;//下载的进度百分百
    private Handler handler;//UI句柄，用于通知下载进度
    private boolean isDownloading = false;
    private List<TaskListener> taskListenerList;
    private OkHttpClient okHttpClient;

    public DownloadTask(OkHttpClient okHttpClient, String url, String savePath,Object tag) {
        this(okHttpClient,url,savePath,null,tag);
    }

    public DownloadTask(OkHttpClient okHttpClient, String url, String savePath, String fileName,Object tag) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (okHttpClient == null || url == null || url.trim().equals("") || savePath == null || savePath.trim().equals("")) {
            Log.e("tag", "DownloadTask params error");
            return;
        }
        this.okHttpClient = okHttpClient;
        initTask(url, savePath,fileName, tag);
    }

    public void initTask(String url, String savePath,String fileName, Object tag) {
        if (taskBean == null) {
            taskBean = new DownloadTaskBean();
            taskBean.setFileSize(-1);
            taskBean.setDownloadUrl(url);
            taskBean.setTag(tag);
            taskBean.setSavePath(savePath);
            taskBean.setFileName(fileName);
            taskBean.setTaskId(url + "_" + savePath);
        }
    }

    public DownloadTaskBean getDownloadTaskBean() {
        return taskBean;
    }

    public String getTaskId() {
        if (taskBean != null) {
            return taskBean.getTaskId();
        }
        return null;
    }

    public void start() {
        startDownload(taskBean.getDownloadUrl(), taskBean.getFileSize(), taskBean.getSavePath(), taskBean.getFileName(), taskBean.getTag());
    }


    public void startDownload(final String url, final String filePath, final Object tag) {
        this.startDownload(url, filePath, null, tag);
    }


    public void startDownload(final String url, final String filePath, final String fileName, final Object tag) {
        this.startDownload(url, -1, filePath, fileName, tag);
    }

    public void startDownload(final String url, long fileSize, final String filePath, final String fileName, final Object tag) {
        initTask(url, filePath, fileName,tag);
        taskBean.setFileSize(fileSize);
        taskBean.setDownloadUrl(url);
        taskBean.setFileName(fileName);
        taskBean.setSavePath(filePath);
        if (taskBean.getFileSize() > 0 && taskBean.getFileName() != null && !taskBean.getFileName().trim().equals("")) {
            download(url, taskBean.getFileSize(), taskBean.getSavePath(), taskBean.getFileName(), tag);
        } else {
            RequestUitl.getAsync(okHttpClient, url, new RequestCallback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.isSuccessful()) {
                        long fSize = response.body().contentLength();
                        String fName = fileName;
                        if (fName == null || fName.trim().equals("")) {
                            Headers headers = response.headers();
                            Set<String> key = headers.names();
                            for (String k : key) {
                                List<String> values = headers.values(k);
                                for (String value : values) {
                                    String result;
                                    try {
                                        result = new String(value.getBytes("ISO-8859-1"), "GBK");
                                        int location = result.indexOf("filename");
                                        if (location >= 0) {
                                            result = result.substring(location + "filename".length());
                                            fName = result.substring(result.indexOf("=") + 1);
                                            if (fName.startsWith("\"") && fName.endsWith("\"")) {
                                                fName = fName.substring(1, fName.length() - 1);
                                            }
                                            break;
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (fName == null || "".equals(fName.trim())) {
                                fName = url.substring(url.lastIndexOf("/") + 1);
                            }
                        }
                        response.body().close();
                        download(url, fSize, filePath, fName, tag);
                    }
                }
            }, tag);

        }
    }

    private void download(String url, long fileSize, String filePath, String fileName, Object tag) {
        initTask(url, filePath,fileName, tag);
        taskBean.setFileSize(fileSize);
        taskBean.setDownloadUrl(url);
        taskBean.setFileName(fileName);
        taskBean.setSavePath(filePath);
        Log.e("tag","fileSize:"+fileSize);
        File file = new File(filePath, fileName);
        if (file.exists()) {
            handler.post(finishRunnable);
            return;
        }
        if (fileSize <= 0) {
            startOrdinaryDownload(url, filePath, fileName, tag);
            return;
        }
        File saveFile = new File(filePath, fileName + ".tmp");
        File recordFile = new File(filePath, fileName + ".dlntmp");
        if (!saveFile.exists()) {
            //下载的文件不存在，则清除可能存在的记录文件以及重置数据
            if (recordFile.exists()) {
                recordFile.delete();
            }
            taskBean.setProgress(0);
            taskBean.setDownloadedSize(0);
        } else {
            if (!(taskBean.getDownloadedSize() > 0 && taskBean.getDownloadedSize() == taskBean.getFileSize())) {
                //没下载完
                if (!recordFile.exists()) {
                    taskBean.setProgress(0);
                    taskBean.setDownloadedSize(0);
                }
            }
        }
        handler.post(startRunnable);
        //大于指定大小则开多线程下载
        if (fileSize > MAX_LITMIT_SIZE_SINGLE) {
            threadCount = DEFAULT_THREAD_COUNT;
        } else {
            threadCount = 1;
        }
        startMutiThreadDownload(url, fileSize, filePath, fileName, threadCount, tag);
    }

    public void startMutiThreadDownload(String url, long fileSize, String filePath, String fileName, int threadCount, Object tag) {
        long blockSize = fileSize / threadCount;
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File downloadFile = new File(filePath, fileName + ".tmp");
        try {
            RandomAccessFile tmpAccessFile = new RandomAccessFile(downloadFile, "rw");// 获取前面已创建的文件.
            tmpAccessFile.setLength(fileSize);// 文件写入的开始位置.
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(taskBean.getTaskId(), e);
        }


        long start = 0l;
        File recordFile = new File(filePath, fileName + ".tmp.dlntmp");
        RandomAccessFile tempFile = null;
        boolean isRecorded = false;
        if (recordFile.exists()) {
            isRecorded = true;
            try {
                tempFile = new RandomAccessFile(recordFile, "rw");// 获取前面已创建的文件.
            } catch (Exception e) {
                e.printStackTrace();
                notifyError(taskBean.getTaskId(), e);
            }
        } else {
            try {
                recordFile.createNewFile();
                tempFile = new RandomAccessFile(recordFile, "rw");// 获取前面已创建的文件.
            } catch (Exception e) {
                e.printStackTrace();
                notifyError(taskBean.getTaskId(), e);
            }
        }

        downloadProgress = new long[threadCount];
        //开线程下载
        for (int threadId = 0; threadId < threadCount; threadId++) {
            long startIndex = threadId * blockSize; // 线程开始下载的位置
            long endIndex = (threadId + 1) * blockSize - 1; // 线程结束下载的位置
            if (threadId == (threadCount - 1)) { // 如果是最后一个线程,将剩下的文件全部交给这个线程完成
                endIndex = fileSize - 1;
            }
            if (isRecorded) {
                //存在下载记录，即下载中
                try {
                    tempFile.seek(threadId * 8);// 文件写入的开始位置.
                    start = tempFile.readLong();
                    if (start <= 0) {
                        start = startIndex;
                    }
                    if (start == endIndex) {
                        downloadProgress[threadId] += (endIndex - startIndex + 1);
                    } else {
                        downloadProgress[threadId] += (start - startIndex);
                        downloadThread(url, start, endIndex, threadId, downloadFile, tag);// 开启线程下载
                    }
                    notifyProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError(taskBean.getTaskId(), e);
                }
            } else {
                //首次下载
                Log.e("tag"," threadId:"+threadId+" startIndex:"+startIndex+" endIndex:"+endIndex+" ");
                downloadThread(url, startIndex, endIndex, threadId, downloadFile, tag);
            }

        }
    }

    public void startOrdinaryDownload(String url, final String filePath, final String fileName, Object tag) {
        RequestUitl.getAsync(okHttpClient, url, new RequestCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    InputStream inputStream = response.body().byteStream();
                    if (response.isSuccessful()) {
                        OutputStream output = null;
                        try {
                            File dir = new File(filePath);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(filePath, fileName + ".tmp");
                            if (file.exists()) {
                                file.delete();
                            }
                            file.createNewFile();
                            output = new FileOutputStream(file);
                            byte buffer[] = new byte[1024];
                            int length = 0;
                            while ((length = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, length);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (taskListenerList != null && taskListenerList.size() > 0) {
                                            for (TaskListener l : taskListenerList) {
                                                l.notifyProgress(taskBean.getTaskId(), 0, taskBean.getDownloadedSize(), 0);
                                            }
                                        }
                                    }
                                });
                            }
                            file.renameTo(new File(filePath, fileName));
                            output.flush();
                            taskBean.setFileSize(file.length());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (taskListenerList != null && taskListenerList.size() > 0) {
                                        for (TaskListener l : taskListenerList) {
                                            l.notifyFinish(taskBean.getTaskId(), taskBean.getSavePath(), taskBean.getFileName(),taskBean.getFileSize());
                                        }
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            notifyError(taskBean.getTaskId(), e);
                        } finally {
                            try {
                                output.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError(taskBean.getTaskId(), e);
                }
            }
        }, tag);
    }

    public void downloadThread(final String url, final long startIndex, final long endIndex, final int threadId, final File file, final Object tag) {
        isDownloading = true;
        RequestUitl.downloadFileByRange(okHttpClient, url, startIndex, endIndex, new RequestCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                notifyError(taskBean.getTaskId(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                RandomAccessFile downloadAccessFile = null;
                RandomAccessFile recordAccessFile = null;
                try {
                    is = response.body().byteStream();// 获取流

                    downloadAccessFile = new RandomAccessFile(file, "rw");// 获取前面已创建的文件.
                    downloadAccessFile.seek(startIndex);// 文件写入的开始位置.

                    File f = new File(file.getParent(), file.getName() + ".dlntmp");
                    recordAccessFile = new RandomAccessFile(f, "rw");// 获取前面已创建的文件.
                    recordAccessFile.seek(threadId * 8);// 文件写入的开始位置.

                  /*  将网络流中的文件写入本地*/
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    long progress = startIndex;
                    long p = 0;
                    while ((length = is.read(buffer)) > 0) {
                        if (!isDownloading) {
                            is.close();
                            response.body().close();
                            recordAccessFile.close();
                            downloadAccessFile.close();
                            return;
                        }
                        progress += length;
                        p += length;
                        downloadAccessFile.write(buffer, 0, length);
                        recordAccessFile.seek(threadId * 8);//long为8个字节
                        recordAccessFile.writeLong(progress - 1);
                        downloadProgress[threadId] += length;
                        notifyProgress();
                    }
//                    Log.e("tag","downloadProgress:"+downloadProgress);
                    Log.e("tag"," threadId:"+threadId+" startIndex:"+startIndex+" endIndex:"+endIndex+" "+" progress:"+progress+" "+" p:"+p);

                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError(taskBean.getTaskId(), e);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (downloadAccessFile != null) {
                        downloadAccessFile.close();
                    }
                    if (recordAccessFile != null) {
                        recordAccessFile.close();
                    }
                    response.body().close();
                }
            }
        }, tag);
    }


    private void notifyProgress() {
        if (taskBean == null) {
            return;
        }
        downloadTotle = 0l;
        for (long p : downloadProgress){
            downloadTotle += p;
        }
        taskBean.setDownloadedSize(downloadTotle);
        taskBean.setProgress((int) (downloadTotle * 100 / taskBean.getFileSize()));
        if (downloadTotle == taskBean.getFileSize()) {
            //下载完后重命名
            File file = new File(taskBean.getSavePath(), taskBean.getFileName() + ".tmp");
            file.renameTo(new File(taskBean.getSavePath(), taskBean.getFileName()));
            //删除数据记录文件
            File tempFile = new File(taskBean.getSavePath(), taskBean.getFileName() + ".tmp.dlntmp");
            if (tempFile.exists()) {
                tempFile.delete();
            }
            handler.post(finishRunnable);
        } else {
            handler.post(progressRunnable);
            if (taskListenerList != null && taskListenerList.size() > 0) {
                int correntProgress = (int) (downloadTotle * 100 / taskBean.getFileSize());
                if(correntProgress != pro){
                    pro = correntProgress;
                    for (TaskListener l : taskListenerList) {
                        l.notifyProgressInBackThread(taskBean.getTaskId(),pro , taskBean.getDownloadedSize(), taskBean.getFileSize());
                    }
                }
            }
        }
    }

    private Runnable progressRunnable = new Runnable() {

        @Override
        public void run() {
            isDownloading = true;
            if (taskListenerList != null && taskListenerList.size() > 0) {
                int correntProgress = (int) (downloadTotle * 100 / taskBean.getFileSize());
                if(correntProgress != pro){
                    pro = correntProgress;
                    for (TaskListener l : taskListenerList) {
                        l.notifyProgressInBackThread(taskBean.getTaskId(),pro , taskBean.getDownloadedSize(), taskBean.getFileSize());
                    }
                }
            }
        }
    };

    private Runnable finishRunnable = new Runnable() {
        @Override
        public void run() {
            isDownloading = false;
            if (taskListenerList != null && taskListenerList.size() > 0) {
                for (TaskListener l : taskListenerList) {
                    l.notifyFinish(taskBean.getTaskId(), taskBean.getSavePath(), taskBean.getFileName(), taskBean.getFileSize());
                }
            }
        }
    };

    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            isDownloading = true;
            if (taskListenerList != null && taskListenerList.size() > 0) {
                for (TaskListener l : taskListenerList) {
                    l.notifyStart(taskBean.getTaskId(), taskBean.getSavePath(), taskBean.getFileName(), taskBean.getFileSize(), taskBean.getDownloadedSize(), taskBean.getProgress());
                }
            }
        }
    };

    public void notifyError(final String taskId, final Exception e) {
        isDownloading = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (taskListenerList != null && taskListenerList.size() > 0) {
                    for (TaskListener l : taskListenerList) {
                        l.notifyError(taskId, e);
                    }
                }
            }
        });
    }

    public void restart() {
        if (!isDownloading) {
            isDownloading = true;
            if (taskBean == null) {
                return;
            }
            startDownload(taskBean.getDownloadUrl(), taskBean.getFileSize(), taskBean.getSavePath(), taskBean.getFileName(), taskBean.getTag());
        }
    }

    public void stop() {
        isDownloading = false;
    }

    public void cancel() {
        isDownloading = false;
        File file = new File(taskBean.getSavePath(), taskBean.getFileName());
        File downloadTmpFile = new File(taskBean.getSavePath(), taskBean.getFileName() + ".tmp");
        File recordFile = new File(taskBean.getSavePath(), taskBean.getFileName() + ".tmp.dlntmp");
        if (downloadTmpFile.exists()) {
            downloadTmpFile.delete();
        }
        if (recordFile.exists()) {
            recordFile.delete();
        }
        if (file.exists()) {
            file.delete();
        }
    }

    public void registerTaskListener(TaskListener listener) {
        if (listener == null) {
            return;
        }
        if (taskListenerList == null) {
            taskListenerList = new ArrayList<TaskListener>();
        }
        if (!isRegisterTaskListener(listener)) {
            taskListenerList.add(listener);
        }
    }

    public void unregisterTaskListener(TaskListener listener) {
        if (listener == null) {
            return;
        }
        if (taskListenerList == null) {
            taskListenerList = new ArrayList<TaskListener>();
        }
        for (int i = 0; i < this.taskListenerList.size(); i++) {
            TaskListener l = this.taskListenerList.get(i);
            if (l == listener) {
                this.taskListenerList.remove(i);
                break;
            }
        }
    }

    private boolean isRegisterTaskListener(TaskListener listener) {
        if (listener == null) {
            return false;
        }
        if (taskListenerList == null) {
            taskListenerList = new ArrayList<TaskListener>();
        }
        for (int i = 0; i < this.taskListenerList.size(); i++) {
            TaskListener l = this.taskListenerList.get(i);
            if (l == listener) {
                return true;
            }
        }
        return false;
    }
}
