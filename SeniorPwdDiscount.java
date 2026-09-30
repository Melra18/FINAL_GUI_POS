package com.pos.model;

public class SeniorPwdDiscount extends Discount {
    @Override public String getName() { return "PWD/Senior"; }
    @Override public double getRate() { return 0.20; }
}