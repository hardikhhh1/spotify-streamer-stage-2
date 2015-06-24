package com.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.PreferenceMenuActivity;
import com.hardikarora.spotify_1.menu.SpotifyNotification;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/24/15.
 */
public class SpotifyBaseActivity extends Activity {


    private SpotifyNotification spotifyNotification;
    public static final String PLAYER_PREFERENCE = "playerPreference";
    public static final String PLAYER_PREFERENCE_KEY = PLAYER_PREFERENCE;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.now_playing_button);
        item.setVisible(false);
        SpotifyPlayerService.PlayerState state = SpotifyPlayerService.spotifyPlayerState;

        if(state == SpotifyPlayerService.PlayerState.Play){
            item.setVisible(true);
        }
        return true;
    }


    @Override
    public void onPause() {
        super.onPause();
//        Log.d(LOG_TAG, "On pause for player fragment called");

        // Check the user preference.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notificationPreference = preferences.getBoolean(PLAYER_PREFERENCE_KEY, true);

        if(!notificationPreference) return;


        try {
            if(spotifyNotification == null){
                if(TrackPlayerDialogFragment.spotifyPlayerService != null){

                    spotifyNotification= new SpotifyNotification(this,

                            (SpotifyTrack) TrackPlayerDialogFragment.spotifyPlayerService.nowPlayingSpotifyTrack);
                    spotifyNotification.startNotification();
                }
            }
        }
        catch (Exception e){
//            Log.e(LOG_TAG,  "Error while, getting log for the player." + e.getMessage() +
//                    e.getStackTrace());
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if(spotifyNotification == null) return;

        // Cancelling the notification.
        spotifyNotification.cancelNotification();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Canceling the notification.
        if(spotifyNotification != null) spotifyNotification.cancelNotification();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_country_code:
                Intent intent = new Intent(this, PreferenceMenuActivity.class);
                startActivity(intent);
                return true;

            case R.id.now_playing_button:

                List<SpotifyTrackComponent> trackList = SpotifyPlayerService.spotifyTrackList;
                if(trackList == null) return true;
                if(trackList.size() == 0) return true;

                int trackIndex = SpotifyPlayerService.trackIndex;


                // TODO : Show as an dialog.
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(TrackListFragment.TRACK_LIST_TAG,
                        (ArrayList<SpotifyTrack>)
                                (ArrayList<?>)trackList);
                arguments.putInt(TrackListFragment.TRACK_INDEX_TAG,
                        trackIndex);
                arguments.putString(TrackListFragment.SPOTIFY_TRACK_ID_TAG,
                        trackList.get(trackIndex).getTrackId());

                TrackPlayerDialogFragment fragment = TrackPlayerDialogFragment.newInstance();
                fragment.setArguments(arguments);

                getFragmentManager().beginTransaction()
                        .replace(getMainContainer(), fragment, TrackPlayerDialogFragment.TAG)
                        .commit();


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getMainContainer(){

        View view = findViewById(R.id.top_tracks_container);
        if(view != null) return R.id.top_tracks_container;

        view = findViewById(R.id.main_container);
        if(view != null) return R.id.main_container;

        view = findViewById(R.id.container);
        if(view != null) return R.id.container;


        return -1;
    }


}
