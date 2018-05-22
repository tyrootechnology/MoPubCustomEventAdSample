# Integrating MoPub + Tyroo SDK v1.1.7 (Android)

This guide will instruct you step-by-step on how to serve Tyroo Native video and Interstitial video ads using MoPub mediation network.

This sample app included Tyroo Video Ad SDK v1.1.7, MoPub SDK v5.0.0 and Custom Event classes for Interstitial Ad and Native Ad.

### NOTES
> The code samples in this document can be copy/pasted into your source code.

> Tyroo support only Interstitial ads or native video ads via MobPub Mediation Network.

> If you have any questions, contact us via support@tyroo.com

## Before You Begin
* The Tyroo Android SDK has been tested with MoPub 5.0.0 and therefore should only be integrated with MoPub 5.0.0 or above.
* Read out Tyroo SDK setup offical documentation [here](https://github.com/tyrootechnology/vid.ai-app)
* The MoPub Dashboard does not include Tyroo SDK Adapters in their mediation network list yet, so you have to write custom event classes for Interstitial Ads and Native Ads.
You can copy/paste `com.mopub.mobileads` or `com.mopub.nativeads` packages from the above source code.
* MoPub must be set up in your app before starting this tutorial. For a step-by-step guide, refer to [MoPub’s Getting Started Guide for Android](https://developers.mopub.com/docs/android/getting-started/).
* You will need Tyroo Placement and Package Name reference IDs to complete mediation setup with MoPub. You can find these IDs in the Tyroo Dashboard (or contact support@tyroo.com).
* Read out Tyroo Interstitial Ad Setup official documentation [here](https://github.com/tyrootechnology/vid.ai-app/wiki/Interstitial-Video)
* Read out Tyroo Native Video Ad Setup official documentation [here](https://github.com/tyrootechnology/vid.ai-app/wiki/In-Feed-Video)

## Set up Tyroo Custom Event with MoPub Mediation Network
In this configuration, your application communicate with the Mopub SDK, which in turn requests content from the Tyroo SDK.
Native or Interstitial information is passed directly from Tyroo’s SDK to your application.
Here is a diagram showing the flow of information in a Tyroo-Mopub custom event mediation:


![MoPubMediation](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/admob_mediation.png | width="100" height="100") 

**Step 1: Tyroo and MoPub SDK Setup in your App**

In your application project root `build.gradle` file, make sure jCenter repository added:

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/screen_gradle_root.png)

Add Tyroo and MoPub SDK dependency to your application project build.gradle:

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/screen_gradle.png)

**Step 2: Adding Custom Events in app project**

Custom events allow you to support native ad networks not bundled with the MoPub SDK, or to execute any of your application code from the MoPub web interface.

To show Tyroo Interstitial Ad via MoPub mediation network you need to write custom event classes inside `/src/main/java/com/mopub/mobileads/` directory in your app’s project.

Similarly, to show Tyroo Native Video Ads via MoPub mediation network you need to write custom event classes inside '/src/main/java/com/mopub/nativeads/' directory in your app's project.

In this sample project, we have already written custom events for your reference. You can copy/paste these custom events packages into your project.

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/mopub_custom_event.png)

**Step 3: Adding Custom Evets MoPub Dashboard**

1. Login into your MoPub account
2. Navigate to "Networks" tab and click "Add a Network"

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/dashboard_one.png)

3. Scroll down to Custom SDK Network and Select:

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/dashboard_two.png)

4. Fill out the network details and click next button:
5. Inside App and Ad Unit Setup: 

- If you are using Interstitial Ad Unit, add `com.mopub.mobileads.TyrooInterstitial` under the Custom Event Class section.
- If you are using Native Ad Unit, add `com.mopub.nativeads.TyrooVideoNative` under the Custom Event Class section.
- Add your Tyroo Placement ID​ and Package Name, and all extra Reference IDs​ in JSON format under the Custom Event Class Data(Both for Interstial and Native Ad Unit)​.
- Data entered in JSON format will receive in custom event classes in serverExtra `Map<String, Object>` format.
- Make sure you have got the valid placement ID and package name from Tyroo Support Team.

![](https://github.com/tyrootechnology/MoPubCustomEventAdSample/blob/master/screenshots/dashboard_three.png)

6. Click Save and Close.

**Step 4: App Level Configuration and set localExtras(Map<String, Object>) for enable video caching**

Tyroo support only video ads and publisher can cache these videos data to maximize the impression. To enable video caching, publisher
need to enable cache(true/false). In case of mediation, publisher can enable/disable video caching at code level whenever request for an
ad using `setLocalExtras(Map<String, Object> extras)` method.

For Example: 

```java
private void loadInterstitialAd() {

        Map<String, Object> enableVideoCachingMap = new HashMap<>();
        enableVideoCachingMap.put("enable_video_cache", true);

        moPubInterstitial = new MoPubInterstitial(this, AD_UNIT_ID_INTERSTITIAL);
        moPubInterstitial.setInterstitialAdListener(this);
        moPubInterstitial.setLocalExtras(enableVideoCachingMap);
        moPubInterstitial.load();
    }
```

```java
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

        Map<String, Object> enableVideoCachingMap = new HashMap<>();
        enableVideoCachingMap.put("enable_video_cache", true);

        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        moPubNative.registerAdRenderer(tyrooVideoAdRenderer);
        moPubNative.setLocalExtras(enableVideoCachingMap);
        moPubNative.makeRequest();
    }
```

You’re finished! Be sure to test your Mobpub – Tyroo mediation implementation to be sure everything is functioning correctly before you release your application to the app store.