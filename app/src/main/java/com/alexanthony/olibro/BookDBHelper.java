package com.alexanthony.olibro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by alex on 08/01/15.
 */
public class BookDBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "olibroDB";
    private final String TABLE_BOOKS = "TABLE_BOOKS";
    private final String KEY_BOOK_ID = "KEY_BOOK_ID";
    private final String KEY_BOOK_NAME = "KEY_BOOK_NAME";
    private final String KEY_BOOK_AUTHOR = "KEY_BOOK_AUTHOR";
    private final String KEY_BOOK_ART_PATH = "KEY_BOOK_ART_PATH";
    // subfolder
    private final String KEY_BOOK_PATH = "KEY_BOOK_PATH";
    private final String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_BOOKS + " ( " +
            KEY_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_BOOK_PATH + " TEXT NOT NULL, " +
            KEY_BOOK_NAME + " TEXT NOT NULL, " +
            KEY_BOOK_ART_PATH + " TEXT NOT NULL, " +
            KEY_BOOK_AUTHOR + " TEXT NOT NULL);";
    private final String TABLE_CHAPTERS = "TABLE_CHAPTERS";
    private final String KEY_CHAPTER_ID = "KEY_CHAPTER_ID";
    private final String KEY_CHAPTER_TITLE = "KEY_CHAPTER_NAME";
    private final String KEY_CHAPTER_DURATION = "KEY_CHAPTER_DURATION";
    private final String KEY_CHAPTER_LAST_PLAYED_POSITION = "KEY_CHAPTER_LAST_PLAYED_POSITION";
    // file name within subfolder
    private final String KEY_CHAPTER_FILE = "KEY_CHAPTER_FILE";
    private final String CREATE_CHAPTER_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_CHAPTERS + " ( " +
            KEY_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CHAPTER_FILE + " TEXT NOT NULL, " +
            KEY_CHAPTER_TITLE + " TEXT NOT NULL, " +
            KEY_CHAPTER_DURATION + " INTEGER NOT NULL, " +
            KEY_CHAPTER_LAST_PLAYED_POSITION + " INTEGER NOT NULL," +
            KEY_BOOK_ID + " INTEGER);";
    private final String TABLE_SUBSCRIPTIONS = "TABLE_SUBSCRIPTIONS";
    private final String KEY_SUBSCRIPTION_ID = "KEY_SUBSCRIPTION_ID";
    private final String KEY_SUBSCRIPTION_NAME = "KEY_SUBSCRIPTION_NAME";
    private final String KEY_SUBSCRIPTION_AUTHOR = "KEY_SUBSCRIPTION_AUTHOR";
    private final String KEY_SUBSCRIPTION_ART_PATH = "KEY_SUBSCRIPTION_ART_PATH";
    // subfolder
    private final String KEY_SUBSCRIPTION_PATH = "KEY_SUBSCRIPTION_PATH";
    private final String CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_SUBSCRIPTIONS + " ( " +
            KEY_SUBSCRIPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SUBSCRIPTION_PATH + " TEXT NOT NULL, " +
            KEY_SUBSCRIPTION_NAME + " TEXT NOT NULL, " +
            KEY_SUBSCRIPTION_ART_PATH + " TEXT NOT NULL, " +
            KEY_SUBSCRIPTION_AUTHOR + " TEXT NOT NULL);";
    private final String TABLE_PODCASTS = "TABLE_PODCASTS";
    private final String KEY_PODCAST_ID = "KEY_PODCAST_ID";
    private final String KEY_PODCAST_TITLE = "KEY_PODCAST_NAME";
    private final String KEY_PODCAST_DURATION = "KEY_PODCAST_DURATION";
    private final String KEY_PODCAST_LAST_PLAYED_POSITION = "KEY_PODCAST_LAST_PLAYED_POSITION";
    // file name within subfolder
    private final String KEY_PODCAST_FILE = "KEY_PODCAST_FILE";
    private final String CREATE_PODCAST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PODCASTS + " ( " +
            KEY_PODCAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_PODCAST_FILE + " TEXT NOT NULL, " +
            KEY_PODCAST_TITLE + " TEXT NOT NULL, " +
            KEY_PODCAST_DURATION + " INTEGER NOT NULL, " +
            KEY_PODCAST_LAST_PLAYED_POSITION + " INTEGER NOT NULL);";
    
    public BookDBHelper(Context c) {
        super(c, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE_BOOK_TABLE);
            db.execSQL(CREATE_CHAPTER_TABLE);
            db.execSQL(CREATE_SUBSCRIPTION_TABLE);
            db.execSQL(CREATE_PODCAST_TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS";
        db.execSQL(query + TABLE_BOOKS);
        db.execSQL(query + TABLE_CHAPTERS);
        db.execSQL(query + TABLE_SUBSCRIPTIONS);
        db.execSQL(query + TABLE_PODCASTS);
        onCreate(db);
    }
    
    public void insertBook(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(KEY_BOOK_PATH, book.getPath());
        vals.put(KEY_BOOK_NAME, book.getName());
        vals.put(KEY_BOOK_ART_PATH, book.getArtPath());
        vals.put(KEY_BOOK_AUTHOR, book.getAuthor());
        db.beginTransaction();
        try {
            db.insert(TABLE_BOOKS, null, vals);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertSubscription(Subscription sub) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(KEY_SUBSCRIPTION_PATH, sub.getPath());
        vals.put(KEY_SUBSCRIPTION_NAME, sub.getName());
        vals.put(KEY_SUBSCRIPTION_ART_PATH, sub.getArtPath());
        vals.put(KEY_SUBSCRIPTION_AUTHOR, sub.getAuthor());
        db.beginTransaction();
        try {
            db.insert(TABLE_SUBSCRIPTIONS, null, vals);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertChapter(Chapter chapter) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(KEY_CHAPTER_FILE, chapter.getFileName());
        vals.put(KEY_CHAPTER_TITLE, chapter.getTitle());
        vals.put(KEY_CHAPTER_DURATION, chapter.getDuration());
        vals.put(KEY_CHAPTER_LAST_PLAYED_POSITION, chapter.getLastPlayed());
        db.beginTransaction();
        try {
            db.insert(TABLE_CHAPTERS, null, vals);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertPodcast(Podcast podcast) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(KEY_PODCAST_FILE, podcast.getFileName());
        vals.put(KEY_PODCAST_TITLE, podcast.getTitle());
        vals.put(KEY_PODCAST_DURATION, podcast.getDuration());
        vals.put(KEY_PODCAST_LAST_PLAYED_POSITION, podcast.getLastPlayed());
        db.beginTransaction();
        try {
            db.insert(TABLE_PODCASTS, null, vals);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    
    public ArrayList<Book> getAllBooks(){
        ArrayList<Book> books = new ArrayList<Book>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor bookCursor = db.query(TABLE_BOOKS, null, null, null, null, null, KEY_BOOK_NAME);
        if (bookCursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(bookCursor.getLong(bookCursor.getColumnIndex(KEY_BOOK_ID)));
                book.setPath(bookCursor.getString(bookCursor.getColumnIndex(KEY_BOOK_PATH)));
                book.setName(bookCursor.getString(bookCursor.getColumnIndex(KEY_BOOK_NAME)));
                book.setArtPath(bookCursor.getString(bookCursor.getColumnIndex(KEY_BOOK_ART_PATH)));
                book.setAuthor(bookCursor.getString(bookCursor.getColumnIndex(KEY_BOOK_AUTHOR)));
                //Cursor trackCursor = db.query(TABLE_CHAPTERS, null, null, null);
                //Cursor trackCursor = db.rawQuery(chapterQuery, new String[] { Long.toString(book.getId()) });
                books.add(book);
            } while (bookCursor.moveToNext());
        }
        return books;
    }
}
