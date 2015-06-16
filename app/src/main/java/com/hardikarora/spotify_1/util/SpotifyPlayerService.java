package com.hardikarora.spotify_1.util;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hardikarora.spotify_1.activity.TrackListFragment;
import com.hardikarora.spotify_1.activity.TrackPlayerFragment;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

import java.io.IOException;
import java.util.List;

/**
 * Created by hardikarora on 6/10/15.
 */
public class SpotifyPlayerService extends Service implements MediaPlayer.OnPreparedListener,
 MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    public static final String SONG_FINISHED_EVENT = "song-finished-event";

    private enum PlayerState {
        Play, Stopped, Paused ;
    }

    // The state is set to stopped when the service is started.
    private static PlayerState spotifyPlayerState = PlayerState.Stopped;

    private MediaPlayer player;

    public List<SpotifyTrackComponent> spotifyTrackList;
    public int trackIndex;

    private final IBinder mBinder = new SpotifyBinder();

    private static final String LOG_TAG = SpotifyPlayerService.class.getSimpleName();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action == null) return super.onStartCommand(intent, flags, startId);

        if(action.equals(TrackPlayerFragment.PLAY_ACTION)){
            playButtonPressed();
        }else if(action.equals(TrackPlayerFragment.NEXT_ACTION)){
            nextTrack();
        }else if(action.equals("Previous")){
            previousTrack();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public int playButtonPressed(){
        String  spotifyTrackUrl = spotifyTrackList.get(trackIndex).getTrackUrl();
        if(spotifyPlayerState == PlayerState.Play){
            player.pause();
            spotifyPlayerState = PlayerState.Paused;
            return trackIndex;
        } else if(spotifyPlayerState == PlayerState.Paused){
            player.start();
            spotifyPlayerState = PlayerState.Play;
            return trackIndex;
        }

        playTrack(spotifyTrackUrl);
        return trackIndex;
    }

    public int nextTrack(){

        trackIndex = (trackIndex + 1) % spotifyTrackList.size();
        String  spotifyTrackUrl = spotifyTrackList.get(this.trackIndex).getTrackUrl();

        // If the player is stopped we just change the track index, but
        // don't play the music.

        if(spotifyPlayerState != PlayerState.Play) {
            return trackIndex;
        } else{
            player.stop();
            player.reset();
        }
        playTrack(spotifyTrackUrl);
        return trackIndex;
    }

    public int previousTrack(){
        if(trackIndex == 0){
            trackIndex = spotifyTrackList.size();
        }
        trackIndex--;

        // If the player is stopped we just change the track index, but
        // don't play the music.
        if(spotifyPlayerState != PlayerState.Play) {
            return trackIndex;
        } else{
            player.stop();
            player.reset();
        }

        String spotifyTrackUrl = spotifyTrackList.get(trackIndex).getTrackUrl();

        playTrack(spotifyTrackUrl);
        return trackIndex;
    }

    private void playTrack(String spotifyTrackUrl){
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(spotifyTrackUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while playing the music" + e.getMessage());
            e.printStackTrace();
        }

        player.prepareAsync();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        spotifyPlayerState = PlayerState.Play;
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(LOG_TAG, "Error occured with the media player.");
        mp.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "On completion has been called as the song has finished.");
        Intent songFinishedIntent = new Intent(SONG_FINISHED_EVENT);
        songFinishedIntent.putExtra(TrackListFragment.TRACK_INDEX_TAG, trackIndex + 1);
        sendBroadcast(songFinishedIntent);
        nextTrack();

    }

    public class SpotifyBinder extends Binder{

        public SpotifyPlayerService getService(){
            return SpotifyPlayerService.this;
        }
    }

}
