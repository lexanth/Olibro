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
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends Activity implements MediaPlayerControl {

	//book list variables
	private ArrayList<Book> bookList;
	private ListView bookView;
    private BookController controller;
    //service
    private PlayerService playerSrv;
    private Intent playIntent;
    //binding
    private boolean bookBound=false;
    private boolean paused=false, playbackPaused=false;

    @Override
    public void start() {
        playerSrv.go();
    }

    @Override
    public void pause() {
        playerSrv.pausePlayer();
        playbackPaused = true;
    }

    @Override
    public int getDuration() {
        if (playerSrv != null && bookBound && playerSrv.isPng())
            return playerSrv.getDur();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (playerSrv != null && bookBound && playerSrv.isPng())
            return playerSrv.getPosn();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        playerSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        return playerSrv != null && bookBound && playerSrv.isPng();
//        if (playerSrv != null && bookBound)
//            return playerSrv.isPng();
//        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

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
        // set up controller
        setController();
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
			bookBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bookBound = false;
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
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
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

    private void setController() {
        controller = new BookController(this);
        // Set up prev and next handlers
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        // Attach the controller to this activity
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.book_list));
        controller.setEnabled(true);
    }

    private void playNext() {
        playerSrv.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playPrev() {
        playerSrv.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }
}
