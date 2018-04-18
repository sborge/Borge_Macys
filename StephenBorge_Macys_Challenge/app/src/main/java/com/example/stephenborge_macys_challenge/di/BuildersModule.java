package com.example.stephenborge_macys_challenge.di;

import com.example.stephenborge_macys_challenge.main.MainActivity;
import com.example.stephenborge_macys_challenge.main.MainModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity bindMainActivity();
}
