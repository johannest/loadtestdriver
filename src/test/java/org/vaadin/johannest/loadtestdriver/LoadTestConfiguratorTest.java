package org.vaadin.johannest.loadtestdriver;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LoadTestConfiguratorTest {

    private LoadTestConfigurator configurator;

    @Before
    public void setup() {
        LoadTestParameters parameters = new LoadTestParameters(10, 5, 2, -1, -1);
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
    public void configureTestFile_tryEndToEndConfiguring_verifyResults() {
        LoadTestParameters parameters = new LoadTestParameters(10, 5, 2, 1, 5);
        configurator = new LoadTestConfigurator(parameters);
        configurator.setTempFilePath("src/test/resources");
        configurator.setResourcesPath("src/test/resources");
        configurator.setClassName("MyUI_ScalabilityTest");

        String resultFile = configurator.configureTestFile(true);
        System.out.println("-----------------------");
        System.out.println(resultFile);
    }

}
