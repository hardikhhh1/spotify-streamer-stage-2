package com.hardikarora.spotify_1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyTrackListAdapter extends ArrayAdapter<SpotifyTrackComponent> {

    private List<SpotifyTrackComponent> artistTracks;
    private int resource;


    public SpotifyTrackListAdapter(Context context, int resource, int textViewResourceId,
                                   List<SpotifyTrackComponent> artistTracks) {
        super(context, resource, textViewResourceId, artistTracks);
        this.artistTracks = artistTracks;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        TrackViewHolder trackViewHolder;
        LayoutInflater inflater = (LayoutInflater)getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null){
            view = inflater.inflate(resource, null);
            trackViewHolder = new TrackViewHolder(view);
            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (TrackViewHolder) view.getTag();
        }

        SpotifyTrackComponent track = artistTracks.get(position);
        if(track == null) return view;

        // Setting the properties of the track view holder.
        trackViewHolder.setHolderProperties(track);
        return view;
    }

    private class TrackViewHolder{

        @InjectView(R.id.spotify_album_textview) TextView albumTextView;
        @InjectView(R.id.spotify_trackname_textview) TextView trackTextView;
        @InjectView(R.id.spotify_album_image) ImageView imageView;

        public TrackViewHolder(View view){
            ButterKnife.inject(view);
        }

        public void setHolderProperties(SpotifyTrackComponent track){
            albumTextView.setText(track.getAlbumName());
            trackTextView.setText(track.getTrackName());
            String imageUrl = track.getImageUrl();
            if(imageView != null) {
                Picasso.with(getContext()).load(imageUrl)
                        .into(imageView);
            }
        }



    }
}
