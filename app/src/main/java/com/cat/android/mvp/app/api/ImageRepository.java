package com.cat.android.mvp.app.api;

import android.graphics.Bitmap;

public interface ImageRepository {
    void loadImage(Callback callback);

    boolean retry();

    interface Callback {
        void onImageSize(int width, int height);
        void onImage(Bitmap bitmap);
        void onError(Throwable t);
    }
}
