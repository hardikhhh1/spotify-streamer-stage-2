package android.hardikarora.spotify_1.util;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by hardikarora on 6/10/15.
 */
public class SpotifyPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private enum PlayerState {
        Play, Stopped, Paused ;
    }

    private String spotifyTrackUrl;
    private static PlayerState spotifyPlayerState;
    MediaPlayer player;
    public IBinder mBinder = new SpotifyBinder();
    private static final String LOG_TAG = SpotifyPlayerService.class.getSimpleName();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.spotifyTrackUrl = intent.getExtras().getString("SpotifyTrackUrl");
        if(player.isPlaying()){
            player.stop();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(spotifyTrackUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while playing the music" + e.getMessage());
            e.printStackTrace();
        }
        player.setOnPreparedListener(this);
        player.prepareAsync();
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        spotifyPlayerState = PlayerState.Play;
        player.start();
    }



//    @Override
//    public void afterExecution(List<SpotifyTrackComponent> input) {
//        spotifyTrack = input.get(0);
//        View view = getView();
//        if(view == null) {
//            Log.e(LOG_TAG, "View is null");
//            return ;
//        }
//        ImageView imageView = (ImageView) view.findViewById(R.id.player_album_image);
//        if(imageView != null){
//            // Load image with picasso.
//            Log.d(LOG_TAG, "Loading the background image with picasso.");
//            String imageUrl = spotifyTrack.getImageUrl();
//            Picasso.with(view.getContext()).load(imageUrl).into(imageView);
//        } else{
//            Log.e(LOG_TAG, "Image view is null");
//        }
//    }

    public class SpotifyBinder extends Binder{

        public SpotifyPlayerService getInstance(){
            return SpotifyPlayerService.this;
        }
    }

}
