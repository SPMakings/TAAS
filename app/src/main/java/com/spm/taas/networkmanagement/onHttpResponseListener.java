package com.spm.taas.networkmanagement;

import org.json.JSONObject;

/**
 * Created by saikatpakira on 10/10/16.
 */

public interface onHttpResponseListener {
    void onSuccess(final JSONObject jObject);
    void onError(final String message);
}
