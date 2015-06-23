package com.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.PreferenceMenuActivity;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.util.SpotifyPlayerService;

import java.util.ArrayList;
import java.util.List;


public class ArtistSearchActivity extends Activity
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
                        .replace(R.id.container, fragment, TrackPlayerDialogFragment.TAG)
                        .commit();


                return true;
        }

        return super.onOptionsItemSelected(item);
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
