package com.cat.android.network;

import java.io.IOException;

public interface Callback<T> {
    void onSuccess(T t);

    void onError(IOException e);

    default boolean runOnMainThread() {
        return true;
    }
}
