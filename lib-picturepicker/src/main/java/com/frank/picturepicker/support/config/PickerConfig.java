package com.frank.picturepicker.support.config;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Frank on 2018/6/19.
 * Email: frankchoochina@gmail.com
 * Version: 1.0
 * Description: 图片选择器的配置属性类
 */
public class PickerConfig implements Parcelable {

    public static final int INVALIDATE_VALUE = -1;

    // 图片选择的相关配置
    public ArrayList<String> userPickedSet = new ArrayList<>();
    public int threshold = 9;
    public int spanCount = 3;

    // Toolbar 背景色
    public int toolbarBkgColor = Color.parseColor("#ff64b6f6");// 背景色
    public int toolbarBkgDrawableResId = INVALIDATE_VALUE;// 背景的Drawable

    // 指示器背景色
    public int indicatorTextColor = Color.WHITE;
    public int indicatorSolidColor = Color.parseColor("#ff64b6f6");// 指示器选中的填充色
    public int indicatorBorderCheckedColor = indicatorSolidColor;// 指示器边框选中的颜色
    public int indicatorBorderUncheckedColor = Color.WHITE;// 指示器边框未被选中的颜色

    // 是否展示滚动动画
    public boolean isShowScrollBehavior = false;

    public PickerConfig() {
    }

    protected PickerConfig(Parcel in) {
        userPickedSet = in.createStringArrayList();
        threshold = in.readInt();
        spanCount = in.readInt();
        toolbarBkgColor = in.readInt();
        toolbarBkgDrawableResId = in.readInt();
        indicatorTextColor = in.readInt();
        indicatorSolidColor = in.readInt();
        indicatorBorderCheckedColor = in.readInt();
        indicatorBorderUncheckedColor = in.readInt();
        isShowScrollBehavior = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(userPickedSet);
        dest.writeInt(threshold);
        dest.writeInt(spanCount);
        dest.writeInt(toolbarBkgColor);
        dest.writeInt(toolbarBkgDrawableResId);
        dest.writeInt(indicatorTextColor);
        dest.writeInt(indicatorSolidColor);
        dest.writeInt(indicatorBorderCheckedColor);
        dest.writeInt(indicatorBorderUncheckedColor);
        dest.writeByte((byte) (isShowScrollBehavior ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PickerConfig> CREATOR = new Creator<PickerConfig>() {
        @Override
        public PickerConfig createFromParcel(Parcel in) {
            return new PickerConfig(in);
        }

        @Override
        public PickerConfig[] newArray(int size) {
            return new PickerConfig[size];
        }
    };
}
