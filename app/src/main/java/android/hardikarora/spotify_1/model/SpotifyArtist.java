package android.hardikarora.spotify_1.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hardikarora on 6/3/15.
 *
 * Class representing the spotify artist model.
 */
public class SpotifyArtist extends SpotifyTrackComponent implements Parcelable {

    private String artistId;
    private String artistName;
    private String imageUrl;

    public SpotifyArtist(String artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public SpotifyArtist(String artistId, String artistName, String imageUrl) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write the text to the parcel.
        dest.writeString(this.artistId);
        dest.writeString(this.artistName);
        dest.writeString(this.imageUrl);
    }

    private static Parcelable.Creator TrackCreator = new Parcelable.Creator() {
        @Override
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source.readString(),
                    source.readString(), source.readString());
        }

        @Override
        public SpotifyArtist[] newArray(int size) { return new SpotifyArtist[size];}
    };

    @Override
    public String getArtistId() { return this.artistId;}

    @Override
    public void setArtistId(String artistId) { this.artistId = artistId; }

    @Override
    public String getArtistName() { return this.artistName; }

    @Override
    public void setArtistName(String artistName) { this.artistName = artistName;}

    @Override
    public String getImageUrl() { return this.imageUrl; }

    @Override
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
