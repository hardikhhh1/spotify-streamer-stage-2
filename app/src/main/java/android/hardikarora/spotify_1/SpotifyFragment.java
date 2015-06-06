package android.hardikarora.spotify_1;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by hardikarora on 6/2/15.
 * Class representing the main fragment of the spotify view.
 * Encapsulating the list retrieved by spotify and is displayed in a {@link android.widget.ListView}
 * layout.
 */
public class SpotifyFragment extends Fragment implements AsyncResponse{

    private static final String LOG_TAG = SpotifyFragment.class.getSimpleName();
    List<SpotifyTrackComponent> artistList = new ArrayList<>();
    SpotifyListAdapter spotifyListAdapter;
    FetchSpotifyData fetchSpotifyData;
    AsyncResponse fetchResponse = this;

    public SpotifyFragment() {
        fetchSpotifyData = new FetchSpotifyData();
        fetchSpotifyData.response = fetchResponse;
        artistList = new ArrayList<>();

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Parcelable> parcelableList = new ArrayList<>();
        if(artistList != null){
            if(artistList.size() > 0 ){
                parcelableList =(ArrayList<Parcelable>) (ArrayList<?>) artistList;
            }
        }
        outState.putParcelableArrayList("Artists", parcelableList);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fetchSpotifyData = new FetchSpotifyData();
        fetchSpotifyData.response = this;
        if(savedInstanceState == null){
            return;
        }
        List<Parcelable> parcelableList;
        parcelableList = savedInstanceState.getParcelableArrayList("Artists");
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
            spotifyListAdapter = new SpotifyListAdapter(
                    getActivity(), R.layout.list_item_spotify,
                    R.id.spotify_item_textview, componentList);
        }

    }


    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        List<SpotifyTrackComponent> artistObjList = input;
        Log.d(LOG_TAG, "Total items : " + artistObjList.size());
        fetchSpotifyData = new FetchSpotifyData();
        fetchSpotifyData.response = this;
        spotifyListAdapter.clear();
        artistList.clear();

        if(artistObjList.size() == 0){
            CharSequence noArtistText = "Couldn't find the artist/album.";
            Toast toast = Toast.makeText(getActivity(), noArtistText, Toast.LENGTH_SHORT);
            toast.show();
        }
        SpotifyTrackComponent spotifyArtist ;
        Artist artist;
        for (Object artistObj : artistObjList) {
            artist = (Artist) artistObj;
            spotifyArtist = new SpotifyArtist(artist.id, artist.name );
            List<Image> artistImagesList = artist.images;
            //TODO : get the smallest image.
            if(artistImagesList != null) {
                if(artistImagesList.size() > 0) {
                    spotifyArtist.setImageUrl(artistImagesList.get(0).url);
                }
            }
            artistList.add(spotifyArtist);
        }
        spotifyListAdapter.notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        artistList = new ArrayList<>();

        spotifyListAdapter = new SpotifyListAdapter(getActivity(), R.layout.list_item_spotify,
                R.id.spotify_item_textview,
                artistList);

        EditText searchText = (EditText) rootView.findViewById(R.id.spotify_search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString();
                if(searchString.equals("")){
                    return;
                }

                    fetchSpotifyData = new FetchSpotifyData();
                    fetchSpotifyData.response = fetchResponse;
                    fetchSpotifyData.execute(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ListView spotifyListView = (ListView) rootView.findViewById(R.id.list_view_spotify);
        spotifyListView.setAdapter(spotifyListAdapter);
        spotifyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyTrackComponent artist = artistList.get(position);
                String artistId = artist.getArtistId();
                Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artistId);
                startActivity(intent);
            }
        });
        return rootView;
    }


    public class FetchSpotifyData extends SpotifyAsyncTask{

        private final String LOG_TAG = FetchSpotifyData.class.getSimpleName();

        @Override
        protected List<Artist> doInBackground(Object... params) {
            ArtistsPager artists;
            List<Artist> artistList = null;
            try {
                artists = SpotifyApiUtil.searchArtists((String) params[0]);
                artistList  = artists.artists.items;
            } catch (Exception e){
                Log.e(LOG_TAG, "Error while getting the spotify data in the task : " + e.getMessage());
            }

            return artistList;
        }

    }


}
