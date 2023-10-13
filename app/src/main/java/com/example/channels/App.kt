package com.example.channels

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val config: YandexMetricaConfig = YandexMetricaConfig.newConfigBuilder("005f0b9e-8188-47bc-a5ea-db3c6c66e254").build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)

        YandexMetricaPush.init(this)
    }
}