package com.cat.android.mvp.app;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Consumer;

class Utils {
    static void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    static void updateLayoutParams(View view, Consumer<ViewGroup.LayoutParams> updater) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        updater.accept(layoutParams);
        view.setLayoutParams(layoutParams);
    }

    private Utils() {
        throw new UnsupportedOperationException();
    }
}
