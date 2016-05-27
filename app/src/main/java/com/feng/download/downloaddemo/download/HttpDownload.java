package com.feng.download.downloaddemo.download;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by liu on 16/5/25.
 */
public class HttpDownload {
    public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloaddemo/";
    public static String FILE_PATH = PATH + "file/";
    private static final int MAX_NUM = 3;
    private Map<String, BeanVO> downloadMap = new HashMap<>();
    private Map<String, DownloadThread> downloadThreadMap = new HashMap<>();
    private List<String> waitingList = new Vector<>();
    private List<String> threadList = new Vector<>();
    private boolean loading = false;

    public static HttpDownload downloadWork = null;
    private DownloadListener listener;


    public static HttpDownload getInstance(DownloadListener listener) {
        if (downloadWork == null) {
            downloadWork = new HttpDownload(listener);
        }
        return downloadWork;
    }

    public HttpDownload(DownloadListener listener) {
        this.listener = listener;
    }

    /**
     * 设置下载等待队列
     * @param vo
     */
    private void setDownloadWaitingList(BeanVO vo)
    {
        downloadMap.put(String.valueOf(vo.getId()), vo);
        if(!waitingList.contains(String.valueOf(vo.getId())))
        {
            waitingList.add(String.valueOf(vo.getId()));
            listener.onStart(vo);
        }
    }


    public void download(final BeanVO vo)
    {
        setDownloadWaitingList(vo);
        if(!loading)
        {
            new Thread(){
                @Override
                public void run() {
                    try {
                        //等待数量
                        int waitingSize = 0;
                        //下载数量
                        int threadlistSize = 0;
                        while ((waitingSize = waitingList.size()) > 0)
                        {
                            loading = true;
                            threadlistSize = threadList.size();
                            if(threadlistSize < MAX_NUM && threadlistSize < waitingSize)
                            {
                                String key = waitingList.get(threadlistSize);
                                if(!threadList.contains(key))
                                {
                                    threadList.add(key);
                                    BeanVO beanVO = downloadMap.get(key);
                                    File file = new File(FILE_PATH);
                                    DownloadThread downloadThread = new DownloadThread(beanVO, file, listener);
                                    downloadThread.start();
                                    downloadThreadMap.put(key, downloadThread);
                                }
                            }
                            Thread.sleep(1000);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        loading = false;
                    }
                }
            }.start();
        }
    }

    /**
     * 停止下载
     * @param vo
     */
    public void stopDownload(BeanVO vo)
    {
        String key = String.valueOf(vo.getId());
        DownloadThread thread = downloadThreadMap.get(key);
        if(thread != null)
        {
            thread.setPause();
        }

    }

    /**
     * 下载完成
     * @param vo
     */
    public void completeDownload(BeanVO vo) {
        String key = String.valueOf(vo.getId());
        if (threadList.contains(key)) {
            threadList.remove(key);
        }
        if (waitingList.contains(key)) {
            waitingList.remove(key);
        }
        downloadMap.remove(key);
        downloadThreadMap.remove(key);
    }

    public void cancle(BeanVO vo) {
        String key = String.valueOf(vo.getId());
        if (threadList.contains(key)) {
            threadList.remove(key);
        }
        if (waitingList.contains(key)) {
            waitingList.remove(key);
        }
        downloadMap.remove(key);
        downloadThreadMap.remove(key);
    }
}
