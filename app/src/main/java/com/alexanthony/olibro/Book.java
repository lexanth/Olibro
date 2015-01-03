package com.alexanthony.olibro;

public class Book {
	
	private long id;
	private String title;
	private String author;
	
	public Book(long bookID, String bookTitle, String bookAuthor){
		id = bookID;
		title = bookTitle;
		author = bookAuthor;
	}
	
	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getAuthor(){return author;}

}
