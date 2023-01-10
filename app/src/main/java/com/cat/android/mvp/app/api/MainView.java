package com.cat.android.mvp.app.api;

import android.graphics.Bitmap;

import com.cat.android.mvp.base.IView;

public interface MainView extends IView {
    void setLoading(boolean isLoading);
    void setError(Throwable throwable);
    void setImageViewAspectRatio(float aspectRatio);
    void setImageBitmap(Bitmap bitmap);
}
