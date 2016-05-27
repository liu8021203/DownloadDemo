package com.feng.download.downloaddemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feng.download.downloaddemo.download.BeanVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 16/5/27.
 */
public class DBController {
    private DBHelper helper;

    public DBController(Context context) {
        helper = new DBHelper(context);
    }

    public void insert(BeanVO vo)
    {
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query("download", new String[]{"id"}, "id = ?", new String[]{String.valueOf(vo.getId())}, null, null,null);
        try{
            if(cursor != null){
                if(!cursor.moveToNext())
                {
                    database.execSQL("insert into download values(null, ?, ?, ?, ?, ?)", new String[]{String.valueOf(vo.getId()),vo.getUrl(), String.valueOf(vo.getSize()), String.valueOf(vo.getCompleteSize()), String.valueOf(vo.getState())});
                }
                else {
                    database.execSQL("update download set state = ?, url = ?, size = ?, completesize = ? where id = ?", new String[]{String.valueOf(vo.getState()), vo.getUrl(), String.valueOf(vo.getSize()), String.valueOf(vo.getCompleteSize()), String.valueOf(vo.getId())});
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(cursor != null)
            {
                cursor.close();
            }
            database.close();
        }
    }

    public List<BeanVO> query()
    {
        List<BeanVO> data = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query("download", new String[]{"id, url, size, completesize, state"}, null, null, null, null,null);
        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                BeanVO vo = new BeanVO();
                vo.setId(cursor.getInt(cursor.getColumnIndex("id")));
                vo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                vo.setSize(cursor.getInt(cursor.getColumnIndex("size")));
                vo.setCompleteSize(cursor.getInt(cursor.getColumnIndex("completesize")));
                vo.setState(cursor.getInt(cursor.getColumnIndex("state")));
                data.add(vo);
            }
        }
        cursor.close();
        database.close();
        return data;
    }


    public void delete(BeanVO vo)
    {
        SQLiteDatabase database = helper.getWritableDatabase();
        try{
            database.delete("download", "id = ?",  new String[]{String.valueOf(vo.getId())});
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            database.close();
        }
    }
}
