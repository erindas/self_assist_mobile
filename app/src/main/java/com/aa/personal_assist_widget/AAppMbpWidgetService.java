package com.aa.personal_assist_widget;

import java.util.concurrent.*;
import android.appwidget.*;
import android.os.*;
import android.content.*;
import android.util.Log;
import android.widget.*;

public class AAppMbpWidgetService extends RemoteViewsService
{
    private static final String TAG;
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
                notifyChanged(this.context);
            }
        }
    }
}
