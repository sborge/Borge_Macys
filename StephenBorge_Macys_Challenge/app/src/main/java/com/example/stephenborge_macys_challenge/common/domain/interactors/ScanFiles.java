package com.example.stephenborge_macys_challenge.common.domain.interactors;

import io.reactivex.Single;

public interface ScanFiles {
    Single<Integer> execute();
}
