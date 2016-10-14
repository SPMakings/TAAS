package com.spm.taas.networkmanagement;

/**
 * Created by saikatpakira on 13/10/16.
 */

public class KeyValuePairModel {

    private String key = "", value = "";

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void add(final String key_,final String value_){
        this.key = key_;
        this.value = value_;
    }
}
