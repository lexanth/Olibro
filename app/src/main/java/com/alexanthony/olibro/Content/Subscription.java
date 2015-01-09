package com.alexanthony.olibro.Content;

public class Subscription extends Compilation {
    public Subscription() {
        super();

    }

    public Subscription(long subID, String subName, String subArtist, String subPath, String subArtPath) {
        super(subID, subName, subArtist, subPath, subArtPath);
    }

    public String getArtist() {
        return getAuthor();
    }
}
