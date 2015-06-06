package android.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardikarora.spotify_1.R;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/*
This class represents the activity to show the top tracks for
an artist.
 */
public class ArtistTopTracksActivity extends Activity{

    private static final String LOG_TAG = ArtistTopTracksActivity.class.getSimpleName();
    private static String artistId;

    /**
     * Overriding the oncreate method, called when the view is created.
     * Here the Fragment is initiated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        Intent intent = getIntent();
        artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(LOG_TAG, "Artist top tracks activity has started : " + artistId);

        if (savedInstanceState == null) {
            TrackListFragment fragment = new TrackListFragment();
            fragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_details, menu);
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


}
