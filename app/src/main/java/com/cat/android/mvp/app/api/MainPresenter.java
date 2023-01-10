package com.cat.android.mvp.app.api;

import android.graphics.Bitmap;

import androidx.core.util.Consumer;

import com.cat.android.mvp.base.Presenter;
import com.cat.android.mvp.base.PresenterProvider;

public class MainPresenter extends Presenter {
    private final ImageRepository repository;
    private ImageRepository.Callback callback;

    public MainPresenter(ImageRepository repository, MainView view) {
        this.repository = repository;
        putView(MainView.class, view);
    }

    private void ensureCallback() {
        if (callback == null) {
            callback = new ImageRepositoryCallback();
        }
    }

    public void loadImage() {
        ensureCallback();
        repository.loadImage(callback);
        setViewLoading(true);
    }

    public boolean retry() {
        boolean started = repository.retry();
        if (started) {
            setViewLoading(true);
        }
        return started;
    }

    public void retryOrLoad() {
        if (!retry()) {
            loadImage();
        }
    }

    private void setViewLoading(boolean isLoading) {
        runIfMainViewNonNull(view -> view.setLoading(isLoading));
    }

    private void runIfMainViewNonNull(Consumer<MainView> consumer) {
        runIfViewNonNull(MainView.class, consumer);
    }

    private class ImageRepositoryCallback implements ImageRepository.Callback {
        @Override
        public void onImageSize(int width, int height) {
            runIfMainViewNonNull(view -> view.setImageViewAspectRatio((float) width / height));
        }

        @Override
        public void onImage(Bitmap bitmap) {
            setViewLoading(false);
            runIfMainViewNonNull(view -> view.setImageBitmap(bitmap));
        }

        @Override
        public void onError(Throwable t) {
            setViewLoading(false);
            runIfMainViewNonNull(view -> view.setError(t));
        }
    }

    public static class Factory implements PresenterProvider.Factory {
        private final ImageRepository repository;
        private final MainView mainView;

        public Factory(ImageRepository repository, MainView mainView) {
            this.repository = repository;
            this.mainView = mainView;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Presenter> T create(Class<T> presenterClass) {
            if (presenterClass.isAssignableFrom(MainPresenter.class)) {
                return (T) new MainPresenter(repository, mainView);
            }
            throw new IllegalArgumentException("Unknown Presenter class");
        }
    }
}
