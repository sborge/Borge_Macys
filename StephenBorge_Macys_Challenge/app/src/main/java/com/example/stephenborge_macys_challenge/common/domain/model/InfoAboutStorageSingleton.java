package com.example.stephenborge_macys_challenge.common.domain.model;

import java.util.HashMap;

public class InfoAboutStorageSingleton {
    // static variable single_instance of type Singleton
    private static InfoAboutStorageSingleton myInstance;
    public long averageFileSize;
    public HashMap<String, Long> tenLargestFiles;
    public HashMap<String, Long> fiveMostCommonExtensions;

    private InfoAboutStorageSingleton() {
        averageFileSize = 0;
        tenLargestFiles = new HashMap<>();
        fiveMostCommonExtensions = new HashMap<>();
    }
    // static method to create instance of Singleton class
    public static InfoAboutStorageSingleton getInstance()
    {
        if (myInstance == null)
            myInstance = new InfoAboutStorageSingleton();

        return myInstance;
    }
}
