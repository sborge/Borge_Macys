package com.example.stephenborge_macys_challenge.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephenborge_macys_challenge.R;
import com.example.stephenborge_macys_challenge.common.domain.model.InfoAboutStorageSingleton;
import com.example.stephenborge_macys_challenge.common.viewmodel.Response;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity {

    InfoAboutStorageSingleton instance = InfoAboutStorageSingleton.getInstance();

    @Inject
    MainViewModelFactory viewModelFactory;

    @BindView(R.id.biggest_files)
    TextView biggestFilesTextView;

    @Nullable
    @BindView(R.id.biggest_files_2)
    TextView biggestFilesTextView_2;

    @BindView(R.id.frequent_extensions)
    TextView frequentExtensionsTextView;

    @BindView(R.id.average_file_size)
    TextView averageFileSizeTextView;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.scan_storage_button)
    Button scanButton;

    @BindView(R.id.cancel_scan_button)
    Button cancelButton;

    @BindView(R.id.share_results_button)
    Button shareButton;

    private MainViewModel viewModel;
    private String shareBody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        viewModel.response().observe(this, response -> processResponse(response));
        runtimePermission();
    }

    public void runtimePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Got Permission", Toast.LENGTH_LONG).show();

                } else {

                    runtimePermission();
                }
                return;
            }
        }
    }

    @OnClick(R.id.scan_storage_button)
    void onScanStorageButtonClicked() {
        viewModel.scanStorageCall();
    }

    @OnClick(R.id.cancel_scan_button)
    void onCancelScanButtonClicked() {
        viewModel.cancelScanCall();
    }

    @OnClick(R.id.share_results_button)
    void onShareResultsButtonClicked() {
        viewModel.shareResultsCall(this, shareBody);
    }

    private void processResponse(Response response) {
        switch (response.status) {
            case LOADING:
                renderLoadingState();
                break;

            case SUCCESS:
                renderDataState(response.data);
                break;

            case ERROR:
                renderErrorState(response.error);
                break;
            case CANCELLED:
                renderCancelState();
                break;
        }
    }

    private void renderCancelState() {
        biggestFilesTextView.setVisibility(View.GONE);
        if(biggestFilesTextView_2 != null)
            biggestFilesTextView_2.setVisibility(View.GONE);
        frequentExtensionsTextView.setVisibility(View.GONE);
        averageFileSizeTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        scanButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
    }

    private void renderLoadingState() {
        biggestFilesTextView.setVisibility(View.GONE);
        if(biggestFilesTextView_2 != null)
            biggestFilesTextView_2.setVisibility(View.GONE);
        frequentExtensionsTextView.setVisibility(View.GONE);
        averageFileSizeTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        scanButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.GONE);

    }

    private void renderDataState(Integer files) {
        loadingIndicator.setVisibility(View.GONE);
        biggestFilesTextView.setVisibility(View.VISIBLE);
        if(biggestFilesTextView_2 != null)
            biggestFilesTextView_2.setVisibility(View.VISIBLE);
        frequentExtensionsTextView.setVisibility(View.VISIBLE);
        averageFileSizeTextView.setVisibility(View.VISIBLE);
        scanButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.VISIBLE);
        constructBiggestFilesText();
        constructFrequentExtensionsText();
        constructAverageSizeText();
        if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            int divider = viewModel.getFileDataLand().getValue().indexOf('!');
            biggestFilesTextView.setText(viewModel.getFileDataLand().getValue().substring(0,divider));
            biggestFilesTextView_2.setText(viewModel.getFileDataLand().getValue().substring(divider + 1,viewModel.getFileDataLand().getValue().length()));
        } else {
            biggestFilesTextView.setText(viewModel.getFileDataPort().getValue());
        }
        frequentExtensionsTextView.setText(viewModel.getExtensionData().getValue());
        averageFileSizeTextView.setText(viewModel.getAverageData().getValue());
        shareBody = (String)biggestFilesTextView.getText() + frequentExtensionsTextView.getText() + averageFileSizeTextView.getText();

    }

    private void renderErrorState(Throwable throwable) {
        Timber.e(throwable);
        loadingIndicator.setVisibility(View.GONE);
        biggestFilesTextView.setVisibility(View.GONE);
        if(biggestFilesTextView_2 != null)
            biggestFilesTextView_2.setVisibility(View.GONE);
        scanButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        Toast.makeText(this, R.string.file_error, Toast.LENGTH_SHORT).show();
    }

    private void constructBiggestFilesText()
    {
        String biggestFilesString = "Top 10 Biggest Files\n\n";
        StringBuilder myStringBuilder = new StringBuilder();
        myStringBuilder.append(biggestFilesString);
        for (Map.Entry<String, Long> entry : instance.tenLargestFiles.entrySet()) {
            myStringBuilder.append("File: ")
                            .append(entry.getKey())
                            .append("\nSize: ")
                            .append(String.valueOf(entry.getValue() / 1000000))
                            .append("MB\n\n");
        }
        biggestFilesString = myStringBuilder.toString();
        viewModel.getFileDataPort().setValue(biggestFilesString);

        biggestFilesString = "";
        myStringBuilder = new StringBuilder();
        myStringBuilder.append(biggestFilesString);
        int count = 0;
        for (Map.Entry<String, Long> entry : instance.tenLargestFiles.entrySet()) {
            count++;
            if(count == 6)
            {
                myStringBuilder.append("!");
            }
            myStringBuilder.append("File: ")
                    .append(entry.getKey())
                    .append("\nSize: ")
                    .append(String.valueOf(entry.getValue() / 1000000))
                    .append("MB\n\n");
        }
        biggestFilesString = myStringBuilder.toString();
        viewModel.getFileDataLand().setValue(biggestFilesString);
    }
    private void constructFrequentExtensionsText()
    {
        String mostFrequentString = "Frequent Extensions\n\n";
        StringBuilder myStringBuilder = new StringBuilder();
        myStringBuilder.append(mostFrequentString);
        for (Map.Entry<String, Long> entry : instance.fiveMostCommonExtensions.entrySet()) {
            myStringBuilder.append("Extension: ")
                    .append(entry.getKey())
                    .append("\nOccurrences: ")
                    .append(String.valueOf((long)entry.getValue()))
                    .append("\n\n");
        }

        mostFrequentString = myStringBuilder.toString();
        viewModel.getExtensionData().setValue(mostFrequentString);
    }
    private void constructAverageSizeText()
    {
        String averageSizeText = "Average File Size\n\n";
        StringBuilder myStringBuilder = new StringBuilder();
        myStringBuilder.append(averageSizeText);
        myStringBuilder.append("Average File Size: ")
                        .append(instance.averageFileSize / 1000000)
                        .append("MB");
        averageSizeText = myStringBuilder.toString();
        viewModel.getAverageData().setValue(averageSizeText);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        viewModel.cancelScanCall();
    }
}
