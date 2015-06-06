package android.hardikarora.spotify_1;

/**
 * Created by hardikarora on 6/5/15.
 */
public abstract class SpotifyTrackComponent {

    private String trackName;
    private String albumName;
    private String artistName;
    private String imageUrl;
    private String artistId;


    public SpotifyTrackComponent() {
    }

    public String getArtistId() {
        throw new UnsupportedOperationException();
    }

    public void setArtistId(String artistId) {
        throw new UnsupportedOperationException();
    }

    public String getTrackName() {
        throw new UnsupportedOperationException();
    }

    public void setTrackName(String trackName) {
        throw new UnsupportedOperationException();
    }

    public String getAlbumName() {
        throw new UnsupportedOperationException();
    }

    public void setAlbumName(String albumName) {
        throw new UnsupportedOperationException();
    }

    public String getArtistName() {
        throw new UnsupportedOperationException();
    }

    public void setArtistName(String artistName) {
        throw new UnsupportedOperationException();
    }

    public String getImageUrl() {
        throw new UnsupportedOperationException();
    }

    public void setImageUrl(String imageUrl) {
        throw new UnsupportedOperationException();
    }
}
