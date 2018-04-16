package com.aa.personal_assist_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    private static  RecyclerView recyclerView;
    private static RecyclerView.Adapter rViewAdapter;

    private static List<ListModel> listDetails;

    private final static String SERV_URL = "https://17edc5af.ngrok.io/mobile";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        RemoteViews view1 = new RemoteViews(context.getPackageName(), R.layout.list_widget);
        views.addView(R.id.widget_main,view1);
        RemoteViews view2 = new RemoteViews(context.getPackageName(), R.layout.list_widget);
        view1.addView(R.id.widget_main,view2);

        //views.setTextViewText(R.id.appwidget_text, widgetText);
        //recyclerView = new RemoteViews(context.getPackageName(),R.id.recycleView2);

        /*recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDetails = new ArrayList<>();

        loadRecyclerViewData();*/


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    /*private static void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                SERV_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("arr");

                            for(int i=0; i<array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                ListModel listDet = new ListModel();
                                listDet.setName(o.getString("name"));
                                listDet.setInfo(o.getString("info"));
                                listDet.setWaitTime(o.getString("wait_time"));
                                listDetails.add(listDet);
                            }

                            *//*ListModel listDet = new ListModel();
                            listDet.setName(jsonObject.getString("name"));
                            listDet.setInfo(jsonObject.getString("info"));
                            listDetails.add(listDet);*//*

                            rViewAdapter = new RecViewAdapter(listDetails, getApplicationContext());
                            recyclerView.setAdapter(rViewAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG);
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/
}

