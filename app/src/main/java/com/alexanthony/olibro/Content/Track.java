package com.alexanthony.olibro.Content;

import android.os.Parcel;
import android.os.Parcelable;

// Thanks to parcelabler.com
// TODO: decide if this should have author
public class Track implements Parcelable {

    private long id;
    private String title;
    private String author;
    private int duration;
    private int lastPlayed = 0;
    private long compID = 0;
    private String fileName;

    public Track() {
    }

    // Might be temporary, used when pulling track from MediaStore
    public Track(long trackID, String trackTitle, String trackAuthor) {
        id = trackID;
        title = trackTitle;
        author = trackAuthor;
    }

    public Track(long trackID, String trackTitle, String trackAuthor, int trackDuration, int lastPlayedPosition, long trackCompID) {
        id = trackID;
        title = trackTitle;
        author = trackAuthor;
        duration = trackDuration;
        lastPlayed = lastPlayedPosition;
        compID = trackCompID;
    }

    public Track(long trackID, String trackTitle, String trackAuthor, int trackDuration, int lastPlayedPosition) {
        id = trackID;
        title = trackTitle;
        author = trackAuthor;
        duration = trackDuration;
        lastPlayed = lastPlayedPosition;
    }

    public Track(long trackID, String trackTitle, String trackAuthor, int trackDuration) {
        id = trackID;
        title = trackTitle;
        author = trackAuthor;
        duration = trackDuration;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // should probably get book author
    public String getAuthor() {
        return author;
    }

    public int getDuration() {
        return duration;
    }

    public int getLastPlayed() {
        return lastPlayed;
    }

    public long getCompID() {
        return compID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLastPlayed(int lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void setCompID(long compID) {
        this.compID = compID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected Track(Parcel in) {
        id = in.readLong();
        title = in.readString();
        author = in.readString();
        duration = in.readInt();
        lastPlayed = in.readInt();
        compID = in.readLong();
        fileName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(duration);
        dest.writeInt(lastPlayed);
        dest.writeLong(compID);
        dest.writeString(fileName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}