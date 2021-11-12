package org.vaadin.johannest.loadtestdriver;

import com.google.common.base.Strings;
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

        @CommandLine.Option(names = { "-d", "--directory" },  description = "Directory path where to save recorded files")
        String folderPath = null;

        @CommandLine.Option(names = { "-n", "--name" },  description = "Test's name")
        String name = null;

        @CommandLine.Option(names = { "-p", "--port" },  description = "Proxy port")
        private Integer proxyPort;

        @Override
        public void run() {
            if (!Strings.isNullOrEmpty(folderPath)) {
                System.out.println("Using given directory for recorded files: "+folderPath);
            } else {
                folderPath = System.getenv("GATLING_HOME");
                if (folderPath == null) {
                    folderPath = System.getProperty("java.io.tmpdir");
                    System.out.println("Using temp directory for recorded files: " + folderPath);
                } else {
                    System.out.println("Using GATLING_HOME directory for recorded files: " + folderPath);
                }
            }
            if (Strings.isNullOrEmpty(name)) {
                name = "Test"+(int)(1000000*Math.random());
                System.out.println("Using random name for the test file: "+name);
            }
            if (proxyPort==null) {
                proxyPort = 8888;
                System.out.println("Using default proxy port 8888");
            }
            LoadTestsRecorder recorder = new LoadTestsRecorder(new RecordingParameters(proxyPort, null, folderPath, folderPath, name,true, false));
            recorder.start();

            while (true) {
                // just wait until the app closes
            }
        }
    }
}
