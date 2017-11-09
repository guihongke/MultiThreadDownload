package com.kgh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kgh.downloaddemo.R;


/**
 * Created by keguihong on 2017/11/9.
 */

public class DownloadAdapter extends RecyclerView.Adapter {
//    private List<>

    public DownloadAdapter() {
        super();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
