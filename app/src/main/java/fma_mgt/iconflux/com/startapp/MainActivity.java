package fma_mgt.iconflux.com.startapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppAd.AdMode;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.NativeAdPreferences.NativeAdBitmapSize;
import com.startapp.android.publish.nativead.StartAppNativeAd;
import com.startapp.android.publish.splash.SplashConfig;
import com.startapp.android.publish.splash.SplashConfig.Theme;
import com.startapp.startappinterstitialexample.R;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    public enum vini {
        rom1, rom2, rom3

    }

    /**
     * StartAppAd object declaration
     */
    private StartAppAd startAppAd = new StartAppAd(this);


    /**
     * StartApp Native Ad declaration
     */
    private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
    private NativeAdDetails nativeAd = null;

    private ImageView imgFreeApp = null;
    private TextView txtFreeApp = null;

    /**
     * Native Ad Callback
     */
    private AdEventListener nativeAdListener = new AdEventListener() {

        @Override
        public void onReceiveAd(Ad ad) {

            // Get the native ad
            ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
            if (nativeAdsList.size() > 0) {
                nativeAd = nativeAdsList.get(0);
            }

            // Verify that an ad was retrieved
            if (nativeAd != null) {

                // When ad is received and displayed - we MUST send impression
                nativeAd.sendImpression(MainActivity.this);

                if (imgFreeApp != null && txtFreeApp != null) {

                    // Set button as enabled
                    imgFreeApp.setEnabled(true);
                    txtFreeApp.setEnabled(true);

                    // Set ad's image
                    imgFreeApp.setImageBitmap(nativeAd.getImageBitmap());

                    // Set ad's title
                    txtFreeApp.setText(nativeAd.getTitle());
                }
            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
            if (txtFreeApp != null) {
                txtFreeApp.setText("Error while loading Native Ad");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler ha = new Handler();

        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotification(new Date().getTime() + "");

                ha.postDelayed(this, 1000);
            }
        }, 5000);


        /** Create Splash Ad **/
        StartAppAd.showSplash(this, savedInstanceState,
                new SplashConfig()
                        .setTheme(Theme.OCEAN)
                        .setLogo(R.drawable.logo)
                        .setAppName("StartApp Example")
        );


        setContentView(R.layout.activity_main);

        /** Add Slider **/
        StartAppAd.showSlider(this);

        startAppNativeAd.loadAd(new NativeAdPreferences());



        /** Initialize Native Ad views **/
        imgFreeApp = (ImageView) findViewById(R.id.imgFreeApp);
        txtFreeApp = (TextView) findViewById(R.id.txtFreeApp);
        if (txtFreeApp != null) {
            txtFreeApp.setText("Loading Native Ad...");
        }

        /**
         * Load Native Ad with the following parameters:
         * 1. Only 1 Ad
         * 2. Download ad image automatically
         * 3. Image size of 150x150px
         */
        startAppNativeAd.loadAd(
                new NativeAdPreferences()
                        .setAdsNumber(1)
                        .setAutoBitmapDownload(true)
                        .setImageSize(NativeAdBitmapSize.SIZE150X150),
                nativeAdListener);
    }

    /**
     * Method to run when the next activity button is clicked.
     *
     * @param view
     */
    public void btnNextActivityClick(View view) {

        // Show an Ad
        startAppAd.showAd(new AdDisplayListener() {

            /**
             * Callback when Ad has been hidden
             * @param ad
             */
            @Override
            public void adHidden(Ad ad) {

                // Run second activity right after the ad was hidden
                Intent nextActivity = new Intent(MainActivity.this,
                        SecondActivity.class);
                startActivity(nextActivity);
            }

            /**
             * Callback when ad has been displayed
             * @param ad
             */
            @Override
            public void adDisplayed(Ad ad) {

            }

            /**
             * Callback when ad has been clicked
             * @param ad
             */
            @Override
            public void adClicked(Ad arg0) {

            }

            /**
             * Callback when ad not displayed
             * @param ad
             */
            @Override
            public void adNotDisplayed(Ad arg0) {

            }
        });
    }

    /**
     * Method to run when rewarded button is clicked
     *
     * @param view
     */
    public void btnShowRewardedClick(View view) {
        final StartAppAd rewardedVideo = new StartAppAd(this);

        sendNotification(new Date().getTime() + "");
        /**
         * This is very important: set the video listener to be triggered after video
         * has finished playing completely
         */
        /*rewardedVideo.setVideoListener(new VideoListener() {

            @Override
            public void onVideoCompleted() {
                Toast.makeText(MainActivity.this, "Rewarded video has completed - grant the user his reward", Toast.LENGTH_LONG).show();
            }
        });*/

        /**
         * Load rewarded by specifying AdMode.REWARDED
         * We are using AdEventListener to trigger ad show
         */
        rewardedVideo.loadAd(AdMode.FULLPAGE, new AdEventListener() {

            @Override
            public void onReceiveAd(Ad arg0) {
                rewardedVideo.showAd();
                sendNotification(new Date().getTime() + "");
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0) {
                /**
                 * Failed to load rewarded video:
                 * 1. Check that FullScreenActivity is declared in AndroidManifest.xml: 
                 * See https://github.com/StartApp-SDK/Documentation/wiki/Android-InApp-Documentation#activities
                 * 2. Is android API level above 16?
                 */
                Log.e("MainActivity", "Failed to load rewarded video with reason: " + arg0.getErrorMessage());
            }
        });
    }

    /**
     * Runs when the native ad is clicked (either the image or the title).
     *
     * @param view
     */
    public void freeAppClick(View view) {
        if (nativeAd != null) {
            nativeAd.sendClick(this);
        }
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here.
     */
    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here
     * for the home button exit ad integration.
     */
    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here
     * for the back button exit ad integration.
     */
    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }

    public void sendNotification(String title) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
//        builder.setSmallIcon(R.drawable.ic_launcher);

        // This intent is fired when notification is clicked
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://javatechig.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),
                0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your notification content here.");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Tap to view documentation about notifications.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }

}
