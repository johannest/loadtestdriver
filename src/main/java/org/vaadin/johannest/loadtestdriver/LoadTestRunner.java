package org.vaadin.johannest.loadtestdriver;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;

public class LoadTestRunner {

    private final LoadTestDriver loadTestDriver;
    private final Recorder recorder;

    public LoadTestRunner(LoadTestDriver loadTestDriver, Recorder recorder) {
        this.loadTestDriver = loadTestDriver;
        this.recorder = recorder;
    }

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        final BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            Logger.getLogger(Recorder.class.getName()).info(name + " " + line);
        }
    }

    private void compileTestFile() {
        Logger.getLogger(Recorder.class.getName()).info("Compiling test file");
        try {
            final String classpath = System.getProperty("java.class.path");

            final StringBuilder cmd = new StringBuilder();
            cmd.append("java -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 ");
            cmd.append("-Xms512M -Xmx512M -Xmn100M -Xss10M ");
            cmd.append("-cp " + classpath);
            cmd.append(" io.gatling.compiler.ZincCompiler ");
            cmd.append("-ccp " + classpath);
            cmd.append(" -sf ");
            cmd.append(recorder.getTempFilePath());
            cmd.append(" -bf ");
            cmd.append(recorder.getResourcesPath());
            // cmd.append(" -ro ");
            // cmd.append(recorder.getTempFilePath()+"/results");

            Logger.getLogger(Recorder.class.getName()).info("Running ZincCompiler with comman: " + cmd.toString());
            final Process pro = Runtime.getRuntime().exec(cmd.toString());
            printLines(" stdout:", pro.getInputStream());
            printLines(" stderr:", pro.getErrorStream());
            pro.waitFor();
            Logger.getLogger(Recorder.class.getName()).info(" exitValue() " + pro.exitValue());
        } catch (final Exception e) {
            Logger.getLogger(Recorder.class.getName()).severe("Compilation failed");
            e.printStackTrace();
        }
    }

    private void showLoadTestMonitor() {
        // TODO Auto-generated method stub

    }

    private void runLoadTest() {
        final GatlingPropertiesBuilder propsBuilder = new GatlingPropertiesBuilder();
        propsBuilder.binariesDirectory(recorder.getTempFilePath() + "/test-classes");
        propsBuilder.resourcesDirectory(recorder.getTempFilePath());
        propsBuilder.resultsDirectory(recorder.getResourcesPath() + "/results");
        propsBuilder.simulationsDirectory(recorder.getTempFilePath());
        propsBuilder.resourcesDirectory(recorder.getResourcesFolderPath());
        propsBuilder.resourcesDirectory(recorder.getDataFolderPath());
        final Map<String, Object> propsMap = propsBuilder.build();
        propsMap.put("gatling.core.mute", true);
        // propsMap.put("gatling.core.directory.reportsOnly",
        // recorder.getTempFilePath());
        Gatling.fromMap(propsMap);
    }

    private void showResultRaport() {
        try {
            Desktop.getDesktop().browse(findReportFile().toURI());
        } catch (final IOException e) {
            Logger.getLogger(Recorder.class.getName()).severe(e.getLocalizedMessage());
            Logger.getLogger(Recorder.class.getName()).severe("Failed to open raport");
        }
    }

    private File findReportFile() {
        Logger.getLogger(Recorder.class.getName()).info("findReportFile");
        final File dir = new File(recorder.getResourcesPath() + "/..");
        final FileFilter fileFilter = new WildcardFileFilter("gatling-*");
        final File[] files = dir.listFiles(fileFilter);

        File newest = null;
        long newestTimeStamp = 0;

        for (final File file : files) {
            Logger.getLogger(Recorder.class.getName()).info(file.getName());
            final String timeStamp = file.getName().split("-")[1];
            final long ts = Long.parseLong(timeStamp);
            if (ts > newestTimeStamp) {
                newest = file;
                newestTimeStamp = ts;
            }
        }
        Logger.getLogger(Recorder.class.getName()).info("Report file " + newest.getName());
        return new File(newest.getPath() + "/index.html");
    }

}
