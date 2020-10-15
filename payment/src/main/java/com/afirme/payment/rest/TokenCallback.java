package com.afirme.payment.rest;

import com.afirme.payment.model.Card;
import com.afirme.payment.rest.model.CreateTokenResponse;
import com.afirme.payment.rest.model.PaymentError;

/**
 * An interface representing a callback to be notified about the results of
 * {@link CreateTokenResponse} creation or requests
 */
public interface TokenCallback {

    /**
     * PaymentError callback method.
     * @param error the error that occurred.
     */
    void onError(PaymentError error);

    /**
     * Success callback method.
     * @param card the {@link Card} that was found or created.
     */
    void onSuccess(Card card);
}