package org.vaadin.johannest.loadtestdriver;

public class LoadTestParameters {

    private int concurrentUsers;
    private int rampUpTime;
    private int repeats;
    private int minPause;
    private int maxPause;

    public LoadTestParameters() {
    }

    public LoadTestParameters(int concurrentUsers, int rampUpTime, int repeats, int minPause, int maxPause) {
        this.concurrentUsers = concurrentUsers;
        this.rampUpTime = rampUpTime;
        this.repeats = repeats;
        this.minPause = minPause;
        this.maxPause = maxPause;
    }

    public int getConcurrentUsers() {
        return concurrentUsers;
    }

    public void setConcurrentUsers(int concurrentUsers) {
        this.concurrentUsers = concurrentUsers;
    }

    public int getRampUpTime() {
        return rampUpTime;
    }

    public void setRampUpTime(int rampUpTime) {
        this.rampUpTime = rampUpTime;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public int getMinPause() {
        return minPause;
    }

    public void setMinPause(int minPause) {
        this.minPause = minPause;
    }

    public int getMaxPause() {
        return maxPause;
    }

    public void setMaxPause(int maxPause) {
        this.maxPause = maxPause;
    }

    public boolean pausesEnabled() {
        return minPause > 0 && maxPause > 0;
    }
}
