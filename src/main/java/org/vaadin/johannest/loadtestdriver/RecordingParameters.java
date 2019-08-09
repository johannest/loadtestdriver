package org.vaadin.johannest.loadtestdriver;

import com.google.common.base.Strings;

import java.util.Random;

public class RecordingParameters {
    private final String[] staticPatterns = { ".*\\.js", ".*\\.cache.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg",
            ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.ttf", ".*\\.otf", ".*\\.png", ".*\\.css?(.*)", ".*\\.js?(.*)" };

    private int proxyPort;
    private String proxyHost;

    private String simulationFilePath;
    private String resourcesPath;
    private String testName;

    boolean ignoreStatics;
    boolean headlessEnabled;

    public RecordingParameters() {
        proxyPort = 8888;
        simulationFilePath = System.getProperty("java.io.tmpdir") + "gatling";
    }

    public RecordingParameters(int proxyPort, String proxyHost, String simulationFilePath, String resourcesPath, String testName) {
        this();
        this.proxyPort = proxyPort;
        this.proxyHost = proxyHost;
        this.simulationFilePath = simulationFilePath;
        this.resourcesPath = resourcesPath;
        this.testName = testName;

        this.simulationFilePath = simulationFilePath;
        this.resourcesPath = resourcesPath;

        if (Strings.isNullOrEmpty(simulationFilePath)) {
            setSimulationFileToTempDirectory();
        } else {
            this.simulationFilePath = removeLastSlashIfNeeded(simulationFilePath);
        }
        if (Strings.isNullOrEmpty(resourcesPath)) {
            this.resourcesPath = this.simulationFilePath;
        } else {
            this.resourcesPath = removeLastSlashIfNeeded(resourcesPath);
        }
        if (Strings.isNullOrEmpty(testName)) {
            this.testName = randomName();
        } else {
            this.testName = testName;
        }
    }

    public RecordingParameters(int proxyPort, String proxyHost, String simulationFilePath, String resourcesPath, String testName, boolean staticResourcesIngnoringEnabled, boolean headlessEnabled) {
        this(proxyPort, proxyHost, simulationFilePath, resourcesPath, testName);
        this.ignoreStatics = staticResourcesIngnoringEnabled;
        this.headlessEnabled = headlessEnabled;
    }

    private String removeLastSlashIfNeeded(String filePath) {
        if (filePath.charAt(filePath.length() - 1) == '/' ||
                filePath.charAt(filePath.length() - 1) == '\\') {
            return filePath.substring(0, filePath.length() - 1);
        }
        return filePath;
    }

    private void setSimulationFileToTempDirectory() {
        this.simulationFilePath = System.getProperty("java.io.tmpdir") + "gatling";
    }

    private String randomName() {
        return "SIMx" + new Random().nextInt(10000);
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

    public String getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public boolean isIgnoreStatics() {
        return ignoreStatics;
    }

    public void setIgnoreStatics(boolean ignoreStatics) {
        this.ignoreStatics = ignoreStatics;
    }

    public boolean isHeadlessEnabled() {
        return headlessEnabled;
    }

    public void setHeadlessEnabled(boolean headlessEnabled) {
        this.headlessEnabled = headlessEnabled;
    }

    public String[] getStaticPatterns() {
        return staticPatterns;
    }
}
