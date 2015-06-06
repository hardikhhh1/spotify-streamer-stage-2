package android.hardikarora.spotify_1.adapter;

import android.content.Context;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hardikarora on 6/3/15.
 * Adapter to display the list of artists on the UI.
 */
public class SpotifyArtistListAdapter extends ArrayAdapter<SpotifyTrackComponent> {

    private List<SpotifyTrackComponent> spotifyArtists;
    private int resource ;
    private int textViewResorceId;

    public SpotifyArtistListAdapter(Context context, int resource, int textViewResourceId,
                                    List<SpotifyTrackComponent> spotifyArtists) {
        super(context, resource, textViewResourceId, spotifyArtists);
        this.resource = resource;
        this.textViewResorceId = textViewResourceId;
        this.spotifyArtists = spotifyArtists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(this.resource, null);
        }

        SpotifyTrackComponent artist = spotifyArtists.get(position);

        if(artist != null){
            TextView artistTextView = (TextView) view.findViewById(this.textViewResorceId);
            artistTextView.setText(artist.getArtistName());
            String imageUrl = artist.getImageUrl();
            ImageView imageView = (ImageView) view.findViewById(R.id.spotify_album_image);

            // If image url is null or empty return the view.
            if(imageUrl == null) return view;
            if(imageUrl.isEmpty()) return view;

            // Load the image with picasso.
            Picasso.with(getContext()).load(imageUrl).into(imageView);
        }
        return view;
    }
}
