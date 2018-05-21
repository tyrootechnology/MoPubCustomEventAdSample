package com.tyroo.mopubadsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.MoPubVideoNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.TyrooVideoAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MoPubInterstitial.InterstitialAdListener {

    private static final String TAG = "MainActivity";

    private static final String AD_UNIT_ID_INTERSTITIAL = "932f8a97b1984eefb6ccd36c924ffebe";
    private static final String AD_UNIT_ID_NATIVE = "9501f99b159849f98228f40ab274397d";

    private Button btnInterstitial, btnNative;
    private MoPubInterstitial moPubInterstitial;
    private MoPubNative moPubNative;
    private RelativeLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnInterstitial = findViewById(R.id.btn_interstitial);
        btnNative = findViewById(R.id.btn_native_ad);
        adView = findViewById(R.id.native_video_ad);

        btnInterstitial.setOnClickListener(this);
        btnNative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_interstitial){
            loadInterstitialAd();
        }
        if (v.getId() == R.id.btn_native_ad){
            loadNativeAd();
        }

    }

    private void loadInterstitialAd() {

        /* Publishers can pass additional data in form of Map.
        * You can get this data in localExtras inside Interstitial Custom Event class #TyrooInterstitial.class.
        * If publisher wants to enable or disable video file caching, he can pass the boolean flag in this localExtras Map.
        * Make sure the kay value will match with VIDEO_CACHING_KEY in TyrooInterstital.class file*/

        Map<String, Object> enableVideoCachingMap = new HashMap<>();
        enableVideoCachingMap.put("enable_video_cache", true);

        moPubInterstitial = new MoPubInterstitial(this, AD_UNIT_ID_INTERSTITIAL);
        moPubInterstitial.setInterstitialAdListener(this);
        moPubInterstitial.setLocalExtras(enableVideoCachingMap);
        moPubInterstitial.load();
    }

    private void loadNativeAd() {
        moPubNative = new MoPubNative(this, AD_UNIT_ID_NATIVE, moPubNativeListener );

        // Set up a renderer for a static native ad.
        final MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_ad_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mainImageId(R.id.native_main_image)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .build()
        );

        // Set up tryoo video native ad ViewBinder
        ViewBinder binder = new ViewBinder.Builder(R.layout.tyroo_video_native_ad)
                .build();

        final TyrooVideoAdRenderer tyrooVideoAdRenderer = new TyrooVideoAdRenderer(binder);

        /* Publishers can pass additional data in form of Map.
         * You can get this data in localExtras inside Native Custom Event class #TyrooVideoNative.class.
         * If publisher wants to enable or disable video file caching, he can pass the boolean flag in this localExtras Map.
         * Make sure the kay value will match with VIDEO_CACHING_KEY in TyrooVideoNative.class file*/

        Map<String, Object> enableVideoCachingMap = new HashMap<>();
        enableVideoCachingMap.put("enable_video_cache", true);

        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        moPubNative.registerAdRenderer(tyrooVideoAdRenderer);
        moPubNative.setLocalExtras(enableVideoCachingMap);
        moPubNative.makeRequest();
    }

    MoPubNative.MoPubNativeNetworkListener moPubNativeListener = new MoPubNative.MoPubNativeNetworkListener() {
        @Override
        public void onNativeLoad(NativeAd nativeAd) {
            Log.d(TAG, "onNativeLoad");
            Log.d(TAG, "rendrer: "+nativeAd.getMoPubAdRenderer());

            View mView = nativeAd.createAdView(getApplicationContext(), adView);
            nativeAd.clear(mView);

            nativeAd.renderAdView(mView);
            nativeAd.prepare(mView);

            adView.removeAllViews();
            adView.addView(mView);
        }

        @Override
        public void onNativeFail(NativeErrorCode errorCode) {
            Log.e(TAG, "onNativeFail: "+errorCode);
        }
    };

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        if (moPubInterstitial.isReady()) {
            moPubInterstitial.show();
        } else {
            Log.d(TAG, "moPubInterstitial not ready");
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Log.e(TAG, "onInterstitialFailed: " + errorCode);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (moPubInterstitial != null) {
            moPubInterstitial.destroy();
        }
        if (moPubNative != null){
            moPubNative.destroy();
        }

    }
}
