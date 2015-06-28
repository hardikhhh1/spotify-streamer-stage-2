package com.hardikarora.spotify_1.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.base.SpotifyBaseActivity;
import com.hardikarora.spotify_1.base.SpotifyBaseFragment;
import com.hardikarora.spotify_1.menu.SpotifyNotification;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;


public class ArtistSearchActivity extends SpotifyBaseActivity
implements ArtistListFragment.Callbacks {

    /**
     * Boolean variable representing if the activity is in two pane mode.
     */
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top_tracks_container) != null){
            // If the activity top track layout is not null then it means,
            // the view is in two pane mode.
            mTwoPane = true;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager nm = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(SpotifyNotification.NOTIFICATION_ID);
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
                    .replace(R.id.top_tracks_container, fragment, TrackListFragment.TAG).commit();
        } else{
            // we start the activity.
            Intent intent = new Intent(this, ArtistTopTracksActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, artistId);
            intent.putExtra(ArtistListFragment.ARTIST_IMAGE_TEXT, imageUrl);
            startActivity(intent);
        }
    }
}
