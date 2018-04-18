package com.example.stephenborge_macys_challenge.main;

import android.os.Environment;

import com.example.stephenborge_macys_challenge.common.domain.interactors.ScanFiles;
import com.example.stephenborge_macys_challenge.common.domain.model.InfoAboutStorageSingleton;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

class ScanExternalStorage implements ScanFiles {
    InfoAboutStorageSingleton instance = InfoAboutStorageSingleton.getInstance();
    private Integer numOfFiles = 0;
    private Long totalFileSize = (long) 0;
    private String minBiggestFileKey = "";
    private Long minBiggestFileSize = (long) 0;
    private HashMap<String, Long> allExtensions = new HashMap<>();

    @Inject
    ScanExternalStorage() {
    }

    @Override
    public Single<Integer> execute() {
        return Single.create(emitter -> {
            Thread thread = new Thread( () -> {
                try {
                    Integer toReturn = executeWrapper();
                    emitter.onSuccess(toReturn);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });


    }

    public Integer executeWrapper() {
        numOfFiles = 0;
        if ((android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY) ||
                (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)))) {
            File dir = android.os.Environment.getExternalStorageDirectory();
            totalFileSize = dir.getTotalSpace() - dir.getFreeSpace();
            walkdir(dir);
        }
        instance.averageFileSize = totalFileSize / numOfFiles;

        while(instance.fiveMostCommonExtensions.size() < 5 && !allExtensions.isEmpty()) {
            String maxKey = null;
            Long max = Collections.max(allExtensions.values());

            for(Map.Entry<String, Long> entry : allExtensions.entrySet()) {
                Long value = entry.getValue();
                if(null != value && max == value) {
                    maxKey = entry.getKey();
                }
            }

            instance.fiveMostCommonExtensions.put(maxKey, allExtensions.get(maxKey));
            allExtensions.remove(maxKey);
        }
        return numOfFiles;
    }

    public void walkdir(File dir) {

        File listFile[] = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {// if its a directory need to get the files under that directory
                    walkdir(listFile[i]);
                } else {// add path of  files to your arraylist for later use
                    numOfFiles++;

                    if (instance.tenLargestFiles.size() < 10) {
                        if(instance.tenLargestFiles.size() == 0)
                        {
                            minBiggestFileKey = listFile[i].getName();
                            minBiggestFileSize = listFile[i].length();
                        }
                        else
                        {
                            if(listFile[i].length() < minBiggestFileSize)
                            {
                                minBiggestFileKey = listFile[i].getName();
                                minBiggestFileSize = listFile[i].length();
                            }
                        }
                        instance.tenLargestFiles.put(listFile[i].getName(), listFile[i].length());
                    } else if (listFile[i].length() > minBiggestFileSize) {
                        instance.tenLargestFiles.remove(minBiggestFileKey);
                        instance.tenLargestFiles.put(listFile[i].getName(), listFile[i].length());
                        minBiggestFileSize = listFile[i].length();
                        minBiggestFileKey = listFile[i].getName();
                        Map.Entry<String, Long> min = null;
                        for (Map.Entry<String, Long> entry : instance.tenLargestFiles.entrySet()) {
                            if (min == null || min.getValue() > entry.getValue()) {
                                min = entry;
                            }
                        }
                        if(min != null) {
                            minBiggestFileKey = min.getKey();
                            minBiggestFileSize = min.getValue();
                        }
                    }
                    String path = listFile[i].getAbsolutePath();
                    String extension = "";

                    int k = path.lastIndexOf('.');
                    if (k > 0) {
                        extension = path.substring(k + 1);
                    }

                    if (allExtensions.containsKey(extension)) {
                        allExtensions.put(extension, allExtensions.get(extension) + 1);
                    } else {
                        allExtensions.put(extension, (long)1);
                    }
                }
            }
        }
    }
}
