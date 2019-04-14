package org.vaadin.johannest.loadtestdriver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoadTestDriver extends ChromeDriver {

    public static final String DEFAULT_PING_URL = "http://www.google.com/search?q=";
    public static final String INJECT_KEYWORD = "INJECT_TRYMAX_LOOP";

    private final LoadTestConfigurator loadTestConfigurator;
    private final boolean headlessEnabled;

    private Recorder recorder;
    private boolean recording;

    private int proxyPort;
    private String proxyHost;
    private String simulationFilePath;
    private String resourcesPath;
    private String testName;

    private boolean testConfiguringEnabled;
    private boolean staticResourcesIngnoringEnabled;

    public LoadTestDriver(ChromeOptions options, LoadTestParameters loadTestParameters, boolean headlessEnabled) {
        super(options);
        loadTestConfigurator = new LoadTestConfigurator(loadTestParameters);
        this.headlessEnabled = headlessEnabled;
    }

    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            Logger.getLogger(LoadTestDriver.class.getName())
                    .warning("Failed to find localhost ip - using 127.0.0.1 instead: " + e.getMessage());
            return "127.0.0.1";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalIpAddressUrlWithPort(int port) {
        return "http://" + getLocalIpAddress() + ":" + port;
    }

    public static String getLocalIpAddressWithPortAndContextPath(int port, String contextPath) {
        return getLocalIpAddressUrlWithPort(port) + "/" + contextPath;
    }

    @Override
    public void get(String url) {
        startRecording();
        super.get(url);
    }

    private void startRecording() {
        Logger.getLogger(LoadTestDriver.class.getName()).info("## startRecording");
        recorder = new Recorder(getProxyPort(), getProxyHost(), getSimulationFilePath(), getResourcesPath(), getTestName(),
                staticResourcesIngnoringEnabled, headlessEnabled);

        loadTestConfigurator.setClassName(recorder.getClassName());
        loadTestConfigurator.setResourcesPath(recorder.getResourcesPath());
        loadTestConfigurator.setTempFilePath(recorder.getSimulationFilePath());

        recording = true;
        recorder.start();
    }

    @Override
    public void close() {
        if (recording) {
            stopRecordingAndSaveResults();
            waitForAWhile();
            super.close();
            if (testConfiguringEnabled) {
                loadTestConfigurator.configureTestFile();
            }
        }
    }

    private void waitForAWhile() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void quit() {
        if (recording) {
            stopRecordingAndSaveResults();
            waitForAWhile();
            super.quit();
            if (testConfiguringEnabled) {
                loadTestConfigurator.configureTestFile();
            }
        }
    }

    private String stopRecordingAndSaveResults() {
        recording = false;
        return recorder.stopAndSave();
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getSimulationFilePath() {
        return simulationFilePath;
    }

    public void setSimulationFilePath(String simulationFilePath) {
        this.simulationFilePath = simulationFilePath;
    }

    private String getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    private String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setStaticResourcesIngnoringEnabled(boolean staticResourcesIngnoringEnabled) {
        this.staticResourcesIngnoringEnabled = staticResourcesIngnoringEnabled;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean isTestConfiguringEnabled() {
        return testConfiguringEnabled;
    }

    public void setTestConfiguringEnabled(boolean testConfiguringEnabled) {
        this.testConfiguringEnabled = testConfiguringEnabled;
    }

    public boolean isStaticResourcesIngnoringEnabled() {
        return staticResourcesIngnoringEnabled;
    }

    /**
     * Add marker for injecting Gatling tryMax loop to poll background thread to finish
     *
     * @param maxTries          how many time server-side is polled before marking the request failed
     * @param pauseBetweenTries pause between polls
     * @param responseRegex     regular expression for successful response
     */
    public void injectTryMaxLoop(int maxTries, int pauseBetweenTries, String responseRegex) {
        injectTryMaxLoop(DEFAULT_PING_URL, maxTries, pauseBetweenTries, responseRegex, -1);
    }

    /**
     * Add marker for injecting Gatling tryMax loop to poll background thread to finish, wait until waitAndforceSyncAfter_ms and then force resync
     *
     * @param maxTries                 how many time server-side is polled before marking the request failed
     * @param pauseBetweenTries        pause between polls
     * @param responseRegex            regular expression for successful response
     * @param waitAndforceSyncAfter_ms wait given time and call forceSync to sync "browser" and server-side after, do not wait force sync if timeout <= 0
     */
    public void injectTryMaxLoop(int maxTries, int pauseBetweenTries, String responseRegex, int waitAndforceSyncAfter_ms) {
        injectTryMaxLoop(DEFAULT_PING_URL, maxTries, pauseBetweenTries, responseRegex, waitAndforceSyncAfter_ms);
    }

    /**
     * Add marker for injecting Gatling tryMax loop to poll background thread to finish
     *
     * @param pingUrl                  url used to inject the marker
     * @param maxTries                 how many time server-side is polled before marking the request failed
     * @param pauseBetweenTries        pause between polls
     * @param responseRegex            regular expression for successful response
     * @param waitAndforceSyncAfter_ms wait given time and call forceSync to sync "browser" and server-side after, do not wait force sync if timeout <= 0
     */
    public void injectTryMaxLoop(String pingUrl, int maxTries, int pauseBetweenTries, String responseRegex, int waitAndforceSyncAfter_ms) {
        ((JavascriptExecutor) this).executeScript("window.open()");
        try {
            Thread.sleep(250);
            ArrayList<String> tabs = new ArrayList<>(getWindowHandles());
            Thread.sleep(250);
            switchTo().window(tabs.get(1));
            Thread.sleep(250);
            super.get(pingUrl + INJECT_KEYWORD + "." + maxTries + "." + pauseBetweenTries + "." + responseRegex + ".");
            Thread.sleep(250);
            this.execute("close");
            switchTo().window(tabs.get(0));
            Thread.sleep(250);

            if (waitAndforceSyncAfter_ms > 0) {
                Thread.sleep(waitAndforceSyncAfter_ms);
                forceSync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Force sync "browser" and server side
     */
    public void forceSync() {
        ((JavascriptExecutor) this).executeScript("window.vaadin.forceSync()");
    }
}
