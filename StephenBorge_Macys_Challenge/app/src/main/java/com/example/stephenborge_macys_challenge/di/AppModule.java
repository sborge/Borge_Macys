package com.example.stephenborge_macys_challenge.di;

import android.content.Context;

import com.example.stephenborge_macys_challenge.App;

import dagger.Module;
import dagger.Provides;

/**
 * This is where I would inject application-wide dependencies. (with more time).
 */
@Module
public class AppModule {

    @Provides
    Context provideContext(App application) {
        return application.getApplicationContext();
    }
}
