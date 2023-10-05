package com.example.channels

import android.app.Application
import com.yandex.appmetrica.push.firebase.FirebasePushServiceControllerProvider
import com.yandex.appmetrica.push.hms.HmsPushServiceControllerProvider
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Creating an extended library configuration.
        val config = AppMetricaConfig.newConfigBuilder("005f0b9e-8188-47bc-a5ea-db3c6c66e254").build()
        // Initializing the AppMetrica SDK.
        AppMetrica.activate(this, config)
        YandexMetricaPush.init(
            applicationContext,
            FirebasePushServiceControllerProvider(this),
            HmsPushServiceControllerProvider(this)
        )
    }
}