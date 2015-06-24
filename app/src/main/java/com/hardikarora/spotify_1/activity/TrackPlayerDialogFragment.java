package com.hardikarora.spotify_1.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.SpotifyNotification;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.service.ServiceSubscriber;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerDialogFragment extends DialogFragment implements View.OnClickListener,
        ServiceSubscriber{

    public static final String TAG = TrackPlayerDialogFragment.class.getSimpleName();
    public static final String LOG_TAG = TrackPlayerDialogFragment.class.getSimpleName();
    public static final String TRACK_SUBJECT_MESSAGE = "A great song indeed !!";
    public static final String PLAYER_PREFERENCE = "playerPreference";
    public static final String PLAYER_PREFERENCE_KEY = PLAYER_PREFERENCE;

    private PlayerViewHolder playerViewHolder;
    private ShareActionProvider mShareActionProvider;

    public static SpotifyPlayerService spotifyPlayerService;

    SeekBar playerSeekBar;
    TextView playerStartTime;
    TextView playerEndTime;
    Handler seekHandler = new Handler();
    SpotifyNotification spotifyNotification;

    List<SpotifyTrackComponent> spotifyTrackList; // List of spotify tracks.
    int trackIndex;

    private BroadcastReceiver playerReciever;

    Runnable run = new Runnable() {
        @Override
        public void run() {
            getPlayerUpdate();
        }

    };

    // A service connection to connect to the spotify api service.
    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            spotifyPlayerService = ((SpotifyPlayerService.SpotifyBinder)service).getService();
            spotifyPlayerService.spotifyTrackList = spotifyTrackList;
            spotifyPlayerService.trackIndex = trackIndex;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            spotifyPlayerService = null;
        }
    };

    public static TrackPlayerDialogFragment newInstance(){
        return new TrackPlayerDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate the service.
        Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if(item == null) return;
        item.setVisible(true);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        if(mShareActionProvider == null) return;
        //TODO: add a spotify as extra text.
        Intent shareButtonIntent = new Intent(Intent.ACTION_SEND);
        shareButtonIntent.setType("text/plain");
        String shareString = spotifyTrackList.get(trackIndex).getTrackUrl();
        shareButtonIntent.putExtra(Intent.EXTRA_SUBJECT, TRACK_SUBJECT_MESSAGE);
        shareButtonIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareButtonIntent);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "On destroy for player fragment called");

        // Canceling the notification.
        if(spotifyNotification != null) spotifyNotification.cancelNotification();

        // Unregistering the reciever.
        getActivity().unregisterReceiver(this.playerReciever);

        // Unbinding and clearing the service.
        getActivity().unbindService(connection);
        Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
        getActivity().stopService(intent);

    }


    @Override
    public void onClick(View v) {
        // If the fragment is clicked we check for the click we check for the click and
        // take action accordingly.
        if(spotifyPlayerService != null){
            spotifyPlayerService.subscribe(this);
        }
        switch (v.getId()) {
            case R.id.player_play_btn_img:
                // If the play button is clicked.
                Log.d(LOG_TAG, "Play button has been clicked.");
                spotifyPlayerService.playButtonPressed();
                break;

            case R.id.player_next_btn_img:
                // If the next button is clicked.
                Log.d(LOG_TAG, "Next song button pressed.");
                spotifyPlayerService.nextTrack();
                break;

            case R.id.player_prev_btn_img:
                Log.d(LOG_TAG, "Previous song button pressed.");
                spotifyPlayerService.previousTrack();
                break;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating the fragment for media player.");
        View rootView = inflater.inflate(R.layout.fragment_song_play, container, false);

        // If root view itself is null, we return null.
        if(rootView == null) return null;

        playerViewHolder = new PlayerViewHolder(rootView);
        rootView.setTag(playerViewHolder);

        //Setting itself as an on click listener for various play back controls.
        setOnClickForImage(playerViewHolder.playButtonImage);
        setOnClickForImage(playerViewHolder.previousButtonImage);
        setOnClickForImage( playerViewHolder.nextButtonImage);

        // If no track id has been set we return the root view.
        spotifyTrackList = (ArrayList<SpotifyTrackComponent>)(ArrayList<?>)
                getArguments().getParcelableArrayList(TrackListFragment.TRACK_LIST_TAG);
        trackIndex = getArguments().getInt(TrackListFragment.TRACK_INDEX_TAG);

        // Setting the view objects with respect to the track.
        playerViewHolder.setViewObjects(rootView, (SpotifyTrack) spotifyTrackList.get(trackIndex));

        // Setting the share intent for share button.
        setHasOptionsMenu(true);

        playerSeekBar = (SeekBar) rootView.findViewById(R.id.seek_bar);
        playerSeekBar.setVisibility(View.VISIBLE);
        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        playerStartTime = (TextView) rootView.findViewById(R.id.seek_present_time);
        playerEndTime = (TextView) rootView.findViewById(R.id.seek_end_time);

        seekHandler.postDelayed(run, 1000);

        initiateReciever();

        getActivity().registerReceiver(this.playerReciever, new IntentFilter(SpotifyPlayerService.SONG_FINISHED_EVENT));

        return rootView;
    }

    public void getPlayerUpdate(){
        MediaPlayer mediaPlayer = SpotifyPlayerService.getPlayer();
        if(mediaPlayer == null){
            return;
        }
        if (mediaPlayer.isPlaying()) {
            playerStartTime.setText(millisToString(mediaPlayer.getCurrentPosition()));
            playerEndTime.setText(millisToString(mediaPlayer.getDuration()));
            playerSeekBar.setMax(mediaPlayer.getDuration());
            playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        }
        seekHandler.postDelayed(run, 1000);
    }

    private String millisToString(long millis){
        StringBuffer buffer = new StringBuffer();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String secondStr = Long.toString(seconds);
        if(secondStr.length() == 1){
            secondStr = "0" + secondStr;
        }
        buffer.append(Long.toString(minutes)).append(":").append(secondStr);
        return buffer.toString();
    }


    private void initiateReciever(){
        playerReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "The track is over, playing next track.");
                String action = intent.getAction();
                if(action == null) return;

                if(!action.equals(SpotifyPlayerService.SONG_FINISHED_EVENT )) return;

                int serviceTrackIndex = intent.getIntExtra(TrackListFragment.TRACK_INDEX_TAG, -1);

                if(serviceTrackIndex == -1) return;

                trackIndex = serviceTrackIndex;
                playerViewHolder.setViewObjects(getView(), (SpotifyTrack) spotifyTrackList.get(trackIndex));
            }

        };
    }

    private void setOnClickForImage(ImageView image){
        if(image != null)
            image.setOnClickListener(this);
    }


    @Override
    public void stateChanged(SpotifyTrackComponent component) {
        if(playerViewHolder != null) {
            playerViewHolder.setViewObjects(getView(), component);
        }
    }



    static class PlayerViewHolder{

        @InjectView(R.id.player_play_btn_img) ImageView playButtonImage;
        @InjectView(R.id.player_next_btn_img) ImageView nextButtonImage;
        @InjectView(R.id.player_prev_btn_img) ImageView previousButtonImage;
        @InjectView(R.id.player_album_image) ImageView albumImageView;
        @InjectView(R.id.player_album_name_textview) TextView albumNameTextView;
        @InjectView(R.id.player_artist_name_textview) TextView artistNameTextView;
        @InjectView(R.id.player_track_name_textview) TextView trackNameTextView;


        public PlayerViewHolder(View view){
            ButterKnife.inject(this, view);
        }

        public void setViewObjects(View view, final SpotifyTrackComponent spotifyTrack)
        {
            if(albumNameTextView != null) albumNameTextView.setText(spotifyTrack.getAlbumName());
            if(artistNameTextView != null) artistNameTextView.setText(spotifyTrack.getArtistName());
            if(trackNameTextView != null) trackNameTextView.setText(spotifyTrack.getTrackName());
            if(albumImageView != null){
                if(view == null) return;
                // Load image with picasso.
                Log.d(LOG_TAG, "Loading the background image with picasso.");
                Picasso.with(view.getContext()).load(spotifyTrack.getImageUrl()).into(albumImageView);
            } else{
                Log.e(LOG_TAG, "Image view is null");
            }


        }
    }
}
