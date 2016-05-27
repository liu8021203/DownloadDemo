package com.feng.download.downloaddemo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.feng.download.downloaddemo.db.DBController;
import com.feng.download.downloaddemo.download.BeanVO;
import com.feng.download.downloaddemo.download.DownloadService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liu on 16/5/25.
 */
public class DownloadAdapter extends BaseAdapter{
    private MainActivity activity;
    private DownloadReceiver downloadReceiver;
    private DBController controller;
    private Map<String, BeanVO> downloadMap = new HashMap<String, BeanVO>();
    private List<BeanVO> data;


    private DownloadOnClickListener downloadOnClickListener;
    private DownloadStopOnClickListener downloadStopOnClickListener;
    private DeleteOnClickListener deleteOnClickListener;


    public DownloadAdapter(MainActivity activity, List<BeanVO> data) {
        this.activity = activity;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.BROADCAST_ACTION_START);
        intentFilter.addAction(DownloadService.BROADCAST_ACTION_PROGRESS);
        intentFilter.addAction(DownloadService.BROADCAST_ACTION_COMPLETE);
        intentFilter.addAction(DownloadService.BROADCAST_ACTION_CANCLE);
        intentFilter.addAction(DownloadService.BROADCAST_ACTION_ERROR);
        downloadReceiver = new DownloadReceiver();
        activity.registerReceiver(downloadReceiver, intentFilter);
        controller = new DBController(activity);
        this.data = data;
        initData();
        downloadOnClickListener = new DownloadOnClickListener();
        downloadStopOnClickListener = new DownloadStopOnClickListener();
        deleteOnClickListener = new DeleteOnClickListener();
    }


    private void initData() {
        List<BeanVO> data = controller.query();
        if(data != null && data.size() > 0)
        {
            for (int i = 0; i < data.size(); i++)
            {
                downloadMap.put(data.get(i).getId() + "", data.get(i));
                if(data.get(i).getState() == 2 || data.get(i).getState() == 1 || data.get(i).getState() == 3)
                {
                        Intent intent = new Intent(activity, DownloadService.class);
                        intent.putExtra("MSG", 1);
                        intent.putExtra("vo", data.get(i));
                        activity.startService(intent);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.list_download_item, null);
            holder.progressBar = (CircleProgressBar) convertView.findViewById(R.id.progress);
            holder.ivDownload = (ImageView) convertView.findViewById(R.id.iv_downlaod);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        BeanVO vo = data.get(position);
        BeanVO tempVO = downloadMap.get(vo.getId() + "");
        if(tempVO != null)
        {
            switch (tempVO.getState())
            {
                //下载错误
                case 0:
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivDownload.setVisibility(View.VISIBLE);
                    holder.ivDownload.setImageResource(R.mipmap.download_stop);
                    holder.ivDownload.setTag(tempVO);
                    holder.ivDownload.setOnClickListener(downloadOnClickListener);

                    break;

                case 1:
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivDownload.setVisibility(View.VISIBLE);
                    holder.ivDownload.setImageResource(R.mipmap.download_init);
                    break;

                case 2:
                    holder.ivDownload.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setTag(tempVO);
                    holder.progressBar.setOnClickListener(downloadStopOnClickListener);
                    break;

                case 3:
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivDownload.setVisibility(View.VISIBLE);
                    holder.ivDownload.setImageResource(R.mipmap.download_stop);
                    holder.ivDownload.setTag(tempVO);
                    holder.ivDownload.setOnClickListener(downloadOnClickListener);
                    break;

                case 4:
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivDownload.setVisibility(View.VISIBLE);
                    holder.ivDownload.setImageResource(R.mipmap.download_complete);
                    holder.ivDownload.setTag(tempVO);
                    holder.ivDownload.setOnClickListener(deleteOnClickListener);
                    break;
            }
            if(tempVO.getSize() != 0) {
                holder.progressBar.setProgress((int) (tempVO.getCompleteSize() * 100 / tempVO.getSize()));
            }
        }
        else
        {
            holder.ivDownload.setImageResource(R.mipmap.download_stop);
            holder.ivDownload.setTag(vo);
            holder.ivDownload.setOnClickListener(downloadOnClickListener);
        }
        return convertView;
    }


    private class ViewHolder
    {
        CircleProgressBar progressBar;
        ImageView ivDownload;
    }


    private void updateDownloadMap(BeanVO vo)
    {
        downloadMap.put(vo.getId() + "", vo);
        notifyDataSetChanged();
    }

    private void removeDownloadMap(BeanVO vo)
    {
        downloadMap.remove(vo.getId() + "");
        notifyDataSetChanged();
    }


    public class DownloadReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getBundleExtra("data");
            BeanVO vo = (BeanVO) bundle.getParcelable("vo");
            if(vo != null) {
                switch (action) {
                    case DownloadService.BROADCAST_ACTION_START:
                        updateDownloadMap(vo);
                        break;

                    case DownloadService.BROADCAST_ACTION_PROGRESS:
                        updateDownloadMap(vo);
                        break;


                    case DownloadService.BROADCAST_ACTION_COMPLETE:
                        updateDownloadMap(vo);
                        break;

                    case DownloadService.BROADCAST_ACTION_CANCLE:
                        updateDownloadMap(vo);
                        break;
                    case DownloadService.BROADCAST_ACTION_ERROR:
                        updateDownloadMap(vo);
                        break;
                }
            }
        }
    }


    /**
     * 开始下载监听
     */
    private class DownloadOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            BeanVO vo = (BeanVO) v.getTag();
            Intent intent = new Intent(activity, DownloadService.class);
            intent.putExtra("MSG", 1);
            intent.putExtra("vo", vo);
            activity.startService(intent);
        }
    }

    /**
     * 暂停下载监听
     */
    private class DownloadStopOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            BeanVO vo = (BeanVO) v.getTag();
            Intent intent = new Intent(activity, DownloadService.class);
            intent.putExtra("MSG", 2);
            intent.putExtra("vo", vo);
            activity.startService(intent);
        }
    }


    /**
     * 删除监听
     */
    private class DeleteOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {

            final BeanVO vo = (BeanVO) v.getTag();
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("确定删除？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    controller.delete(vo);
                    removeDownloadMap(vo);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }


}
