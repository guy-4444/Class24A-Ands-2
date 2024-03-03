package com.guy.class24a_ands_2;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MCT5.initHelper();
    }
}
