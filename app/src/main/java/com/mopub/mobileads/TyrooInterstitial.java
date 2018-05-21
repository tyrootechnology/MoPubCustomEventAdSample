package com.mopub.mobileads;

import android.content.Context;
import android.util.Log;

import com.mopub.common.logging.MoPubLog;
import com.tyroo.tva.interfaces.TyrooAdListener;
import com.tyroo.tva.sdk.ErrorCode;
import com.tyroo.tva.sdk.TyrooVidAiSdk;

import java.util.Map;

public class TyrooInterstitial extends CustomEventInterstitial implements TyrooAdListener{

    public static final String TAG = "TyrooInterstitial";

    private static final String PLACEMENT_ID_KEY = "placement_id";
    private static final String PACKAGE_NAME_KEY = "package_name";
    private static final String VIDEO_CACHING_KEY = "enable_video_cache";

    private TyrooVidAiSdk tyrooVidAiSdk;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        //Log.d(TAG, "loadInterstitial: localExtras- "+localExtras.toString());
        //Log.d(TAG, "loadInterstitial: serverExtras- "+serverExtras.toString());

        MoPubLog.d("Loading Tyroo interstitial ad");

        mInterstitialListener = customEventInterstitialListener;

        final String placementId, packageName;
        if (serverExtrasAreValid(serverExtras)) {
            placementId = serverExtras.get(PLACEMENT_ID_KEY);
            packageName = serverExtras.get(PACKAGE_NAME_KEY);
        } else {
            if (mInterstitialListener != null) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            }
            return;
        }

        try {
            this.mInterstitialListener = customEventInterstitialListener;
            tyrooVidAiSdk = TyrooVidAiSdk.initialize(context,placementId, packageName, this);
            tyrooVidAiSdk.enableCaching(getLocalExtrasValue(localExtras));
            tyrooVidAiSdk.loadAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showInterstitial() {
        if (tyrooVidAiSdk != null && tyrooVidAiSdk.isAdLoaded()) {
            MoPubLog.d("Showing Tyroo interstitial ad.");
            tyrooVidAiSdk.showAds();
        } else {
            MoPubLog.d("Tried to show a Tyroo interstitial ad when it's not ready. Please try again.");
            if (mInterstitialListener != null) {
                onFailure(ErrorCode.UNKNOWN, "No Ads found.");
            } else {
                MoPubLog.d("Interstitial listener not instantiated. Please load interstitial again.");
            }
        }

    }

    @Override
    protected void onInvalidate() {
        if (tyrooVidAiSdk != null) {
            tyrooVidAiSdk.flush();
            tyrooVidAiSdk = null;
            mInterstitialListener = null;
            Log.d(TAG, "tyrooVidAiSdk: "+tyrooVidAiSdk);
        }
    }

    @Override
    public void onAdLoaded(String placementId) {
        MoPubLog.d("Tyroo interstitial ad loaded successfully.");
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onAdDisplayed() {
        MoPubLog.d("Showing Tyroo interstitial ad.");
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialShown();
        }
    }

    @Override
    public void onAdOpened() {
        MoPubLog.d("Tyroo interstitial ad opened.");
    }

    @Override
    public void onAdClosed() {
        MoPubLog.d("Tyroo interstitial ad closed.");
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }

    @Override
    public void onAdCompleted() {
        MoPubLog.d("Tyroo interstitial ad completed.");
    }

    @Override
    public void onAdClicked() {
        MoPubLog.d("Tyroo interstitial ad clicked.");
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialClicked();
        }
    }

    @Override
    public void onAdLeftApplication() {
        MoPubLog.d("Tyroo interstitial ad left application.");
        if (mInterstitialListener != null) {
            mInterstitialListener.onLeaveApplication();
        }
    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {
        MoPubLog.d("Tyroo interstitial ad failed to load: "+errorMsg);
        switch (errorCode){
            case ErrorCode.BAD_REQUEST:
                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
                }
                break;
            case ErrorCode.NETWORK_ERROR:
                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
                }
                break;
            case ErrorCode.NO_INVENTORY:
                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
                }
                break;
            case ErrorCode.UNKNOWN:
                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
                }
                break;
            default:

        }
    }

    private boolean serverExtrasAreValid(final Map<String, String> serverExtras) {
        if (!serverExtras.containsKey(PLACEMENT_ID_KEY) && !serverExtras.containsKey(PACKAGE_NAME_KEY)){
            MoPubLog.e("Placement id or Package name key do not match with server extras");
            return false;
        }
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        final String pkgName = serverExtras.get(PACKAGE_NAME_KEY);
        return (placementId != null && placementId.length() > 0 && pkgName != null && pkgName.length() > 0);
    }

    private boolean getLocalExtrasValue(final Map<String, Object> localExtras) {
        if (!localExtras.containsKey(VIDEO_CACHING_KEY)){
            MoPubLog.e("Enable video caching key do not match with local extras");
            return false;
        }
        final Boolean isVideoCacheEnabled = (Boolean) localExtras.get(VIDEO_CACHING_KEY);
        return isVideoCacheEnabled;
    }
}

