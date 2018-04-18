package com.example.stephenborge_macys_challenge.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;

import com.example.stephenborge_macys_challenge.common.domain.interactors.ScanFiles;
import com.example.stephenborge_macys_challenge.common.viewmodel.Response;
import com.example.stephenborge_macys_challenge.rx.SchedulersFacade;

import io.reactivex.disposables.Disposable;

class MainViewModel extends ViewModel {

    private final ScanExternalStorage scanExternalStorage;

    private final SchedulersFacade schedulersFacade;

    private Disposable disposable;

    private final MutableLiveData<Response> response = new MutableLiveData<>();

    private final MutableLiveData<String> fileDataLand = new MutableLiveData<>();

    private final MutableLiveData<String> fileDataPort = new MutableLiveData<>();

    private final MutableLiveData<String> extensionData = new MutableLiveData<>();

    private final MutableLiveData<String> averageData = new MutableLiveData<>();

    MainViewModel(ScanExternalStorage scanExternalStorage,
                  SchedulersFacade schedulersFacade) {
        this.scanExternalStorage = scanExternalStorage;
        this.schedulersFacade = schedulersFacade;
    }

    @Override
    protected void onCleared() {
        if(disposable != null) {
            disposable.dispose();
        }
    }

    void scanStorageCall() {
        scanStorage(scanExternalStorage);
    }

    void cancelScanCall() {
        cancelScan();
    }

    void shareResultsCall(Context context, String shareBody){
        shareResults(context, shareBody);
    }

    MutableLiveData<Response> response() {
        return response;
    }

    MutableLiveData<String> getFileDataLand() {
        return fileDataLand;
    }

    MutableLiveData<String> getFileDataPort() {
        return fileDataPort;
    }

    MutableLiveData<String> getExtensionData() {
        return extensionData;
    }

    MutableLiveData<String> getAverageData() {
        return averageData;
    }

    private void scanStorage(ScanFiles scanFiles) {
        disposable = scanFiles.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnDispose(() -> response.setValue(Response.cancel()))
                .doOnSubscribe(__ -> response.setValue(Response.loading()))
                .subscribe(
                        files -> response.setValue(Response.success(files)),
                        throwable -> response.setValue(Response.error(throwable))
                );
    }

    private void cancelScan() {
        if(disposable != null) {
            disposable.dispose();
        }
    }

    private void shareResults(Context context, String shareBody) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(sharingIntent);
    }
}
