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
        String exampleResponseFileContent = "{\"v-uiId\":0," +
                "\"uidl\":\"{\\\"Vaadin-Security-Key\\\":\\\"dc217c0d-2d94-432d-8b71-08d45bbbb009\\\"," +
                "\\\"Vaadin-Push-ID\\\":\\\"c9677c84-fefe-464d-88c1-74ad09663e1a\\\",\\\"syncId\\\": 0, \\\"resynchronize\\\": true, \\\"clientId\\\": 0, \\\"changes\\\" : [[\\\"change\\\",{\\\"pid\\\":\\\"0\\\"},[\\\"0\\\",{\\\"id\\\":\\\"0\\\",\\\"location\\\":\\\"http://192.168.1.117:8080\\\\/ui\\\",\\\"focused\\\":\\\"5\\\",\\\"v\\\":{\\\"action\\\":\\\"\\\"}},[\\\"actions\\\",{},[\\\"action\\\",{\\\"key\\\":\\\"1\\\",\\\"kc\\\":13,\\\"mk\\\":[]}]]]],[\\\"change\\\",{\\\"pid\\\":\\\"5\\\"},[\\\"1\\\",{\\\"id\\\":\\\"5\\\"}]],[\\\"change\\\",{\\\"pid\\\":\\\"6\\\"},[\\\"2\\\",{\\\"id\\\":\\\"6\\\"}]]], \\\"state\\\":{\\\"0\\\":{\\\"pageState\\\":{\\\"title\\\":\\\"My\\\"},\\\"localeServiceState\\\":{\\\"localeData\\\":[{\\\"name\\\":\\\"en_US\\\",\\\"monthNames\\\":[\\\"January\\\",\\\"February\\\",\\\"March\\\",\\\"April\\\",\\\"May\\\",\\\"June\\\",\\\"July\\\",\\\"August\\\",\\\"September\\\",\\\"October\\\",\\\"November\\\",\\\"December\\\"],\\\"shortMonthNames\\\":[\\\"Jan\\\",\\\"Feb\\\",\\\"Mar\\\",\\\"Apr\\\",\\\"May\\\",\\\"Jun\\\",\\\"Jul\\\",\\\"Aug\\\",\\\"Sep\\\",\\\"Oct\\\",\\\"Nov\\\",\\\"Dec\\\"],\\\"shortDayNames\\\":[\\\"Sun\\\",\\\"Mon\\\",\\\"Tue\\\",\\\"Wed\\\",\\\"Thu\\\",\\\"Fri\\\",\\\"Sat\\\"],\\\"dayNames\\\":[\\\"Sunday\\\",\\\"Monday\\\",\\\"Tuesday\\\",\\\"Wednesday\\\",\\\"Thursday\\\",\\\"Friday\\\",\\\"Saturday\\\"],\\\"firstDayOfWeek\\\":0,\\\"dateFormat\\\":\\\"M/d/yy\\\",\\\"twelveHourClock\\\":true,\\\"hourMinuteDelimiter\\\":\\\":\\\",\\\"am\\\":\\\"AM\\\",\\\"pm\\\":\\\"PM\\\"}]},\\\"theme\\\":\\\"mytheme\\\",\\\"height\\\":\\\"100.0%\\\",\\\"width\\\":\\\"100.0%\\\"},\\\"10\\\":{\\\"styles\\\":[\\\"login-information\\\"]},\\\"11\\\":{\\\"contentMode\\\":\\\"HTML\\\",\\\"text\\\":\\\"<h1>Login Information</h1>Log in as &quot;admin&quot; to have full access. Log in with any other username to have read-only access. For all users, any password is fine\\\",\\\"width\\\":\\\"100.0%\\\"},\\\"2\\\":{\\\"styles\\\":[\\\"login-screen\\\"]},\\\"3\\\":{\\\"childData\\\":{\\\"4\\\":{\\\"alignmentBitmask\\\":48,\\\"expandRatio\\\":0}},\\\"width\\\":\\\"100.0%\\\",\\\"styles\\\":[\\\"centering-layout\\\"]},\\\"4\\\":{\\\"spacing\\\":true,\\\"childData\\\":{\\\"5\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0},\\\"6\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0},\\\"7\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0}},\\\"styles\\\":[\\\"login-form\\\"]},\\\"5\\\":{\\\"text\\\":\\\"admin\\\",\\\"width\\\":\\\"15.0em\\\",\\\"caption\\\":\\\"Username\\\",\\\"id\\\":\\\"login-uname-field\\\"},\\\"6\\\":{\\\"text\\\":\\\"\\\",\\\"width\\\":\\\"15.0em\\\",\\\"description\\\":\\\"Write anything\\\",\\\"caption\\\":\\\"Password\\\",\\\"id\\\":\\\"login-passwd-field\\\"},\\\"7\\\":{\\\"styles\\\":[\\\"buttons\\\"]},\\\"8\\\":{\\\"disableOnClick\\\":true,\\\"clickShortcutKeyCode\\\":13,\\\"caption\\\":\\\"Login\\\",\\\"styles\\\":[\\\"friendly\\\"],\\\"id\\\":\\\"login-login-btn\\\"},\\\"9\\\":{\\\"caption\\\":\\\"Forgot password?\\\",\\\"styles\\\":[\\\"link\\\"]}}, \\\"types\\\":{\\\"0\\\":\\\"0\\\",\\\"1\\\":\\\"3\\\",\\\"10\\\":\\\"6\\\",\\\"11\\\":\\\"9\\\",\\\"2\\\":\\\"7\\\",\\\"3\\\":\\\"8\\\",\\\"4\\\":\\\"5\\\",\\\"5\\\":\\\"1\\\",\\\"6\\\":\\\"2\\\",\\\"7\\\":\\\"6\\\",\\\"8\\\":\\\"4\\\",\\\"9\\\":\\\"4\\\"}, \\\"hierarchy\\\":{\\\"0\\\":[\\\"2\\\",\\\"1\\\"],\\\"1\\\":[],\\\"10\\\":[\\\"11\\\"],\\\"2\\\":[\\\"3\\\",\\\"10\\\"],\\\"3\\\":[\\\"4\\\"],\\\"4\\\":[\\\"5\\\",\\\"6\\\",\\\"7\\\"],\\\"7\\\":[\\\"8\\\",\\\"9\\\"]}, \\\"rpc\\\" : [], \\\"meta\\\" : {\\\"repaintAll\\\":true}, \\\"resources\\\" : {}, \\\"typeMappings\\\" : { \\\"com.vaadin.ui.AbstractSingleComponentContainer\\\" : 10 , \\\"com.vaadin.ui.AbstractFocusable\\\" : 11 , \\\"com.vaadin.ui.AbstractField\\\" : 12 , \\\"com.vaadin.server.AbstractClientConnector\\\" : 13 , \\\"com.vaadin.ui.CssLayout\\\" : 6 , \\\"org.vaadin.test.webinar.MyUI\\\" : 0 , \\\"com.vaadin.ui.AbstractComponentContainer\\\" : 14 , \\\"com.vaadin.server.AbstractExtension\\\" : 15 , \\\"com.vaadin.ui.AbstractOrderedLayout\\\" : 16 , \\\"com.vaadin.ui.AbstractComponent\\\" : 17 , \\\"com.vaadin.ui.FormLayout\\\" : 5 , \\\"com.vaadin.ui.TextField\\\" : 1 , \\\"com.vaadin.ui.AbstractTextField\\\" : 18 , \\\"com.vaadin.ui.Label\\\" : 9 , \\\"com.vaadin.ui.PasswordField\\\" : 2 , \\\"com.vaadin.ui.AbstractLayout\\\" : 19 , \\\"com.vaadin.server.Responsive\\\" : 3 , \\\"com.vaadin.ui.VerticalLayout\\\" : 8 , \\\"com.vaadin.ui.UI\\\" : 20 , \\\"com.vaadin.ui.Button\\\" : 4 , \\\"org.vaadin.test.webinar.samples.authentication.LoginScreen\\\" : 7 }, \\\"typeInheritanceMap\\\" : { \\\"10\\\" : 17 , \\\"11\\\" : 17 , \\\"12\\\" : 17 , \\\"6\\\" : 19 , \\\"0\\\" : 20 , \\\"14\\\" : 17 , \\\"15\\\" : 13 , \\\"16\\\" : 19 , \\\"17\\\" : 13 , \\\"5\\\" : 16 , \\\"1\\\" : 18 , \\\"18\\\" : 12 , \\\"9\\\" : 17 , \\\"2\\\" : 18 , \\\"19\\\" : 14 , \\\"3\\\" : 15 , \\\"8\\\" : 16 , \\\"20\\\" : 10 , \\\"4\\\" : 11 , \\\"7\\\" : 6 }, \\\"timings\\\":[72, 72]}\"}";
        configurator.readConnectorMap(exampleResponseFileContent, "tmp");

        final Map<String, String> propertyKeyMap = configurator.getConnectorIdToMatchingPropertyKeyMap();
        final Map<String, String> propertyValueMap = configurator.getConnectorIdToMatchingPropertyValueMap();
        final Map<String, List<String>> fileNameMap = configurator.getConnectorIdToRequestFileNames();

        Assert.assertEquals(9, propertyValueMap.size());
        Assert.assertEquals("Forgot password?", propertyValueMap.get("9"));
        Assert.assertEquals("[\"login-screen\"]", propertyValueMap.get("2"));
        Assert.assertEquals("login-uname-field", propertyValueMap.get("5"));

        Assert.assertEquals("caption", propertyKeyMap.get("9"));
        Assert.assertEquals("styles", propertyKeyMap.get("2"));
        Assert.assertEquals("id", propertyKeyMap.get("5"));
    }

    @Test
    public void readConnectorMap_giveExampleResponseWithoutHtmlEncoding_connectorMappingsAreFound() {
        String exampleResponseFileContent = "for(;;);[{\"syncId\": 1, \"resynchronize\": true, \"clientId\": 1, \"changes\" : [[\"change\",{\"pid\":\"0\"},[\"0\",{\"id\":\"0\",\"location\":\"http:\\/\\/192.168.1.117:8080\\/ui\",\"v\":{\"action\":\"\"}},[\"actions\",{},[\"action\",{\"key\":\"1\",\"kc\":13,\"mk\":[]}]]]],[\"change\",{\"pid\":\"5\"},[\"1\",{\"id\":\"5\"}]],[\"change\",{\"pid\":\"6\"},[\"2\",{\"id\":\"6\"}]]], \"state\":{\"0\":{\"pageState\":{\"title\":\"My\"},\"localeServiceState\":{\"localeData\":[{\"name\":\"en_US\",\"monthNames\":[\"January\",\"February\",\"March\",\"April\",\"May\",\"June\",\"July\",\"August\",\"September\",\"October\",\"November\",\"December\"],\"shortMonthNames\":[\"Jan\",\"Feb\",\"Mar\",\"Apr\",\"May\",\"Jun\",\"Jul\",\"Aug\",\"Sep\",\"Oct\",\"Nov\",\"Dec\"],\"shortDayNames\":[\"Sun\",\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\"],\"dayNames\":[\"Sunday\",\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"],\"firstDayOfWeek\":0,\"dateFormat\":\"M/d/yy\",\"twelveHourClock\":true,\"hourMinuteDelimiter\":\":\",\"am\":\"AM\",\"pm\":\"PM\"}]},\"theme\":\"mytheme\",\"height\":\"100.0%\",\"width\":\"100.0%\"},\"10\":{\"styles\":[\"login-information\"]},\"11\":{\"contentMode\":\"HTML\",\"text\":\"<h1>Login Information</h1>Log in as &quot;admin&quot; to have full access. Log in with any other username to have read-only access. For all users, any password is fine\",\"width\":\"100.0%\"},\"2\":{\"styles\":[\"login-screen\"]},\"3\":{\"childData\":{\"4\":{\"alignmentBitmask\":48,\"expandRatio\":0}},\"width\":\"100.0%\",\"styles\":[\"centering-layout\"]},\"4\":{\"spacing\":true,\"childData\":{\"5\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"6\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"7\":{\"alignmentBitmask\":5,\"expandRatio\":0}},\"styles\":[\"login-form\"]},\"5\":{\"text\":\"admin\",\"width\":\"15.0em\",\"caption\":\"Username\",\"id\":\"login-uname-field\"},\"6\":{\"text\":\"\",\"width\":\"15.0em\",\"description\":\"Write anything\",\"caption\":\"Password\",\"id\":\"login-passwd-field\"},\"7\":{\"styles\":[\"buttons\"]},\"8\":{\"disableOnClick\":true,\"clickShortcutKeyCode\":13,\"caption\":\"Login\",\"styles\":[\"friendly\"],\"id\":\"login-login-btn\"},\"9\":{\"caption\":\"Forgot password?\",\"styles\":[\"link\"]}}, \"types\":{\"0\":\"0\",\"1\":\"3\",\"10\":\"6\",\"11\":\"9\",\"2\":\"7\",\"3\":\"8\",\"4\":\"5\",\"5\":\"1\",\"6\":\"2\",\"7\":\"6\",\"8\":\"4\",\"9\":\"4\"}, \"hierarchy\":{\"0\":[\"2\",\"1\"],\"1\":[],\"10\":[\"11\"],\"2\":[\"3\",\"10\"],\"3\":[\"4\"],\"4\":[\"5\",\"6\",\"7\"],\"7\":[\"8\",\"9\"]}, \"rpc\" : [], \"meta\" : {\"repaintAll\":true}, \"resources\" : {}, \"typeMappings\" : { \"com.vaadin.ui.AbstractSingleComponentContainer\" : 10 , \"com.vaadin.ui.AbstractFocusable\" : 11 , \"com.vaadin.ui.AbstractField\" : 12 , \"com.vaadin.server.AbstractClientConnector\" : 13 , \"com.vaadin.ui.CssLayout\" : 6 , \"org.vaadin.test.webinar.MyUI\" : 0 , \"com.vaadin.ui.AbstractComponentContainer\" : 14 , \"com.vaadin.server.AbstractExtension\" : 15 , \"com.vaadin.ui.AbstractOrderedLayout\" : 16 , \"com.vaadin.ui.AbstractComponent\" : 17 , \"com.vaadin.ui.FormLayout\" : 5 , \"com.vaadin.ui.TextField\" : 1 , \"com.vaadin.ui.AbstractTextField\" : 18 , \"com.vaadin.ui.Label\" : 9 , \"com.vaadin.ui.PasswordField\" : 2 , \"com.vaadin.ui.AbstractLayout\" : 19 , \"com.vaadin.server.Responsive\" : 3 , \"com.vaadin.ui.VerticalLayout\" : 8 , \"com.vaadin.ui.UI\" : 20 , \"com.vaadin.ui.Button\" : 4 , \"org.vaadin.test.webinar.samples.authentication.LoginScreen\" : 7 }, \"typeInheritanceMap\" : { \"10\" : 17 , \"11\" : 17 , \"12\" : 17 , \"6\" : 19 , \"0\" : 20 , \"14\" : 17 , \"15\" : 13 , \"16\" : 19 , \"17\" : 13 , \"5\" : 16 , \"1\" : 18 , \"18\" : 12 , \"9\" : 17 , \"2\" : 18 , \"19\" : 14 , \"3\" : 15 , \"8\" : 16 , \"20\" : 10 , \"4\" : 11 , \"7\" : 6 }, \"timings\":[108, 36]}]";
        configurator.readConnectorMap(exampleResponseFileContent, "tmp");

        final Map<String, String> propertyKeyMap = configurator.getConnectorIdToMatchingPropertyKeyMap();
        final Map<String, String> propertyValueMap = configurator.getConnectorIdToMatchingPropertyValueMap();
        final Map<String, List<String>> fileNameMap = configurator.getConnectorIdToRequestFileNames();

        Assert.assertEquals(9, propertyValueMap.size());
        Assert.assertEquals("Forgot password?", propertyValueMap.get("9"));
        Assert.assertEquals("[\"login-information\"]", propertyValueMap.get("10"));
        Assert.assertEquals("login-uname-field", propertyValueMap.get("5"));

        Assert.assertEquals("caption", propertyKeyMap.get("9"));
        Assert.assertEquals("styles", propertyKeyMap.get("10"));
        Assert.assertEquals("id", propertyKeyMap.get("5"));
    }

    @Test
    public void createExtractorRegex_giveExampleResponseAndMaps_correctRegexpsAreGet() {
        String exampleResponseFileContent = "for(;;);[{\"syncId\": 1, \"resynchronize\": true, \"clientId\": 1, \"changes\" : [[\"change\",{\"pid\":\"0\"},[\"0\",{\"id\":\"0\",\"location\":\"http:\\/\\/192.168.1.117:8080\\/ui\",\"v\":{\"action\":\"\"}},[\"actions\",{},[\"action\",{\"key\":\"1\",\"kc\":13,\"mk\":[]}]]]],[\"change\",{\"pid\":\"5\"},[\"1\",{\"id\":\"5\"}]],[\"change\",{\"pid\":\"6\"},[\"2\",{\"id\":\"6\"}]]], \"state\":{\"0\":{\"pageState\":{\"title\":\"My\"},\"localeServiceState\":{\"localeData\":[{\"name\":\"en_US\",\"monthNames\":[\"January\",\"February\",\"March\",\"April\",\"May\",\"June\",\"July\",\"August\",\"September\",\"October\",\"November\",\"December\"],\"shortMonthNames\":[\"Jan\",\"Feb\",\"Mar\",\"Apr\",\"May\",\"Jun\",\"Jul\",\"Aug\",\"Sep\",\"Oct\",\"Nov\",\"Dec\"],\"shortDayNames\":[\"Sun\",\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\"],\"dayNames\":[\"Sunday\",\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"],\"firstDayOfWeek\":0,\"dateFormat\":\"M/d/yy\",\"twelveHourClock\":true,\"hourMinuteDelimiter\":\":\",\"am\":\"AM\",\"pm\":\"PM\"}]},\"theme\":\"mytheme\",\"height\":\"100.0%\",\"width\":\"100.0%\"},\"10\":{\"styles\":[\"login-information\"]},\"11\":{\"contentMode\":\"HTML\",\"text\":\"<h1>Login Information</h1>Log in as &quot;admin&quot; to have full access. Log in with any other username to have read-only access. For all users, any password is fine\",\"width\":\"100.0%\"},\"2\":{\"styles\":[\"login-screen\"]},\"3\":{\"childData\":{\"4\":{\"alignmentBitmask\":48,\"expandRatio\":0}},\"width\":\"100.0%\",\"styles\":[\"centering-layout\"]},\"4\":{\"spacing\":true,\"childData\":{\"5\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"6\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"7\":{\"alignmentBitmask\":5,\"expandRatio\":0}},\"styles\":[\"login-form\"]},\"5\":{\"text\":\"admin\",\"width\":\"15.0em\",\"caption\":\"Username\",\"id\":\"login-uname$-field\"},\"6\":{\"text\":\"\",\"width\":\"15.0em\",\"description\":\"Write anything\",\"caption\":\"Password\",\"id\":\"login-passwd-field\"},\"7\":{\"styles\":[\"buttons\"]},\"8\":{\"disableOnClick\":true,\"clickShortcutKeyCode\":13,\"caption\":\"Login\",\"styles\":[\"friendly\"],\"id\":\"login-login-btn\"},\"9\":{\"caption\":\"Forgot password?\",\"styles\":[\"link\"]}}, \"types\":{\"0\":\"0\",\"1\":\"3\",\"10\":\"6\",\"11\":\"9\",\"2\":\"7\",\"3\":\"8\",\"4\":\"5\",\"5\":\"1\",\"6\":\"2\",\"7\":\"6\",\"8\":\"4\",\"9\":\"4\"}, \"hierarchy\":{\"0\":[\"2\",\"1\"],\"1\":[],\"10\":[\"11\"],\"2\":[\"3\",\"10\"],\"3\":[\"4\"],\"4\":[\"5\",\"6\",\"7\"],\"7\":[\"8\",\"9\"]}, \"rpc\" : [], \"meta\" : {\"repaintAll\":true}, \"resources\" : {}, \"typeMappings\" : { \"com.vaadin.ui.AbstractSingleComponentContainer\" : 10 , \"com.vaadin.ui.AbstractFocusable\" : 11 , \"com.vaadin.ui.AbstractField\" : 12 , \"com.vaadin.server.AbstractClientConnector\" : 13 , \"com.vaadin.ui.CssLayout\" : 6 , \"org.vaadin.test.webinar.MyUI\" : 0 , \"com.vaadin.ui.AbstractComponentContainer\" : 14 , \"com.vaadin.server.AbstractExtension\" : 15 , \"com.vaadin.ui.AbstractOrderedLayout\" : 16 , \"com.vaadin.ui.AbstractComponent\" : 17 , \"com.vaadin.ui.FormLayout\" : 5 , \"com.vaadin.ui.TextField\" : 1 , \"com.vaadin.ui.AbstractTextField\" : 18 , \"com.vaadin.ui.Label\" : 9 , \"com.vaadin.ui.PasswordField\" : 2 , \"com.vaadin.ui.AbstractLayout\" : 19 , \"com.vaadin.server.Responsive\" : 3 , \"com.vaadin.ui.VerticalLayout\" : 8 , \"com.vaadin.ui.UI\" : 20 , \"com.vaadin.ui.Button\" : 4 , \"org.vaadin.test.webinar.samples.authentication.LoginScreen\" : 7 }, \"typeInheritanceMap\" : { \"10\" : 17 , \"11\" : 17 , \"12\" : 17 , \"6\" : 19 , \"0\" : 20 , \"14\" : 17 , \"15\" : 13 , \"16\" : 19 , \"17\" : 13 , \"5\" : 16 , \"1\" : 18 , \"18\" : 12 , \"9\" : 17 , \"2\" : 18 , \"19\" : 14 , \"3\" : 15 , \"8\" : 16 , \"20\" : 10 , \"4\" : 11 , \"7\" : 6 }, \"timings\":[108, 36]}]";
        configurator.readConnectorMap(exampleResponseFileContent, "tmp");

        Assert.assertEquals("\tval extract_5_Id = regex(\"\"\",\"([0-9]*)\":\\{[^\\}]{0,250}\"id\":\"login\\-uname\\$\\-field\"\"\").saveAs(\"_5_Id\")", configurator.createExtractorRegex(String.valueOf(5)));
        Assert.assertEquals("\tval extract_6_Id = regex(\"\"\",\"([0-9]*)\":\\{[^\\}]{0,250}\"id\":\"login\\-passwd\\-field\"\"\").saveAs(\"_6_Id\")", configurator.createExtractorRegex(String.valueOf(6)));
        Assert.assertEquals("\tval extract_9_Id = regex(\"\"\",\"([0-9]*)\":\\{[^\\}]{0,250}\"caption\":\"Forgot password\\?\"\"\").saveAs(\"_9_Id\")", configurator.createExtractorRegex(String.valueOf(9)));
    }

    @Test
    public void doRequestBodyTreatments_giveExampleRequestBodyAfterConnectorMapping_verifyThatRequestBodyIsTreatedRight() {
        String exampleResponseFileContent1 = "{\"v-uiId\":0," +
                "\"uidl\":\"{\\\"Vaadin-Security-Key\\\":\\\"dc217c0d-2d94-432d-8b71-08d45bbbb009\\\"," +
                "\\\"Vaadin-Push-ID\\\":\\\"c9677c84-fefe-464d-88c1-74ad09663e1a\\\",\\\"syncId\\\": 0, \\\"resynchronize\\\": true, \\\"clientId\\\": 0, \\\"changes\\\" : [[\\\"change\\\",{\\\"pid\\\":\\\"0\\\"},[\\\"0\\\",{\\\"id\\\":\\\"0\\\",\\\"location\\\":\\\"http://192.168.1.117:8080\\\\/ui\\\",\\\"focused\\\":\\\"5\\\",\\\"v\\\":{\\\"action\\\":\\\"\\\"}},[\\\"actions\\\",{},[\\\"action\\\",{\\\"key\\\":\\\"1\\\",\\\"kc\\\":13,\\\"mk\\\":[]}]]]],[\\\"change\\\",{\\\"pid\\\":\\\"5\\\"},[\\\"1\\\",{\\\"id\\\":\\\"5\\\"}]],[\\\"change\\\",{\\\"pid\\\":\\\"6\\\"},[\\\"2\\\",{\\\"id\\\":\\\"6\\\"}]]], \\\"state\\\":{\\\"0\\\":{\\\"pageState\\\":{\\\"title\\\":\\\"My\\\"},\\\"localeServiceState\\\":{\\\"localeData\\\":[{\\\"name\\\":\\\"en_US\\\",\\\"monthNames\\\":[\\\"January\\\",\\\"February\\\",\\\"March\\\",\\\"April\\\",\\\"May\\\",\\\"June\\\",\\\"July\\\",\\\"August\\\",\\\"September\\\",\\\"October\\\",\\\"November\\\",\\\"December\\\"],\\\"shortMonthNames\\\":[\\\"Jan\\\",\\\"Feb\\\",\\\"Mar\\\",\\\"Apr\\\",\\\"May\\\",\\\"Jun\\\",\\\"Jul\\\",\\\"Aug\\\",\\\"Sep\\\",\\\"Oct\\\",\\\"Nov\\\",\\\"Dec\\\"],\\\"shortDayNames\\\":[\\\"Sun\\\",\\\"Mon\\\",\\\"Tue\\\",\\\"Wed\\\",\\\"Thu\\\",\\\"Fri\\\",\\\"Sat\\\"],\\\"dayNames\\\":[\\\"Sunday\\\",\\\"Monday\\\",\\\"Tuesday\\\",\\\"Wednesday\\\",\\\"Thursday\\\",\\\"Friday\\\",\\\"Saturday\\\"],\\\"firstDayOfWeek\\\":0,\\\"dateFormat\\\":\\\"M/d/yy\\\",\\\"twelveHourClock\\\":true,\\\"hourMinuteDelimiter\\\":\\\":\\\",\\\"am\\\":\\\"AM\\\",\\\"pm\\\":\\\"PM\\\"}]},\\\"theme\\\":\\\"mytheme\\\",\\\"height\\\":\\\"100.0%\\\",\\\"width\\\":\\\"100.0%\\\"},\\\"10\\\":{\\\"styles\\\":[\\\"login-information\\\"]},\\\"11\\\":{\\\"contentMode\\\":\\\"HTML\\\",\\\"text\\\":\\\"<h1>Login Information</h1>Log in as &quot;admin&quot; to have full access. Log in with any other username to have read-only access. For all users, any password is fine\\\",\\\"width\\\":\\\"100.0%\\\"},\\\"2\\\":{\\\"styles\\\":[\\\"login-screen\\\"]},\\\"3\\\":{\\\"childData\\\":{\\\"4\\\":{\\\"alignmentBitmask\\\":48,\\\"expandRatio\\\":0}},\\\"width\\\":\\\"100.0%\\\",\\\"styles\\\":[\\\"centering-layout\\\"]},\\\"4\\\":{\\\"spacing\\\":true,\\\"childData\\\":{\\\"5\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0},\\\"6\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0},\\\"7\\\":{\\\"alignmentBitmask\\\":5,\\\"expandRatio\\\":0}},\\\"styles\\\":[\\\"login-form\\\"]},\\\"5\\\":{\\\"text\\\":\\\"admin\\\",\\\"width\\\":\\\"15.0em\\\",\\\"caption\\\":\\\"Username\\\",\\\"id\\\":\\\"login-uname-field\\\"},\\\"6\\\":{\\\"text\\\":\\\"\\\",\\\"width\\\":\\\"15.0em\\\",\\\"description\\\":\\\"Write anything\\\",\\\"caption\\\":\\\"Password\\\",\\\"id\\\":\\\"login-passwd-field\\\"},\\\"7\\\":{\\\"styles\\\":[\\\"buttons\\\"]},\\\"8\\\":{\\\"disableOnClick\\\":true,\\\"clickShortcutKeyCode\\\":13,\\\"caption\\\":\\\"Login\\\",\\\"styles\\\":[\\\"friendly\\\"],\\\"id\\\":\\\"login-login-btn\\\"},\\\"9\\\":{\\\"caption\\\":\\\"Forgot password?\\\",\\\"styles\\\":[\\\"link\\\"]}}, \\\"types\\\":{\\\"0\\\":\\\"0\\\",\\\"1\\\":\\\"3\\\",\\\"10\\\":\\\"6\\\",\\\"11\\\":\\\"9\\\",\\\"2\\\":\\\"7\\\",\\\"3\\\":\\\"8\\\",\\\"4\\\":\\\"5\\\",\\\"5\\\":\\\"1\\\",\\\"6\\\":\\\"2\\\",\\\"7\\\":\\\"6\\\",\\\"8\\\":\\\"4\\\",\\\"9\\\":\\\"4\\\"}, \\\"hierarchy\\\":{\\\"0\\\":[\\\"2\\\",\\\"1\\\"],\\\"1\\\":[],\\\"10\\\":[\\\"11\\\"],\\\"2\\\":[\\\"3\\\",\\\"10\\\"],\\\"3\\\":[\\\"4\\\"],\\\"4\\\":[\\\"5\\\",\\\"6\\\",\\\"7\\\"],\\\"7\\\":[\\\"8\\\",\\\"9\\\"]}, \\\"rpc\\\" : [], \\\"meta\\\" : {\\\"repaintAll\\\":true}, \\\"resources\\\" : {}, \\\"typeMappings\\\" : { \\\"com.vaadin.ui.AbstractSingleComponentContainer\\\" : 10 , \\\"com.vaadin.ui.AbstractFocusable\\\" : 11 , \\\"com.vaadin.ui.AbstractField\\\" : 12 , \\\"com.vaadin.server.AbstractClientConnector\\\" : 13 , \\\"com.vaadin.ui.CssLayout\\\" : 6 , \\\"org.vaadin.test.webinar.MyUI\\\" : 0 , \\\"com.vaadin.ui.AbstractComponentContainer\\\" : 14 , \\\"com.vaadin.server.AbstractExtension\\\" : 15 , \\\"com.vaadin.ui.AbstractOrderedLayout\\\" : 16 , \\\"com.vaadin.ui.AbstractComponent\\\" : 17 , \\\"com.vaadin.ui.FormLayout\\\" : 5 , \\\"com.vaadin.ui.TextField\\\" : 1 , \\\"com.vaadin.ui.AbstractTextField\\\" : 18 , \\\"com.vaadin.ui.Label\\\" : 9 , \\\"com.vaadin.ui.PasswordField\\\" : 2 , \\\"com.vaadin.ui.AbstractLayout\\\" : 19 , \\\"com.vaadin.server.Responsive\\\" : 3 , \\\"com.vaadin.ui.VerticalLayout\\\" : 8 , \\\"com.vaadin.ui.UI\\\" : 20 , \\\"com.vaadin.ui.Button\\\" : 4 , \\\"org.vaadin.test.webinar.samples.authentication.LoginScreen\\\" : 7 }, \\\"typeInheritanceMap\\\" : { \\\"10\\\" : 17 , \\\"11\\\" : 17 , \\\"12\\\" : 17 , \\\"6\\\" : 19 , \\\"0\\\" : 20 , \\\"14\\\" : 17 , \\\"15\\\" : 13 , \\\"16\\\" : 19 , \\\"17\\\" : 13 , \\\"5\\\" : 16 , \\\"1\\\" : 18 , \\\"18\\\" : 12 , \\\"9\\\" : 17 , \\\"2\\\" : 18 , \\\"19\\\" : 14 , \\\"3\\\" : 15 , \\\"8\\\" : 16 , \\\"20\\\" : 10 , \\\"4\\\" : 11 , \\\"7\\\" : 6 }, \\\"timings\\\":[72, 72]}\"}";
        String exampleResponseFileContent2 = "for(;;);[{\"syncId\": 1, \"resynchronize\": true, \"clientId\": 1, " +
                "\"changes\" : [[\"change\",{\"pid\":\"0\"},[\"0\",{\"id\":\"0\",\"location\":\"http:\\/\\/192.168.1.117:8080\\/ui\",\"v\":{\"action\":\"\"}},[\"actions\",{},[\"action\",{\"key\":\"1\",\"kc\":13,\"mk\":[]}]]]],[\"change\",{\"pid\":\"5\"},[\"1\",{\"id\":\"5\"}]],[\"change\",{\"pid\":\"6\"},[\"2\",{\"id\":\"6\"}]]], \"state\":{\"0\":{\"pageState\":{\"title\":\"My\"},\"localeServiceState\":{\"localeData\":[{\"name\":\"en_US\",\"monthNames\":[\"January\",\"February\",\"March\",\"April\",\"May\",\"June\",\"July\",\"August\",\"September\",\"October\",\"November\",\"December\"],\"shortMonthNames\":[\"Jan\",\"Feb\",\"Mar\",\"Apr\",\"May\",\"Jun\",\"Jul\",\"Aug\",\"Sep\",\"Oct\",\"Nov\",\"Dec\"],\"shortDayNames\":[\"Sun\",\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\"],\"dayNames\":[\"Sunday\",\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"],\"firstDayOfWeek\":0,\"dateFormat\":\"M/d/yy\",\"twelveHourClock\":true,\"hourMinuteDelimiter\":\":\",\"am\":\"AM\",\"pm\":\"PM\"}]},\"theme\":\"mytheme\",\"height\":\"100.0%\",\"width\":\"100.0%\"},\"10\":{\"styles\":[\"login-information\"]},\"11\":{\"contentMode\":\"HTML\",\"text\":\"<h1>Login Information</h1>Log in as &quot;admin&quot; to have full access. Log in with any other username to have read-only access. For all users, any password is fine\",\"width\":\"100.0%\"},\"2\":{\"styles\":[\"login-screen\"]},\"3\":{\"childData\":{\"4\":{\"alignmentBitmask\":48,\"expandRatio\":0}},\"width\":\"100.0%\",\"styles\":[\"centering-layout\"]},\"4\":{\"spacing\":true,\"childData\":{\"5\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"6\":{\"alignmentBitmask\":5,\"expandRatio\":0},\"7\":{\"alignmentBitmask\":5,\"expandRatio\":0}},\"styles\":[\"login-form\"]},\"5\":{\"text\":\"admin\",\"width\":\"15.0em\",\"caption\":\"Username\",\"id\":\"login-uname-field\"},\"6\":{\"text\":\"\",\"width\":\"15.0em\",\"description\":\"Write anything\",\"caption\":\"Password\",\"id\":\"login-passwd-field\"},\"7\":{\"styles\":[\"buttons\"]},\"8\":{\"disableOnClick\":true,\"clickShortcutKeyCode\":13,\"caption\":\"Login\",\"styles\":[\"friendly\"],\"id\":\"login-login-btn\"},\"9\":{\"caption\":\"Forgot password?\",\"styles\":[\"link\"]}}, \"types\":{\"0\":\"0\",\"1\":\"3\",\"10\":\"6\",\"11\":\"9\",\"2\":\"7\",\"3\":\"8\",\"4\":\"5\",\"5\":\"1\",\"6\":\"2\",\"7\":\"6\",\"8\":\"4\",\"9\":\"4\"}, \"hierarchy\":{\"0\":[\"2\",\"1\"],\"1\":[],\"10\":[\"11\"],\"2\":[\"3\",\"10\"],\"3\":[\"4\"],\"4\":[\"5\",\"6\",\"7\"],\"7\":[\"8\",\"9\"]}, \"rpc\" : [], \"meta\" : {\"repaintAll\":true}, \"resources\" : {}, \"typeMappings\" : { \"com.vaadin.ui.AbstractSingleComponentContainer\" : 10 , \"com.vaadin.ui.AbstractFocusable\" : 11 , \"com.vaadin.ui.AbstractField\" : 12 , \"com.vaadin.server.AbstractClientConnector\" : 13 , \"com.vaadin.ui.CssLayout\" : 6 , \"org.vaadin.test.webinar.MyUI\" : 0 , \"com.vaadin.ui.AbstractComponentContainer\" : 14 , \"com.vaadin.server.AbstractExtension\" : 15 , \"com.vaadin.ui.AbstractOrderedLayout\" : 16 , \"com.vaadin.ui.AbstractComponent\" : 17 , \"com.vaadin.ui.FormLayout\" : 5 , \"com.vaadin.ui.TextField\" : 1 , \"com.vaadin.ui.AbstractTextField\" : 18 , \"com.vaadin.ui.Label\" : 9 , \"com.vaadin.ui.PasswordField\" : 2 , \"com.vaadin.ui.AbstractLayout\" : 19 , \"com.vaadin.server.Responsive\" : 3 , \"com.vaadin.ui.VerticalLayout\" : 8 , \"com.vaadin.ui.UI\" : 20 , \"com.vaadin.ui.Button\" : 4 , \"org.vaadin.test.webinar.samples.authentication.LoginScreen\" : 7 }, \"typeInheritanceMap\" : { \"10\" : 17 , \"11\" : 17 , \"12\" : 17 , \"6\" : 19 , \"0\" : 20 , \"14\" : 17 , \"15\" : 13 , \"16\" : 19 , \"17\" : 13 , \"5\" : 16 , \"1\" : 18 , \"18\" : 12 , \"9\" : 17 , \"2\" : 18 , \"19\" : 14 , \"3\" : 15 , \"8\" : 16 , \"20\" : 10 , \"4\" : 11 , \"7\" : 6 }, \"timings\":[108, 36]}]";

        String exampleRequestBody = "{\"csrfToken\":\"dc217c0d-2d94-432d-8b71-08d45bbbb009\",\"rpc\":[[\"0\",\"com" +
                ".vaadin.shared.ui.ui.UIServerRpc\",\"resize\",[300,400,400,300]],[\"8\",\"com.vaadin.shared.ui.button.ButtonServerRpc\",\"disableOnClick\",[]],[\"8\",\"com.vaadin.shared.ui.button.ButtonServerRpc\",\"click\",[{\"altKey\":false,\"button\":\"LEFT\",\"clientX\":192,\"clientY\":254,\"ctrlKey\":false,\"metaKey\":false,\"relativeX\":171,\"relativeY\":16,\"shiftKey\":false,\"type\":1}]]],\"syncId\":1,\"clientId\":1,\"wsver\":\"7.7.4\"}";

        configurator.readConnectorMap(exampleResponseFileContent1, "tmp1");
        configurator.readConnectorMap(exampleResponseFileContent2, "tmp2");

        final String treatedRequestBody = configurator.doRequestBodyTreatments(exampleRequestBody);
        System.out.println(treatedRequestBody);
        Assert.assertEquals("{\"csrfToken\":\"${seckey}\",\"rpc\":[[\"0\",\"com.vaadin.shared.ui.ui.UIServerRpc\",\"resize\",[300,400,400,300]],[\"${_8_Id}\",\"com.vaadin.shared.ui.button.ButtonServerRpc\",\"disableOnClick\",[]],[\"${_8_Id}\",\"com.vaadin.shared.ui.button.ButtonServerRpc\",\"click\",[{\"altKey\":false,\"button\":\"LEFT\",\"clientX\":192,\"clientY\":254,\"ctrlKey\":false,\"metaKey\":false,\"relativeX\":171,\"relativeY\":16,\"shiftKey\":false,\"type\":1}]]],\"syncId\":${syncId},\"clientId\":${clientId},\"wsver\":\"7.7.4\"}",treatedRequestBody);
    }

    @Test
    public void configureTestFile_tryEndToEndConfiguring_verifyResults() {
        LoadTestParameters parameters = new LoadTestParameters(1, 1, 1, 1, 5);
        configurator = new LoadTestConfigurator(parameters);
        configurator.setTempFilePath("/dev/idea/loadtestdriver/src/test/resources");
        configurator.setResourcesPath("/dev/idea/loadtestdriver/src/test/resources/resources");
        configurator.setClassName("Barista_addOrder");

        String resultFile = configurator.configureTestFile(false);
        System.out.println("-----------------------");
        System.out.println(resultFile);
    }

}