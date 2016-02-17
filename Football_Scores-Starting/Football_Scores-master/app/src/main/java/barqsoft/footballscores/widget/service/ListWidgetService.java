package barqsoft.footballscores.widget.service;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.widget.collection.ListWidgetProvider;


/**
 * Created by abhijeet.burle on 2016/02/17.
 */
public class ListWidgetService extends RemoteViewsService {
    private final static String TAG = ListWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG, "onGetViewFactory");
        return new ListWidgetProvider(getApplicationContext(), intent);
    }

}
