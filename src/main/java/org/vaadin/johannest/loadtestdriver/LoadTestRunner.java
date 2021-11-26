package org.vaadin.johannest.loadtestdriver;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;

public class LoadTestRunner {

    private final RecordingParameters recordingParameters;

    public LoadTestRunner(RecordingParameters recordingParameters) {
        this.recordingParameters = recordingParameters;
    }

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        final BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            Logger.getLogger(LoadTestsRecorder.class.getName()).info(name + " " + line);
        }
    }

    public void compileTestFile() {
        Logger.getLogger(LoadTestsRecorder.class.getName()).info("Compiling test file");
        try {
            final String classpath = System.getProperty("java.class.path");
            String compilerClasspath = System.getenv("GATLING_HOME");
            if (Strings.isNullOrEmpty(compilerClasspath)) {
                System.out.println("GATLING_HOME not found, using compilation class path: [classpath]/lib/*");
                compilerClasspath = classpath;
            }
            if (compilerClasspath.contains("\\")) {
                compilerClasspath += "\\lib\\*";
            } else {
                compilerClasspath += "/lib/*";
            }

            final StringBuilder cmd = new StringBuilder();
            cmd.append("java -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 ");
            cmd.append("-Xms512M -Xmx512M -Xmn100M -Xss10M ");
            cmd.append("-cp " + compilerClasspath);
            cmd.append(" io.gatling.compiler.ZincCompiler ");
            cmd.append(" -sf ");
            cmd.append(recordingParameters.getSimulationFilePath());
            cmd.append(" -bf ");
            cmd.append(recordingParameters.getResourcesPath());
            // cmd.append(" -ro ");
            // cmd.append(recorder.getSimulationFilePath()+"/results");

            Logger.getLogger(LoadTestsRecorder.class.getName()).info("Running ZincCompiler with comman: " + cmd.toString());
            final Process pro = Runtime.getRuntime().exec(cmd.toString());
            printLines(" stdout:", pro.getInputStream());
            printLines(" stderr:", pro.getErrorStream());
            pro.waitFor();
            Logger.getLogger(LoadTestsRecorder.class.getName()).info(" exitValue() " + pro.exitValue());
        } catch (final Exception e) {
            Logger.getLogger(LoadTestsRecorder.class.getName()).severe("Compilation failed");
            e.printStackTrace();
        }
    }

    public void showLoadTestMonitor() {
        // TODO Auto-generated method stub

    }

    public void runLoadTest() {
        final GatlingPropertiesBuilder propsBuilder = new GatlingPropertiesBuilder();
        propsBuilder.binariesDirectory(recordingParameters.getSimulationFilePath() + "/test-classes");
        propsBuilder.resourcesDirectory(recordingParameters.getSimulationFilePath());
        propsBuilder.resultsDirectory(recordingParameters.getResourcesPath() + "/results");
        propsBuilder.simulationsDirectory(recordingParameters.getSimulationFilePath());
        propsBuilder.resourcesDirectory(recordingParameters.getResourcesPath());
        final Map<String, Object> propsMap = propsBuilder.build();
        propsMap.put("gatling.core.mute", true);
        // propsMap.put("gatling.core.directory.reportsOnly",
        // recorder.getSimulationFilePath());
        Gatling.fromMap(propsMap);
    }

    public void showResultRaport() {
        try {
            Desktop.getDesktop().browse(findReportFile().toURI());
        } catch (final IOException e) {
            Logger.getLogger(LoadTestsRecorder.class.getName()).severe(e.getLocalizedMessage());
            Logger.getLogger(LoadTestsRecorder.class.getName()).severe("Failed to open raport");
        }
    }

    public File findReportFile() {
        Logger.getLogger(LoadTestsRecorder.class.getName()).info("findReportFile");
        final File dir = new File(recordingParameters.getResourcesPath() + "/..");
        final FileFilter fileFilter = new WildcardFileFilter("gatling-*");
        final File[] files = dir.listFiles(fileFilter);

        File newest = null;
        long newestTimeStamp = 0;

        for (final File file : files) {
            Logger.getLogger(LoadTestsRecorder.class.getName()).info(file.getName());
            final String timeStamp = file.getName().split("-")[1];
            final long ts = Long.parseLong(timeStamp);
            if (ts > newestTimeStamp) {
                newest = file;
                newestTimeStamp = ts;
            }
        }
        Logger.getLogger(LoadTestsRecorder.class.getName()).info("Report file " + newest.getName());
        return new File(newest.getPath() + "/index.html");
    }
}
