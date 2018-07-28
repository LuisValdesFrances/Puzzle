package puzzle.dam.luis.com.puzzle;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import puzzle.dam.luis.com.puzzle.com.dam.game.Puzzle;

public class MainActivity extends Activity{

    Handler handler;
    Puzzle puzzle;

    private InterstitialAd mInterstitialAd;

    public static final String ADMOB_ID = "ca-app-pub-9871578065265688~4227977718";
    public static final String INTERSTITIAL_ID = "ca-app-pub-9871578065265688/8690677342";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, ADMOB_ID);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INTERSTITIAL_ID);
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT){

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        puzzle = new Puzzle(this);

        setContentView(puzzle);


        Thread gameLoop = new Thread(puzzle);
        gameLoop.start();




        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
               // mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });


        /*
        handler=new Handler();
        handler.postDelayed(GameLoop,1);
        */

    }
    /*
    public Runnable GameLoop = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(GameLoop,1);
            puzzle.run();
        }
    };
    */

    public void loadInterstitial(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

     public void requestInterstitial() {
         runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                     mInterstitialAd.show();
                }
             }
         });
     }
}
