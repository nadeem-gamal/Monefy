package com.monefy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money arithmetic for the float-based balance fields.
 * Adds through BigDecimal and rounds to 2 decimals so repeated additions
 * don't accumulate float noise (e.g. 0.1 + 0.2).
 */
final class BalanceCalculator {

    private BalanceCalculator() {
    }

    static float add(float balance, float delta) {
        return new BigDecimal(Float.toString(balance))
                .add(new BigDecimal(Float.toString(delta)))
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    static float subtract(float balance, float delta) {
        return new BigDecimal(Float.toString(balance))
                .subtract(new BigDecimal(Float.toString(delta)))
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }
}
