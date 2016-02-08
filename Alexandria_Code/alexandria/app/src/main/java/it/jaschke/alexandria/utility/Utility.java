package it.jaschke.alexandria.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.prefs.PreferenceChangeEvent;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.services.BookService;

/**
 * Created by abhijeet.burle on 2016/02/03.
 */
public class Utility {
    /*
        @return true : if network is accessible
                false : if network is not accessible
     */
    public static boolean isNetworkAvailable(Context objContext){
        ConnectivityManager objConnectivityManager =
                (ConnectivityManager)objContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo objNetworkInfo = objConnectivityManager.getActiveNetworkInfo();
        return (objNetworkInfo != null && objNetworkInfo.isConnectedOrConnecting());
    }

    @SuppressWarnings("ResourceType")
    static  public  @BookService.ConnectivityStatus
    int getConnectionStatus(Context c){
        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getInt(c.getString(R.string.pref_connectivity_status_key),
                BookService.STATUS_ERROR_CONNECTION_UNKNOWN);
    }


}
