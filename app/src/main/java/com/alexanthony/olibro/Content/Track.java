package com.alexanthony.olibro.Content;

// TODO: Decide if this should have an author
public class Track {

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
}
