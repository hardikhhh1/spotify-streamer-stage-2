package com.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.PreferenceMenuActivity;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;

import java.util.ArrayList;
import java.util.List;


public class ArtistSearchActivity extends SpotifyBaseActivity
implements ArtistListFragment.Callbacks {

    /**
     * Boolean variable representing if the activity is in two pane mode.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top_tracks_container) != null){
//            If the activity top track layout is not null then it means,
//            the view is in two pane mode.
            mTwoPane = true;
        }

    }


    @Override
    public void onListItemSelected(String artistId, String imageUrl) {
        if(mTwoPane){
            // If the view is in two pane mode, we start the fragment.
            Bundle artistArguments = new Bundle();
            artistArguments.putString(Intent.EXTRA_TEXT, artistId);
            artistArguments.putString(ArtistListFragment.ARTIST_IMAGE_TEXT, imageUrl);

            TrackListFragment fragment = new TrackListFragment();
            fragment.setArguments(artistArguments);

            getFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, fragment, TrackListFragment.TAG)
                    .commit();


        } else{
            // we start the activity
            Intent intent = new Intent(this, ArtistTopTracksActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, artistId);
            intent.putExtra(ArtistListFragment.ARTIST_IMAGE_TEXT, imageUrl);
            startActivity(intent);

        }
    }
}
