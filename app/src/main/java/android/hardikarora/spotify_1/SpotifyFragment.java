package android.hardikarora.spotify_1;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by hardikarora on 6/2/15.
 * Class representing the main fragment of the spotify view.
 * Encapsulating the list retrieved by spotify and is displayed in a {@link android.widget.ListView}
 * layout.
 */
public class SpotifyFragment extends Fragment{

    private static final String LOG_TAG = SpotifyFragment.class.getSimpleName();

        public SpotifyFragment() {
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

        int itemId = item.getItemId();
        if(itemId == R.id.action_refresh){
            Log.d(LOG_TAG, "Refresh button has been clicked");
            FetchSpotifyData spotifyData = new FetchSpotifyData();
//            spotifyData.execute("coldplay");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        FetchSpotifyData spotifyData = new FetchSpotifyData();
        List<String> artistList = null;
        try {
            ArtistsPager artistsPager = spotifyData.execute("coldplay").get();

            Pager<Artist> artists = artistsPager.artists;
            List<Artist> artistObjList = artists.items;
            artistList = new ArrayList<>();
            for (Artist artist : artistObjList) {
                artistList.add(artist.name);
            }
        }
        catch(InterruptedException e){
            Log.e(LOG_TAG, "Error while setting artists list :" +  e.getMessage());
        }
        catch(ExecutionException e){
            Log.e(LOG_TAG, "Error while setting artists list :" +  e.getMessage());
        }

//        String[] sampleData = { "sample 1", "sample 2", "sample 3"};
//            List<String> sampleDataList = new ArrayList<>(Arrays.asList(sampleData));

            ArrayAdapter<String> spotifyListAdapter = new ArrayAdapter(getActivity(),
                    R.layout.list_item_spotify, R.id.spotify_item_textview, artistList);

            ListView spotifyListView = (ListView) rootView.findViewById(R.id.list_view_spotify);
            spotifyListView.setAdapter(spotifyListAdapter);
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
                Log.d(LOG_TAG, "No of artists recieved : " + artists.artists.total);

            } catch (Exception e){
                Log.e(LOG_TAG, "Error while getting the spottify data : " + e.getMessage());
            }

            return artists;
        }
    }


}
