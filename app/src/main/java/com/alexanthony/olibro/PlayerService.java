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
			Log.e("BOOK SERVICE", "Error setting data source", e);
		}
		player.prepareAsync(); 
	}

	//set the book
	public void setBook(int bookIndex){
		bookPosn=bookIndex;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		//start playback
		mp.start();
	}

}
