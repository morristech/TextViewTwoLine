package com.example.kimcy929.textviewtwoline;

import android.app.Application;

import com.kimcy929.localeutils.LocaleUtils;

import java.util.Locale;

import timber.log.Timber;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //Locale locale = new Locale("ar"); RTL
        Locale locale = new Locale("en"); //The default is LTR

        LocaleUtils.setLocale(locale);
    }
}
