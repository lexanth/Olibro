package com.alexanthony.olibro.Content;

import java.util.ArrayList;

public class Compilation {
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
        tracks.add((Track) track);
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
}
