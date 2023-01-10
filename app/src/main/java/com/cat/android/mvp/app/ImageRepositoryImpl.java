package com.cat.android.mvp.app;

import android.graphics.Bitmap;
import android.net.Uri;

import com.cat.android.mvp.app.api.Image;
import com.cat.android.mvp.app.api.ImageRepository;

class ImageRepositoryImpl implements ImageRepository {
    private final ImageDataSource dataSource;
    private Image image;
    private Bitmap bitmap;
    private Runnable retryAction;

    ImageRepositoryImpl(ImageDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void loadImage(Callback callback) {
        dataSource.loadImage(image -> {
            this.image = image;
            callback.onImageSize(image.getWidth(), image.getHeight());
            retryAction = null;
            loadBitmap(image.getUrl(), callback);
        }, throwable -> {
            callback.onError(throwable);
            retryAction = () -> loadImage(callback);
        });
    }

    private void loadBitmap(Uri url, Callback callback) {
        dataSource.loadBitmap(url.toString(), bitmap -> {
            this.bitmap = bitmap;
            callback.onImage(bitmap);
            retryAction = null;
        }, throwable -> {
            callback.onError(throwable);
            retryAction = () -> loadBitmap(url, callback);
        });
    }

    @Override
    public boolean retry() {
        if (retryAction != null) {
            retryAction.run();
            retryAction = null;
            return true;
        }
        return false;
    }
}
