package org.vaadin.johannest.loadtestdriver;

import org.junit.Before;
import org.junit.Test;

public class LoadTestConfiguratorTest {

    private LoadTestConfigurator configurator;

    @Before
    public void setup() {
        ConfigurationParameters parameters = new ConfigurationParameters(10, 5, 2, -1, -1);
        configurator = new LoadTestConfigurator(parameters);
        configurator.setTempFilePath("src/test/resources");
        configurator.setResourcesPath("src/test/resources");
        configurator.setClassName("MyUI_ScalabilityTest");
    }

    @Test
    public void readConnectorMap_giveExampleResponseWithHtmlEcoding_connectorMappingsAreFound() {
        String exampleResponseFileContent = configurator.readFileContent("src/test/resources/example_init.html");
        configurator.readConnectorMap(exampleResponseFileContent, "tmp");
    }

    @Test
    public void doRequestBodyTreatments_withExampleRequestBodyAndConnectorMapping_connectorIdsAreReplacedRight() {
        String exampleResponseFileContent = configurator.readFileContent("src/test/resources/example_init.html");
        configurator.readConnectorMap(exampleResponseFileContent, "tmp");
        String result = configurator.doRequestBodyTreatments(configurator.readFileContent("src/test/resources/resources/Bakery_AddOrder_0009_request.txt"));
        System.out.println(result);
    }

    @Test
    public void configureTestFile_tryEndToEndConfiguring_verifyResults() {
        ConfigurationParameters parameters = new ConfigurationParameters(1, 1, 1, 1, 5);
        configurator = new LoadTestConfigurator(parameters);
        configurator.setTempFilePath("src/test/resources");
        configurator.setResourcesPath("src/test/resources/resources");
        configurator.setClassName("LoginTest");

        String resultFile = configurator.configureTestFile(true);
        System.out.println("-----------------------");
        System.out.println(resultFile);
    }



}
