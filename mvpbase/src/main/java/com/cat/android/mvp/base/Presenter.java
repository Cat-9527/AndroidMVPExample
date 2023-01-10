package com.cat.android.mvp.base;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

public abstract class Presenter {
    static final String DEFAULT_KEY = "android.mvp.Presenter.DefaultKey:%s";
    private static final String TAG = "Presenter";

    private ViewStore viewStore;

    private ViewStore getViewStore() {
        if (viewStore == null) {
            viewStore = new ViewStore();
        }
        return viewStore;
    }

    static String defaultKeyFor(Class<? extends IView> viewClass) {
        String canonicalName = viewClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be Views");
        }
        return String.format(DEFAULT_KEY, canonicalName);
    }

    public final <T extends IView> void putView(Class<T> viewClass, T view) {
        putView(defaultKeyFor(viewClass), view);
    }

    public final void putView(String key, IView view) {
        getViewStore().put(key, view);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public final <T extends IView> T getView(Class<T> viewClass) {
        IView view = getView(defaultKeyFor(viewClass));
        if (viewClass.isInstance(view)) {
            return (T) view;
        } else if (view != null) {
            Log.w(TAG, view + " is not an instance of " + viewClass);
        }
        return null;
    }

    @Nullable
    public final IView getView(String key) {
        return getViewStore().get(key);
    }

    public final void removeView(Class<? extends IView> viewClass) {
        removeView(defaultKeyFor(viewClass));
    }

    public final void removeView(String key) {
        getViewStore().remove(key);
    }

    protected final <T extends IView> void runIfViewNonNull(Class<T> viewClass,
        Consumer<T> consumer) {
        T view = getView(viewClass);
        if (view != null) {
            consumer.accept(view);
        }
    }

    protected final void runIfViewNonNull(String key, Consumer<IView> consumer) {
        IView view = getView(key);
        if (view != null) {
            consumer.accept(view);
        }
    }

    protected void onDestroy() {
    }

    final void destroy() {
        getViewStore().clear();
        onDestroy();
    }
}
