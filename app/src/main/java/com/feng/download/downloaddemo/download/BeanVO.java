package com.feng.download.downloaddemo.download;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by liu on 16/5/25.
 */
public class BeanVO implements Parcelable {
    private String url;
    private long size;
    private long completeSize;
    private int id;
    // 1：开始下载， 2：下载中， 3：下载暂停， 4：下载完成， 0：下载错误
    private int state;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeLong(this.size);
        dest.writeLong(this.completeSize);
        dest.writeInt(this.id);
        dest.writeInt(this.state);
    }

    public BeanVO() {
    }

    protected BeanVO(Parcel in) {
        this.url = in.readString();
        this.size = in.readLong();
        this.completeSize = in.readLong();
        this.id = in.readInt();
        this.state = in.readInt();
    }

    public static final Parcelable.Creator<BeanVO> CREATOR = new Parcelable.Creator<BeanVO>() {
        @Override
        public BeanVO createFromParcel(Parcel source) {
            return new BeanVO(source);
        }

        @Override
        public BeanVO[] newArray(int size) {
            return new BeanVO[size];
        }
    };
}
