package org.vaadin.johannest.loadtestdriver;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import io.gatling.commons.util.DefaultClock;
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
    private final String[] staticPatterns = { ".*\\.js", ".*\\.cache.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg",
            ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.ttf", ".*\\.otf", ".*\\.png", ".*\\.css?(.*)", ".*\\.js?(.*)" };

    private String className;
    private String resourcesPath;
    private String simulationFilePath;

    public Recorder() {
        this(8888);
    }

    public Recorder(int proxyPort) {
        this(proxyPort, null, System.getProperty("java.io.tmpdir") + "gatling");
    }

    public Recorder(int proxyPort, String proxyHost) {
        this(proxyPort, proxyHost, System.getProperty("java.io.tmpdir") + "gatling");
    }

    public Recorder(int proxyPort, String proxyHost, String simulationFilePath) {
        this(proxyPort, proxyHost, simulationFilePath, simulationFilePath, null, false, false);
    }

    public Recorder(int proxyPort, String proxyHost, String simulationFilePath, String resourcesPath, String testName,
                    boolean ignoreStatics, boolean headlessEnabled) {
        Logger.getLogger(Recorder.class.getName()).info(proxyHost + ":" + proxyPort);

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


        final Option<Path> path = createPathToRecorderConf();
        final Map<String, Object> map = scala.collection.mutable.Map$.MODULE$.<String, Object> empty();
        map.put("recorder.core.resourcesFolder", resourcesPath);
        map.put("recorder.core.headless", headlessEnabled);

        final RecorderPropertiesBuilder props = buildRecorderProperties(proxyPort, testName,
                ignoreStatics);

        RecorderConfiguration.initialSetup(map, path);
        RecorderConfiguration.reload(props.build());
        recorderController = new RecorderController(new DefaultClock());
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

    private Option<Path> createPathToRecorderConf() {
        final Path pathToRecorderConf = FileSystems.getDefault().getPath("recorder.conf");
        Logger.getLogger(Recorder.class.getName()).info(pathToRecorderConf.toString());
        return Option.apply(pathToRecorderConf);
    }

    private RecorderPropertiesBuilder buildRecorderProperties(int proxyPort, String testName,
                                                              boolean ignoreStatics) {
        final RecorderPropertiesBuilder props = new RecorderPropertiesBuilder();
        props.mode(RecorderMode.apply("Proxy"));
        props.localPort(proxyPort);
        if (Strings.isNullOrEmpty(testName)) {
            props.simulationClassName(className = randomName());
        } else {
            props.simulationClassName(className = testName);
        }
        props.simulationsFolder(simulationFilePath);
        props.resourcesFolder(resourcesPath);
        props.followRedirect(true);
        props.removeCacheHeaders(true);
        props.inferHtmlResources(false);
        props.automaticReferer(true);
        props.checkResponseBodies(true);
        if (ignoreStatics) {
            props.filterStrategy("BlacklistFirst");
            props.blacklist(Arrays.asList(staticPatterns));
        }
        return props;
    }

    private String randomName() {
        return "SIMx" + new Random().nextInt(10000);
    }

    void start() {
        Logger.getLogger(Recorder.class.getName()).info("Starting the recording");
        recorderController.startRecording();
    }

    String stopAndSave() {
        System.out.println("### stopAndSave");
        // removePreviousTests();
        final String fileName = new StringBuilder(simulationFilePath).append('/').append(className).toString();
        Logger.getLogger(Recorder.class.getName()).info("Saving the recording: " + fileName);
        try {
            recorderController.stopRecording(true);
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(Recorder.class.getName()).severe("Saving failed: "+e.getMessage());
        }
        System.out.println("### Saved: "+fileName);
        return fileName;
    }

    private void removePreviousTests() {
        FileUtils.deleteQuietly(new File(simulationFilePath));
    }

    String getClassName() {
        return className;
    }

    String getSimulationFilePath() {
        return simulationFilePath;
    }

    String getResourcesPath() {
        return resourcesPath;
    }
}
