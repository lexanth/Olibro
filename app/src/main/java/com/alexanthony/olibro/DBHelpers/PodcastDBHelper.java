package com.alexanthony.olibro.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alexanthony.olibro.Content.Podcast;

import java.util.ArrayList;

public class PodcastDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "olibroDB";
    private final String KEY_SUBSCRIPTION_ID = "KEY_SUBSCRIPTION_ID";

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
            KEY_PODCAST_LAST_PLAYED_POSITION + " INTEGER, " +
            KEY_SUBSCRIPTION_ID + " INTEGER);";

    public PodcastDBHelper(Context c) {
        super(c, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE_PODCAST_TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS";
        db.execSQL(query + TABLE_PODCASTS);
        onCreate(db);
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

    public ArrayList<Podcast> getAllPodcastsForSubscription(long subID) {
        // Use getAllPodcastsForSubscription(-1) to get all for all subs
        ArrayList<Podcast> pods = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor podCursor;
        if (subID == -1) {
            podCursor = db.query(TABLE_PODCASTS, null, null, null, null, null, KEY_SUBSCRIPTION_ID);
        } else {
            podCursor = db.query(TABLE_PODCASTS, null, KEY_SUBSCRIPTION_ID + "= ?", new String[]{Long.toString(subID)}, null, null, KEY_PODCAST_TITLE);
        }
        if (podCursor.moveToFirst()) {
            do {
                Podcast pod = new Podcast();
                pod.setId(podCursor.getLong(podCursor.getColumnIndex(KEY_PODCAST_ID)));
                pod.setFileName(podCursor.getString(podCursor.getColumnIndex(KEY_PODCAST_FILE)));
                pod.setTitle(podCursor.getString(podCursor.getColumnIndex(KEY_PODCAST_TITLE)));
                pod.setDuration(podCursor.getInt(podCursor.getColumnIndex(KEY_PODCAST_DURATION)));
                pod.setLastPlayed(podCursor.getInt(podCursor.getColumnIndex(KEY_PODCAST_LAST_PLAYED_POSITION)));
                pod.setSubscriptionID(podCursor.getLong(podCursor.getColumnIndex(KEY_SUBSCRIPTION_ID)));
                pods.add(pod);
            } while (podCursor.moveToNext());
        }
        return pods;
    }

    public ArrayList<Podcast> getAllPodcasts() {
        return getAllPodcastsForSubscription(-1);
    }
}
