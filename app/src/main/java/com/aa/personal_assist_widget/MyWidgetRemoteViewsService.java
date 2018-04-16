package com.aa.personal_assist_widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by by kummukes on 4/4/2018.
 */

public class MyWidgetRemoteViewsService extends RemoteViewsService {
    private static final String TAG = "WidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
