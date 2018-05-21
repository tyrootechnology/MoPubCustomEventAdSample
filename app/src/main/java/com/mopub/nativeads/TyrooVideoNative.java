package com.mopub.nativeads;

import android.content.Context;
import android.view.View;

import com.mopub.common.logging.MoPubLog;
import com.tyroo.tva.interfaces.TyrooAdListener;
import com.tyroo.tva.sdk.AdView;
import com.tyroo.tva.sdk.ErrorCode;
import com.tyroo.tva.sdk.TyrooVidAiSdk;

import java.util.Map;

/**
 * TyrooVideoAdRenderer is also necessary in order to show video ads.
 * Tyroo supports only video native ads.
 */

public class TyrooVideoNative extends CustomEventNative {
    private static final String PLACEMENT_ID_KEY = "placement_id";
    private static final String PACKAGE_NAME_KEY = "package_name";
    private static final String VIDEO_CACHING_KEY = "enable_video_cache";
    private static final String VIDEO_ENABLED_KEY = "video_enabled";

    /**
     * Tyroo support only native video ads, so you have to set it true. This value is overridden with
     * server extras.
     */
    private static boolean VIDEO_ENABLED = true;

    // CustomEventNative implementation
    @Override
    protected void loadNativeAd(final Context context,
                                final CustomEventNativeListener customEventNativeListener,
                                final Map<String, Object> localExtras,
                                final Map<String, String> serverExtras) {

        final String placementId, packageName;
        final Boolean enableVideoCaching;

        if (serverExtrasAreValid(serverExtras)) {
            placementId = serverExtras.get(PLACEMENT_ID_KEY);
            packageName = serverExtras.get(PACKAGE_NAME_KEY);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final String videoEnabledString = serverExtras.get(VIDEO_ENABLED_KEY);
        boolean videoEnabledFromServer = Boolean.parseBoolean(videoEnabledString);

        if (shouldUseVideoEnabledNativeAd(videoEnabledString, videoEnabledFromServer)) {
            enableVideoCaching = getLocalExtrasValue(localExtras);
            final TyrooVideoNativeAd tyrooVideoNativeAd = new TyrooVideoNativeAd(context, placementId, packageName, enableVideoCaching, customEventNativeListener);
            tyrooVideoNativeAd.loadAd();
        } else {
            MoPubLog.e("Tyroo supports only video native ads.");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    static boolean shouldUseVideoEnabledNativeAd(final String videoEnabledString, final boolean videoEnabledFromServer) {
        return (videoEnabledString != null && videoEnabledFromServer) ||
                (videoEnabledString == null && VIDEO_ENABLED);
    }


    private boolean serverExtrasAreValid(final Map<String, String> serverExtras) {
        if (!serverExtras.containsKey(PLACEMENT_ID_KEY) && !serverExtras.containsKey(PACKAGE_NAME_KEY)) {
            MoPubLog.e("Placement id or Package name key do not match with server extras");
            return false;
        }
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
        final String pkgName = serverExtras.get(PACKAGE_NAME_KEY);
        return (placementId != null && placementId.length() > 0 && pkgName != null && pkgName.length() > 0);
    }

    private boolean getLocalExtrasValue(final Map<String, Object> localExtras) {
        if (!localExtras.containsKey(VIDEO_CACHING_KEY)) {
            MoPubLog.e("Enable video caching key do not match with local extras");
            return false;
        }
        final Boolean isVideoCacheEnabled = (Boolean) localExtras.get(VIDEO_CACHING_KEY);
        return isVideoCacheEnabled;
    }


    static class TyrooVideoNativeAd extends BaseNativeAd implements TyrooAdListener {

        private final Context mContext;
        private final String placementId, packageName;
        private final TyrooVidAiSdk tyrooNativeAd;
        private final CustomEventNativeListener mCustomEventNativeListener;


        TyrooVideoNativeAd(final Context context, final String plaId, final String pkgName, Boolean enableVideoCaching, final CustomEventNativeListener customEventNativeListener) {
            mContext = context.getApplicationContext();
            placementId = plaId;
            packageName = pkgName;
            mCustomEventNativeListener = customEventNativeListener;
            tyrooNativeAd = TyrooVidAiSdk.initialize(mContext, placementId, packageName, this);
            tyrooNativeAd.enableCaching(enableVideoCaching);
        }

        void loadAd() {
            tyrooNativeAd.loadAds();
        }

        public void showAd(View view) {
            tyrooNativeAd.setAdViewLayout((AdView) view);
            if (tyrooNativeAd.isAdLoaded()) {
                tyrooNativeAd.showAds();
            }else {
                MoPubLog.d("Unable to load Tyroo Native Ad.");
            }
        }
        // BaseForwardingNativeAd
        @Override
        public void prepare(final View view) {
            MoPubLog.d("prepare");
        }

        @Override
        public void clear(final View view) {

        }

        @Override
        public void destroy() {
            MoPubLog.d("Tyroo Video Native ad destroyed");
            tyrooNativeAd.flush();
        }

        @Override
        public void onAdLoaded(String placementId) {
            MoPubLog.d("Tyroo Video Native ad loaded");
            mCustomEventNativeListener.onNativeAdLoaded(TyrooVideoNativeAd.this);
        }

        @Override
        public void onAdDisplayed() {
            MoPubLog.d("Tyroo Video Native Ad Displayed.");
        }

        @Override
        public void onAdOpened() {
            notifyAdImpressed();
        }

        @Override
        public void onAdClosed() {

        }

        @Override
        public void onAdCompleted() {

        }

        @Override
        public void onAdClicked() {
            notifyAdClicked();
        }

        @Override
        public void onAdLeftApplication() {

        }

        @Override
        public void onFailure(int errorCode, String errorMsg) {
            MoPubLog.d("Tyroo native video ad failed to load: " + errorMsg);
            switch (errorCode) {
                case ErrorCode.BAD_REQUEST:
                    if (mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.INVALID_REQUEST_URL);
                    }
                    break;
                case ErrorCode.NETWORK_ERROR:
                    if (mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_REQUEST);
                    }
                    break;
                case ErrorCode.NO_INVENTORY:
                    if (mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
                    }
                    break;
                case ErrorCode.UNKNOWN:
                    if (mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
                    }
                    break;
                default:

            }
        }
    }
}
