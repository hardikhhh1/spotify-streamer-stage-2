package com.hardikarora.spotify_1.activity;

import android.os.Bundle;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.base.SpotifyBaseActivity;


/*
This class represents the activity to show the top tracks for
an artist.
 */
public class ArtistTopTracksActivity extends SpotifyBaseActivity{

    /**
     * Overriding the oncreate method, called when the view is created.
     * Here the Fragment is initiated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_top_tracks);
        if (savedInstanceState != null) return;

        TrackListFragment fragment = new TrackListFragment();
        fragment.setArguments(getIntent().getExtras());

        getFragmentManager().beginTransaction()
                .replace(R.id.top_tracks_container, fragment, TrackListFragment.TAG)
                .commit();
    }

}
