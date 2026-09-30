package com.pos.model;

public abstract class Discount {
    public abstract String getName();
    public abstract double getRate();

    public double compute(double subtotal) {
        return subtotal * getRate();
    }
}