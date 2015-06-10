package android.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.app.Fragment;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.hardikarora.spotify_1.R;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;

public class SongPlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);
        if (savedInstanceState == null) {
            TrackPlayerFragment fragment = TrackPlayerFragment.newInstance();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, TrackPlayerFragment.TAG)
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_play, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TrackPlayerFragment extends Fragment {

        public static final String TAG = TrackPlayerFragment.class.getSimpleName();

        public TrackPlayerFragment() {
        }

        public static TrackPlayerFragment newInstance(){
            return new TrackPlayerFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_song_play, container, false);

            // If no track id has been set we return the root view.
            if(savedInstanceState == null) return rootView;

            String trackId = savedInstanceState.getString(TrackListFragment.SPOTIFY_TRACK_ID_TAG);
            if(trackId == null) return rootView;
            if(trackId.isEmpty()) return rootView;

            SpotifyTrackComponent spotifyTrack = SpotifyApiUtility.getTrackFromSpotify(trackId);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.player_album_image);
            if(imageView != null){
                // Load image with picasso.
                String imageUrl = spotifyTrack.getImageUrl();
                Picasso.with(rootView.getContext()).load(imageUrl).into(imageView);
            }
            return rootView;
        }
    }

//    public class PlayerDataHolder{
//
//        ImageView backgroundImageView;
//
//
//    }
}
