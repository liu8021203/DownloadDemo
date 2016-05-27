package com.feng.download.downloaddemo.download;

/**
 * Created by liu on 16/5/25.
 */
public interface DownloadListener {
    public void onStart(BeanVO vo);
    public void onProgress(BeanVO vo);
    public void onComplete(BeanVO vo);
    public void onError(BeanVO vo);
    public void onCancle(BeanVO vo);
}
