package ducic.plumbum.com.bjp.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by pankaj on 25/10/17.
 */

public class BhartiyaApplication extends Application {
    private static BhartiyaApplication mInstance;
    //Declare a private  RequestQueue variable
    private RequestQueue requestQueue;
    private SharedPreferences sp;

    public static synchronized BhartiyaApplication getInstance() {
        return mInstance;
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sp = PreferenceManager.getDefaultSharedPreferences(mInstance);
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    /**
     * Create a getRequestQueue() method to return the instance of
     * RequestQueue.This kind of implementation ensures that
     * the variable is instatiated only once and the same
     * instance is used throughout the application
     **/
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    /**
     * public method to add the Request to the the single
     * instance of RequestQueue created above.Setting a tag to every
     * request helps in grouping them. Tags act as identifier
     * for requests and can be used while cancelling them
     **/
    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);

    }

    /**
     * Cancel all the requests matching with the given tag
     **/

    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
