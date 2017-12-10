package com.udacity.stockhawk.data.model;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("quandl_error")
    private QuandlError quandlError;

    public void setQuandlError(QuandlError quandlError) {
        this.quandlError = quandlError;
    }

    public QuandlError getQuandlError() {
        return quandlError;
    }
}