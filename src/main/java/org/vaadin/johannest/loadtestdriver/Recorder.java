package org.vaadin.johannest.loadtestdriver;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import io.gatling.recorder.config.RecorderConfiguration;
import io.gatling.recorder.config.RecorderMode;
import io.gatling.recorder.config.RecorderPropertiesBuilder;
import io.gatling.recorder.controller.RecorderController;
import scala.Option;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;

public class Recorder {

    private final RecorderController recorderController;

    private String className;
    private String tempFilePath;
    private final String resourcesPath;
    private final String bodiesFolderPath;
    private final String dataFolderPath;
    private final String[] staticPatterns = { ".*\\.js", ".*\\.cache.js",
            ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico",
            ".*\\.woff", ".*\\.ttf", ".*\\.otf", ".*\\.png", ".*\\.css?(.*)",
            ".*\\.js?(.*)" };

    public Recorder() {
        this(8888);
    }

    public Recorder(int proxyPort) {
        this(proxyPort, null, System.getProperty("java.io.tmpdir") + "gatling");
    }

    public Recorder(int proxyPort, String proxyHost) {
        this(proxyPort, proxyHost,
                System.getProperty("java.io.tmpdir") + "gatling");
    }

    public Recorder(int proxyPort, String proxyHost, String tempFilePath) {
        this(proxyPort, proxyHost, tempFilePath, tempFilePath, null, false);
    }

    public Recorder(int proxyPort, String proxyHost, String tempFilePath,
            String resourcesPath, String testName, boolean ignoreStatics) {
        Logger.getLogger(Recorder.class.getName())
                .info(proxyHost + ":" + proxyPort);
        getTempFilePath(tempFilePath);
        getProxyHost(proxyHost);

        this.resourcesPath = resourcesPath;
        bodiesFolderPath = resourcesPath + "/bodies";
        dataFolderPath = resourcesPath + "/data";

        final Option<Path> path = createPathToRecorderConf();
        final Map<String, Object> map = scala.collection.mutable.Map$.MODULE$
                .<String, Object> empty();
        map.put("recorder.core.bodiesFolder", bodiesFolderPath);
        // map.put("recorder.core.headless", true);

        final RecorderPropertiesBuilder props = buildRecorderProperties(
                proxyPort, tempFilePath, testName, ignoreStatics);

        RecorderConfiguration.initialSetup(map, path);
        RecorderConfiguration.reload(props.build());
        recorderController = new RecorderController();
    }

    private void getTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
        if (tempFilePath == null || tempFilePath.isEmpty()) {
            this.tempFilePath = System.getProperty("java.io.tmpdir")
                    + "gatling";
        }
        if (tempFilePath.charAt(tempFilePath.length() - 1) == '/'
                || tempFilePath.charAt(tempFilePath.length() - 1) == '\\') {
            this.tempFilePath = tempFilePath.substring(0,
                    tempFilePath.length() - 1);
        }
    }

    private void getProxyHost(String proxyHost) {
        if (proxyHost == null) {
            try {
                proxyHost = InetAddress.getLocalHost().getHostAddress();
            } catch (final UnknownHostException e) {
                proxyHost = "127.0.0.1";
            }
        }
    }

    private Option<Path> createPathToRecorderConf() {
        final Path pathToRecorderConf = FileSystems.getDefault()
                .getPath("recorder.conf");
        Logger.getLogger(Recorder.class.getName())
                .info(pathToRecorderConf.toString());
        final Option<Path> path = Option.apply(pathToRecorderConf);
        return path;
    }

    private RecorderPropertiesBuilder buildRecorderProperties(int proxyPort,
            String tempFilePath, String testName, boolean ignoreStatics) {
        final RecorderPropertiesBuilder props = new RecorderPropertiesBuilder();
        props.mode(RecorderMode.apply("Proxy"));
        props.localPort(proxyPort);
        if (testName == null) {
            props.simulationClassName(className = randomName());
        }
        props.simulationClassName(className = testName);
        props.simulationOutputFolder(tempFilePath);
        props.followRedirect(true);
        props.removeCacheHeaders(true);
        props.inferHtmlResources(false);
        props.automaticReferer(true);
        if (ignoreStatics) {
            props.filterStrategy("BlacklistFirst");
            props.blacklist(Arrays.asList(staticPatterns));
        }
        return props;
    }

    private String randomName() {
        return "SIMx" + new Random().nextInt(10000);
    }

    public void start() {
        Logger.getLogger(Recorder.class.getName())
                .info("Starting the recording");
        recorderController.startRecording();
    }

    public String stopAndSave() {
        // removePreviousTests();
        final String fileName = new StringBuilder(tempFilePath).append('/')
                .append(className).toString();
        Logger.getLogger(Recorder.class.getName())
                .info("Saving the recording: " + fileName);
        recorderController.stopRecording(true);
        return fileName;
    }

    private void removePreviousTests() {
        FileUtils.deleteQuietly(new File(tempFilePath));
    }

    public String getClassName() {
        return className;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }

    public String getBodiesFolderPath() {
        return bodiesFolderPath;
    }

    public String getDataFolderPath() {
        return dataFolderPath;
    }
}
