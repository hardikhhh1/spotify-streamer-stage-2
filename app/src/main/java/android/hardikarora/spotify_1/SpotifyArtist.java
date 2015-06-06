package android.hardikarora.spotify_1;


/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyArtist extends SpotifyTrackComponent {

    private String imageUrl;
    private String artistId;
    private String artistName;

    public SpotifyArtist(String artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    @Override
    public String getArtistId() {
        return this.artistId;
    }

    @Override
    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public String getArtistName() {
        return this.artistName;
    }

    @Override
    public void setArtistName(String artistName) {

        this.artistName = artistName;
    }

    @Override
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
