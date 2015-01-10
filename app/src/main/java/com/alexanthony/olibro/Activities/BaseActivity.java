package com.alexanthony.olibro.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alexanthony.olibro.PlayerService;
import com.alexanthony.olibro.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class BaseActivity extends Activity implements SeekBar.OnSeekBarChangeListener, SlidingUpPanelLayout.PanelSlideListener {
    private final String TAG = "com.alexanthony.olibro.BaseActivity";
    //service
    protected PlayerService playerSrv;
    protected Intent playIntent;
    //binding
    protected boolean trackBound = false;
    protected SeekBar mSeekBar;
    protected Toolbar mToolBar;
    protected ImageButton playPauseButton1;
    protected ImageButton playPauseButton2;
    protected SlidingUpPanelLayout sliding_layout;
    // TODO: remove one of these
    protected boolean paused = false, playbackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        // set up sliding layout
        setUpSlidingLayout();
        // using Toolbar instead of ActionBar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolBar);
        getActionBar().setTitle("Olibro");
        getActionBar().setDisplayShowTitleEnabled(true);
        // Set up listener for play/pause buttons
        setPlayPauseButtonListener();
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////////////// Content Frame ///////////////////////////
    protected View setContentFrame(int viewID) {
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(viewID, null);
        contentFrame.addView(contentView);
        return contentView;

    }
        
    ///////////////////// Sliding Layout //////////////////////////
    public void setUpSlidingLayout() {
        Log.i(TAG, "setUpSlidingLayout");
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        Log.i(TAG, "setUpSlidingLayout" + sliding_layout.isPanelExpanded());
        sliding_layout.setPanelSlideListener(this);
    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {
        findViewById(R.id.current_track_art_image_small).setVisibility(View.VISIBLE);
        playPauseButton1.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPanelExpanded(View view) {
        findViewById(R.id.current_track_art_image_small).setVisibility(View.GONE);
        playPauseButton1.setVisibility(View.GONE);
        Log.i(TAG, "onPanelExpanded");
        new SeekBarHandler().execute();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    ///////////////////// Play Status ////////////////////////////
    public boolean isPlaying() {
        return playerSrv != null && trackBound && playerSrv.isPng();
//        if (playerSrv != null && trackBound)
//            return playerSrv.isPng();
//        return false;
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

    ///////////////////// Play Control /////////////////////////////
    private void resume() {
        start();
    }

    public void start() {
        playerSrv.go();
        setMediaControl();
    }

    public void pause() {
        playerSrv.pausePlayer();
        playbackPaused = true;
    }

    private void playNext() {
        playerSrv.playNext();
        if (playbackPaused) {
            //TODO: Update media player
            playbackPaused = false;
            setMediaControl();
        }
    }

    private void playPrev() {
        playerSrv.playPrev();
        if (playbackPaused) {
            // TODO: Update media player
            playbackPaused = false;
            setMediaControl();
        }
    }

    public void seekTo(int pos) {
        playerSrv.seek(pos);
        paused = false;
        setMediaControl();
    }

    ///////////////////// Player Controls /////////////////////////
    protected void setMediaControl() {
        Log.i(TAG, "setMediaControl " + paused);
        setPlayPauseButtons(!paused);
        setMediaTextViews();
        setProgressControls();
        new SeekBarHandler().execute();
    }

    protected void setMediaTextViews() {
        String title = playerSrv.getTrackTitle();
        Log.i(TAG, "setMediaControl " + title);
        String author = playerSrv.getTrackAuthor();
        Log.i(TAG, "setMediaControl " + author);
        ((TextView) findViewById(R.id.track_name_text_view)).setText(title);
        ((TextView) findViewById(R.id.track_author_text_view)).setText(playerSrv.getTrackAuthor());
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

    protected void setPlayPauseButtons(boolean playing) {
        Log.i(TAG, "setPlayPauseButtons " + playing);
        // Dark and light are the colours of the backgrounds, not the images
        if (playing) {
            playPauseButton1.setImageResource(R.drawable.ic_action_av_pause_dark);
            playPauseButton2.setImageResource(R.drawable.ic_action_av_pause_light);
        } else {
            playPauseButton1.setImageResource(R.drawable.ic_action_av_play_arrow_dark);
            playPauseButton2.setImageResource(R.drawable.ic_action_av_play_arrow_light);
        }
    }

    protected void setPlayPauseButtonListener() {
        playPauseButton1 = (ImageButton) findViewById(R.id.play_pause_button);
        playPauseButton2 = (ImageButton) findViewById(R.id.play_pause_button_2);
        View.OnClickListener playPauseListener = new View.OnClickListener() {
            public void onClick(View view) {
                onPlayPauseClick(view);
            }
        };
        playPauseButton1.setOnClickListener(playPauseListener);
        playPauseButton2.setOnClickListener(playPauseListener);
    }

    ///////////////////// Media Button actions ////////////////////
    public void onPrevClick(View view) {
        playPrev();
    }

    public void onNextClick(View view) {
        playNext();
    }

    public void onBackClick(View view) {
        //
        int progress = getCurrentPosition();
        seekTo(progress - 5000);
    }

    public void onForwardClick(View view) {
        //
        int progress = getCurrentPosition();
        seekTo(progress - 5000);
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

    ///////////////////// Seek Bar ////////////////////////////////
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG, "onProgressChanged " + progress + fromUser);
        if (fromUser) {
            playerSrv.seek(progress * 1000);
            paused = false;
            setPlayPauseButtons(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public class SeekBarHandler extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            Log.d("##########Seek Bar Handler ################", "###################Destroyed##################");
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            Log.i(TAG, "doInBackground" + isPlaying() + sliding_layout.isPanelExpanded());
            while (isPlaying() && sliding_layout.isPanelExpanded()) {
                //               Log.i(TAG, "doInBackground");
                try {
                    Thread.sleep(1000);
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

}
