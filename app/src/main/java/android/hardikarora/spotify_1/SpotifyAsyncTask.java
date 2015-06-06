package android.hardikarora.spotify_1;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by harora on 6/5/15.
 */
public class SpotifyAsyncTask extends AsyncTask {

    public AsyncResponse response = null;



    @Override
    protected void onPostExecute(Object o) {
        response.afterExecution((List<SpotifyTrackComponent>) o);
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }




}
