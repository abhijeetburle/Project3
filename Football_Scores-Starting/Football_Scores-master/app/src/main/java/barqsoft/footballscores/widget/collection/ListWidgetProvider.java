package barqsoft.footballscores.widget.collection;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.Utilities;

/**
 * Created by abhijeet.burle on 2016/02/17.
 */
public class ListWidgetProvider  implements RemoteViewsFactory {
    private final static String TAG = ListWidgetProvider.class.getSimpleName();

    private Context mContext = null;
    private Cursor cursor = null;

    @SuppressWarnings("unused")
    public ListWidgetProvider(Context context, Intent intent) {
        Log.i(TAG, "ListWidgetProvider");
        mContext = context;
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount");
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.i(TAG, "getLoadingView");
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG, "getViewAt: position="+position);
        if (position == AdapterView.INVALID_POSITION) {
            Log.w(TAG, "AdapterView.INVALID_POSITION");
            return null;
        }
        if (cursor == null || ! cursor.moveToPosition(position)) {
            Log.w(TAG, "invalid cursor");
            return null;
        }

        String homeTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_COL));
        int homeScore =  cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_GOALS_COL));
        String awayTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_COL));
        int awayScore =  cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_GOALS_COL));
        String vs = "vs";
        String colon = ":";
        String gameInfo = homeTeam + " " + vs + " " + awayTeam;

        String scoreInfo = Utilities.getScores(homeScore, awayScore) + colon + " ";
        Log.v(TAG, "scoreInfo=" + scoreInfo + ", gameInfo=" + gameInfo);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
        remoteViews.setTextViewText(android.R.id.text1, scoreInfo + " " + gameInfo);
        remoteViews.setTextColor(android.R.id.text1, Color.WHITE);

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(ScoresWidgetProvider.ACTION_WIDGET_ITEM_CLICK);
        final Bundle bundle = new Bundle();
        bundle.putString(ScoresWidgetProvider.EXTRA_STRING, String.valueOf(position));
        fillInIntent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
        return remoteViews;
    }

    @Override
    public int getViewTypeCount() {
        Log.v(TAG, "getViewTypeCount");
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        Log.v(TAG, "hasStableIds");
        return true;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
    }

    @Override
    public void onDataSetChanged() {
        Log.v(TAG, "onDataSetChanged");
        if (cursor != null) {
            cursor.close();
        }
            final long token = Binder.clearCallingIdentity();
            try {
                Log.v(TAG, "--> QUERY FOR WIDGET DATA <--");
                Uri scoreUriForDate = DatabaseContract.ScoresTable.buildScoreWithDate();
                Log.v(TAG, "scoreUriForDate=" + scoreUriForDate);
                long timeToday = System.currentTimeMillis();
                Locale locale = mContext.getResources().getConfiguration().locale;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
                String[] queryForDate = new String[1];
                queryForDate[0] = dateFormat.format(timeToday);
                cursor = mContext.getContentResolver()
                        .query(scoreUriForDate,
                                DatabaseContract.ScoresTable.getScoresTableColumnsForWidget(),
                                "date",
                                queryForDate,
                                null
                        );
                Log.v(TAG, "cursor=" + cursor);
            } finally {
                Binder.restoreCallingIdentity(token);
            }

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }
}
