package android.hardikarora.spotify_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyListAdapter extends ArrayAdapter<SpotifyArtist> {

    private List<SpotifyArtist> spotifyArtists;


    public SpotifyListAdapter(Context context,int resource, int textViewResourceId,
                              List<SpotifyArtist> spotifyArtists) {
        super(context, resource, textViewResourceId, spotifyArtists);
        this.spotifyArtists = spotifyArtists;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_spotify, null);

        }

        SpotifyArtist artist = spotifyArtists.get(position);

        if(artist != null){
            TextView artistTextView = (TextView) view.findViewById(R.id.spotify_item_textview);
            artistTextView.setText(artist.getArtistName());
                        Image image = artist.getImage();
            ImageView imageView = (ImageView) view.findViewById(R.id.spotify_album_image);
            if(image != null) {
                Picasso.with(getContext()).load(image.url).into(imageView);
            }
        }
        return view;
    }
}
