package com.aa.personal_assist_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by kummukes on 4/4/2018.
 */

public class CollectionAppWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_LABEL = "TASK_TEXT";

    Timer timer = null;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);

            // click event handler for the title, launches the app when the user clicks on title
            Intent titleIntent = new Intent(context, Main2Activity.class);
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
            views.setOnClickPendingIntent(R.id.widgetTitleLabel, titlePendingIntent);

            Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
            views.setRemoteAdapter(R.id.widgetListView, intent);

            // template to handle the click listener for each item
            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

            Intent aaComIntent = context.getPackageManager().getLaunchIntentForPackage("com.aa.android");
            PendingIntent aaComPendingIntent = PendingIntent.getActivity(context, 0, aaComIntent, 0);
            views.setOnClickPendingIntent(R.id.aa_com, aaComPendingIntent);

            Intent googleAssistIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.googleassistant");
            googleAssistIntent.putExtra("query", "Talk to My Agent");
            PendingIntent googleAssistPendingIntent = PendingIntent.getActivity(context, 0, googleAssistIntent, 0);
            views.setOnClickPendingIntent(R.id.talk_agent, googleAssistPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        /*if(timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("SCHEDULERRRRR", "Running every 20 seconds.....");
                    sendRefreshBroadcast(context);
                }
            }, 0, 20000);

        }*/
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CollectionAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CollectionAppWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
        }
        super.onReceive(context, intent);
    }
}