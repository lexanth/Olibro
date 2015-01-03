package com.alexanthony.olibro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.alexanthony.olibro.PlayerService.MusicBinder;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {

	//book list variables
	private ArrayList<Book> bookList;
	private ListView bookView;

	//service
	private PlayerService playerSrv;
	private Intent playIntent;
	//binding
	private boolean musicBound=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//retrieve list view
		bookView = (ListView)findViewById(R.id.book_list);
		//instantiate list
		bookList = new ArrayList<Book>();
		//get songs from device
		getBookList();
		//sort alphabetically by title
		Collections.sort(bookList, new Comparator<Book>(){
			public int compare(Book a, Book b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		//create and set adapter
		BookAdapter bookAdt = new BookAdapter(this, bookList);
		bookView.setAdapter(bookAdt);
	}

	//connect to the service
	private ServiceConnection playerConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder)service;
			//get service
			playerSrv = binder.getService();
			//pass list
			playerSrv.setList(bookList);
			musicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicBound = false;
		}
	};

	//start and bind the service when the activity starts
	@Override
	protected void onStart() {
		super.onStart();
		if(playIntent==null){
			playIntent = new Intent(this, PlayerService.class);
			bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
			startService(playIntent);
		}
	}

	//user book select
	public void bookPicked(View view){
		playerSrv.setBook(Integer.parseInt(view.getTag().toString()));
		playerSrv.playBook();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//menu item selected
		switch (item.getItemId()) {
		case R.id.action_shuffle:
			//shuffle
			break;
		case R.id.action_end:
			stopService(playIntent);
			playerSrv=null;
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//method to retrieve book info from device
	public void getBookList(){
		//query external audio
		ContentResolver mediaResolver = getContentResolver();
		Uri mediaUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor mediaCursor = mediaResolver.query(mediaUri, null, null, null, null);
		//iterate over results if valid
		if(mediaCursor!=null && mediaCursor.moveToFirst()){
			//get columns
			int titleColumn = mediaCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = mediaCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int authorColumn = mediaCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ARTIST);
			//add songs to list
			do {
				long thisId = mediaCursor.getLong(idColumn);
				String thisTitle = mediaCursor.getString(titleColumn);
				String thisAuthor = mediaCursor.getString(authorColumn);
				bookList.add(new Book(thisId, thisTitle, thisAuthor));
			} 
			while (mediaCursor.moveToNext());
		}
	}

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		playerSrv=null;
		super.onDestroy();
	}

}
