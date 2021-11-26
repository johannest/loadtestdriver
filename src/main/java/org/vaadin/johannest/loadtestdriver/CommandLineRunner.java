package org.vaadin.johannest.loadtestdriver;

import com.google.common.base.Strings;
import picocli.CommandLine;

@CommandLine.Command(name = "prepare", version = "LoadTestTool 0.1", mixinStandardHelpOptions = true,
        subcommands = {CommandLineRunner.Recorder.class})
public class CommandLineRunner implements Runnable {

    @CommandLine.Option(names = {"-f", "--file-path"}, description = "Gatling Scala file path")
    String simulationFilePathWithFile = null;

    @CommandLine.Option(names = {"-r", "--request-bodies-path"}, description = "Request bodies folder's path. If not given request bodies are assumed to be in the same folder as the test script.")
    String resourcesPath = null;

    @CommandLine.Option(names = {"-c", "--concurrent-users"}, description = "Concurrent users")
    int concurrentUsers = 1;

    @CommandLine.Option(names = {"-t", "--ramp-up-time"}, description = "Ramp up time (s)")
    int rampUpTime = 1;

    @CommandLine.Option(names = {"-n", "--repeats"}, description = "Repeats")
    int repeats = 1;

    @CommandLine.Option(names = {"-p1", "--min-pause"}, description = "Min pause (s)")
    int minPause = -1;

    @CommandLine.Option(names = {"-p2", "--max-pause"}, description = "Max pause (s)")
    int maxPause = -1;

    @Override
    public void run() {
        if (Strings.isNullOrEmpty(simulationFilePathWithFile)) {
            CommandLine.usage(CommandLineRunner.class, System.out);
        }
        String testName = extractTestNameFromTheFileNamePath(simulationFilePathWithFile);
        String tempFilePath = extractDirectoryPathFromTheFileNamePath(simulationFilePathWithFile);
        if (Strings.isNullOrEmpty(resourcesPath)) {
            resourcesPath = tempFilePath;
        }

        System.out.println("Running the configuration with the following parameters:");
        System.out.println(testName);
        System.out.println(simulationFilePathWithFile);
        System.out.println(resourcesPath);
        System.out.println(concurrentUsers + ", " + rampUpTime + ", " + repeats + ", " + minPause + ", " + maxPause);

        ConfigurationParameters configurationParameters = new ConfigurationParameters(concurrentUsers, rampUpTime, repeats,
                minPause, maxPause);
        LoadTestConfigurator loadTestConfigurator = new LoadTestConfigurator(configurationParameters);
        loadTestConfigurator.setClassName(testName);
        loadTestConfigurator.setResourcesPath(simulationFilePathWithFile);
        loadTestConfigurator.setTempFilePath(tempFilePath);
        loadTestConfigurator.setResourcesPath(resourcesPath);
        loadTestConfigurator.configureTestFile();
    }

    @CommandLine.Command(name = "record", description = "Starts Gatling Recorder with preferred settings")
    public static class Recorder implements Runnable {

        @CommandLine.Option(names = {"-f", "--har-file-path"}, description = "Import the given HAR file instead of recording (recommended).")
        String harFileName = null;

        @CommandLine.Option(names = {"-d", "--directory"}, description = "Directory path where to save recorded file. If not given: In case of a HAR file import the directory of the file is used. Otherwise GATLING_HOME or temp directory is used.")
        String directoryPath = null;

        @CommandLine.Option(names = {"-r", "---resources"}, description = "Directory path where to save recorded request payload files. If not given: In case of a HAR file import the directory of the file is used. Otherwise GATLING_HOME or temp directory is used.")
        String resourcesPath = null;

        @CommandLine.Option(names = {"-n", "--name"}, description = "Test's name. If not given random name is used or (in case of a HAR file import) name is got the HAR file name.")
        String name = null;

        @CommandLine.Option(names = {"-p", "--port"}, description = "Proxy port. By default 8888 and not needed in a HAR file import.")
        private Integer proxyPort;

        @CommandLine.Option(names = {"-h", "--headless"}, description = "Headless mode. Recommended when using a HAR file import.")
        private boolean headless = false;

        @Override
        public void run() {
            RecordingParameters recordingParameters = new RecordingParameters();
            if (!Strings.isNullOrEmpty(harFileName)) {
                recordingParameters.setHarFileName(harFileName);
                if (Strings.isNullOrEmpty(directoryPath)) {
                    directoryPath = extractDirectoryPathFromTheFileNamePath(harFileName);
                }
                if (Strings.isNullOrEmpty(name)) {
                    name = extractTestNameFromTheFileNamePath(harFileName);
                    name = name.replaceAll("-", "_");
                    name = name.replaceAll("\\s", "_");
                }
            }

            if (!Strings.isNullOrEmpty(directoryPath)) {
                System.out.println("Using given directory for recorded files: " + directoryPath);
            } else {
                directoryPath = System.getenv("GATLING_HOME");
                if (directoryPath == null) {
                    directoryPath = System.getProperty("java.io.tmpdir");
                    System.out.println("Using temp directory for recorded files: " + directoryPath);
                } else {
                    System.out.println("Using GATLING_HOME directory for recorded files: " + directoryPath);
                }
            }
            if (Strings.isNullOrEmpty(resourcesPath)) {
                resourcesPath = directoryPath;
            }
            if (Strings.isNullOrEmpty(name)) {
                name = "Test" + (int) (1000000 * Math.random());
                System.out.println("Using random name for the test file: " + name);
            }
            if (proxyPort == null && harFileName == null) {
                proxyPort = 8888;
                System.out.println("Using default proxy port 8888");
            }
            recordingParameters.setTestName(name);
            recordingParameters.setResourcesPath(resourcesPath);
            recordingParameters.setSimulationFilePath(directoryPath);
            recordingParameters.setHeadlessEnabled(headless);
            recordingParameters.setIgnoreStatics(true);
            if (proxyPort != null) {
                recordingParameters.setProxyPort(proxyPort);
            }
            LoadTestsRecorder recorder = new LoadTestsRecorder(recordingParameters);

            if (!headless) {
                recorder.start();
                while (true) {
                    // just wait until the GUI closes
                }
            }
        }
    }

    /**
     * Unfinished
     */
    @CommandLine.Command(name = "run", description = "Run given Gatling Scala load test script")
    public static class TestRunner implements Runnable {

        @CommandLine.Option(names = {"-f", "--file-path"}, description = "Path to the test script")
        String filePath = null;

        @CommandLine.Option(names = {"-r", "--request-bodies-path"}, description = "Request bodies folder's path. If not given request bodies are assumed to be in the same folder as the test script.")
        String resourcesPath = null;

        @Override
        public void run() {
            String name = "";
            if (!Strings.isNullOrEmpty(filePath)) {
                if (Strings.isNullOrEmpty(resourcesPath)) {
                    resourcesPath = extractDirectoryPathFromTheFileNamePath(filePath);
                }
                name = extractTestNameFromTheFileNamePath(filePath);
            } else {
                CommandLine.usage(TestRunner.class, System.out);
            }

            RecordingParameters recordingParameters = new RecordingParameters();
            recordingParameters.setResourcesPath(resourcesPath);
            recordingParameters.setSimulationFilePath(filePath);
            recordingParameters.setTestName(name);

            LoadTestRunner loadTestRunner = new LoadTestRunner(recordingParameters);
            System.out.println("Compiling the test script...");
            loadTestRunner.compileTestFile();
            System.out.println("Running the test...");
            loadTestRunner.runLoadTest();
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CommandLineRunner()).execute(args);
        System.exit(exitCode);
    }

    private static String extractTestNameFromTheFileNamePath(String filePath) {
        if (filePath.contains("\\")) {
            return filePath.substring(filePath.lastIndexOf('\\') + 1).replace(".scala", "").replace(".har", "");
        }
        else if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf('/') + 1).replace(".scala", "").replace(".har", "");
        } else {
            return filePath.replace(".scala", "").replace(".har", "");
        }
    }

    private static String extractDirectoryPathFromTheFileNamePath(String filePath) {
        if (filePath.contains("\\")) {
            return filePath.substring(0, filePath.lastIndexOf('\\'));
        }
        else {
            return filePath.substring(0, filePath.lastIndexOf('/'));
        }
    }
}
