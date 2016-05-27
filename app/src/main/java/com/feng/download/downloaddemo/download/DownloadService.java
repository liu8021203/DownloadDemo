package com.feng.download.downloaddemo.download;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.feng.download.downloaddemo.db.DBController;

/**
 * Created by liu on 16/5/25.
 */
public class DownloadService extends Service implements DownloadListener{
    /**
     * 下载
     */
    public static final int ACTION_DOWN = 1;
    /**
     * 暂停
     */
    public static final int ACTION_STOP = 2;

    /**
     * 下载进度条
     */
    public static final String BROADCAST_ACTION_PROGRESS = "com.demo.download.progress";
    /**
     * 下载完成
     */
    public static final String BROADCAST_ACTION_COMPLETE = "com.demo.download.complete";
    /**
     * 下载开始
     */
    public static final String BROADCAST_ACTION_START = "com.demo.download.start";
    /**
     * 下载错误
     */
    public static final String BROADCAST_ACTION_ERROR = "com.demo.download.error";
    /**
     * 下载取消
     */
    public static final String BROADCAST_ACTION_CANCLE = "com.demo.download.cancle";


    private static final int HandlerStart = 1;
    private static final int HandlerProgress = 2;
    private static final int HandlerComplete = 3;
    private static final int HandlerError = 4;
    private static final int HandleCancle = 5;

    private HttpDownload download;

    private DBController controller;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int action = msg.what;
            Intent intent = new Intent();
            switch (action) {
                case HandlerStart:
                    intent.setAction(BROADCAST_ACTION_START);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    break;

                case HandlerProgress:
                    intent.setAction(BROADCAST_ACTION_PROGRESS);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    break;

                case HandlerComplete:

                    intent.setAction(BROADCAST_ACTION_COMPLETE);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    break;

                case HandlerError:
                    intent.setAction(BROADCAST_ACTION_ERROR);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    break;

                case HandleCancle:
                    intent.setAction(BROADCAST_ACTION_CANCLE);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate() {
        super.onCreate();
        download = HttpDownload.getInstance(this);
        controller = new DBController(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
        {
            return 0;
        }
        int msg = intent.getIntExtra("MSG", 0);
        BeanVO vo = intent.getParcelableExtra("vo");
        switch (msg)
        {
            case ACTION_DOWN:
                downAction(vo);
                break;

            case ACTION_STOP:
                stopAction(vo);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 开始下载
     * @param result
     */
    private void downAction(BeanVO result)
    {
        if(result == null)
        {
            return;
        }
        download.download(result);
    }

    private void stopAction(BeanVO vo)
    {
        if(vo == null)
        {
            return;
        }
        download.stopDownload(vo);
    }



    @Override
    public void onStart(BeanVO vo) {
        vo.setState(1);
        controller.insert(vo);
        Message message = new Message();
        message.what = HandlerStart;
        Bundle bundle = new Bundle();
        bundle.putParcelable("vo", vo);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onProgress(BeanVO vo) {
        System.out.println("下载进行中");
        vo.setState(2);
        controller.insert(vo);
        Message message = new Message();
        message.what = HandlerProgress;
        Bundle bundle = new Bundle();
        bundle.putParcelable("vo", vo);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onComplete(BeanVO vo) {
        System.out.println("下载完成");
        vo.setState(4);
        controller.insert(vo);
        Message message = new Message();
        message.what = HandlerComplete;
        Bundle bundle = new Bundle();
        bundle.putParcelable("vo", vo);
        message.setData(bundle);
        download.completeDownload(vo);
        handler.sendMessage(message);
    }

    @Override
    public void onError(BeanVO vo) {
        System.out.println("下载错误");
        vo.setState(0);
        controller.insert(vo);
        Message message = new Message();
        message.what = HandlerError;
        Bundle bundle = new Bundle();
        bundle.putParcelable("vo", vo);
        message.setData(bundle);
        download.cancle(vo);
        handler.sendMessage(message);
    }

    @Override
    public void onCancle(BeanVO vo) {
        System.out.println("下载暂停");
        vo.setState(3);
        controller.insert(vo);
        Message message = new Message();
        message.what = HandleCancle;
        Bundle bundle = new Bundle();
        bundle.putParcelable("vo", vo);
        message.setData(bundle);
        download.cancle(vo);
        handler.sendMessage(message);
    }
}
