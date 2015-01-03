package com.alexanthony.olibro;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.app.Notification;
import android.app.PendingIntent;

public class PlayerService extends Service implements
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener {

	//media player
	private MediaPlayer player;
	//book list
	private ArrayList<Book> books;
	//current position
	private int bookPosn;
	//binder
	private final IBinder musicBind = new MusicBinder();
    // book title
    private String bookTitle="";
    // Notification ID
    private static final int NOTIFY_ID=1;

	public void onCreate(){
		//create the service
		super.onCreate();
		//initialize position
		bookPosn=0;
		//create player
		player = new MediaPlayer();
		//initialize
		initMusicPlayer();
	}

	public void initMusicPlayer(){
		//set player properties
		player.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		//set listeners
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	//pass book list
	public void setList(ArrayList<Book> theBooks){
		books = theBooks;
	}

	//binder
	public class MusicBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
	}

	//activity will bind to service
	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	//release resources when unbind
	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		player.release();
		return false;
	}

	//play a book
	public void playBook(){
		//play
		player.reset();
		//get book
		Book playBook = books.get(bookPosn);
        bookTitle = playBook.getTitle();
		//get id
		long currBook = playBook.getID();
		//set uri
		Uri trackUri = ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				currBook);
		//set the data source
		try{ 
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e){
			Log.e("PLAYER SERVICE", "Error setting data source", e);
		}
		player.prepareAsync(); 
	}

	//set the book
	public void setBook(int bookIndex){
		bookPosn=bookIndex;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		//start playback
		mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(bookTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(bookTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
	}

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        bookPosn--;
        if(bookPosn<0) bookPosn=books.size()-1;
        playBook();
    }

    public void playNext(){
        bookPosn++;
        if(bookPosn>books.size()) bookPosn=0;
        playBook();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
