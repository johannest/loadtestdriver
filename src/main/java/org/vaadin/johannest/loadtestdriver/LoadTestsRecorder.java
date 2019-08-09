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

public class LoadTestsRecorder {

    private final RecorderController recorderController;
    private RecordingParameters recordingParameters;

    public LoadTestsRecorder(RecordingParameters recordingParameters) {
        this.recordingParameters = recordingParameters;

        final Option<Path> path = createPathToRecorderConf();
        final Map<String, Object> map = scala.collection.mutable.Map$.MODULE$.<String, Object> empty();
        map.put("recorder.core.resourcesFolder", recordingParameters.getResourcesPath());
        map.put("recorder.core.headless", recordingParameters.isHeadlessEnabled());

        final RecorderPropertiesBuilder props = buildRecorderProperties();

        RecorderConfiguration.initialSetup(map, path);
        RecorderConfiguration.reload(props.build());
        recorderController = new RecorderController(new DefaultClock());
    }

    private Option<Path> createPathToRecorderConf() {
        final Path pathToRecorderConf = FileSystems.getDefault().getPath("recorder.conf");
        Logger.getLogger(LoadTestsRecorder.class.getName()).info(pathToRecorderConf.toString());
        return Option.apply(pathToRecorderConf);
    }

    private RecorderPropertiesBuilder buildRecorderProperties() {
        final RecorderPropertiesBuilder props = new RecorderPropertiesBuilder();
        props.mode(RecorderMode.apply("Proxy"));
        props.localPort(recordingParameters.getProxyPort());
        props.simulationClassName(recordingParameters.getTestName());
        props.simulationsFolder(recordingParameters.getSimulationFilePath());
        props.resourcesFolder(recordingParameters.getResourcesPath());
        props.followRedirect(true);
        props.removeCacheHeaders(true);
        props.inferHtmlResources(false);
        props.automaticReferer(true);
        props.checkResponseBodies(true);
        if (recordingParameters.isIgnoreStatics()) {
            props.filterStrategy("BlacklistFirst");
            props.blacklist(Arrays.asList(recordingParameters.getStaticPatterns()));
        }
        return props;
    }

    public void start() {
        Logger.getLogger(LoadTestsRecorder.class.getName()).info("Starting the recording");
        recorderController.startRecording();
    }

    public String stopAndSave() {
        System.out.println("### stopAndSave");
        // removePreviousTests();
        final String fileName = new StringBuilder(recordingParameters.getSimulationFilePath()).append('/').append(recordingParameters.getTestName()).toString();
        Logger.getLogger(LoadTestsRecorder.class.getName()).info("Saving the recording: " + fileName);
        try {
            recorderController.stopRecording(true);
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(LoadTestsRecorder.class.getName()).severe("Saving failed: "+e.getMessage());
        }
        System.out.println("### Saved: "+fileName);
        return fileName;
    }

    private void removePreviousTests() {
        FileUtils.deleteQuietly(new File(recordingParameters.getSimulationFilePath()));
    }

}
