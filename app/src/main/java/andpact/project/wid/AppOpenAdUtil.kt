package andpact.project.wid

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*

private const val LOG_TAG = "WiD AppOpenAdUtil"
private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294" // 테스트 용 오프닝 광고 ID
class AppOpenAdUtil : Application(), Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        // Log the Mobile Ads SDK version.
        Log.d(LOG_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground() {
//        // Show the ad (if available) when the app moves to foreground.
//        currentActivity?.let {
//            appOpenAdManager.showAdIfAvailable(it)
//        }
//    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        if (event == Lifecycle.Event.ON_START) {
//            currentActivity?.let {
//                appOpenAdManager.showAdIfAvailable(it)
//            }
//        }
    }

//    /** Show the ad if one isn't already showing. */
//    fun showAdIfAvailable(activity: Activity) {
//        showAdIfAvailable(
//            activity,
//            object : OnShowAdCompleteListener {
//                override fun onShowAdComplete() {
//                    // Empty because the user will go back to the activity that shows the ad.
//                }
//            })
//    }
//
//    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
//        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
//        // class.
//        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
//    }

    companion object {
        // Add a boolean flag to track whether the app is in the foreground.
        private var isAppInForeground = false
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { // 얘도 앱 전환 시 동작하는데?
        Log.d(LOG_TAG, "onActivityCreated - 1")
        if (currentActivity === activity) {
            Log.d(LOG_TAG, "onActivityCreated - 2")
        }
    }
    override fun onActivityStarted(activity: Activity) { // 얘도 앱 전환 시 동작하는데?
        Log.d(LOG_TAG, "onActivityStarted - 1")
        // Updating the currentActivity only when an ad is not showing.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
            Log.d(LOG_TAG, "onActivityStarted - 2")
            appOpenAdManager.showAdIfAvailable(activity)
        }
    }
    override fun onActivityResumed(activity: Activity) { // 앱 전환 시 동작
        Log.d(LOG_TAG, "onActivityResumed - 1")
        // Check if the resumed activity is the same as the current activity.
        if (currentActivity === activity) {
            // Show the ad if the app is in the foreground.
            Log.d(LOG_TAG, "onActivityResumed - 2")
//            appOpenAdManager.showAdIfAvailable(activity)
        }
    }
    override fun onActivityPaused(activity: Activity) {
        Log.d(LOG_TAG, "onActivityPaused - 1")
    }
    override fun onActivityStopped(activity: Activity) {
        Log.d(LOG_TAG, "onActivityStopped - 1")
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        Log.d(LOG_TAG, "onActivityDestroyed - 1")
    }

    /** Interface definition for a callback to be invoked when an app open ad is complete. */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /** Request an ad. */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context, AD_UNIT_ID, request,
//                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        // Called when an app open ad has loaded.
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(LOG_TAG, "Ad was loaded.")
//                        Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show()

                        // Now that the ad is loaded, you can check if the app is in the foreground
                        // and show the ad accordingly.
                        if (isAppInForeground) {
                            showAdIfAvailable(currentActivity!!)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Called when an app open ad has failed to load.
                        isLoadingAd = false
                        Log.d(LOG_TAG, "onAdFailedToLoad : " + loadAdError.message)
//                        Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3_600_000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /** Show the ad if one isn't already showing. */
        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                }
            )
        }

        /** Shows the ad if one isn't already showing. */
        fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            Log.d(LOG_TAG, "Will show ad.")

            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when full screen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "Ad dismissed fullscreen content.")
        //                        Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show()

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when fullscreen content failed to show.
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent : " + adError.message)
        //                        Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show()

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d(LOG_TAG, "Ad showed fullscreen content.")
        //                        Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show()
                }
            }
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }
}