package org.vaadin.johannest.loadtestdriver;

import io.gatling.commons.util.DefaultClock;
import io.gatling.recorder.config.RecorderConfiguration;
import io.gatling.recorder.config.RecorderMode;
import io.gatling.recorder.config.RecorderPropertiesBuilder;
import io.gatling.recorder.controller.RecorderController;
import io.gatling.recorder.render.template.Format;
import org.apache.commons.io.FileUtils;
import scala.Option;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;

public class LoadTestsRecorder {

    private final RecorderController recorderController;
    private RecordingParameters recordingParameters;

    public LoadTestsRecorder(RecordingParameters recordingParameters) {
        this.recordingParameters = recordingParameters;

        final Option<Path> path = createPathToRecorderConf();
        final Map<String, Object> map = (Map<String, Object>) scala.collection.mutable.Map$.MODULE$.<String, Object> empty();
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
        if (recordingParameters.hasHarFile()) {
            props.mode(RecorderMode.apply("Har"));
            props.harFilePath(recordingParameters.getHarFileName());
        } else {
            props.mode(RecorderMode.apply("Proxy"));
            props.localPort(recordingParameters.getProxyPort());
        }
        props.simulationFormat(Format.fromString("java11"));
        props.simulationClassName(recordingParameters.getTestName());
        props.simulationsFolder(recordingParameters.getSimulationFilePath());
        props.resourcesFolder(recordingParameters.getResourcesPath());
        props.followRedirect(true);
        props.removeCacheHeaders(true);
        props.inferHtmlResources(false);
        props.automaticReferer(true);
        props.checkResponseBodies(true);
        if (recordingParameters.isIgnoreStatics()) {
            props.enableFilters(true);
            props.denyList(Arrays.asList(recordingParameters.getStaticPatterns()));
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
