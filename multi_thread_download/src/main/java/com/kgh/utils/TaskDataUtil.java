package com.kgh.utils;

import com.alibaba.fastjson.JSON;
import com.kgh.bean.DownloadTaskBean;

/**
 * Created by keguihong on 2017/11/3.
 */

public class TaskDataUtil {

    public static void cleanTaskData(String taskId) {
        SharedPreferencesUtil.remove(taskId);
    }

    public static void saveTaskData(String taskId, DownloadTaskBean bean) {
        SharedPreferencesUtil.setString(taskId, JSON.toJSONString(bean));
    }

}
