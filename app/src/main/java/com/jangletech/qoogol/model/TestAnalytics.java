package com.jangletech.qoogol.model;

public class TestAnalytics {

    private String featureName;
    private double count;

    public TestAnalytics(String featureName, double count) {
        this.featureName = featureName;
        this.count = count;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
