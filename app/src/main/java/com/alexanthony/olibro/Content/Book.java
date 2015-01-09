package com.alexanthony.olibro.Content;

public class Book extends Compilation {
    public Book() {
        super();

    }

    public Book(long bookID, String bookName, String bookAuthor, String bookPath, String bookArtPath) {
        //
        super(bookID, bookName, bookAuthor, bookPath, bookArtPath);
    }

}
