package org.vaadin.johannest.loadtestdriver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;

public class LoadTestConfigurator {

    private final ConfigurationParameters configurationParameters;

    private final Map<String, String> nodeIdToCssIdMap = new HashMap<>();
    private final Map<String, String> nodeIdToLabelMap = new HashMap<>();
    private final Map<String, String> nodeIdToTextMap = new HashMap<>();
    private final Map<String, String> nodeIdToAddMap = new HashMap<>();
    private final Map<String, String> nodeIdToTagMap = new HashMap<>();
    private final Map<String, String> nodeIdToThemeMap = new HashMap<>();

    private final Map<String, List<String>> nodeIdToRequestFileNames = new HashMap<>();
    private final Set<String> requiredConnectorIds = new HashSet<>();

    private TreeMap<String, String> requestNamesAndBodies = new TreeMap<>();
    private TreeMap<String, String> responseNamesAndBodies = new TreeMap<>();
    private TreeMap<String, String> requestNamesAndBodiesProcessed = new TreeMap<>();

    private String uiInitRequestFileName;
    private String resourcesPath;
    private String tempFilePath;
    private String className;

    private Properties props;
    private List<String> lines;
    private List<String> connectorIdExtractors;

    private Integer uidlHeadersNo;

    private String processedScalaFilesContent;
    private String testFileNameWithPath;

    public LoadTestConfigurator(ConfigurationParameters configurationParameters) {
        this.configurationParameters = configurationParameters;
        loadPropertiesFile();
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void configureTestFile() {
        configureTestFile(true);
    }

    public String configureTestFile(boolean saveResults) {
        System.out.println("### configureTestFile, save=" + saveResults);
        testFileNameWithPath = tempFilePath + "/" + className + ".scala";
        Logger.getLogger(LoadTestConfigurator.class.getName()).info("Configuring test file: " + testFileNameWithPath);
        try {
            final File file = new File(testFileNameWithPath);
            final FileReader fr = new FileReader(file);
            final BufferedReader br = new BufferedReader(fr);

            lines = new ArrayList<>();
            connectorIdExtractors = new ArrayList<>();

            readScalaScriptAndDoInitialRefactoring(br, saveResults);

            addRegexExtractChecks();
            addRegexExtractDefinitions();
            addAdditionalImports();
            removePossibleBodyByteCheck();
            replacehardCodedUiIdAndPushIds();

            if (saveResults) {
                saveResultFile(file, lines);
            } else {
                StringBuilder sb = new StringBuilder();
                for (final String s : lines) {
                    sb.append(s).append("\n");
                }
                return sb.toString();
            }
        } catch (final FileNotFoundException e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to found file: " + testFileNameWithPath);
            e.printStackTrace();
        } catch (final IOException e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to access file: " + testFileNameWithPath);
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removePossibleBodyByteCheck() {
        List<String> newLines = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            String aline = lines.get(i);
            if (aline.contains(".check(bodyBytes.is(")) {
                newLines.add("\t\t\t)");
            } else {
                newLines.add(aline);
            }
        }
        lines = newLines;
    }

    private void readScalaScriptAndDoInitialRefactoring(BufferedReader br, boolean saveResults) throws IOException {
        boolean syncIdsInitialized = false;
        String line;
        String newLine = null;
        String previousLine;

        while ((line = br.readLine()) != null) {
            previousLine = newLine;
            newLine = line;

            if (newLine.contains("val scn")) {
                insertHelperMethods(lines);
            }

            if (!syncIdsInitialized) {
                syncIdsInitialized = initializeSyncAndClientIds(newLine, lines);
            }

            if (newLine.contains(".exec(http(")) {
                if (configurationParameters.pausesEnabled()) {
                    lines.add("\t\t.pause(" + configurationParameters.getMinPause() + ", " +
                            configurationParameters.getMaxPause() + ")");
                }
            }

            extractUidlHeaderNumber(newLine, previousLine);

            newLine = requestBodyTreatments(newLine, saveResults);

            if (newLine.contains("atOnceUsers")) {
                newLine = newLine.replaceFirst("inject\\(atOnceUsers\\(1\\)\\)",
                        "inject(rampUsers(" + configurationParameters.getConcurrentUsers() + ") during (" +
                                configurationParameters.getRampUpTime() + " seconds))");
            }

            lines.add(newLine);
        }
        br.close();
    }

    private void extractUidlHeaderNumber(String newLine, String previousLine) {
        if (uidlHeadersNo==null && newLine.contains("headers(headers_") && previousLine!=null && previousLine.contains("UIDL/?v-uiId=")) {
            try {
                String[] s = newLine.split("_");
                uidlHeadersNo = Integer.parseInt(s[1].substring(0, s[1].length()-1));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to parse headers no: "+newLine+" using the default value 2");
                uidlHeadersNo = 2;
            }
        }
    }

    private void addRegexExtractChecks() {
        for (int i = 0; i < lines.size(); i++) {
            final String aline = lines.get(i);

            if (aline.contains(".post(") && aline.contains("?v-r=uidl&v-uiId=")) {
               lines.add(i + 2, "\t\t\t.check(syncIdExtract).check(clientIdExtract)");
            }
            for (Map.Entry<String, List<String>> entry : nodeIdToRequestFileNames.entrySet()) {
                List<String> fileNamesList = entry.getValue();
                if (containsStringInAListOfStrings(aline, fileNamesList) ||
                        (uiInitRequestFileName != null && aline.contains("check(xsrfTokenExtract)") &&
                                containsStringInAListOfStrings(uiInitRequestFileName, fileNamesList))) {
                    String requiredConnectorId = entry.getKey();

                    if (requiredConnectorIds.contains(requiredConnectorId)) {
                        String regexExtractor = createExtractorRegex(requiredConnectorId);
                        if (!connectorIdExtractors.contains(regexExtractor)) {
                            regexExtractor = escapeCurlyBraces(regexExtractor);
                            // no need to add duplicate extractor
                            connectorIdExtractors.add(regexExtractor);
                            lines.add(i, "\t\t\t.check(extract_" + requiredConnectorId + "_Id)");
                            ++i;
                        }
                    }
                }
            }
        }
    }

    private void addRegexExtractDefinitions() {
        for (int i = 0; i < lines.size(); i++) {
            final String aline = lines.get(i);
            if (aline.contains("val scn = scenario")) {
                for (String extractor : connectorIdExtractors) {
                    lines.add(i - 1, extractor);
                }
                lines.add(i - 1, "\n");
                break;
            }
        }
    }

    private void replacehardCodedUiIdAndPushIds() {
        for (int i = 0; i < lines.size(); i++) {
            final String aline = lines.get(i);
            if (aline.contains("v-uiId=")) {
                lines.remove(i);
                String newLine = aline.replaceFirst("v\\-uiId=\\d{0,2}", Matcher.quoteReplacement("v-uiId=${uiId}"));
                lines.add(i, newLine);
            }
            if (aline.contains("v-pushId=")) {
                lines.remove(i);
                String newLine = aline.replaceFirst("v\\-pushId=[a-z0-9\\-]{1,50}&", Matcher.quoteReplacement("v-pushId=${pushId}&"));
                lines.add(i, newLine);
            }
        }
    }

    private String escapeCurlyBraces(String regexExtractor) {
        // TODO: this is rather ugly hack to escape curly braces
        System.out.println(regexExtractor);
        regexExtractor = regexExtractor.replaceAll(":\\{", (":\\\\{"));
        System.out.println(regexExtractor);
        if (regexExtractor.contains("}}")) {
            regexExtractor = regexExtractor.replaceAll("([^0-9\\\\])\\}\\}", "$1\\\\}\\\\}");
            System.out.println(regexExtractor);
        } else {
            regexExtractor = regexExtractor.replaceAll("([^0-9\\\\])\\}", "$1\\\\}");
            System.out.println(regexExtractor);
        }
        return regexExtractor;
    }

    private boolean containsStringInAListOfStrings(String firstString, List<String> listOfStrings) {
        for (String str : listOfStrings) {
            if (firstString.contains(str)) {
                return true;
            }
        }
        return false;
    }

    String createExtractorRegex(String requiredConnectorId) {
        final String propertyValueCss = nodeIdToCssIdMap.get(requiredConnectorId);
        final String propertyValueLabel = nodeIdToLabelMap.get(requiredConnectorId);
        final String propertyValueText = nodeIdToTextMap.get(requiredConnectorId);
        final String propertyValueTheme = nodeIdToThemeMap.get(requiredConnectorId);
        final String propertyValueAdd = nodeIdToAddMap.get(requiredConnectorId);
        final String propertyValueTag = nodeIdToTagMap.get(requiredConnectorId);
        if (propertyValueCss!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_id");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueCss));
            return regexExtractor;
        } else if (propertyValueLabel!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_label");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueLabel));
            return regexExtractor;
        }  else if (propertyValueText!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_text");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueLabel));
            return regexExtractor;
        } else if (propertyValueTheme!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_theme");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueLabel));
            return regexExtractor;
        } else if (propertyValueAdd!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_add");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueLabel));
            return regexExtractor;
        } else if (propertyValueTag!=null) {
            String regexExtractor = props.getProperty("connectorid_extractor_regex_template_tag");
            regexExtractor = regexExtractor.replace("_XXX_", "_" + requiredConnectorId + "_");
            regexExtractor = regexExtractor.replace("_YYY_", escapePropertyValue(propertyValueLabel));
            return regexExtractor;
        }

        return null;
    }

    private String escapePropertyValue(String propertyValue) {
        char[] specialChars = new char[]{'\\','.','[',']','{','}','(',')','*','+','-','?','^','$','|'};
        String result = propertyValue;
        for (char specialChar : specialChars) {
            result = result.replace(Character.toString(specialChar), "\\"+specialChar);
        }
        return result;
    }

    private void addAdditionalImports() {
        lines.add(0, "import io.gatling.core.body.ElFileBody");
    }

    private String requestBodyTreatments(String newLine, boolean saveResults) throws IOException {
        if (newLine.contains("RawFileBody")) {
            newLine = replaceWithELFileBody(newLine, saveResults);
            Logger.getLogger(LoadTestConfigurator.class.getName()).info(newLine);
        }
        return newLine;
    }

    private boolean initializeSyncAndClientIds(String newLine, final List<String> lines) {
        if (newLine.contains(".exec(http(")) {
            lines.add("\t\t.exec(initSyncAndClientIds)");
            return true;
        }
        return false;
    }

    private String replaceWithELFileBody(String newLine, boolean saveRequest) throws IOException {
        final String requestFileName = getRequestFileName(newLine);
        if (requestFileName != null) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).info(requestFileName);

            String responseFilename = requestFileName.replaceFirst("request", "response");
            String requestBody = readRequestResponseFileContent(requestFileName);
            String responseBody = readRequestResponseFileContent(responseFilename);
            requestNamesAndBodies.put(requestFileName, requestBody);

            if (requestBody.contains("\"Vaadin-Security-Key\":")) {
                uiInitRequestFileName = requestFileName;
                lines.add("\t\t\t.check(uIdExtract)");
                lines.add("\t\t\t.check(pushIdExtract)");
                lines.add("\t\t\t.check(xsrfTokenExtract)");
            }

            readConnectorMap(responseBody, requestFileName);
            if (!requestFileName.contains("response")) {
                requestBody = doRequestBodyTreatments(requestBody);
                responseNamesAndBodies.put(responseFilename, responseBody);
                requestNamesAndBodiesProcessed.put(requestFileName, requestBody);

                if (saveRequest) {
                    saveRequestFile(resourcesPath + (resourcesPath.charAt(resourcesPath.length() - 1) == '/' ? "" : "/") + requestFileName, requestBody);
                } else {
                    Logger.getLogger(LoadTestConfigurator.class.getName())
                            .info("--- New RequestBody " + "---\n" + requestBody + "\n-----------------------");
                }
            }
            newLine = newLine.replaceFirst("RawFileBody", "ElFileBody");
        }
        return newLine;
    }

    String doRequestBodyTreatments(String requestBody) {
        String regex = "\"node\":([0-9]{1,5}),";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(requestBody);

        while (matcher.find()) {
            String connectorId = matcher.group(1);
            int index = matcher.start();
            if ((nodeIdToCssIdMap.get(connectorId)!=null || nodeIdToLabelMap.get(connectorId)!=null) && nodeIdToRequestFileNames.get(connectorId)!=null) {
                String idName = "_" + connectorId + "_Id";
                requestBody = requestBody.substring(0, index+7)+"${" + idName + "}"+requestBody.substring(index + connectorId.length()+7);
                matcher = p.matcher(requestBody);
                requiredConnectorIds.add(connectorId);
            }
        }

        requestBody = requestBody.replaceFirst("syncId\":[0-9]+", Matcher.quoteReplacement("syncId\":${syncId}"));
        requestBody = requestBody.replaceFirst("clientId\":[0-9]+", Matcher.quoteReplacement("clientId\":${clientId}"));
        requestBody = requestBody.replaceFirst("csrfToken\":\"[a-z0-9\\-]+\"", Matcher.quoteReplacement("csrfToken\":\"${seckey}\""));
       return requestBody;
    }

    private void saveRequestFile(String fileName, String requesBody) throws IOException {
        final File requestFile = new File(fileName);
        final FileWriter requestWriter = new FileWriter(requestFile, false);
        requestWriter.write(requesBody);
        requestWriter.close();
    }

    private String getRequestFileName(String line) {
        final Pattern pattern = Pattern.compile("\"(.*?)\"");
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public String readRequestResponseFileContent(final String fileName) {
        return readFileContent(resourcesPath + "/"+ fileName);
    }

    public String readFileContent(String filename) {
        String content = "";
        try (Scanner scanner = new Scanner(new File(filename))) {
            content = scanner.useDelimiter("\\Z").next();
        } catch (final Exception e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to read request");
            e.printStackTrace();
        }
        return content;
    }

    private void insertHelperMethods(List<String> lines) {
        lines.add(props.getProperty("sync_and_client_id_init"));
        lines.add("\n");
        lines.add(props.getProperty("sync_id_extract"));
        lines.add(props.getProperty("client_id_extract"));
        lines.add(props.getProperty("xsrf_token_extract"));
        lines.add(props.getProperty("push_id_extract"));
        lines.add(props.getProperty("uiid_id_extract"));
        lines.add("\n");
    }

    void readConnectorMap(String responseFileContent, String requestFilename) {
        String responseJson = "";
        boolean htmlRequest = false;
        try {
            responseJson = responseFileContent.replace("for(;;);", "");
            if (responseJson.contains("Vaadin-Security-Key")) {
                // remove initial html
                responseJson = responseJson.substring(responseJson.indexOf("var uidl =") + 10);
                responseJson = responseJson.substring(0, responseJson.indexOf("var config ="));
                responseJson = responseJson.trim();
                responseJson = responseJson.substring(0, responseJson.length()-1);
                // remove escaped quotes
                responseJson = responseJson.replace("\\\"", "\"");
                // remove inner htmls
                int strartIndex = responseJson.indexOf(",\"key\":\"innerHTML\"");
                while (strartIndex >= 0) {
                    int endIndex = responseJson.substring(strartIndex).indexOf("\"},{");
                    String start = responseJson.substring(0, strartIndex-1);
                    String end = responseJson.substring(strartIndex + endIndex);
                    responseJson = start + end;
                    strartIndex = responseJson.indexOf(",\"key\":\"innerHTML\"");
                }
            }

            if (!Strings.isNullOrEmpty(responseJson)) {

                JsonObject jsonObject;
                try {
                    JsonArray jsonArray = Json.instance().parse(responseJson);
                    jsonObject = jsonArray.getObject(0);
                } catch (ClassCastException e) {
                    jsonObject = Json.parse(responseJson);
                }
                final JsonArray changesArray = jsonObject.getArray("changes");
                if (changesArray!=null) {
                    JsonObject prevNode = null;
                    for (int i = 0; i < changesArray.length(); i++) {
                        JsonObject node = changesArray.get(i);
                        String nodeId = node.get("node").asString();

                        extractCssIdNode(requestFilename, node, nodeId);
                        extractLabelNode(requestFilename, node, nodeId);
                        extractTextNode(requestFilename, node, prevNode, nodeId);
                        extractAddNode(requestFilename, node, nodeId);
                        extractTagNode(requestFilename, node, nodeId);
                        extractThemeNode(requestFilename, node, nodeId);
                        prevNode = node;
                    }
                }
            }

            System.out.println("x");

        } catch (Exception e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe(responseJson);
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to parse response json " + responseJson);
        }
    }

    private void extractCssIdNode(String requestFilename, JsonObject node, String nodeId) {
        if (node.hasKey("key") && node.getString("key").equals("payload")) {
            // {"node":44,"type":"put","key":"payload","feat":0,"value":{"type":"@id","payload":"sendComment"}}
            JsonObject payload = node.get("value");
            if ("@id".equals(payload.getString("type"))) {
                String cssId = payload.getString("payload");
                nodeIdToCssIdMap.put(nodeId, cssId);
                stroreNodeIdToFileNamesMap(requestFilename, nodeId);
            }
        }
    }

    private void extractLabelNode(String requestFilename, JsonObject node, String nodeId) {
        if (node.hasKey("key") && node.getString("key").equals("label")) {
            // {"node":39,"type":"put","key":"label","feat":1,"value":"Category"}
            JsonString labelValue = node.get("value");
            if (!Strings.isNullOrEmpty(labelValue.getString())) {
                nodeIdToLabelMap.put(nodeId, labelValue.getString());
                stroreNodeIdToFileNamesMap(requestFilename, nodeId);
            }
        }
    }

    private void extractTextNode(String requestFilename, JsonObject currNode, JsonObject prevNode, String nodeId) {
        if (prevNode.hasKey("key") && prevNode.getString("key").equals("text") &&
            currNode.hasKey("type") && currNode.getString("type").equals("attach")) {
            // {"node":33,"type":"put","key":"text","feat":7,"value":"Fiction"}
            // {"node":34,"type":"attach"},
            JsonString tagValue = prevNode.get("value");
            if (!Strings.isNullOrEmpty(tagValue.getString())) {
                nodeIdToTextMap.put(nodeId, tagValue.getString());
                stroreNodeIdToFileNamesMap(requestFilename, nodeId);
            }
        }
    }

    private void extractAddNode(String requestFilename, JsonObject node, String nodeId) {
        if (node.hasKey("splice") && node.hasKey("add")) {
            // {"node":62,"type":"splice","feat":19,"index":0,"add":["sortersChanged","select",..
            JsonArray addValues = node.get("add");
            if (addValues.length()>0) {
                String addValue = addValues.getString(0);
                if (!Strings.isNullOrEmpty(addValue)) {
                    nodeIdToAddMap.put(nodeId, addValue);
                    stroreNodeIdToFileNamesMap(requestFilename, nodeId);
                }
            }
        }
    }

    private void extractTagNode(String requestFilename, JsonObject node, String nodeId) {
        if (node.hasKey("key") && node.getString("key").equals("tag")) {
            // {"node":62,"type":"put","key":"tag","feat":0,"value":"vaadin-grid"}
            JsonString tagValue = node.get("value");
            if (!Strings.isNullOrEmpty(tagValue.getString())) {
                nodeIdToTagMap.put(nodeId, tagValue.getString());
                stroreNodeIdToFileNamesMap(requestFilename, nodeId);
            }
        }
    }

    private void extractThemeNode(String requestFilename, JsonObject node, String nodeId) {
        if (node.hasKey("key") && node.getString("key").equals("theme")) {
            // {"node":65,"type":"put","key":"theme","feat":3,"value":"primary"},
            JsonString tagValue = node.get("value");
            if (!Strings.isNullOrEmpty(tagValue.getString())) {
                nodeIdToThemeMap.put(nodeId, tagValue.getString());
                stroreNodeIdToFileNamesMap(requestFilename, nodeId);
            }
        }
    }

    private void stroreNodeIdToFileNamesMap(String requestFilename, String nodeId) {
        nodeIdToRequestFileNames.computeIfAbsent(nodeId, k -> new ArrayList<>());
        nodeIdToRequestFileNames.get(nodeId).add(requestFilename);
    }

    private void loadPropertiesFile() {
        props = new Properties();
        try {
            InputStream inputStream = LoadTestConfigurator.class.getClassLoader().getResourceAsStream(("loadtestdriver.properties"));
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String, String> getRequestNamesAndBodies() {
        return requestNamesAndBodies;
    }

    public TreeMap<String, String> getResponseNamesAndBodies() {
        checkOtherResponses();
        return responseNamesAndBodies;
    }

    private void checkOtherResponses() {
        File folder = new File(resourcesPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("_response.txt") && !responseNamesAndBodies.containsKey(file.getName())) {
                responseNamesAndBodies.put(file.getName(), readFileContent(file.getPath()));
            }
        }
    }

    public TreeMap<String, String> getRequestNamesAndBodiesProcessed() {
        return requestNamesAndBodiesProcessed;
    }

    public void setProcessedRequestBody(String fileName, String fileContent) {
        requestNamesAndBodiesProcessed.put(fileName, fileContent);
    }

    public void setProcessedScalaFilesContent(String processedScalaFilesContent) {
        this.processedScalaFilesContent = processedScalaFilesContent;
    }

    public void saveProject() throws IOException {
        saveResultFile(new File(testFileNameWithPath), processedScalaFilesContent);
        requestNamesAndBodiesProcessed.forEach((fileName, fileContent) -> {
            try {
                saveRequestFile(resourcesPath+"/"+fileName, fileContent);
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        });
    }

    private void saveResultFile(File file, List<String> lines) throws IOException {
        final FileWriter fw = new FileWriter(file);
        final BufferedWriter bw = new BufferedWriter(fw);
        for (final String s : lines) {
            bw.write(s + "\n");
        }

        bw.flush();
        bw.close();
    }

    private void saveResultFile(File file, String fileContent) throws IOException {
        final FileWriter fw = new FileWriter(file);
        final BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fileContent);

        bw.flush();
        bw.close();
    }

    public String getTestFileNameWithPath() {
        return testFileNameWithPath;
    }
}