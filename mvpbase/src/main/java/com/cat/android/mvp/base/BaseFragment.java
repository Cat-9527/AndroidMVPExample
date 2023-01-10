package com.cat.android.mvp.base;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements PresenterStoreOwner {
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
    public void onDestroy() {
        super.onDestroy();
        if (presenterStore != null) {
            presenterStore.clear();
        }
    }
}
