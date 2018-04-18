package com.example.stephenborge_macys_challenge.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.stephenborge_macys_challenge.rx.SchedulersFacade;

class MainViewModelFactory implements ViewModelProvider.Factory {

    private final ScanExternalStorage scanExternalStorage;

    private final SchedulersFacade schedulersFacade;

    MainViewModelFactory(ScanExternalStorage scanExternalStorage,
                         SchedulersFacade schedulersFacade) {
        this.scanExternalStorage = scanExternalStorage;
        this.schedulersFacade = schedulersFacade;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(scanExternalStorage, schedulersFacade);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
