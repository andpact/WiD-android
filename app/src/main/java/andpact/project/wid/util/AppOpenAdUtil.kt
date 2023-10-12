package andpact.project.wid.util

import andpact.project.wid.activity.MainActivity
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*

private const val LOG_TAG = "WiD AppOpenAdUtil"
//private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294" // 앱 오프닝 광고 단위 ID 테스트 용
private const val AD_UNIT_ID = "ca-app-pub-3641806776840744/6249474966" // 앱 오프닝 광고 단위 ID
class AppOpenAdUtil : Application(), Application.ActivityLifecycleCallbacks, LifecycleEventObserver {
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() { // 앱이 처음 생성될 때 실행되는 메서드.
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        Log.d(LOG_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

//    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START) // 동작 안하는데 이거?
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

        // 홈버튼 누른 후 다시 실행했을 때
//        if (event == Lifecycle.Event.ON_RESUME) {
//            currentActivity?.let {
//                appOpenAdManager.loadAd(it)
//            }
//        }
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

    private fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
//        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)

        appOpenAdManager.loadAd(this)
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onActivityCreated - 1")
    }
    override fun onActivityStarted(activity: Activity) {
        // Updating the currentActivity only when an ad is not showing.
        if (!appOpenAdManager.isShowingAd) {
            Log.d(LOG_TAG, "onActivityStarted - 1")
            currentActivity = activity
        }
    }
    override fun onActivityResumed(activity: Activity) {
        Log.d(LOG_TAG, "onActivityResumed - 1")
    }
    override fun onActivityPaused(activity: Activity) {
        Log.d(LOG_TAG, "onActivityPaused - 1")
    }
    override fun onActivityStopped(activity: Activity) {
        Log.d(LOG_TAG, "onActivityStopped - 1")
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
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
                Log.d(LOG_TAG, "loadAd : return")
                return
            }

            Log.d(LOG_TAG, "loadAd : Will load AD")

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context, AD_UNIT_ID, request,
//                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAdLoadCallback() {
                    // 광고는 비동기로 호출되므로 광고가 호출되는 동안 다른 메서드들이 실행됨.
                    override fun onAdLoaded(ad: AppOpenAd) {
                        // 광고가 로드 되지 않는 상황.
//                        onAdFailedToLoad(LoadAdError(3, "TEST : Ad not loaded", "", null, null))
//                        return
                        // Called when an app open ad has loaded.
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(LOG_TAG, "onAdLoaded : Ad was loaded.")

                        // 광고가 비동기로 로드되니까 광고 로드 후 화면에 띄움
                        appOpenAdManager.showAdIfAvailable(currentActivity!!)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Called when an app open ad has failed to load.
                        isLoadingAd = false
                        Log.d(LOG_TAG, "onAdFailedToLoad : " + loadAdError.message)

                        appOpenAdManager.showAdIfAvailable(currentActivity!!)
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
            val isAvailable = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
            Log.d(LOG_TAG, "isAdAvailable: $isAvailable")
            return isAvailable
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
                Log.d(LOG_TAG, "showAdIfAvailable : The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "showAdIfAvailable : The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()

                // 광고를 표시할 수 없을 때 메인 액티비티로 전환시킴.
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()

//                loadAd(activity)
                return
            }

            Log.d(LOG_TAG, "showAdIfAvailable : Will show ad.")

            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "Ad dismissed fullscreen content.")

                    onShowAdCompleteListener.onShowAdComplete()
//                    loadAd(activity)

                    // 광고를 닫으면 메인 화면으로 전환.
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent : " + adError.message)

                    onShowAdCompleteListener.onShowAdComplete()
//                    loadAd(activity)

                    // 광고를 가져왔지만 못 보여주면 메인 화면으로 전환.
//                    val intent = Intent(activity, MainActivity::class.java)
//                    activity.startActivity(intent)
//                    activity.finish()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d(LOG_TAG, "Ad showed fullscreen content.")
                }
            }
            isShowingAd = true
            Log.d(LOG_TAG, "showAdIfAvailable : show app open ads")
            appOpenAd!!.show(activity)
        }
    }
}