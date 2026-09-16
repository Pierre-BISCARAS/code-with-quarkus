package org.acme.models.dao;

import java.math.BigDecimal;

public enum DeliveryType {
    BASIC(100.0, new BigDecimal("5.00")),
    EXPRESS(300.0, new BigDecimal("15.00")),
    ULTRA(1235.0, new BigDecimal("50.00"));

    public final double speedKmh;
    public final BigDecimal cost;

    DeliveryType(double speedKmh, BigDecimal cost) {
        this.speedKmh = speedKmh;
        this.cost = cost;
    }
}
