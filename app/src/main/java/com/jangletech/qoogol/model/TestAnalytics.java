package com.jangletech.qoogol.model;

public class TestAnalytics {

    private String featureName;
    private int count;

    public TestAnalytics(String featureName, int count) {
        this.featureName = featureName;
        this.count = count;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
