package com.alexanthony.olibro;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.alexanthony.olibro.Activities.MainActivity;
import com.alexanthony.olibro.Content.Track;

import java.util.ArrayList;

public class PlayerService extends Service implements
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

	//media player
	private MediaPlayer player;
	//track list
	private ArrayList<Track> tracks;
	//current position
	private int trackPosn=-1;
	//binder
	private final IBinder trackBind = new MediaBinder();
    // track title
    private String trackTitle="";
    // track author
    private String trackAuthor="";
    // Notification ID
    private static final int NOTIFY_ID=1;

    private static final String TAG = "PlayerService";
    
    public String getTrackTitle() {
        Log.i(TAG, "getTrackTitle " + trackTitle);
        if (trackTitle.equals("")) {
            return trackTitle;
        } else {
            Log.i(TAG, "getTrackTitle " + tracks.get(trackPosn).getTitle());
            return tracks.get(trackPosn).getTitle();
        }
    }
    
    public String getTrackAuthor() {return trackAuthor;}

	public void onCreate(){
		//create the service
		super.onCreate();
		//initialize position
		trackPosn=0;
		//create player
		player = new MediaPlayer();
		//initialize
		initMediaPlayer();
        requestAudioFocus();
	}

	public void initMediaPlayer(){
		//set player properties
		player.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		//set listeners
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	//pass track list
	public void setList(ArrayList<Track> theTracks){
		tracks = theTracks;
	}

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // Resume playback, set volume back to full
                if (player == null) initMediaPlayer();
                if (!player.isPlaying()) player.start();
                player.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Stop playback and release MediaPlayer
                if (player.isPlaying()) player.stop();
                player.release();
                player = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Pause playback, but don't release MediaPlayer
                if (player.isPlaying()) player.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Reduce volume
                // TODO: Make dependent on config option
                if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                break;
        }
    }

    //binder
	public class MediaBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
	}

	//activity will bind to service
	@Override
	public IBinder onBind(Intent intent) {
		return trackBind;
	}

	//release resources when unbind
	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		//player.release();
		return false;
	}

	//play a track
	public void playTrack(){
		//play
		player.reset();
		//get track
		Track playTrack = tracks.get(trackPosn);
        trackTitle = playTrack.getTitle();
        Log.i(TAG, "playTrack " + trackTitle);
        trackAuthor = playTrack.getAuthor();
		//get id
		long currTrack = playTrack.getID();
		//set uri
		Uri trackUri = ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				currTrack);
		//set the data source
		try{ 
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e){
			Log.e("PLAYER SERVICE", "Error setting data source", e);
		}
		player.prepareAsync(); 
	}

	//set the track
	public void setTrack(int trackIndex){
		trackPosn=trackIndex;
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
                .setTicker(trackTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(trackTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
	}

    public int getPosition(){
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
        trackPosn--;
        if(trackPosn<0) trackPosn= tracks.size()-1;
        playTrack();
    }

    public void playNext(){
        trackPosn++;
        if(trackPosn> tracks.size()) trackPosn=0;
        playTrack();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (player != null) player.release();
    }

    private boolean requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }
}
