package android.hardikarora.spotify_1;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by hardikarora on 6/5/15.
 */
public class SpotifyApiUtil {

    public static ArtistsPager searchArtists(String artistName){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        ArtistsPager artists = spotifyService.searchArtists(artistName);
        return artists;
    }


    public static Tracks getArtistsTopTracks(String artistID){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService service = spotifyApi.getService();
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put("country", "US");
        Tracks tracks = service.getArtistTopTrack(artistID, apiOptions);
        return tracks;
    }



}
