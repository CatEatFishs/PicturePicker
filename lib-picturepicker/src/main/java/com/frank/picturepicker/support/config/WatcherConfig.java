package com.frank.picturepicker.support.config;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Frank on 2018/6/19.
 * Email: frankchoochina@gmail.com
 * Version: 1.0
 * Description:
 */
public class WatcherConfig implements Parcelable {

    // 图片选择的相关配置
    public ArrayList<String> pictureUris;// 需要展示的集合
    public ArrayList<String> userPickedSet;// 选中的集合
    public int threshold;// 阈值

    // 指示器背景色
    public int indicatorTextColor = Color.WHITE;
    public int indicatorSolidColor = Color.parseColor("#ff64b6f6");// 指示器选中的填充色
    public int indicatorBorderCheckedColor = indicatorSolidColor;// 指示器边框选中的颜色
    public int indicatorBorderUncheckedColor = Color.WHITE;// 指示器边框未被选中的颜色

    // 定位展示的位置
    public int position;

    public WatcherConfig() {
    }

    protected WatcherConfig(Parcel in) {
        pictureUris = in.createStringArrayList();
        userPickedSet = in.createStringArrayList();
        threshold = in.readInt();
        indicatorTextColor = in.readInt();
        indicatorSolidColor = in.readInt();
        indicatorBorderCheckedColor = in.readInt();
        indicatorBorderUncheckedColor = in.readInt();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(pictureUris);
        dest.writeStringList(userPickedSet);
        dest.writeInt(threshold);
        dest.writeInt(indicatorTextColor);
        dest.writeInt(indicatorSolidColor);
        dest.writeInt(indicatorBorderCheckedColor);
        dest.writeInt(indicatorBorderUncheckedColor);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatcherConfig> CREATOR = new Creator<WatcherConfig>() {
        @Override
        public WatcherConfig createFromParcel(Parcel in) {
            return new WatcherConfig(in);
        }

        @Override
        public WatcherConfig[] newArray(int size) {
            return new WatcherConfig[size];
        }
    };
}
