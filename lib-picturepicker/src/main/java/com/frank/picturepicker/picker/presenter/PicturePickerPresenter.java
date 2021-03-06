package com.frank.picturepicker.picker.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.frank.picturepicker.R;
import com.frank.picturepicker.picker.PicturePickerContract;
import com.frank.picturepicker.picker.data.PictureFolder;
import com.frank.picturepicker.picker.model.PicturePickerModel;
import com.frank.picturepicker.picker.view.activity.PicturePickerActivity;
import com.frank.picturepicker.picker.view.dialog.PicturePickerDialog;
import com.frank.picturepicker.support.callback.WatcherCallback;
import com.frank.picturepicker.support.config.PickerConfig;
import com.frank.picturepicker.support.loader.PictureLoader;
import com.frank.picturepicker.support.manager.picker.PicturePickerFragment;
import com.frank.picturepicker.support.manager.watcher.PictureWatcherManager;

import java.util.ArrayList;
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

    @Override
    public void setupUserPickedSet(ArrayList<String> userPicked) {
        mModel.setUserPickedSet(userPicked == null ? new ArrayList<String>() : userPicked);
        if (mView == null) return;
        mView.updateEnsureAndPreviewTextContent(fetchUserPickedSet().size(), mModel.getThreshold());
    }

    @Override
    public void setupThreshold(int threshold) {
        mModel.setThreshold(threshold);
    }

    @Override
    public void initData(Context context) {
        mModel.getSystemPictures(context, new PicturePickerContract.ModelInitializeCallback() {
            @Override
            public void onComplete(List<PictureFolder> pictureFolders) {
                mMainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 展示第一个图片文件夹
                        PictureFolder allPictureFolder = mModel.getPictureFolderAt(0);
                        if (mView == null) return;
                        mView.displayPictures(allPictureFolder.getFolderName(), allPictureFolder.getImagePaths());
                        mView.updateEnsureAndPreviewTextContent(fetchUserPickedSet().size(), mModel.getThreshold());
                    }
                });
            }

            @Override
            public void onFailed(Throwable throwable) {
                mMainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView == null) return;
                        mView.showMsg(mView.getString(R.string.activity_picture_picker_msg_fetch_album_failed));
                    }
                });
            }
        });
    }

    @Override
    public void fetchDisplayPictures(int position) {
        PictureFolder target = mModel.getPictureFolderAt(position);
        if (mView == null) return;
        mView.displayPictures(target.getFolderName(), target.getImagePaths());
    }

    @Override
    public ArrayList<PictureFolder> fetchAllPictureFolders() {
        return mModel.getAllPictureFolders();
    }

    @Override
    public ArrayList<String> fetchUserPickedSet() {
        return mModel.getUserPickedSet();
    }

    @Override
    public boolean performPictureChecked(String uri) {
        if (fetchUserPickedSet().size() == mModel.getThreshold()) {
            mView.showMsg(mView.getString(R.string.activity_picture_picker_msg_over_threshold_prefix)
                    + mModel.getThreshold()
                    + mView.getString(R.string.activity_picture_picker_msg_over_threshold_suffix)
            );
            return false;
        }
        mModel.addPickedPicture(uri);
        mView.updateEnsureAndPreviewTextContent(fetchUserPickedSet().size(), mModel.getThreshold());
        return true;
    }

    @Override
    public void performPictureUnchecked(String imagePath) {
        mModel.removePickedPicture(imagePath);
        mView.updateEnsureAndPreviewTextContent(fetchUserPickedSet().size(), mModel.getThreshold());
    }

    @Override
    public void performPictureClicked(View sharedElement, String uri, int position, PickerConfig config, ArrayList<String> pictureUris) {
        PictureWatcherManager.with(sharedElement.getContext())
                .setThreshold(mModel.getThreshold())
                .setIndicatorTextColor(config.indicatorTextColor)
                .setIndicatorSolidColor(config.indicatorSolidColor)
                .setIndicatorBorderColor(config.indicatorBorderCheckedColor, config.indicatorBorderUncheckedColor)
                .setPictureUris(pictureUris, position)
                .setUserPickedSet(fetchUserPickedSet())
                .setSharedElement(sharedElement)
                .setPictureLoader(PictureLoader.getPictureLoader())
                .start(new WatcherCallback() {
                    @Override
                    public void onResult(ArrayList<String> userPickedSet) {
                        if (mView == null) return;
                        setupUserPickedSet(userPickedSet);
                        mView.notifyUserPickedSetChanged();
                    }
                });
    }

    @Override
    public void performPreviewClicked(Context context, PickerConfig config) {
        if (fetchUserPickedSet().size() == 0) {
            mView.showMsg(mView.getString(R.string.activity_picture_picker_msg_preview_failed));
            return;
        }
        PictureWatcherManager.with(context)
                .setThreshold(mModel.getThreshold())
                .setIndicatorTextColor(config.indicatorTextColor)
                .setIndicatorSolidColor(config.indicatorSolidColor)
                .setIndicatorBorderColor(config.indicatorBorderCheckedColor, config.indicatorBorderUncheckedColor)
                .setPictureUris(fetchUserPickedSet(), 0)
                .setUserPickedSet(fetchUserPickedSet())
                .setPictureLoader(PictureLoader.getPictureLoader())
                .start(new WatcherCallback() {
                    @Override
                    public void onResult(ArrayList<String> userPickedSet) {
                        if (mView == null) return;
                        setupUserPickedSet(userPickedSet);
                        mView.notifyUserPickedSetChanged();
                    }
                });
    }

    @Override
    public void performEnsureClicked(PicturePickerActivity bind) {
        if (fetchUserPickedSet().size() == 0) {
            mView.showMsg(mView.getString(R.string.activity_picture_picker_msg_ensure_failed));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(PicturePickerFragment.RESULT_EXTRA_PICKED_PICTURES, fetchUserPickedSet());
        bind.setResult(PicturePickerFragment.REQUEST_CODE_PICKED, intent);
        bind.finish();
    }

    @Override
    public void performBottomMenuClicked(Context context) {
        new PicturePickerDialog(context, fetchAllPictureFolders())
                .setOnItemClickedListener(new PicturePickerDialog.OnItemClickedListener() {
                    @Override
                    public void onDialogItemClicked(int position) {
                        fetchDisplayPictures(position);
                    }
                })
                .show();
    }
}
