package com.cat.android.mvp.base;

import android.util.Log;

public class PresenterProvider {
    static final String DEFAULT_KEY = "android.mvp.PresenterProvider.DefaultKey:%s";
    private static final String TAG = "PresenterProvider";

    public interface Factory {
        <T extends Presenter> T create(Class<T> presenterClass);
    }

    private final PresenterStore presenterStore;
    private final Factory factory;

    public PresenterProvider(PresenterStoreOwner owner) {
        this(owner.getPresenterStore(), NewInstanceFactory.getInstance());
    }

    public PresenterProvider(PresenterStoreOwner owner, Factory factory) {
        this(owner.getPresenterStore(), factory);
    }

    public PresenterProvider(PresenterStore presenterStore, Factory factory) {
        this.presenterStore = presenterStore;
        this.factory = factory;
    }

    public <T extends Presenter> T get(Class<T> presenterClass) {
        String canonicalName = presenterClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be Presenters");
        }
        return get(String.format(DEFAULT_KEY, canonicalName), presenterClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Presenter> T get(String key, Class<T> presenterClass) {
        Presenter presenter = presenterStore.get(key);
        if (presenterClass.isInstance(presenter)) {
            return (T) presenter;
        } else if (presenter != null) {
            Log.w(TAG, "Presenter associated with '" + key + "' is not an instance of " +
                presenterClass);
        }
        presenter = factory.create(presenterClass);
        presenterStore.put(key, presenter);
        return (T) presenter;
    }

    static class NewInstanceFactory implements Factory {
        private static NewInstanceFactory sInstance;

        static NewInstanceFactory getInstance() {
            if (sInstance == null) {
                sInstance = new NewInstanceFactory();
            }
            return sInstance;
        }

        @Override
        public <T extends Presenter> T create(Class<T> presenterClass) {
            try {
                return presenterClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + presenterClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + presenterClass, e);
            }
        }
    }
}
