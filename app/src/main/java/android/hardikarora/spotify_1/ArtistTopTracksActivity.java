package android.hardikarora.spotify_1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistTopTracksActivity extends Activity {

    private static final String LOG_TAG = ArtistTopTracksActivity.class.getSimpleName();
    private static String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Intent intent = getIntent();
        artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(LOG_TAG, "Artist top tracks activity has started : " + artistId);


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

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        SpotifyTrackListAdapter spotifyTrackListAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
            FetchArtistTopTrackData topTrackTask = new FetchArtistTopTrackData();
            try {

                List<Track> artistTopTracks = topTrackTask.execute(artistId).get();


                spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                        rootView.getContext(), R.layout.list_item_spotify,
                        R.id.spotify_item_textview, artistTopTracks
                );

                ListView spotifyListView = (ListView) rootView.findViewById(R.id.list_view_spotify2);
                spotifyListView.setAdapter(spotifyTrackListAdapter );


            } catch(Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
            }

            return rootView;
        }
    }

    public class FetchArtistTopTrackData extends AsyncTask<String, Void, List<Track>>{

        @Override
        protected List<Track> doInBackground(String... params) {
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService service = spotifyApi.getService();
            Log.d(LOG_TAG,"Getting the artist top tracks : " + params[0]);
            Tracks tracks;
            Map<String, Object> apiOptions = new HashMap<>();
            apiOptions.put("country", "US");
            try {
                tracks = service.getArtistTopTrack(params[0], apiOptions);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
                return new ArrayList<>();
            }
            List<Track> tracksList = new ArrayList<>();
            if(tracks != null) {
                tracksList = tracks.tracks;
                Log.d(LOG_TAG,"No of tracks are : " + tracksList.size());
            }
            return tracksList;
        }
    }
}
