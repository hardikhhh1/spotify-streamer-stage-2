package android.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.app.Fragment;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.os.Bundle;
import android.util.Log;
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

    public static final String LOG_TAG = SongPlayActivity.class.getSimpleName();

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



//    public class PlayerDataHolder{
//
//        ImageView backgroundImageView;
//
//
//    }
}
