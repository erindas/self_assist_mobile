package com.aa.personal_assist_widget;

import android.content.Context;
import android.util.Log;
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
import java.util.concurrent.CountDownLatch;

/**
 * Created by kummukes on 4/18/2018.
 */

public class AppUtils {

    public void sendRequest(String restUrl, final Context context, final StateHolder stateHolder) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                restUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            stateHolder.setJsonObject(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        stateHolder.setRequestProcessed(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG);
                        stateHolder.setRequestProcessed(true);
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public List<ListModel> getListDetails(StateHolder stateHolder) {
        //StateHolder stateHolder = new StateHolder();
        //sendRequest(restUrl,context,stateHolder);
        while(!stateHolder.isRequestProcessed()) { }
        JSONObject jsonObject = stateHolder.getJsonObject();
        List<ListModel> listDetails = new ArrayList<>();
        try {
            JSONArray array = jsonObject.getJSONArray("arr");
            for(int i=0; i<array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                ListModel listDet = new ListModel();
                listDet.setName(o.getString("name"));
                listDet.setInfo(o.getString("info"));
                listDet.setWaitTime(o.getString("wait_time"));
                listDetails.add(listDet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listDetails;
    }

    public static List<ListModel> getDetails(JSONArray array) {
        List<ListModel> listDetails = new ArrayList<>();
        for(int i=0; i<array.length(); i++) {
            JSONObject o = null;
            try {
                o = array.getJSONObject(i);
                ListModel listDet = new ListModel();
                listDet.setName(o.getString("name"));
                listDet.setInfo(o.getString("info"));
                listDet.setWaitTime(o.getString("wait_time"));
                listDet.setStatus(o.getString("status"));
                listDetails.add(listDet);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return listDetails;
    }

    class StateHolder {
        boolean requestProcessed = false;
        JSONObject jsonObject;

        public boolean isRequestProcessed() {
            return requestProcessed;
        }

        public void setRequestProcessed(boolean requestProcessed) {
            this.requestProcessed = requestProcessed;
        }

        public JSONObject getJsonObject() {
            return jsonObject;
        }

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }
    }

}