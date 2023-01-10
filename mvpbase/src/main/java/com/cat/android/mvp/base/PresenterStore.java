package com.cat.android.mvp.base;

import java.util.HashMap;

public class PresenterStore {
    private final HashMap<String, Presenter> presenters = new HashMap<>();

    final void put(String key, Presenter presenter) {
        Presenter old = presenters.put(key, presenter);
        if (old != null) {
            old.destroy();
        }
    }

    final Presenter get(String key) {
        return presenters.get(key);
    }

    final void clear() {
        for (Presenter presenter : presenters.values()) {
            presenter.destroy();
        }
        presenters.clear();
    }
}
