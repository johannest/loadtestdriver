package org.vaadin.johannest.loadtestdriver;

import picocli.CommandLine;

@CommandLine.Command(name = "prepare", version = "LoadTestTool 0.1", mixinStandardHelpOptions = true, subcommands = { CommandLineRunner.Recorder.class })
public class CommandLineRunner implements Runnable {

    @CommandLine.Option(names = { "-f", "--file-path" },  description = "Gatling Scala file path")
    String simulationFilePathWithFile = null;

    @CommandLine.Option(names = { "-r", "--request-bodies-path" }, description = "Request bodies folder's path")
    String resourcesPath = null;

    @CommandLine.Option(names = { "-c", "--concurrent-users" }, description = "Concurrent users")
    int concurrentUsers = 1;

    @CommandLine.Option(names = { "-t", "--ramp-up-time" }, description = "Ramp up time (s)")
    int rampUpTime = 1;

    @CommandLine.Option(names = { "-n", "--repeats" }, description = "Repeats")
    int repeats = 1;

    @CommandLine.Option(names = { "-p1", "--min-pause" }, description = "Min pause (s)")
    int minPause = -1;

    @CommandLine.Option(names = { "-p2", "--max-pause" }, description = "Max pause (s)")
    int maxPause = -1;

    @Override
    public void run() {
        String testName = null;
        String tempFilePath = null;

        int concurrentUsers = 1;
        int rampUpTime = 1;
        int repeats = 1;
        int minPause = -1;
        int maxPause = -1;

        if (simulationFilePathWithFile.contains("\\")) {
            testName = simulationFilePathWithFile.substring(simulationFilePathWithFile.lastIndexOf('\\')+1);
            tempFilePath = simulationFilePathWithFile.substring(0, simulationFilePathWithFile.lastIndexOf('\\'));
            testName = testName.replace(".scala", "");
        }
        if (simulationFilePathWithFile.contains("/")) {
            testName = simulationFilePathWithFile.substring(simulationFilePathWithFile.lastIndexOf('/')+1);
            tempFilePath = simulationFilePathWithFile.substring(0, simulationFilePathWithFile.lastIndexOf('/'));
            testName = testName.replace(".scala", "");
        }


        System.out.println("Running the configuration with the following parameters:");
        System.out.println(testName);
        System.out.println(simulationFilePathWithFile);
        System.out.println(resourcesPath);
        System.out.println(concurrentUsers+", "+rampUpTime+", "+repeats+", "+minPause+", "+maxPause);

        ConfigurationParameters configurationParameters = new ConfigurationParameters(concurrentUsers, rampUpTime, repeats,
                minPause, maxPause);
        LoadTestConfigurator loadTestConfigurator = new LoadTestConfigurator(configurationParameters);
        loadTestConfigurator.setClassName(testName);
        loadTestConfigurator.setResourcesPath(simulationFilePathWithFile);
        loadTestConfigurator.setTempFilePath(tempFilePath);
        loadTestConfigurator.setResourcesPath(resourcesPath);
        loadTestConfigurator.configureTestFile();
    }

    /**
     * LoadTestDriver Configurator Command Line Runner
     * usage: java -jar LoadTestDriver.jar -f=ScalaFilePath -r=request_body_folder_path [-c=concurrent_users] [-t=ramp_up_time] [-n=repeats] [-p1=min_pause] [-p2=max_pause]
     * @param args command line parameters
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CommandLineRunner()).execute(args);
        System.exit(exitCode);
    }

    @CommandLine.Command(name = "record", description = "Starts Gatling Recorder with preferred settings")
    public static class Recorder implements Runnable {

        @Override
        public void run() {
            String gatlingHome = System.getenv("GATLING_HOME");
            if (gatlingHome == null) {
                gatlingHome = System.getProperty("java.io.tmpdir");
                System.out.println("Using temp directory for recorded files: "+gatlingHome);
            } else {
                System.out.println("Using GATLING_HOME directory for recorded files: "+gatlingHome);
            }
            RecordingParameters params = new RecordingParameters();
            params.setResourcesPath(gatlingHome);
            LoadTestsRecorder recorder = new LoadTestsRecorder(params);
            recorder.start();
        }
    }
}
