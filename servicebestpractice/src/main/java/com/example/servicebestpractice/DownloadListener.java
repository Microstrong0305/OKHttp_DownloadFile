package com.example.servicebestpractice;

/**
 * Created by Administrator on 2017/2/23.
 */
public interface DownloadListener {


    /**
     * 通知当前的下载进度
     * @param progress
     */
    void onProgress(int progress);

    /**
     * 通知下载成功
     */
    void onSuccess();

    /**
     * 通知下载失败
     */
    void onFailed();

    /**
     * 通知下载暂停
     */
    void onPaused();

    /**
     * 通知下载取消事件
     */
    void onCanceled();

}
