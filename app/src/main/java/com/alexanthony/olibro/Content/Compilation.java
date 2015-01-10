package com.alexanthony.olibro.Content;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Compilation implements Parcelable {
    private long id;
    private String name;
    private String author;
    private String path;
    private String artPath;
    private ArrayList tracks;

    public Compilation() {
    }

    public Compilation(long compID, String compName, String compAuthor, String compPath, String compArtPath) {
        id = compID;
        name = compName;
        author = compAuthor;
        path = compPath;
        artPath = compArtPath;

    }

    public void addTrack(Track track) {
        track.setCompID(id);
        tracks.add(track);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getPath() {
        return path;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    public void setTracks(ArrayList tracks) {
        this.tracks = tracks;
    }

    protected Compilation(Parcel in) {
        id = in.readLong();
        name = in.readString();
        author = in.readString();
        path = in.readString();
        artPath = in.readString();
        if (in.readByte() == 0x01) {
            tracks = new ArrayList<>();
            in.readList(tracks, getClass().getClassLoader());
        } else {
            tracks = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(path);
        dest.writeString(artPath);
        if (tracks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tracks);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Compilation> CREATOR = new Parcelable.Creator<Compilation>() {
        @Override
        public Compilation createFromParcel(Parcel in) {
            return new Compilation(in);
        }

        @Override
        public Compilation[] newArray(int size) {
            return new Compilation[size];
        }
    };
}