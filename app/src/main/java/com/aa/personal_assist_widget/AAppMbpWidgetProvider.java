package com.aa.personal_assist_widget;

import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.*;
import android.content.*;
import android.net.*;
import android.appwidget.*;
import android.os.*;
import java.util.Timer;
import java.util.TimerTask;


public class AAppMbpWidgetProvider extends AppWidgetProvider
{
    private static final String TAG;
    Timer timer = null;

    static {
        TAG = AAppMbpWidgetProvider.class.getSimpleName();
    }

    private RemoteViews getWidget(final Context context, final int n) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_mbp);
        Intent in = new Intent(context, MyWidgetRemoteViewsService.class);
        remoteViews.setRemoteAdapter(R.id.widgetListView, in);

        final Intent intent = new Intent(context, AAppMbpWidgetService.class);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.putExtra("appWidgetId", n);
        remoteViews.setRemoteAdapter(R.id.widget_flipper, intent);
        remoteViews.setEmptyView(R.id.widget_flipper, R.id.widget_empty_view);

        Intent clickIntentTemplate = new Intent(context, Main2Activity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

        Intent aaComIntent = context.getPackageManager().getLaunchIntentForPackage("com.aa.android");
        PendingIntent aaComPendingIntent = PendingIntent.getActivity(context, 0, aaComIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_mbp_left_button, aaComPendingIntent);

        final Intent intent3 = new Intent();
        intent3.setClassName("com.aa.android","com.aa.android.boardingpass.view.BoardingPassActivity");

        intent3.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        final PendingIntent activity2 = PendingIntent.getActivity(context, 0, intent3, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_mbp_right_button, activity2);

        remoteViews.setInt(R.id.widget_mbp_header, "setBackgroundColor", context.getResources().getColor(R.color.abc_background_cache_hint_selector_material_light));
        final Intent intent4 = new Intent(context, (Class)AAppMbpWidgetProvider.class);
        intent4.setAction("com.aa.android.appwidget.ITEM");
        intent4.putExtra("appWidgetId", n);
        remoteViews.setPendingIntentTemplate(R.id.widget_flipper, PendingIntent.getBroadcast(context, 0, intent4, 0));

        Intent aComIntent = context.getPackageManager().getLaunchIntentForPackage("com.aa.android");
        PendingIntent aComPendingIntent = PendingIntent.getActivity(context, 0, aComIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.aa_com, aaComPendingIntent);

        Intent googleAssistIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.googleassistant");
        googleAssistIntent.putExtra("query", "Talk to My Agent");
        PendingIntent googleAssistPendingIntent = PendingIntent.getActivity(context, 0, googleAssistIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.talk_agent, googleAssistPendingIntent);

        /*final Intent intent3 = new Intent(context, (Class)FlightSchedulePickerActivity.class);
        intent3.setFlags(335544320);
        final PendingIntent activity2 = PendingIntent.getActivity(context, 0, intent3, 268435456);
        remoteViews.setOnClickPendingIntent(2131757245, activity2);

        intent3.setPackage("com.aa.android");
        intent3.setClassName("com.aa.android","com.aa.android.flightinfo.view.FlightSchedulePickerActivity");*/

        return remoteViews;
    }

    public void onAppWidgetOptionsChanged(final Context context, final AppWidgetManager appWidgetManager, final int n, final Bundle bundle) {
        this.onUpdate(context, appWidgetManager, new int[] { n });
    }

    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, AAppMbpWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_flipper);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            final int n = array[i];
            appWidgetManager.updateAppWidget(n, this.getWidget(context, n));
        }
        if(timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "Running every" +  (AppConstants.REFRESH_MILLI_SECS/1000) + "seconds.....");
                    sendRefreshBroadcast(context);
                }
            }, 0, AppConstants.REFRESH_MILLI_SECS);

        }
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, AAppMbpWidgetProvider.class));
        context.sendBroadcast(intent);
    }
}