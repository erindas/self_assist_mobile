package com.aa.personal_assist_widget;

import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.*;
import android.content.*;
import android.net.*;
/*import com.aa.android.view.*;*/
/*import com.aa.android.flightinfo.view.*;*/
import android.appwidget.*;
import android.os.*;
/*import com.aa.android.aabase.util.*;
import com.aa.android.terminalmaps.view.*;
import com.aa.android.feature.boardingpass.*;
import com.aa.android.model.reservation.*;
import com.aa.android.util.*;
import com.aa.android.boardingpass.view.*;*/

public class AAppMbpWidgetProvider extends AppWidgetProvider
{
    private static final String TAG;

    static {
        TAG = AAppMbpWidgetProvider.class.getSimpleName();
    }

    private RemoteViews getWidget(final Context context, final int n) {
        final Intent intent = new Intent(context, AAppMbpWidgetService.class);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.putExtra("appWidgetId", n);
        //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_mbp);
        remoteViews.setRemoteAdapter(R.id.widget_flipper, intent);
        remoteViews.setEmptyView(R.id.widget_flipper, R.id.widget_empty_view);

        /*final Intent intent2 = new Intent(context, (Class)SplashActivity.class);
        intent2.setAction("android.intent.action.MAIN");
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setFlags(2097152);
        final PendingIntent activity = PendingIntent.getActivity(context, 0, intent2, 268435456);
        remoteViews.setOnClickPendingIntent(2131757238, activity); */

        Intent in = new Intent(context, MyWidgetRemoteViewsService.class);
        remoteViews.setRemoteAdapter(R.id.widgetListView, in);

        // template to handle the click listener for each item
        Intent clickIntentTemplate = new Intent(context, Main2Activity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

        Intent aaComIntent = context.getPackageManager().getLaunchIntentForPackage("com.aa.android");
        PendingIntent aaComPendingIntent = PendingIntent.getActivity(context, 0, aaComIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_mbp_left_button, aaComPendingIntent);

        /*final Intent intent3 = new Intent(context, (Class)FlightSchedulePickerActivity.class);
        intent3.setFlags(335544320);
        final PendingIntent activity2 = PendingIntent.getActivity(context, 0, intent3, 268435456);
        remoteViews.setOnClickPendingIntent(2131757245, activity2);*/

        final Intent intent3 = new Intent();
        //intent3.setPackage("com.aa.android");
        //intent3.setClassName("com.aa.android","com.aa.android.flightinfo.view.FlightSchedulePickerActivity");
        intent3.setClassName("com.aa.android","com.aa.android.boardingpass.view.BoardingPassActivity");

        //intent3.setFlags(335544320);
        intent3.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        //final PendingIntent activity2 = PendingIntent.getActivity(context, 0, intent3, 268435456);
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
        return remoteViews;
    }

    public void onAppWidgetOptionsChanged(final Context context, final AppWidgetManager appWidgetManager, final int n, final Bundle bundle) {
        this.onUpdate(context, appWidgetManager, new int[] { n });
    }

    public void onReceive(final Context context, final Intent intent) {
        int n = 1;

        final AppWidgetManager instance = AppWidgetManager.getInstance(context.getApplicationContext());
        final boolean booleanExtra = intent.getBooleanExtra("com.aa.android.appwidget.LEFT", false);
        final boolean booleanExtra2 = intent.getBooleanExtra("com.aa.android.appwidget.RIGHT", false);
        if (booleanExtra || booleanExtra2) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_mbp);
            if (booleanExtra) {
                remoteViews.showPrevious(R.id.widget_flipper);
            }
            else if (booleanExtra2) {
                remoteViews.showNext(R.id.widget_flipper);
            }
            instance.updateAppWidget(AAppMbpWidgetService.getAppWidgetIds(context, instance), remoteViews);
        }
        /*else if (intent.hasExtra("com.aa.android.airport_code")) {
            final String stringExtra = intent.getStringExtra("com.aa.android.airport_code");
            if (!stringExtra.equals("com.aa.android.appwidget.NO_CODE")) {
                final Intent intent2 = new Intent(context, (Class)TerminalMapActivity.class);
                intent2.putExtra("com.aa.android.airport_code", stringExtra);
                if (intent.hasExtra("com.aa.android.airport_gate")) {
                    final String stringExtra2 = intent.getStringExtra("com.aa.android.airport_gate");
                    final boolean booleanExtra3 = intent.getBooleanExtra("com.aa.android.is_departure_gate", true);
                    intent2.putExtra("com.aa.android.airport_gate", stringExtra2);
                    intent2.putExtra("com.aa.android.is_departure_gate", booleanExtra3);
                }
                intent2.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                context.startActivity(intent2);
            }
        }
        else if (intent.hasExtra("com.aa.android.appwidget.BOARDING")) {
            final String stringExtra3 = intent.getStringExtra("com.aa.android.appwidget.BOARDING");
            if (!stringExtra3.equals("com.aa.android.appwidget.MBP_CANCELED")) {
                if (AAFeatureMultiPaxBoardingPass.getNativeInstance().getRelevancy() < 90) {
                    n = 0;
                }
                if (n != 0) {
                    final BoardingPass queryWithSerialNumber = BoardingPass.queryWithSerialNumber(stringExtra3);
                    final Bundle bundle = new Bundle();
                    bundle.putString("com.aa.android.extra.flight_key", queryWithSerialNumber.getFlightKey());
                    bundle.putString("com.aa.android.extra.traveler.id", queryWithSerialNumber.getTravelerId());
                    bundle.putInt("com.aa.android.intent_flags", Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    EventsUtils.get().startActivity("com.aa.android.action.boarding_pass_widget", bundle);
                }
                else {
                    final Intent activityIntent = LegacyBoardingPassActivity.getActivityIntent(context, stringExtra3);
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    context.startActivity(activityIntent);
                }
            }
        }
        else if (intent.getBooleanExtra("com.aa.android.appwidget.HEADER", false)) {
            final Intent intent3 = new Intent(context, (Class)SplashActivity.class);
            intent3.setAction("android.intent.action.MAIN");
            intent3.addCategory("android.intent.category.LAUNCHER");
            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent3);
        }*/
        super.onReceive(context, intent);
    }

    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            final int n = array[i];
            appWidgetManager.updateAppWidget(n, this.getWidget(context, n));
        }
    }
}