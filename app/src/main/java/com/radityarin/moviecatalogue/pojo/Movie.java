package com.radityarin.moviecatalogue.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;

public class Movie implements Parcelable, Serializable {

    private int id;
    private String title, year, description, rating, photo, type;

    public Movie(int id, String title, String year, String description, String rating, String photo, String type) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.description = description;
        this.rating = rating;
        this.photo = photo;
        this.type = type;
    }

    public Movie(JSONObject result, String type) {
        try {
            if (type.equals("movie")) {
                setTitle(result.getString("title"));
                setYear(result.getString("release_date"));
            } else {
                setTitle(result.getString("name"));
                setYear(result.getString("first_air_date"));
            }
            setDescription(result.getString("overview"));
            setRating(result.getString("vote_average"));
            setPhoto(result.getString("poster_path"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setType(type);
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getPhoto() {
        return photo;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setYear(String year) {
        this.year = year;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setRating(String rating) {
        this.rating = rating;
    }

    private void setPhoto(String photo) {
        this.photo = photo;
    }

    private void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.year);
        dest.writeString(this.description);
        dest.writeString(this.rating);
        dest.writeString(this.photo);
        dest.writeString(this.type);
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.year = in.readString();
        this.description = in.readString();
        this.rating = in.readString();
        this.photo = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}