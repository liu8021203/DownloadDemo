package com.feng.download.downloaddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.feng.download.downloaddemo.download.BeanVO;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        List<BeanVO> data = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            BeanVO vo = new BeanVO();
            vo.setUrl("http://7xku92.com1.z0.glb.clouddn.com/app-release.apk");
            vo.setId(i);
            data.add(vo);
        }
        DownloadAdapter adapter = new DownloadAdapter(this, data);
        listView.setAdapter(adapter);
//        System.out.println(String.format("*f","{0:15} {1:<8} {2:<8} {3:18} {4:18} {5:18} {6:18} {7:18} {8:18}\n"));
    }
}
