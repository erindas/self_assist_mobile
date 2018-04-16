package com.aa.personal_assist_widget;

import java.util.concurrent.*;
import android.util.Log;
import android.widget.*;
import android.content.*;
import android.support.v4.content.*;
import android.net.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

class MbpWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory
{
    private static final String TAG;
    //private Map<String, VenueInfo> airportCodes;
    private Map<String, String> airportCodes;
    private List<BoardingPass> boardingPasses;
    private final Context context;

    private boolean reqComplete = false;
    private List<ListModel> listDetails;
    //private final String SERV_URL = "http://17edc5af.ngrok.io/mobile";
    private final String SERV_URL = "https://hack-wars-5.herokuapp.com/mobile";

    static {
        TAG = MbpWidgetViewFactory.class.getSimpleName();
    }
    
    MbpWidgetViewFactory(final Context context) {
        this.context = context;
    }
    
    private void await(final CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        }
        catch (InterruptedException ex) {
            Log.e(MbpWidgetViewFactory.TAG, "failed awaiting", ex);
        }
    }
    
    private RemoteViews getEmptyView(final RemoteViews remoteViews) {
        final Intent intent = new Intent();
        intent.setClassName("com.aa.android","com.aa.android.view.SplashActivity");
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        final Intent intent2 = new Intent();
        intent2.setClassName("com.aa.android","com.aa.android.flightinfo.view.FlightSchedulePickerActivity");
        intent2.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        remoteViews.setOnClickFillInIntent(R.id.widget_mbp_left_button, intent);
        remoteViews.setOnClickFillInIntent(R.id.widget_mbp_right_button, intent2);
        return remoteViews;
    }
    
    private List<BoardingPass> queryBoardingPasses() {
        List<BoardingPass> boardingPasses = new ArrayList<>();
        //BoardingPass bp1 = new BoardingPass(context, travelerData.getFirstName(), travelerData.getLastName(), flightData.getPnr(), travelerData.getTravelerID(), segmentData.getFlight(), segmentData.getCheckInInfo().getOriginCode(), buildFlightKey, flightData, segmentData);
        BoardingPass bp1 = new BoardingPass("1","7201","DFW","SFO","Sunday APR 29, 2018","DELAYED","04:15pm","22A",false,"E","E27");
        boardingPasses.add(bp1);
        return boardingPasses;
        /*return (List<BoardingPass>)DbUtils.query((Class)BoardingPass.class, (DbUtils.QueryHelper)new DbUtils.QueryHelper<BoardingPass>() {
            public PreparedQuery<BoardingPass> buildQuery(final QueryBuilder<BoardingPass, ?> queryBuilder) throws Exception {
                return (PreparedQuery<BoardingPass>)queryBuilder.orderBy("depart_date", true).prepare();
            }
        });*/
    }
    
    private void setAirportCodeClickIntent(final RemoteViews remoteViews, final int n, final String s, final boolean b, final boolean b2, final String s2) {
        final Intent intent = new Intent();
        if (b) {
            intent.putExtra("com.aa.android.airport_code", "com.aa.android.appwidget.NO_CODE");
            remoteViews.setTextColor(n, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setInt(n, "setBackgroundColor", ContextCompat.getColor(this.context, R.color.abc_background_cache_hint_selector_material_light));
        }
        else if (this.airportCodes != null && this.airportCodes.containsKey(s)) {
            intent.putExtra("com.aa.android.airport_code", s);
            intent.putExtra("com.aa.android.airport_gate", s2);
            intent.putExtra("com.aa.android.is_departure_gate", b2);
        }
        else {
            intent.putExtra("com.aa.android.airport_code", "com.aa.android.appwidget.NO_CODE");
            remoteViews.setTextColor(n, ContextCompat.getColor(this.context, R.color.american_dark_blue_grey));
            remoteViews.setInt(n, "setBackgroundColor", ContextCompat.getColor(this.context, R.color.abc_background_cache_hint_selector_material_light));
        }
        remoteViews.setOnClickFillInIntent(n, intent);
    }
    
    private void setArrows(final RemoteViews remoteViews, final int n) {
        if (this.boardingPasses.size() >= 2) {
            remoteViews.setViewVisibility(R.id.widget_mbp_left_arrow, 0);
            remoteViews.setViewVisibility(R.id.widget_mbp_right_arrow, 0);
            remoteViews.setViewVisibility(R.id.widget_header_logo_padding, 0);
            if (n == 0) {
                remoteViews.setImageViewResource(R.id.widget_mbp_left_arrow, R.drawable.ic_circle_arrow_left_blue_disabled);
                remoteViews.setInt(R.id.widget_mbp_left_arrow, "setBackgroundColor", ContextCompat.getColor(this.context, R.color.abc_background_cache_hint_selector_material_light));
            }
            else {
                final Intent intent = new Intent(this.context, (Class)AAppMbpWidgetProvider.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                intent.putExtra("com.aa.android.appwidget.LEFT", true);
                intent.putExtra("com.aa.android.appwidget.BOARDING", this.boardingPasses.get(n).getSerialNumber());
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                remoteViews.setOnClickFillInIntent(R.id.widget_mbp_left_arrow, intent);
            }
            if (n != this.boardingPasses.size() - 1) {
                final Intent intent2 = new Intent(this.context, (Class)AAppMbpWidgetProvider.class);
                intent2.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                intent2.putExtra("com.aa.android.appwidget.RIGHT", true);
                intent2.putExtra("com.aa.android.appwidget.BOARDING", this.boardingPasses.get(n).getSerialNumber());
                intent2.setData(Uri.parse(intent2.toUri(Intent.URI_INTENT_SCHEME)));
                remoteViews.setOnClickFillInIntent(R.id.widget_mbp_right_arrow, intent2);
                return;
            }
            remoteViews.setImageViewResource(R.id.widget_mbp_right_arrow, R.drawable.ic_circle_arrow_right_blue_disabled);
            remoteViews.setInt(R.id.widget_mbp_right_arrow, "setBackgroundColor", ContextCompat.getColor(this.context, R.color.abc_background_cache_hint_selector_material_light));
        }
    }
    
    private void setBoardingClickIntent(final RemoteViews remoteViews, final BoardingPass boardingPass, final boolean b) {
        final Intent intent = new Intent();
        if (b) {
            intent.putExtra("com.aa.android.appwidget.BOARDING", "com.aa.android.appwidget.MBP_CANCELED");
            remoteViews.setInt(R.id.widget_mbp_right_button, "setBackgroundColor", ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextColor(R.id.widget_mbp_right_button, ContextCompat.getColor(this.context, R.color.disabled_gray_text_color));
        }
        else {
            intent.putExtra("com.aa.android.appwidget.BOARDING", boardingPass.getSerialNumber());
        }
        remoteViews.setOnClickFillInIntent(R.id.widget_mbp_right_button, intent);
    }
    
    private void setFlightData(final RemoteViews remoteViews, final BoardingPass boardingPass, final boolean b) {
        remoteViews.setTextViewText(R.id.widget_mbp_flight_num, (CharSequence)this.context.getString(R.string.widget_mbp_flight, new Object[] { boardingPass.getFlight() }));
        remoteViews.setViewVisibility(R.id.widget_mbp_operator, 8);
        remoteViews.setTextViewText(R.id.widget_mbp_flight_from, (CharSequence)boardingPass.getDepartAirportCode());
        remoteViews.setTextViewText(R.id.widget_mbp_flight_dest, (CharSequence)boardingPass.getArriveAirportCode());
        remoteViews.setTextViewText(R.id.widget_mbp_flight_date, (CharSequence)boardingPass.getDepartDate());
        remoteViews.setTextViewText(R.id.widget_mbp_status_data, (CharSequence)boardingPass.getFlightStatus());
        if (b) {
            remoteViews.setTextColor(R.id.widget_mbp_flight_from, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextColor(R.id.widget_mbp_flight_dest, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextColor(R.id.widget_mbp_flight_date, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextViewText(R.id.widget_mbp_depart_data, (CharSequence)this.context.getString(R.string.widget_no_data));
            remoteViews.setTextColor(R.id.widget_mbp_depart_data, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextViewText(R.id.widget_mbp_gate_data, (CharSequence)this.context.getString(R.string.widget_no_data));
            remoteViews.setTextColor(R.id.widget_mbp_gate_data, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextViewText(R.id.widget_mbp_seat_data, (CharSequence)this.context.getString(R.string.widget_no_data));
            remoteViews.setTextColor(R.id.widget_mbp_seat_data, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            remoteViews.setTextViewText(R.id.widget_mbp_term_data, (CharSequence)this.context.getString(R.string.widget_no_data));
            remoteViews.setTextColor(R.id.widget_mbp_term_data, ContextCompat.getColor(this.context, R.color.american_medium_grey));
            return;
        }
        remoteViews.setTextViewText(R.id.widget_mbp_depart_data, (CharSequence)boardingPass.getDepartTime().toLowerCase());
        remoteViews.setTextViewText(R.id.widget_mbp_seat_data, (CharSequence)boardingPass.getSeat());
        if (boardingPass.isExitRow()) {
            remoteViews.setViewVisibility(R.id.widget_mbp_seat_data_subtext, 0);
        }
        else {
            remoteViews.setViewVisibility(R.id.widget_mbp_seat_data_subtext, 4);
        }
        if ("--".equals(boardingPass.getDepartTerminal()) || "".equals(boardingPass.getDepartTerminal())) {
            remoteViews.setViewVisibility(R.id.widget_mbp_terminal_column, 8);
        }
        else {
            remoteViews.setTextViewText(R.id.widget_mbp_term_data, (CharSequence)boardingPass.getDepartTerminal());
        }
        if ("--".equals(boardingPass.getGate()) || "".equals(boardingPass.getGate())) {
            remoteViews.setTextViewText(R.id.widget_mbp_gate_data, (CharSequence)this.context.getString(R.string.widget_no_data));
            return;
        }
        remoteViews.setTextViewText(R.id.widget_mbp_gate_data, (CharSequence)boardingPass.getGate());
    }
    
    private void setHeaderClickIntent(final RemoteViews remoteViews) {
        final Intent intent = new Intent();
        intent.putExtra("com.aa.android.appwidget.HEADER", true);
        remoteViews.setOnClickFillInIntent(R.id.widget_mbp_header, intent);
    }
    
    private void setMbpData(final RemoteViews remoteViews, final BoardingPass boardingPass, final boolean b) {
        this.setBoardingClickIntent(remoteViews, boardingPass, b);
        this.setAirportCodeClickIntent(remoteViews, R.id.widget_mbp_flight_from, boardingPass.getDepartAirportCode(), b, true, boardingPass.getGate());
        this.setAirportCodeClickIntent(remoteViews, R.id.widget_mbp_flight_dest, boardingPass.getArriveAirportCode(), b, false, "");
        this.setFlightData(remoteViews, boardingPass, b);
    }
    
    public int getCount() {
        return this.boardingPasses.size();
    }
    
    public long getItemId(final int n) {
        long n2 = 0L;
        if (n < this.getCount()) {
            n2 = this.boardingPasses.get(n).getSerialNumber().hashCode();
        }
        return n2;
    }
    
    public RemoteViews getLoadingView() {
        Log.d(MbpWidgetViewFactory.TAG, "getLoadingView()");
        return new RemoteViews(this.context.getPackageName(), R.layout.widget_mbp_loading);
    }
    
    public RemoteViews getViewAt(final int n) {
        final RemoteViews headerClickIntent = new RemoteViews(this.context.getPackageName(), R.layout.widget_mbp_item);
        this.getEmptyView(headerClickIntent);
        headerClickIntent.setViewVisibility(R.id.widget_mbp_left_button, 8);
        headerClickIntent.setViewVisibility(R.id.widget_mbp_button_spacing, 8);
        headerClickIntent.setViewVisibility(R.id.widget_mbp_flight_info, 0);
        headerClickIntent.setTextViewText(R.id.widget_mbp_right_button, (CharSequence)this.context.getString(R.string.boarding_pass));
        if (this.boardingPasses.size() > n) {
            final BoardingPass boardingPass = this.boardingPasses.get(n);
            final String upperCase = boardingPass.getFlightStatus().toUpperCase(Locale.US);

            if ("CANCELLED".equals(upperCase)) {
                this.setMbpData(headerClickIntent, boardingPass, true);
                headerClickIntent.setImageViewResource(R.id.widget_mbp_plane_icon, R.drawable.ic_airplane_slate_disabled);
                headerClickIntent.setTextColor(R.id.widget_mbp_status_data, ContextCompat.getColor(this.context, R.color.bright_red));
                headerClickIntent.setTextViewText(R.id.widget_mbp_status_data, (CharSequence)this.context.getString(R.string.Cancelled));
            }
            else {
                this.setMbpData(headerClickIntent, boardingPass, false);
                if ("ON TIME".equals(upperCase) || "ARRIVED".equals(upperCase)) {
                    headerClickIntent.setTextColor(R.id.widget_mbp_status_data, ContextCompat.getColor(this.context, R.color.american_green));
                }
                if ("DELAYED".equals(upperCase) || "DELAYED DEPARTURE".equals(upperCase) || "DELAYED ARRIVAL".equals(upperCase)) {
                    headerClickIntent.setTextColor(R.id.widget_mbp_status_data, ContextCompat.getColor(this.context, R.color.bright_red));
                    headerClickIntent.setTextViewText(R.id.widget_mbp_status_data, (CharSequence)this.context.getString(R.string.delayed));
                }
            }
        }
        this.setArrows(headerClickIntent, n);
        this.setHeaderClickIntent(headerClickIntent);
        return headerClickIntent;
    }
    
    public int getViewTypeCount() {
        return 2;
    }
    
    public boolean hasStableIds() {
        return true;
    }
    
    public void onCreate() {
        Log.d(MbpWidgetViewFactory.TAG, "onCreate()");
        this.boardingPasses = this.queryBoardingPasses();
    }
    
    public void onDataSetChanged() {
        Log.i(MbpWidgetViewFactory.TAG, "onDataSetChanged()");
        this.boardingPasses = this.queryBoardingPasses();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        /*final Locus instance = Locus.getInstance();
        instance.addListener((Locus.Listener)new Locus.SimpleListener() {
            public void onAirportVenuesLoaded(@NonNull final Map<String, VenueInfo> map) {
                instance.removeListener((Locus$Listener)this);
                MbpWidgetViewFactory.this.airportCodes = map;
                countDownLatch.countDown();
            }
        });
        instance.loadAirportVenues();*/
        /*loadRecyclerViewData();
        while(!reqComplete) { }
        reqComplete = false;*/
        countDownLatch.countDown();
        this.await(countDownLatch);

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
                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG);
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    
    public void onDestroy() {
        Log.d(MbpWidgetViewFactory.TAG, "onDestroy()");
    }

    class BoardingPass {
        String serialNumber;
        String flight;
        String departAirportCode;
        String arriveAirportCode;
        String departDate;
        String flightStatus;
        String departTime;
        String seat;

        boolean isExitRow;
        String departTerminal;
        String gate;

        public BoardingPass(String serialNumber, String flight, String departAirportCode, String arriveAirportCode, String departDate, String flightStatus, String departTime, String seat, boolean isExitRow, String departTerminal, String gate) {
            this.serialNumber = serialNumber;
            this.flight = flight;
            this.departAirportCode = departAirportCode;
            this.arriveAirportCode = arriveAirportCode;
            this.departDate = departDate;
            this.flightStatus = flightStatus;
            this.departTime = departTime;
            this.seat = seat;
            this.isExitRow = isExitRow;
            this.departTerminal = departTerminal;
            this.gate = gate;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getFlight() {
            return flight;
        }

        public String getDepartAirportCode() {
            return departAirportCode;
        }

        public String getArriveAirportCode() {
            return arriveAirportCode;
        }

        public String getDepartDate() {
            return departDate;
        }

        public String getFlightStatus() {
            return flightStatus;
        }

        public String getDepartTime() {
            return departTime;
        }

        public String getSeat() {
            return seat;
        }

        public boolean isExitRow() {
            return isExitRow;
        }

        public String getDepartTerminal() {
            return departTerminal;
        }

        public String getGate() {
            return gate;
        }
    }
}
