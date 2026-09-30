package com.pos.model;

public class NoDiscount extends Discount {
    @Override public String getName() { return "None"; }
    @Override public double getRate() { return 0.0; }
}