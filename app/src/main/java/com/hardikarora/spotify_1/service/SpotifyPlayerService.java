package com.hardikarora.spotify_1.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hardikarora.spotify_1.activity.TrackListFragment;
import com.hardikarora.spotify_1.menu.SpotifyNotification;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/10/15.
 */
public class SpotifyPlayerService extends Service implements MediaPlayer.OnPreparedListener,
 MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, ServicePublisher{

    public static final String SONG_FINISHED_EVENT = "song-finished-event";

    public enum PlayerState {
        Play, Stopped, Paused ;
    }

    static List<ServiceSubscriber> subscribersList = new ArrayList<>() ;

    // The state is set to stopped when the service is started.
    public static PlayerState spotifyPlayerState = PlayerState.Stopped;

    private static MediaPlayer player;

    public static List<SpotifyTrackComponent> spotifyTrackList;
    public static SpotifyTrackComponent nowPlayingSpotifyTrack;
    public static int trackIndex;
    public int previousTrackIndex;

    private final IBinder mBinder = new SpotifyBinder();

    private static final String LOG_TAG = SpotifyPlayerService.class.getSimpleName();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null) return super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if(action == null) return super.onStartCommand(intent, flags, startId);

        if(action.equals(SpotifyNotification.PLAY_ACTION)){
            playButtonPressed();
        }else if(action.equals(SpotifyNotification.NEXT_ACTION)){
            nextTrack();
        }else if(action.equals(SpotifyNotification.PREVIOUS_ACTION)){
            previousTrack();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void playButtonPressed(){
        Log.d(LOG_TAG, "Play button has been pressed");
        if(spotifyPlayerState == PlayerState.Play){
            Log.d(LOG_TAG, "Previos State : PLAY");
            if(spotifyTrackList.get(trackIndex).getTrackUrl().equals(
                    nowPlayingSpotifyTrack.getTrackUrl())){
                Log.d(LOG_TAG, "Play button has been pressed for the same track, it should pause");
                player.pause();
                spotifyPlayerState = PlayerState.Paused;
                return;
            }
            else{
                Log.d(LOG_TAG, "Play button has been pressed for different track, it play different track");
                resetAndPlayTrack();
                updateStateChanged();
            }
        } else if(spotifyPlayerState == PlayerState.Paused){
            Log.d(LOG_TAG, "Previos State : PAUSE");

            if(spotifyTrackList.get(trackIndex).getTrackUrl().equals(
                    nowPlayingSpotifyTrack.getTrackUrl())){
                Log.d(LOG_TAG, "Play button has been pressed for the same track, it should play again");
                player.start();
                spotifyPlayerState = PlayerState.Play;
                return;
            } else{
                Log.d(LOG_TAG, "Play button has been pressed for the different track, it should play different track");
                player.pause();
                resetAndPlayTrack();
                updateStateChanged();
            }
        } else{
            playTrack(spotifyTrackList.get(trackIndex).getTrackUrl());
            updateStateChanged();
        }

    }

    public void pauseTrack(){
        if(player.isPlaying()){
            player.pause();
            spotifyPlayerState = PlayerState.Paused;
            return;
        }
    }

    public void nextTrack(){

        trackIndex = (trackIndex + 1) % spotifyTrackList.size();

        // If the player is stopped we just change the track index, but
        // don't play the music.
        if(spotifyPlayerState != PlayerState.Play) {
            updateStateChanged();
            return;
        }

        resetAndPlayTrack();
        updateStateChanged();
    }

    public void previousTrack(){
        if(trackIndex == 0){
            trackIndex = spotifyTrackList.size();
        }

        // If the player is stopped we just change the track index, but
        // don't play the music.
        if(spotifyPlayerState != PlayerState.Play) {
            updateStateChanged();
            return;
        }

        resetAndPlayTrack();
        updateStateChanged();
    }

    private void resetAndPlayTrack(){
        String spotifyTrackUrl = spotifyTrackList.get(trackIndex).getTrackUrl();
        if(player.isPlaying()) {
            player.stop();
        }
        player.reset();
        playTrack(spotifyTrackUrl);
        return;
    }

    private void playTrack(String spotifyTrackUrl){
        if(player.isPlaying()){
            player.reset();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(spotifyTrackUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while playing the music" + e.getMessage());
            e.printStackTrace();
        }
        nowPlayingSpotifyTrack = spotifyTrackList.get(trackIndex);
        player.prepareAsync();
        return;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(player == null) {
            player = new MediaPlayer();
        }
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
        return true;
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
        public SpotifyPlayerService getService(){ return SpotifyPlayerService.this; }
    }

    public static PlayerState getSpotifyPlayerState() {
        return spotifyPlayerState;
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    @Override
    public void updateStateChanged() {
        for(ServiceSubscriber subscriber : subscribersList){
            subscriber.stateChanged(spotifyTrackList.get(trackIndex));
        }
    }

    @Override
    public void subscribe(ServiceSubscriber subscriber) {
        subscribersList.add(subscriber);
    }

    @Override
    public void unsubscribe(ServiceSubscriber subscriber) {
        if(subscribersList == null) return;
        for(ServiceSubscriber memberSubscriber : subscribersList){
            if(memberSubscriber == subscriber){
                subscribersList.remove(subscriber);
            }
        }
    }
}
