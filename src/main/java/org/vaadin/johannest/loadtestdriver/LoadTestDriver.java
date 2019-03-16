package org.vaadin.johannest.loadtestdriver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LoadTestDriver extends PhantomJSDriver {

    public static final String DEFAULT_PING_URL = "https://www.google.com/search?q=";
    public static final String INJECT_KEYWORD = "INJECT_TRYMAX_LOOP";
    private final LoadTestConfigurator loadTestConfigurator;
    private final boolean headlessEnabled;

    private Recorder recorder;
    private boolean recording;

    private int proxyPort;
    private String proxyHost;
    private String tempFilePath;
    private String resourcesPath;
    private String testName;

    private boolean testConfiguringEnabled;
    private boolean staticResourcesIngnoringEnabled;

    public LoadTestDriver(DesiredCapabilities capabilities, LoadTestParameters loadTestParameters, boolean headlessEnabled) {
        super(capabilities);
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
        recorder = new Recorder(getProxyPort(), getProxyHost(), getTempFilePath(), getResourcesPath(), getTestName(),
                staticResourcesIngnoringEnabled, headlessEnabled);

        loadTestConfigurator.setClassName(recorder.getClassName());
        loadTestConfigurator.setResourcesPath(recorder.getResourcesPath());
        loadTestConfigurator.setTempFilePath(recorder.getTempFilePath());

        recording = true;
        recorder.start();
    }

    @Override
    public void close() {
        if (recording) {
            stopRecordingAndSaveResults();
            super.close();
            if (testConfiguringEnabled) {
                loadTestConfigurator.configureTestFile();
            }
        }
    }

    @Override
    protected void stopClient() {
        if (recording) {
            stopRecordingAndSaveResults();
            super.stopClient();
            if (testConfiguringEnabled) {
                loadTestConfigurator.configureTestFile();
            }
        }
    }

    @Override
    public void quit() {
        if (recording) {
            stopRecordingAndSaveResults();
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

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
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
     * @param maxTries how many time server-side is polled before marking the request failed
     * @param pauseBetweenTries pause between polls
     * @param responseRegex regular expression for successful response
     */
    public void injectTryMaxLoop(int maxTries, int pauseBetweenTries, String responseRegex) {
        injectTryMaxLoop(DEFAULT_PING_URL, maxTries, pauseBetweenTries, responseRegex);
    }

    /**
     * Add marker for injecting Gatling tryMax loop to poll background thread to finish
     *
     * @param pingUrl url used to inject the marker
     * @param maxTries how many time server-side is polled before marking the request failed
     * @param pauseBetweenTries pause between polls
     * @param responseRegex regular expression for successful response
     */
    public void injectTryMaxLoop(String pingUrl, int maxTries, int pauseBetweenTries, String responseRegex) {
        ((JavascriptExecutor)this).executeScript("window.open('"+pingUrl+ INJECT_KEYWORD + "." +maxTries+"."+pauseBetweenTries+"."+responseRegex+".','_blank');");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
