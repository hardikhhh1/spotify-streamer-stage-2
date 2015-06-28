package com.hardikarora.spotify_1.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.base.SpotifyBaseActivity;


/*
This class represents the activity to play the song currently selected.
 */
public class SongPlayerActivity extends SpotifyBaseActivity {

    public static final String SPOTIFY_PLAYER_TITLE = "Spotify player";

    /**
     * Overriding the oncreate method, called when the view is created.
     * Here the Fragment is initiated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);

        if (savedInstanceState != null) return;
        FragmentManager manager = getFragmentManager();
        TrackPlayerDialogFragment fragment = new TrackPlayerDialogFragment();
        fragment.setArguments(getIntent().getExtras());
        if (ArtistSearchActivity.mTwoPane){
            fragment.show(manager, SPOTIFY_PLAYER_TITLE);
        }
        else {

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.container, fragment,
                    TrackPlayerDialogFragment.TAG)
                    .commit();
        }

    }

}
