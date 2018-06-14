package com.frank.lib_picturepicker.picturepicker.mvp.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.frank.lib_picturepicker.picturepicker.data.PictureFolder;
import com.frank.lib_picturepicker.picturepicker.mvp.PicturePickerContract;
import com.frank.lib_picturepicker.picturepicker.mvp.model.PicturePickerModel;

import java.util.List;

/**
 * Created by think on 2018/5/26.
 * Email: frankchoochina@gmail.com
 * Version: 1.0
 * Description: 图片选择器的 Presenter
 */
public class PicturePickerPresenter implements PicturePickerContract.IPresenter {

    private PicturePickerContract.IView mView;
    private PicturePickerModel mModel = new PicturePickerModel();
    private Handler mMainLooperHandler = new Handler(Looper.getMainLooper());

    @Override
    public void attach(PicturePickerContract.IView view) {
        this.mView = view;
    }

    /**
     * 添加用户跳转到 View 时携带的图片地址集合
     *
     * @param userPicked
     */
    @Override
    public void setupUserPicked(List<String> userPicked) {
        if (userPicked != null && !userPicked.isEmpty()) {
            for (String path : userPicked) mModel.addPickedPicture(path);
        }
    }

    /**
     * 配置阈值
     *
     * @param threshold
     */
    @Override
    public void setupThreshold(int threshold) {
        mModel.setThreshold(threshold);
    }

    /**
     * 初始化 Model 的数据
     *
     * @param context
     */
    @Override
    public void initData(Context context) {
        mModel.init(context, new PicturePickerContract.ModelInitializeCallback() {
            @Override
            public void onComplete(List<PictureFolder> pictureFolders) {
                mMainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 展示第一个图片文件夹
                        PictureFolder allPictureFolder = mModel.getPictureFolderAt(0);
                        if (mView == null) return;
                        mView.displaySelectedFolder(allPictureFolder.getFolderName(), allPictureFolder.getImagePaths());
                        mView.updateTextContent(mModel.getPickedPictures().size(), mModel.getThreshold());
                        mView.updateTextViewVisibility(mModel.getPickedPictures().size() > 0);
                    }
                });
            }

            @Override
            public void onFailed(Throwable throwable) {
                mMainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView == null) return;
                        mView.showMsg("获取相册数据失败");
                    }
                });
            }
        });
    }

    /**
     * 获取 <<指定索引处的>> 图片文件夹
     *
     * @param position
     */
    @Override
    public void fetchPicturePathsAt(int position) {
        PictureFolder target = mModel.getPictureFolderAt(position);
        if (mView == null) return;
        mView.displaySelectedFolder(target.getFolderName(), target.getImagePaths());
    }

    /**
     * 获取所有图片文件夹
     */
    @Override
    public List<PictureFolder> fetchAllPictureFolders() {
        return mModel.getAllPictureFolders();
    }

    /**
     * 获取用户选中的所有图片
     *
     * @return
     */
    @Override
    public List<String> getPickedPictures() {
        return mModel.getPickedPictures();
    }

    /**
     * 处理图片被选中了
     *
     * @param imagePath
     */
    @Override
    public boolean performPicturePicked(String imagePath) {
        if (getPickedPictures().size() == mModel.getThreshold()) {
            mView.showMsg("最多只可选择 " + mModel.getThreshold() + " 张图片");
            return false;
        }
        mModel.addPickedPicture(imagePath);
        mView.updateTextContent(mModel.getPickedPictures().size(), mModel.getThreshold());
        mView.updateTextViewVisibility(mModel.getPickedPictures().size() > 0);
        return true;
    }

    /**
     * 处理图片被移除了
     *
     * @param imagePath
     */
    @Override
    public void performPictureRemoved(String imagePath) {
        mModel.removePickedPicture(imagePath);
        mView.updateTextContent(mModel.getPickedPictures().size(), mModel.getThreshold());
        mView.updateTextViewVisibility(mModel.getPickedPictures().size() > 0);
    }


}