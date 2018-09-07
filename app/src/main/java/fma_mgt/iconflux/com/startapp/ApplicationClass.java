package fma_mgt.iconflux.com.startapp;

import android.app.Application;

import com.startapp.android.publish.StartAppSDK;

/**
 * Created by siddharth on 12/17/2015.
 */
public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StartAppSDK.init(this, "111685543", "211794599", true); //TODO: Replace with your IDs

    }
}
