package com.kgh.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kgh.adapter.DownloadAdapter;
import com.kgh.bean.DownloadTaskBean;
import com.kgh.downloaddemo.R;
import com.kgh.interfaces.TaskListener;
import com.kgh.manager.DownloadTask;
import com.kgh.manager.DownloadTaskManager;
import com.kgh.utils.FileUtil;
import com.kgh.utils.TaskManager;

import java.util.ArrayList;
import java.util.List;


public class DownLoadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DownloadAdapter adapter;

    private String url1 = "http://gdown.baidu.com/data/wisegame/7f857180ca440ae5/wenjianguanli_70101318.apk";
    private String url2 = "http://p.gdown.baidu.com/0e7ad53c1404fea096987b69935acd664cfaa1852de5ada0ec41fe52cad3184383cf17010d7afafadd7706038f08448bfd3aa225a3a6f9367b0a35459b624382613d815ece0fcd8afa724f27a6480851cd4e99a88eca55bcf350eb97f73d81a2390f3db4ff11bb94a383c5e7e70b110afa92edab27c868eb960d58c9be574c11c5c97134e50f7d5e4975b1a1b719607a708d9f1ea6952710997d3fcdfc09521300ce39aa09193eb5c58f29b5df2eeb115b25e4bd4026901344b9bbe9683fa50c09fa3c6bb76203fcf79b9c0e4708e939334b9a679a8b1b800e5e9f9ee392983fe1dda2065685b485bb70447a5794bfb9d44d4a7ba7dde74088ae47761809c0c6";
    private String url3 = "http://gdown.baidu.com/data/wisegame/a9fa5a97d9d1c63c/ESwenjianliulanqi_610.apk";
    private String url4 = "http://gdown.baidu.com/data/wisegame/c1a61fd2ef3b0235/aiqiyi_80960.apk";
    private String url5 = "http://dldir1.qq.com/qqfile/qq/TIM2.0.0/22317/TIM2.0.0.exe";
    private String url6 = "http://dldir1.qq.com/foxmail/MacFoxmail/Foxmail_for_Mac_V1.2.0.dmg";
    private String url7 = "http://down.txt53.com/zip/23135/大主宰.zip";
    private String url8 = "http://txt.mht.la/12821/至尊兵王.zip";
    private String url9 = "http://14.215.166.32/appdl.hicloud.com/dl/appdl/application/apk/0f/0f65064266cc4ea393cbde86f12af4f6/com.xunmeng.pinduoduo.1711091337.apk?mkey=5a044cf08625078c&f=9e16&c=0&sign=portal@portal1510238712727&source=portalsite&p=.apk";
    private String url10 = "http://14.215.166.41/appdl.hicloud.com/dl/appdl/application/apk/59/59b8eaa50d4646e290ce366bdece5a41/com.ss.android.article.news.1709141637.apk?mkey=5a044f088625078c&f=9e16&c=0&sign=portal@portal1510238712717&source=portalsite&p=.apk";
    private String url11 = "http://shouji.360tpcdn.com/171101/ffb1ef2f9ebe317d8744fccd8ccf830d/com.qihoo360.mobilesafe_257.apk";
    private String url12 = "http://shouji.360tpcdn.com/171106/49bf3fb249a0e9a6cb9b2a63b3fdb80a/com.chaozh.iReaderFree_721.apk";
    private String url13 = "http://shouji.360tpcdn.com/171030/b331b124da2c94f137959a362f45c204/com.v.study_124.apk";

    private String url[] = new String[]{url1, url2, url3, url4, url5, url6, url7, url1, url8, url9, url10, url11, url12, url13};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        initRecyclerView();
        initData();
        registerDownlistener();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_download);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {
        List<DownloadTask> list = getList();
        adapter = new DownloadAdapter(list);
        recyclerView.setAdapter(adapter);
        for (DownloadTask task : list) {
            task.registerTaskListener(TaskManager.getInstance());
        }
    }


    private List<DownloadTask> getList() {
        List<DownloadTask> list = new ArrayList<DownloadTask>();
        for (int i = 0; i < url.length; i++) {
            DownloadTask taskBean = DownloadTaskManager.getInstance().addTask(url[i], FileUtil.getDownloadFilePath(), i);
            list.add(taskBean);
        }
        return list;
    }

    private void registerDownlistener() {
    }


}
