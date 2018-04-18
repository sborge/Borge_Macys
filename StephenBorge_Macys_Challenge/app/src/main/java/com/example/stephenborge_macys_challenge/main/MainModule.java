package com.example.stephenborge_macys_challenge.main;

import com.example.stephenborge_macys_challenge.rx.SchedulersFacade;

import dagger.Module;
import dagger.Provides;

/**
 * Define MainActivity-specific dependencies here.
 */
@Module
public class MainModule {

    @Provides
    MainViewModelFactory provideLobbyViewModelFactory(ScanExternalStorage scanExternalStorage,
                                                      SchedulersFacade schedulersFacade) {
        return new MainViewModelFactory(scanExternalStorage, schedulersFacade);
    }
}
