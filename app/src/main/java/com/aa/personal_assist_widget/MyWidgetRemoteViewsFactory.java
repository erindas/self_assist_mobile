package com.aa.personal_assist_widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kummukes on 4/4/2018.
 */

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    private boolean reqComplete = false;
    private List<ListModel> listDetails;
    //private final String SERV_URL = "http://17edc5af.ngrok.io/mobile";
    private final String SERV_URL = "https://hack-wars-5.herokuapp.com/mobile";

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        loadRecyclerViewData();
        while(!reqComplete) { }
        reqComplete = false;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listDetails.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        ListModel lDet = listDetails.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_widget);
        rv.setTextViewText(R.id.txtViewName, lDet.getName());
        rv.setTextViewText(R.id.txtViewInfo, lDet.getInfo());
        rv.setTextViewText(R.id.txtViewWaitTime, lDet.getWaitTime() + " mins");

        if(lDet.getStatus().equalsIgnoreCase("Done")) {
            int col = mContext.getResources().getColor(R.color.american_dark_green);
            rv.setInt(R.id.status_border, "setBackgroundColor", col);
            rv.setTextColor(R.id.txtViewName,col);
        } else {
            int col = mContext.getResources().getColor(R.color.american_orange);
            rv.setInt(R.id.status_border, "setBackgroundColor", col);
            rv.setTextColor(R.id.txtViewName,col);
        }
        switch (position) {
            case 0: rv.setImageViewResource(R.id.row_img, R.drawable.img_checkin); break;
            case 1: rv.setImageViewResource(R.id.row_img, R.drawable.icon_security_2); break;
            case 2: rv.setImageViewResource(R.id.row_img, R.drawable.icon_gate2); break;
            default: rv.setImageViewResource(R.id.row_img, R.drawable.img_checkin);
        }


        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(CollectionAppWidgetProvider.EXTRA_LABEL, lDet.getName());
        rv.setOnClickFillInIntent(R.id.list_items, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadRecyclerViewData() {
        listDetails = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                SERV_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("arr");

                            for(int i=0; i<array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                ListModel listDet = new ListModel();
                                listDet.setName(o.getString("name"));
                                listDet.setInfo(o.getString("info"));
                                listDet.setWaitTime(o.getString("wait_time"));
                                listDet.setStatus(o.getString("status"));
                                listDetails.add(listDet);
                            }
                            reqComplete = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG);
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

}