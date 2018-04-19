package com.aa.personal_assist_widget;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import java.util.logging.Logger;

public class Main2Activity extends AppCompatActivity {

    Logger logger = Logger.getLogger(Main2Activity.class.getName());

    private RecyclerView recyclerView;
    private RecyclerView.Adapter rViewAdapter;

    private List<ListModel> listDetails;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleAssistIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.googleassistant");
                googleAssistIntent.putExtra("query", "Talk to My Agent");
                PendingIntent googleAssistPendingIntent = PendingIntent.getActivity(context, 0, googleAssistIntent, 0);
                startActivity(googleAssistIntent);
            }
        });

        recyclerView = findViewById(R.id.recycleView2);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecor.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        RecyclerView.ItemDecoration itDec = new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z1), true);
        recyclerView.addItemDecoration(itDec);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDetails = new ArrayList<>();
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppUtils.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("arr");
                            listDetails = AppUtils.getDetails(array);
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
    }

}