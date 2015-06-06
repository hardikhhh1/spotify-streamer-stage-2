package android.hardikarora.spotify_1.util;

import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by harora on 6/5/15.
 * Class representing the async task used to get the spotify data,
 * after the async task is completed {@link android.hardikarora.spotify_1.util.AsyncResponse}
 * method is called.
 */
public class SpotifyAsyncTask extends AsyncTask {

    public AsyncResponse callback = null;

    public SpotifyAsyncTask(AsyncResponse callback) {
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Object o) {
        // After the execution of the task, after execution is called.
        if(callback != null)
            callback.afterExecution((List<SpotifyTrackComponent>) o);
    }

    @Override
    protected Object doInBackground(Object[] objects) { return null; }

}
