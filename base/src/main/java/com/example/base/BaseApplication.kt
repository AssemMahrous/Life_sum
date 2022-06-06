package com.example.base

import android.app.Application
import androidx.annotation.CallSuper
import com.example.base.di.KoinMainModules
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber


abstract class BaseApplication : Application() {
    abstract val modulesList: ArrayList<Module>
    abstract val interceptors: List<Interceptor>
    abstract val baseUrl: String
    lateinit var koin: KoinApplication private set

    private fun initKoin() {
        val modules = KoinMainModules.initApplicationModule(
            baseUrl,
            interceptors
        )

        modules.addAll(modulesList)

        koin = startKoin {
            // Koin Android logger
            androidLogger(Level.ERROR)
            //inject Android context
            androidContext(this@BaseApplication)
            modules(modules)
        }
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(2) // (Optional) How many method line to show. Default 2
            .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("Life_sum") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
            }
        })

        // If debug will add debug tree
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Logger.log(priority, tag, message, t)
            }
        })

        initKoin()
    }
}