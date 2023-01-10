package com.cat.android.mvp.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cat.android.mvp.app.api.ImageRepository;
import com.cat.android.mvp.app.api.MainPresenter;
import com.cat.android.mvp.app.api.MainView;
import com.cat.android.mvp.base.BaseActivity;
import com.cat.android.mvp.base.PresenterProvider;

public class MainActivity extends BaseActivity implements MainView {
    private ImageView imageView;
    private TextView button;
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        button = findViewById(R.id.button);
        loadingView = findViewById(R.id.loading);

        ImageRepository repo = new ImageRepositoryImpl(new ImageDataSource());
        MainPresenter presenter = new PresenterProvider(this,
            new MainPresenter.Factory(repo, this)).get(MainPresenter.class);

        button.setOnClickListener(v -> presenter.retryOrLoad());
    }

    @Override
    public void setLoading(boolean isLoading) {
        Utils.setVisible(loadingView, isLoading);
        button.setEnabled(!isLoading);
        if (isLoading) {
            button.setText(R.string.loading);
            button.setError(null);
        }
    }

    @Override
    public void setError(Throwable throwable) {
        button.setText(R.string.retry);
        button.setError("Failed to load image");
    }

    @Override
    public void setImageViewAspectRatio(float aspectRatio) {
        int newHeight = (int) (imageView.getWidth() / aspectRatio);
        Utils.updateLayoutParams(imageView, layoutParams -> layoutParams.height = newHeight);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        button.setText(R.string.update_image);
        button.setError(null);
    }
}