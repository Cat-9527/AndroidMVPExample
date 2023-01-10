package com.cat.android.mvp.base;

import androidx.annotation.Nullable;

import java.util.HashMap;

class ViewStore {
    private final HashMap<String, IView> views = new HashMap<>();

    final void put(String key, IView view) {
        views.put(key, view);
    }

    @Nullable
    final IView get(String key) {
        return views.get(key);
    }

    final void remove(String key) {
        views.remove(key);
    }

    final void clear() {
        views.clear();
    }
}
