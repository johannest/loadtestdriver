package org.vaadin.johannest.loadtestdriver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoadTestDriver extends ChromeDriver {

    private final LoadTestConfigurator loadTestConfigurator;
    private ConfigurationParameters configurationParameters;
    private final boolean headlessEnabled;

    private LoadTestsRecorder loadTestsRecorder;
    private boolean recording;

    private int proxyPort;
    private String proxyHost;

    private boolean testConfiguringEnabled;
    private boolean staticResourcesIngnoringEnabled;
    private RecordingParameters recordinParameters;

    public LoadTestDriver(ChromeOptions options, ConfigurationParameters configurationParameters, boolean headlessEnabled) {
        super(options);
        recordinParameters = new RecordingParameters();
        loadTestConfigurator = new LoadTestConfigurator(configurationParameters);
        this.configurationParameters = configurationParameters;
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
        recordinParameters = new RecordingParameters(getProxyPort(), getProxyHost(), getSimulationFilePath(), getResourcesPath(), getTestName(),
                staticResourcesIngnoringEnabled, headlessEnabled);
        loadTestsRecorder = new LoadTestsRecorder(recordinParameters);

        loadTestConfigurator.setClassName(recordinParameters.getTestName());
        loadTestConfigurator.setResourcesPath(recordinParameters.getResourcesPath());
        loadTestConfigurator.setTempFilePath(recordinParameters.getSimulationFilePath());

        recording = true;
        loadTestsRecorder.start();
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
        return loadTestsRecorder.stopAndSave();
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
        return recordinParameters.getSimulationFilePath();
    }

    public void setSimulationFilePath(String simulationFilePath) {
        this.recordinParameters.setSimulationFilePath(simulationFilePath);
    }

    private String getResourcesPath() {
        return recordinParameters.getResourcesPath();
    }

    public void setResourcesPath(String resourcesPath) {
        recordinParameters.setResourcesPath(resourcesPath);
    }

    private String getTestName() {
        return recordinParameters.getTestName();
    }

    public void setTestName(String testName) {
        recordinParameters.setTestName(testName);
    }

    public void setStaticResourcesIngnoringEnabled(boolean staticResourcesIngnoringEnabled) {
        this.staticResourcesIngnoringEnabled = staticResourcesIngnoringEnabled;
    }

    public LoadTestsRecorder getLoadTestsRecorder() {
        return loadTestsRecorder;
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

}
