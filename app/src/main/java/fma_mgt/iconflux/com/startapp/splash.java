package fma_mgt.iconflux.com.startapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.SDKAdPreferences;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.StartAppNativeAd;
import com.startapp.startappinterstitialexample.R;

import java.util.ArrayList;
import java.util.Iterator;

public class splash extends Activity {
    private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
    private NativeAdDetails nativeAd = null;
    ImageView splashimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashimage = (ImageView) findViewById(R.id.splashimage);

        StartAppSDK.init(this,
                "Your App ID",
                new SDKAdPreferences()
                        .setAge(35)
                        .setGender(SDKAdPreferences.Gender.FEMALE));


        NativeAdPreferences nativePrefs = new NativeAdPreferences()
                .setAdsNumber(3)                // Load 3 Native Ads
                .setAutoBitmapDownload(true)    // Retrieve Images object
                .setImageSize(NativeAdPreferences.NativeAdBitmapSize.SIZE340X340);

// Declare Ad Callbacks Listener
        AdEventListener adListener = new AdEventListener() {     // Callback Listener
            @Override
            public void onReceiveAd(Ad arg0) {
                // Native Ad received
                ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();    // get NativeAds list

                if (ads.size() > 0) {
                    nativeAd = ads.get(0);
                }
                // Print all ads details to log
                Iterator<NativeAdDetails> iterator = ads.iterator();
                while (iterator.hasNext()) {
                    Log.d("MyApplication", iterator.next().toString());
                    if (nativeAd != null) {

                        // When ad is received and displayed - we MUST send impression
                        nativeAd.sendImpression(splash.this);

                        // Set ad's image
                        splashimage.setImageBitmap(nativeAd.getImageBitmap());
                        Log.e("MyApplication", "Error while loading Ad");

                    }

                }
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0) {
                // Native Ad failed to receive
                Log.e("MyApplication", "Error while loading Ad");
            }
        };


        startAppNativeAd.loadAd(nativePrefs, adListener);

    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
