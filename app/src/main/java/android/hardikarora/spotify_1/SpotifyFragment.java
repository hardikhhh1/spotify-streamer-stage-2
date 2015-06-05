package android.hardikarora.spotify_1;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by hardikarora on 6/2/15.
 * Class representing the main fragment of the spotify view.
 * Encapsulating the list retrieved by spotify and is displayed in a {@link android.widget.ListView}
 * layout.
 */
public class SpotifyFragment extends Fragment{

    private static final String LOG_TAG = SpotifyFragment.class.getSimpleName();
    List<SpotifyArtist> artistList = new ArrayList<>();
    SpotifyListAdapter spotifyListAdapter;

    public SpotifyFragment() {
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
                FetchSpotifyData spotifyData = new FetchSpotifyData();

                try {

                    ArtistsPager artistsPager = spotifyData.execute(searchString).get();

                    Log.d(LOG_TAG, "Got artists after searching : " + artistsPager.artists.total);



                    List<Artist> artistObjList = artistsPager.artists.items;
                    Log.d(LOG_TAG, "Total items : " + artistObjList.size());
                    spotifyListAdapter.clear();
                    artistList.clear();

                    if(artistObjList.size() == 0){
                        CharSequence noArtistText = "Couldn't find the artist/album.";
                        Toast toast = Toast.makeText(getActivity(), noArtistText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    SpotifyArtist spotifyArtist ;
                    for (Artist artist : artistObjList) {
                        spotifyArtist = new SpotifyArtist(artist.name, artist.id);

                        List<Image> artistImagesList = artist.images;
                        //TODO : get the smallest image.
                        spotifyArtist.setImage(artistImagesList.get(0));
                        artistList.add(spotifyArtist);
                    }
                    spotifyListAdapter.notifyDataSetChanged();

                } catch (Exception e){
                    Log.e(LOG_TAG, "Error while getting the spotify data : " + e.getMessage());
                    CharSequence errorText = "Error while getting the artists list, " +
                            "Please try again later";
                    Toast toast = Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT);
                    toast.show();
                }

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
                SpotifyArtist artist = artistList.get(position);
                String artistId = artist.getArtistId();
                Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artistId);
                startActivity(intent);
            }
        });
        return rootView;
    }


    public class FetchSpotifyData extends AsyncTask<String, Void, ArtistsPager>{

        private final String LOG_TAG = FetchSpotifyData.class.getSimpleName();

        @Override
        protected ArtistsPager doInBackground(String... params) {
            ArtistsPager artists = null;
            try {
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotifyService = spotifyApi.getService();
                String artistName = params[0];
                artists = spotifyService.searchArtists(artistName);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error while getting the spotify data : " + e.getMessage());
                CharSequence errorText = "Error while getting the artists list, " +
                        "Please try again later";
                Toast toast = Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT);
                toast.show();
            }

            return artists;
        }

    }


}
