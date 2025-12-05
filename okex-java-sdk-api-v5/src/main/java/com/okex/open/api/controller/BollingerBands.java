package com.okex.open.api.controller;

public class BollingerBands {
    private final double[] upperBand;
    private final double[] middleBand;
    private final double[] lowerBand;

    public BollingerBands(double[] upperBand, double[] middleBand, double[] lowerBand) {
        this.upperBand = upperBand;
        this.middleBand = middleBand;
        this.lowerBand = lowerBand;
    }

    public double[] getUpperBand() {
        return upperBand;
    }

    public double[] getMiddleBand() {
        return middleBand;
    }

    public double[] getLowerBand() {
        return lowerBand;
    }
}
