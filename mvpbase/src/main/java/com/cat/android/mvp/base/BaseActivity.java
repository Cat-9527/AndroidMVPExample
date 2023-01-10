package com.cat.android.mvp.base;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements PresenterStoreOwner {
    private PresenterStore presenterStore;

    private void ensurePresenterStore() {
        if (presenterStore == null) {
            presenterStore = new PresenterStore();
        }
    }

    @Override
    public PresenterStore getPresenterStore() {
        ensurePresenterStore();
        return presenterStore;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenterStore != null) {
            presenterStore.clear();
        }
    }
}
