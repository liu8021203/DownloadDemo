package com.feng.download.downloaddemo.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liu on 16/5/25.
 */
public class DownloadThread extends Thread{
    private BeanVO vo;
    private DownloadListener listener;
    private String fileUrl = null;
    private File saveFile;
    private boolean isPause = false;


    public DownloadThread(BeanVO vo, File fileSaveDir, DownloadListener listener)
    {
        this.vo = vo;
        this.listener = listener;
        this.fileUrl = vo.getUrl();
        if(!fileSaveDir.exists())
        {
            fileSaveDir.mkdirs();
        }
        this.saveFile = new File(fileSaveDir, "list_world" + vo.getId() + ".apk");
    }

    public void setPause() {
        isPause = true;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        long fileSize = 0;
        long completeSize = 0;
        try {
            fileSize = getResourceSize(fileUrl);
            completeSize = vo.getCompleteSize();
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Referer", url.toString());
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Range", "bytes=" + completeSize + "-" + fileSize);//设置获取实体数据的范围
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            connection.setRequestProperty("Connection", "Keep-Alive");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL)
            {
                vo.setSize(fileSize);
                inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int offset = 0;
                randomAccessFile = new RandomAccessFile(saveFile, "rwd");
                randomAccessFile.seek(completeSize);
                long currentTime = 0;
                while ((offset = inputStream.read(buffer)) != -1 && !isPause)
                {
                    randomAccessFile.write(buffer, 0, offset);
                    completeSize += offset;
                    if(System.currentTimeMillis() - currentTime >= 3000)
                    {
                        currentTime = System.currentTimeMillis();
                        vo.setCompleteSize(completeSize);
                        listener.onProgress(vo);
                    }
                }
                vo.setCompleteSize(completeSize);
                if(!isPause)
                {
                    listener.onComplete(vo);
                }
                else
                {
                    listener.onCancle(vo);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            listener.onError(vo);
        }
        finally {
            try{
                if(inputStream != null)
                {
                    inputStream.close();
                }
                if(randomAccessFile != null)
                {
                    randomAccessFile.close();
                }
                if(connection != null)
                {
                    connection.disconnect();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * 网络获取下载文件的大小
     * @param url
     * @return
     */
    private long getResourceSize(String url)
    {
        long size = 0;
        try{
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            InputStream inputStream = connection.getInputStream();
            size = connection.getContentLength();
            inputStream.close();
            connection.disconnect();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return size;
    }

}
