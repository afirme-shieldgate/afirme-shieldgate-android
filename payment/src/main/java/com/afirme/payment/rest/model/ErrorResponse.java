package com.afirme.payment.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    @Expose
    private PaymentError error;

    public PaymentError getError() {
        return error;
    }

    public void setError(PaymentError error) {
        this.error = error;
    }

}
