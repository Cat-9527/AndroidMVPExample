package com.cat.android.mvp.app.api;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class Image implements Parcelable {
    private final String id;
    private final Uri url;
    private final int width;
    private final int height;

    public Image(@NonNull String id, @NonNull Uri url, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
    }

    private Image(Parcel in) {
        id = in.readString();
        url = in.readParcelable(Uri.class.getClassLoader());
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(url, flags);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public Uri getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return (float) width / height;
    }

    public String getUniqueId() {
        return String.format(Locale.US, "%s-%dx%d", id, width, height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;
        Image image = (Image) o;
        return getUniqueId().equals(image.getUniqueId());
    }

    @Override
    public String toString() {
        return "Image{" +
            "id='" + id + '\'' +
            ", url=" + url +
            ", width=" + width +
            ", height=" + height +
            '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static Image fromJsonObject(JSONObject jsonObject) throws JSONException {
        return new Image(
            jsonObject.getString("id"),
            Uri.parse(jsonObject.getString("url")),
            jsonObject.getInt("width"),
            jsonObject.getInt("height"));
    }

    public static Image fromJsonString(String jsonString) {
        try {
            return fromJsonObject(new JSONObject(jsonString));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Image> fromJsonArrayString(String jsonArrayString) {
        List<Image> images;
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            images = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                images.add(fromJsonObject(jsonObject));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return Collections.unmodifiableList(images);
    }

    public static final Creator<Image> CREATOR = new Creator<>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
