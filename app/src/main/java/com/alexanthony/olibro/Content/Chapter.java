package com.alexanthony.olibro.Content;

public class Chapter extends Track {
    public Chapter() {
        super();

    }

    public Chapter(long chapterID, String chapterTitle, long chapterDuration, long chapterLastPlayedPosition, long chapterBookID) {
        super(chapterID, chapterTitle, chapterDuration, chapterLastPlayedPosition, chapterBookID);
    }

    public long getBookID() {
        return getCompID();
    }

    public void setBookID(long bookID) {
        setCompID(bookID);
    }
}
