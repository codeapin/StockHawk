package com.udacity.stockhawk.data.model;

import com.google.gson.annotations.SerializedName;

public class QuandlError {

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}