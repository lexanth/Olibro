package com.alexanthony.olibro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.alexanthony.olibro.PlayerService.MediaBinder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.AsyncTask;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{

	//track list variables
	private ArrayList<Track> trackList;
	private ListView trackView;
    private TrackController controller;
    //service
    private PlayerService playerSrv;
    private Intent playIntent;
    //binding
    private boolean trackBound=false;
    private boolean paused=false, playbackPaused=false;
    private String bookPath="Audiobooks";
    private SlidingUpPanelLayout sliding_layout;
    private static final String TAG = "MainActivity";
    private SeekBar mSeekBar;

    public class SeekBarHandler extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            Log.d("##########Seek Bar Handler ################","###################Destroyed##################");
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while(isPlaying() && sliding_layout.isPanelExpanded()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressControls();
                    }
                    
                });
            }
            return null;
        }

    }

    public void start() {
        playerSrv.go();
        new SeekBarHandler().execute();
        // TODO: Update media player
        setMediaControl();
        ;
    }

    public void pause() {
        playerSrv.pausePlayer();
        playbackPaused = true;
    }

    public int getDuration() {
        if (playerSrv != null && trackBound && playerSrv.isPng())
            return playerSrv.getDur();
        return 0;
    }

    public int getCurrentPosition() {
        if (playerSrv != null && trackBound && playerSrv.isPng())
            return playerSrv.getPosn();
        return 0;
    }

    public void seekTo(int pos) {
        playerSrv.seek(pos);
    }

    public boolean isPlaying() {
        return playerSrv != null && trackBound && playerSrv.isPng();
//        if (playerSrv != null && trackBound)
//            return playerSrv.isPng();
//        return false;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//retrieve list view
		trackView = (ListView)findViewById(R.id.track_list);
		//instantiate list
		trackList = new ArrayList<Track>();
		//get songs from device
		getTrackList();
		//sort alphabetically by title
		Collections.sort(trackList, new Comparator<Track>(){
			public int compare(Track a, Track b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		//create and set adapter
		TrackAdapter trackAdt = new TrackAdapter(this, trackList);
		trackView.setAdapter(trackAdt);
        // set up controller
        setUpSlidingLayout();
	}

	//connect to the service
	private ServiceConnection playerConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MediaBinder binder = (MediaBinder)service;
			//get service
			playerSrv = binder.getService();
			//pass list
			playerSrv.setList(trackList);
			trackBound = true;
            // TODO: If last playing track from previous run available, load it and setMediaControl()
            //setMediaControl();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			trackBound = false;
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

	//user track select
	public void trackPicked(View view){
		playerSrv.setTrack(Integer.parseInt(view.getTag().toString()));
		playerSrv.playTrack();
        if (playbackPaused) {
            playbackPaused = false;
            //TODO Set up my media control
        }
        setMediaControl();
        new SeekBarHandler().execute();
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

	//method to retrieve track info from device
	public void getTrackList(){
		//query external audio
		ContentResolver mediaResolver = getContentResolver();
		Uri mediaUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA + " like " + "'%" + bookPath + "/%'";
		Cursor mediaCursor = mediaResolver.query(mediaUri, null, selection, null, null);
		//iterate over results if valid
        if (mediaCursor == null) {
            // Handle error
            Log.e("MainActivity", "MainActivity.getTrackList - null cursor");
        }
        if (mediaCursor.getCount() < 1) {
            // No media
        }
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
				trackList.add(new Track(thisId, thisTitle, thisAuthor));
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

    private void playNext() {
        playerSrv.playNext();
        if (playbackPaused) {
            //TODO: Update media player
            playbackPaused = false;
            setMediaControl();
            new SeekBarHandler().execute();
        }
    }

    private void playPrev() {
        playerSrv.playPrev();
        if (playbackPaused) {
            // TODO: Update media player
            playbackPaused = false;
            setMediaControl();
            new SeekBarHandler().execute();
        }
    }
    
    private void resume() {
        start();
    }
    
    public void setUpSlidingLayout() {
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        sliding_layout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                findViewById(R.id.current_track_art_image_small).setVisibility(View.VISIBLE);
                findViewById(R.id.play_pause_button).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                findViewById(R.id.current_track_art_image_small).setVisibility(View.GONE);
                findViewById(R.id.play_pause_button).setVisibility(View.GONE);
                new SeekBarHandler().execute();
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });
        mSeekBar = (SeekBar)findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
    }
    
    public void onPlayPauseClick(View view) {
        if (paused) {
            resume();
            paused = false;
        } else {
            pause();
            paused = true;
        }
        setPlayPauseButtons(!paused);
    }
    
    public void onPrevClick(View view) {
        playPrev();
    }
    
    public void onNextClick(View view) {
        playNext();
    }
    
    public void onBackClick(View view) {
        //
        
    }
    
    public void onForwardClick(View view) {
        //
        
    }
    
    private void setPlayPauseButtons(boolean playing) {
        Log.i(TAG, "setPlayPauseButtons " + playing);
        if (playing) {
//            int id = getResources().getIdentifier("ic_media_pause", "drawable", getPackageName());
            //((ImageView)findViewById(R.id.play_pause_button)).setImageResource(id);
            ((ImageView)findViewById(R.id.play_pause_button)).setImageResource(android.R.drawable.ic_media_pause);
            ((ImageView)findViewById(R.id.play_pause_button_2)).setImageResource(android.R.drawable.ic_media_pause);
            //((ImageView)findViewById(R.id.play_pause_button_2)).setImageResource(id);
        } else {
//            int id = getResources().getIdentifier("ic_media_play", "drawable", getPackageName());
//            ((ImageView) findViewById(R.id.play_pause_button)).setImageResource(id);
//            ((ImageView) findViewById(R.id.play_pause_button_2)).setImageResource(id);
            ((ImageView)findViewById(R.id.play_pause_button)).setImageResource(android.R.drawable.ic_media_play);
            ((ImageView)findViewById(R.id.play_pause_button_2)).setImageResource(android.R.drawable.ic_media_play);
        }
    }
    
    private void setMediaControl() {
        Log.i(TAG, "setMediaControl " + paused);
        setPlayPauseButtons(!paused);
        String title = playerSrv.getTrackTitle();
        Log.i(TAG, "setMediaControl " + title);
        String author = playerSrv.getTrackAuthor();
        Log.i(TAG, "setMediaControl " + author);
        ((TextView)findViewById(R.id.track_name_text_view)).setText(title);
        ((TextView)findViewById(R.id.track_author_text_view)).setText(playerSrv.getTrackAuthor());
        setProgressControls();
    }
    
    public void setProgressControls() {
        if (!paused) {
            Log.i(TAG, "setProgressControls");
            // TODO: Only do this if not already set
            int dur = getDuration() / 1000;
            mSeekBar.setMax(dur);

            ((TextView) findViewById(R.id.duration_text_view)).setText(String.format("%02d", dur / 60) + ":" + String.format("%02d", dur % 60));

            int cur = getCurrentPosition() / 1000;
            mSeekBar.setProgress(cur);
            ((TextView) findViewById(R.id.elapsed_time_text_view)).setText(String.format("%02d", cur / 60) + ":" + String.format("%02d", cur % 60));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG, "onProgressChanged " + progress + fromUser);
        if (fromUser) {seekTo(progress*1000);}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
