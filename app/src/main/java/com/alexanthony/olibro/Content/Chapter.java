package com.alexanthony.olibro.Content;

public class Chapter extends Track {
    public Chapter() {
        super();

    }

    public Chapter(long chapterID, String chapterTitle, String chapterAuthor, int chapterDuration, int chapterLastPlayedPosition, long chapterBookID) {
        super(chapterID, chapterTitle, chapterAuthor, chapterDuration, chapterLastPlayedPosition, chapterBookID);
    }

    public long getBookID() {
        return getCompID();
    }

    public void setBookID(long bookID) {
        setCompID(bookID);
    }
}
