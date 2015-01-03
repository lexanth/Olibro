package com.alexanthony.olibro;

public class Track {
	
	private long id;
	private String title;
	private String author;
	
	public Track(long trackID, String trackTitle, String trackAuthor){
		id = trackID;
		title = trackTitle;
		author = trackAuthor;
	}
	
	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getAuthor(){return author;}

}
