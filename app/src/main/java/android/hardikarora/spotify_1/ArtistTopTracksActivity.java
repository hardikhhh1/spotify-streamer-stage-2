package android.hardikarora.spotify_1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistTopTracksActivity extends Activity{

    private static final String LOG_TAG = ArtistTopTracksActivity.class.getSimpleName();
    private static String artistId;

    static FetchArtistTopTrackData fetchArtistTopTrackData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchArtistTopTrackData = new FetchArtistTopTrackData();
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
    public static class PlaceholderFragment extends Fragment implements AsyncResponse {


        SpotifyTrackListAdapter spotifyTrackListAdapter;
        List<SpotifyTrackComponent> topTracks = new ArrayList<>();

        public PlaceholderFragment() {
            fetchArtistTopTrackData.response = this;
        }



        @Override
        public void afterExecution(List<SpotifyTrackComponent> input) {
            topTracks = input;
            spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                    getActivity(), R.layout.list_item_spotify,
                    R.id.spotify_item_textview, input);

            ListView spotifyListView = (ListView) getActivity().findViewById(R.id.list_view_spotify2);
            spotifyListView.setAdapter(spotifyTrackListAdapter);

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            ArrayList<Parcelable> parcelableList = new ArrayList<>();
            if(topTracks != null){
                if(topTracks.size() > 0 ){
                    parcelableList =(ArrayList<Parcelable>) (ArrayList<?>) topTracks;
                }
            }
            outState.putParcelableArrayList("TopTracks", parcelableList);

        }

        @Override
        public void onViewStateRestored(Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
            fetchArtistTopTrackData.response = this;
            if(savedInstanceState == null){
                return;
            }
            List<Parcelable> parcelableList;
            parcelableList = savedInstanceState.getParcelableArrayList("TopTracks");
            if(parcelableList == null){
                return;
            }
            if(!(parcelableList.size() > 0)){
                return;
            }


            List<SpotifyTrackComponent> componentList =
                    (List<SpotifyTrackComponent>)(List<?>) parcelableList;
            if(parcelableList.size() > 0){
                //TODO : send it to the adapter list.
                spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                        getActivity(), R.layout.list_item_spotify,
                        R.id.spotify_item_textview, componentList);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
            try {
                fetchArtistTopTrackData.execute(artistId);

            } catch(Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
            }

            return rootView;
        }
    }

    public class FetchArtistTopTrackData extends SpotifyAsyncTask{
        @Override
        protected List<SpotifyTrackComponent> doInBackground(Object[] params) {
            Tracks tracks;
            try {
                tracks = SpotifyApiUtil.getArtistsTopTracks((String)params[0]);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
                return new ArrayList<>();
            }
            List<SpotifyTrackComponent> spotifyTracks = new ArrayList<>();
            if(tracks == null)
                return spotifyTracks;
            if(tracks.tracks.size() > 0){
                for(Track track : tracks.tracks){
                    String albumName = track.album.name;
                    String trackName = track.name;
                    String imageUrl = track.album.images.get(0).url;
                    spotifyTracks.add(new SpotifyTrack(albumName, trackName,
                            imageUrl));
                }
            }
            return spotifyTracks;
        }
    }
}
