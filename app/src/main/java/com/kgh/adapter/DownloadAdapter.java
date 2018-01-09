package com.kgh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kgh.bean.DownloadTaskBean;
import com.kgh.bean.TaskItemViewBean;
import com.kgh.downloaddemo.R;
import com.kgh.manager.DownloadTask;
import com.kgh.utils.TaskManager;

import java.util.List;


/**
 * Created by keguihong on 2017/11/9.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private List<DownloadTask> list = null;

    public DownloadAdapter(List<DownloadTask> list) {
        this.list = list;
    }

    @Override
    public DownloadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DownloadAdapter.ViewHolder holder,final int position) {
        final DownloadTask task = list.get(position);
        if (task == null) {
            return;
        }
        DownloadTaskBean taskBean = task.getDownloadTaskBean();
        if (taskBean == null) {
            return;
        }
        TaskManager.getInstance().addTask(taskBean.getTaskId(), new TaskItemViewBean(holder.progressBar, holder.tvPro, holder.tvSize,holder.tvName));
        holder.tvName.setText(taskBean.getFileName());
        if (taskBean.getProgress() > 0) {
            holder.tvPro.setText(taskBean.getProgress() + "%");
            holder.progressBar.setProgress(taskBean.getProgress());
        } else {
            holder.progressBar.setProgress(50);
            holder.tvPro.setText("");
        }
        if (taskBean.getFileSize() > 0) {
            holder.tvSize.setText(taskBean.getDownloadedSize() / 1024 / 1024 + "m/" + taskBean.getFileSize() / 1024 / 1024 + "m");
        } else {
            holder.tvSize.setText(taskBean.getDownloadedSize() / 1024 / 1024 + "m");
        }
        holder.imgtatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(position).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView imgtatus;
        private TextView tvPro;
        private TextView tvSize;
        private TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            imgtatus = (ImageView) itemView.findViewById(R.id.img_status);
            tvName = (TextView) itemView.findViewById(R.id.text_download_name);
            tvSize = (TextView) itemView.findViewById(R.id.text_dowload_size);
            tvPro = (TextView) itemView.findViewById(R.id.text_download_pro);

        }
    }


}
