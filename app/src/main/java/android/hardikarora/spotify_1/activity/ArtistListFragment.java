package android.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.content.Intent;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.adapter.SpotifyArtistListAdapter;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.util.AsyncResponse;
import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.hardikarora.spotify_1.util.SpotifyAsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/2/15.
 * Class representing the main fragment of the spotify view.
 * Encapsulating the list retrieved by spotify and is displayed in a {@link android.widget.ListView}
 * layout.
 */
public class ArtistListFragment extends Fragment implements AsyncResponse {

    private static final String LOG_TAG = ArtistListFragment.class.getSimpleName();

    public static final String TAG = ArtistListFragment.class.getSimpleName();
    public static final String NO_ALBUM_TOAST = "Couldn't find the artist/album.";
    public static final String ARTISTS_TAG = "Artists";
    public static final String ARTIST_IMAGE_TEXT = "ArtistImage";

    List<SpotifyTrackComponent> artistList;
    SpotifyArtistListAdapter spotifyArtistListAdapter;
    FetchSpotifyArtistData fetchSpotifyData;
    AsyncResponse fetchResponse = this;
    ListView spotifyListView;

    /**
     * Default constructor for the fragment.
     */
    public ArtistListFragment() {
        artistList = new ArrayList<>();
        fetchSpotifyData = new FetchSpotifyArtistData(this);
    }

    public static ArtistListFragment newInstance(){
        return new ArtistListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        artistList = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // To save the state we write all the artist list
        // in a parcelable list.
        ArrayList<Parcelable> parcelableList = new ArrayList<>();
        if(artistList != null){
            if(artistList.size() > 0 ){
                parcelableList = (ArrayList<Parcelable>) (ArrayList<?>) artistList;
            }
        }
        outState.putParcelableArrayList(ARTISTS_TAG, parcelableList);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fetchSpotifyData = new FetchSpotifyArtistData(this);

        // If the saved instance is null, function is exited.
        if(savedInstanceState == null) return;

        List<Parcelable> parcelableList;
        parcelableList = savedInstanceState.getParcelableArrayList(ARTISTS_TAG);

        // If the list is empty or null, function is exited.
        if(parcelableList == null) return;
        if(parcelableList.size() <= 0) return;

        List<SpotifyTrackComponent> componentList =
                (List<SpotifyTrackComponent>)(List<?>) parcelableList;
        spotifyArtistListAdapter.clear();
        artistList.clear();
        artistList.addAll(componentList);
    }

    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        Log.d(LOG_TAG, "Artist list has been recieved, total items : " + input.size());

        // Reinitialising the async task as it can be executed only once.
        fetchSpotifyData = new FetchSpotifyArtistData(this);

        // Clearing the list adapter, so that the new data can be added.
        spotifyArtistListAdapter.clear();
        artistList.clear();

        if(input.size() == 0){
            // If there is not artist we show a toast.
            CharSequence noArtistText = NO_ALBUM_TOAST;
            Toast toast = Toast.makeText(getActivity(), noArtistText, Toast.LENGTH_SHORT);
            toast.show();
        }

        artistList.addAll(input);
        spotifyArtistListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Initializing the adapter and the list view.
        spotifyArtistListAdapter = new SpotifyArtistListAdapter(getActivity(), R.layout.artist_details_item,
                R.id.spotify_item_textview, artistList);
        spotifyListView = (ListView) rootView.findViewById(R.id.list_view_spotify);
        spotifyListView.setAdapter(spotifyArtistListAdapter);

        EditText searchText = (EditText) rootView.findViewById(R.id.spotify_search_text);
        searchText.addTextChangedListener(new ArtistTextSearchTextWatcher());
        spotifyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When an item in the list, that is, the artist name is clicked on the UI
                // a new intent is started to display the top tracks.
                SpotifyTrackComponent artist = artistList.get(position);
                String artistId = artist.getArtistId();
                Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artistId);
                intent.putExtra(ARTIST_IMAGE_TEXT, artist.getImageUrl());
                startActivity(intent);
            }
        });
        return rootView;
    }

    /**
     * Inner class representing the text watcher to search the artist name.
     */
    public class ArtistTextSearchTextWatcher  implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String searchString = s.toString();
            if(searchString.isEmpty()){
                // If the text is empty, we clear the list and return, no api
                // call is made.
                spotifyArtistListAdapter.clear();
                artistList.clear();
                spotifyArtistListAdapter.notifyDataSetChanged();
                return;
            }

            // If the text is valid we fetch the artist information from the
            // spotify api.
            fetchSpotifyData = new FetchSpotifyArtistData(fetchResponse);
            try {
                fetchSpotifyData.execute(searchString);
            }catch (Exception e){
                Log.e(LOG_TAG, "Error while getting artist data : " + e.getMessage());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * Inner class representing the async task used to get the artists from spotify.
     */
    public class FetchSpotifyArtistData extends SpotifyAsyncTask {

        public FetchSpotifyArtistData(AsyncResponse callback) {
            super(callback);
        }

        private final String LOG_TAG = FetchSpotifyArtistData.class.getSimpleName();

        @Override
        protected List<SpotifyTrackComponent> doInBackground(Object... params) {
            String artistName = (String) params[0];
            List<SpotifyTrackComponent> spotifyTrackComponents = new ArrayList<>();
            try {
                spotifyTrackComponents = SpotifyApiUtility.searchArtists(artistName);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error while getting the spotify artists list" +
                        " data in the task : " + e.getMessage());
            }

            return spotifyTrackComponents;
        }

    }


}
