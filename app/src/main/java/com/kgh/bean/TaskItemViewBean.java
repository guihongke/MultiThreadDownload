package com.kgh.bean;

import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by keguihong on 2017/11/13.
 */

public class TaskItemViewBean {

    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvDownloadSize;
    private TextView tvName;

    public TaskItemViewBean(ProgressBar progressBar, TextView tvProgress, TextView tvDownloadSize,TextView tvName) {
        this.progressBar = progressBar;
        this.tvProgress = tvProgress;
        this.tvDownloadSize = tvDownloadSize;
        this.tvName = tvName;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public TextView getTvProgress() {
        return tvProgress;
    }

    public void setTvProgress(TextView tvProgress) {
        this.tvProgress = tvProgress;
    }

    public TextView getTvDownloadSize() {
        return tvDownloadSize;
    }

    public void setTvDownloadSize(TextView tvDownloadSize) {
        this.tvDownloadSize = tvDownloadSize;
    }

    public TextView getTvName() {
        return tvName;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }
}
