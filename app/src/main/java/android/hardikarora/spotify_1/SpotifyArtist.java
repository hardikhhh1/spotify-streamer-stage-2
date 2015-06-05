package android.hardikarora.spotify_1;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyArtist {

    private Image image;
    private String artistId;
    private String artistName;

    public SpotifyArtist(String artistName,  String artistId) {
        this.artistName = artistName;
        this.artistId = artistId;
    }

    public SpotifyArtist(Image image, String artistName) {
        this.image = image;
        this.artistName = artistName;
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }
}
