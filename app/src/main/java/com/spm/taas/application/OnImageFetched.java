package com.spm.taas.application;

/**
 * Created by saikatpakira on 23/10/16.
 */

public interface OnImageFetched {

    void onSuccess(final String path_);

    void onError(final String errorMessage_);
}
