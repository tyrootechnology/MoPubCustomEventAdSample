package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.tyroo.tva.sdk.AdView;

import java.util.WeakHashMap;

/**
 * Include this class if you want to use Tyroo native video ads. This renderer handles Tyroo
 * video native ads.
 * Certified with Tyroo Ad Network 1.1.7
 */
public class TyrooVideoAdRenderer implements MoPubAdRenderer<TyrooVideoNative.TyrooVideoNativeAd> {
    private final ViewBinder mViewBinder;

    /**
     * A weak hash map used to keep track of view holder so that the views can be properly recycled.
     */
    private final WeakHashMap<View, TyrooNativeViewHolder> mViewHolderMap;

    public TyrooVideoAdRenderer(final ViewBinder viewBinder) {
        mViewBinder = viewBinder;
        this.mViewHolderMap = new WeakHashMap<>();
    }

    @Override
    public View createAdView(final Context context, final ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(mViewBinder.layoutId, parent, false);
        return view;
    }

    @Override
    public void renderAdView(final View view, final TyrooVideoNative.TyrooVideoNativeAd nativeAd) {
        TyrooNativeViewHolder viewHolder = mViewHolderMap.get(view);
        if (viewHolder == null) {
            viewHolder = TyrooNativeViewHolder.fromViewBinder(view, mViewBinder);
            mViewHolderMap.put(view, viewHolder);
        }

        if (view instanceof AdView){
            nativeAd.showAd(view);
        }else {
            MoPubLog.e("Required Tyroo com.tyroo.tva.sdk.AdView instance as root element.");
        }
    }

    @Override
    public boolean supports(final BaseNativeAd nativeAd) {
        Preconditions.checkNotNull(nativeAd);
        return nativeAd instanceof TyrooVideoNative.TyrooVideoNativeAd;
    }

    private static class TyrooNativeViewHolder {
        @Nullable
        View mMainView;

        private static final TyrooNativeViewHolder EMPTY_VIEW_HOLDER =
                new TyrooNativeViewHolder();

        @NonNull
        public static TyrooNativeViewHolder fromViewBinder(@NonNull View view,
                                                           @NonNull ViewBinder viewBinder) {
            try {
                final TyrooNativeViewHolder viewHolder = new TyrooNativeViewHolder();
                viewHolder.mMainView = view;

                return viewHolder;
            } catch (ClassCastException exception) {
                MoPubLog.w("Could not cast from id in ViewBinder to expected View type", exception);
                return EMPTY_VIEW_HOLDER;
            }
        }
    }


}