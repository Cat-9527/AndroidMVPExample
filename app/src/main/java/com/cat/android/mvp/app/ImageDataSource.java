package com.cat.android.mvp.app;

import android.graphics.Bitmap;

import androidx.core.util.Consumer;

import com.cat.android.mvp.app.api.Image;
import com.cat.android.network.Callback;
import com.cat.android.network.HttpClient;

import java.io.IOException;
import java.util.List;

class ImageDataSource {
    private static final String URL = "https://api.thecatapi.com/v1/images/search";

    private HttpClient client;

    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient.Builder().build();
        }
        return client;
    }

    void loadImage(Consumer<Image> onSuccess, Consumer<Throwable> onError) {
        getClient().getString(URL, new Callback<>() {
            @Override
            public void onSuccess(String s) {
                List<Image> images = Image.fromJsonArrayString(s);
                onSuccess.accept(images.get(0));
            }

            @Override
            public void onError(IOException e) {
                onError.accept(e);
            }
        });
    }

    void loadBitmap(String url, Consumer<Bitmap> onSuccess, Consumer<Throwable> onError) {
        getClient().getBitmap(url, new Callback<>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                onSuccess.accept(bitmap);
            }

            @Override
            public void onError(IOException e) {
                onError.accept(e);
            }
        });
    }
}
