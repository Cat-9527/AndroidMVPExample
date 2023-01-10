package com.cat.android.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.util.Consumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.Executor;

public class HttpClient {
    private final Executor mainExecutor;
    private final Executor ioExecutor;

    private HttpClient(Executor mainExecutor, Executor ioExecutor) {
        this.mainExecutor = mainExecutor;
        this.ioExecutor = ioExecutor;
    }

    private void get(String url, IOConsumer<HttpURLConnection> onSuccess,
        Consumer<IOException> onError) {
        ioExecutor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                connection = openConnection(url);
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == -1) {
                    throw new IOException("Could not retrieve response code");
                }
                onSuccess.accept(connection);
            } catch (IOException e) {
                onError.accept(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private HttpURLConnection openConnection(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setUseCaches(false);
        connection.setDoInput(true);
        return (HttpURLConnection) connection;
    }

    public void getString(String url, Callback<String> callback) {
        get(url, httpURLConnection -> {
            int contentLength;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                long longLength = httpURLConnection.getContentLengthLong();
                contentLength = longLength > Integer.MAX_VALUE ? -1 : (int) longLength;
            } else {
                contentLength = httpURLConnection.getContentLength();
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bytes = inputStreamToBytes(inputStream, contentLength);
            String result = new String(bytes, "UTF-8");
            success(callback, result);
        }, e -> {
            error(callback, e);
        });
    }

    public void getBitmap(String url, Callback<Bitmap> callback) {
        get(url, httpURLConnection -> {
            InputStream inputStream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            success(callback, bitmap);
        }, e -> {
            error(callback, e);
        });
    }

    private <T> void success(Callback<T> callback, T t) {
        Runnable runnable = () -> callback.onSuccess(t);
        if (callback.runOnMainThread()) {
            mainExecutor.execute(runnable);
        } else {
            runnable.run();
        }
    }

    private void error(Callback<?> callback, IOException e) {
        Runnable runnable = () -> callback.onError(e);
        if (callback.runOnMainThread()) {
            mainExecutor.execute(runnable);
        } else {
            runnable.run();
        }
    }

    static byte[] inputStreamToBytes(InputStream in, int contentLength) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(contentLength, 256));
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    public static final class Builder {
        private Executor mainExecutor = MainThreadExecutor.getsInstance();
        private Executor ioExecutor = IOExecutor.getInstance();

        public Builder setMainExecutor(Executor mainExecutor) {
            this.mainExecutor = Objects.requireNonNull(mainExecutor, "mainExecutor == null");
            return this;
        }

        public Builder setIoExecutor(Executor ioExecutor) {
            this.ioExecutor = Objects.requireNonNull(ioExecutor, "ioExecutor == null");
            return this;
        }

        public HttpClient build() {
            return new HttpClient(mainExecutor, ioExecutor);
        }
    }
}
