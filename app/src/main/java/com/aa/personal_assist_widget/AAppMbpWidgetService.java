package com.aa.personal_assist_widget;

import java.util.concurrent.*;
import android.appwidget.*;
import android.os.*;
import android.content.*;
import android.util.Log;
import android.widget.*;

public class AAppMbpWidgetService extends RemoteViewsService
{
    public static final String BOARDING_PASS = "com.aa.android.appwidget.BOARDING";
    public static final String HEADER = "com.aa.android.appwidget.HEADER";
    public static final String LEFT_ARROW = "com.aa.android.appwidget.LEFT";
    public static final String MBP_CANCELED = "com.aa.android.appwidget.MBP_CANCELED";
    public static final String NO_CODE = "com.aa.android.appwidget.NO_CODE";
    public static final String PACKAGE = "com.aa.android.appwidget.";
    public static final String RIGHT_ARROW = "com.aa.android.appwidget.RIGHT";
    private static final String TAG;
    public static final String WIDGET_ITEM_ACTION = "com.aa.android.appwidget.ITEM";
    private static final long WIDGET_UPDATE_DELAY;
    private static final Object sLock;
    private static volatile long sWidgetLastUpdated;
    private static volatile boolean sWidgetUpdatePending;

    static {
        TAG = AAppMbpWidgetService.class.getSimpleName();
        WIDGET_UPDATE_DELAY = TimeUnit.SECONDS.toMillis(10L);
        sLock = new Object();
    }

    public static int[] getAppWidgetIds(final Context context, final AppWidgetManager appWidgetManager) {
        return appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), (Class)AAppMbpWidgetProvider.class));
    }

    private static void notifyChanged(final Context context) {
        final AppWidgetManager instance = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = getAppWidgetIds(context, instance);
        if (appWidgetIds.length > 0) {
            instance.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_flipper);
        }
    }

    public static void notifyWidgetUpdated(final Context context) {
        synchronized (AAppMbpWidgetService.sLock) {
            if (AAppMbpWidgetService.sWidgetUpdatePending) {
                return;
            }
            AAppMbpWidgetService.sWidgetUpdatePending = true;
            // monitorexit(AAppMbpWidgetService.sLock)
            final long max = Math.max(SystemClock.uptimeMillis(), AAppMbpWidgetService.sWidgetLastUpdated + AAppMbpWidgetService.WIDGET_UPDATE_DELAY);
        }
    }

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(final Intent intent) {
        Log.i(AAppMbpWidgetService.TAG, "onGetViewFactory()");
        return new MbpWidgetViewFactory(this.getApplicationContext());
    }

    private static final class WidgetRunnable implements Runnable
    {
        private static WidgetRunnable sRunnable;
        private final Context context;

        private WidgetRunnable(final Context context) {
            this.context = context.getApplicationContext();
        }

        private static WidgetRunnable getRunnable(final Context context) {
            synchronized (WidgetRunnable.class) {
                if (WidgetRunnable.sRunnable == null) {
                    WidgetRunnable.sRunnable = new WidgetRunnable(context.getApplicationContext());
                }
                return WidgetRunnable.sRunnable;
            }
        }

        @Override
        public void run() {
            synchronized (AAppMbpWidgetService.sLock) {
                AAppMbpWidgetService.sWidgetLastUpdated = SystemClock.uptimeMillis();
                AAppMbpWidgetService.sWidgetUpdatePending = false;
                // monitorexit(AAppMbpWidgetService.access$100())
                notifyChanged(this.context);
            }
        }
    }
}
