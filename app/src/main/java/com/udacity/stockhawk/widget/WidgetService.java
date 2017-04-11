package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

import java.lang.annotation.Target;
import java.util.concurrent.ExecutionException;

import static java.lang.annotation.Target.*;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetViewsFactory widgetViewsFactory = new WidgetViewsFactory();
        return widgetViewsFactory;
    }

    private class WidgetViewsFactory implements RemoteViewsFactory{

        private Cursor cursor = null;

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_provider_item);
            // get data from cursor and inflate layout with data
            String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            views.setTextViewText(R.id.widget_item_symbol, symbol);
            views.setTextViewText(R.id.widget_item_price, Float.toString(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
            views.setTextViewText(R.id.widget_item_change, Float.toString(cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE)));

            // fill intent to launch detail activity
            final Intent fillInIntent = new Intent();
            fillInIntent.setData(Contract.Quote.makeUriForStock(symbol));
            views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_provider_item);

        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (cursor.moveToPosition(position))
                return cursor.getLong(Contract.Quote.POSITION_ID);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return cursor == null ? 0 : cursor.getCount();
        }

        @Override
        public void onDataSetChanged() {
            if (cursor != null) {
                cursor.close();
            }
            final long identityToken = Binder.clearCallingIdentity();
            cursor = getContentResolver().query(Contract.Quote.uri, Contract.Quote.QUOTE_COLUMNS, null, null, null);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        @Override
        public void onCreate() {

        }
    }

}
