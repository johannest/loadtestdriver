package org.vaadin.johannest.loadtestdriver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;

public class LoadTestConfigurator {

    private final LoadTestParameters loadTestParameters;

    private final Map<String, String> nodeIdToCssIdMap = new HashMap<>();
    private final Map<String, String> nodeIdToLabelMap = new HashMap<>();

    private final Map<String, List<String>> nodeIdToRequestFileNames = new HashMap<>();
    private final Set<String> requiredConnectorIds = new HashSet<>();

    private String uiInitRequestFileName;
    private String resourcesPath;
    private String tempFilePath;
    private String className;

    private Properties props;
    private List<String> lines;
    private List<String> connectorIdExtractors;

    private Integer uidlHeadersNo;

    public LoadTestConfigurator(LoadTestParameters loadTestParameters) {
        this.loadTestParameters = loadTestParameters;
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
        final String fileName = tempFilePath + "/" + className + ".scala";
        Logger.getLogger(LoadTestConfigurator.class.getName()).info("Configuring test file: " + fileName);
        try {
            final File file = new File(fileName);
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
                final FileWriter fw = new FileWriter(file);
                final BufferedWriter bw = new BufferedWriter(fw);
                for (final String s : lines) {
                    bw.write(s + "\n");
                }

                bw.flush();
                bw.close();
            } else {
                StringBuilder sb = new StringBuilder();
                for (final String s : lines) {
                    sb.append(s).append("\n");
                }
                return sb.toString();
            }
        } catch (final FileNotFoundException e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to found file: " + fileName);
            e.printStackTrace();
        } catch (final IOException e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to access file: " + fileName);
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removePossibleBodyByteCheck() {
        lines = lines.stream().filter(line -> !line.contains("check(bodyBytes.is(RawFileBody")).collect(Collectors.toList());
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
                if (loadTestParameters.pausesEnabled()) {
                    lines.add("\t\t.pause(" + loadTestParameters.getMinPause() + ", " +
                            loadTestParameters.getMaxPause() + ")");
                }
            }

            extractUidlHeaderNumber(newLine, previousLine);

            newLine = requestBodyTreatments(newLine, saveResults);

            if (newLine.contains("atOnceUsers")) {
                newLine = newLine.replaceFirst("inject\\(atOnceUsers\\(1\\)\\)",
                        "inject(rampUsers(" + loadTestParameters.getConcurrentUsers() + ") during (" +
                                loadTestParameters.getRampUpTime() + " seconds))");
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
            if (aline.contains("?v-uiId=")) {
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

            String requestBody = readRequestResponseFileContent(requestFileName);
            String responseBody = readRequestResponseFileContent(requestFileName.replaceFirst("request", "response"));

            if (requestBody.contains("\"Vaadin-Security-Key\":")) {
                uiInitRequestFileName = requestFileName;
                lines.add("\t\t\t.check(uIdExtract)");
                lines.add("\t\t\t.check(pushIdExtract)");
                lines.add("\t\t\t.check(xsrfTokenExtract)");
            }

            readConnectorMap(responseBody, requestFileName);
            if (!requestFileName.contains("response")) {
                requestBody = doRequestBodyTreatments(requestBody);

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

    private String readRequestResponseFileContent(final String fileName) {
        return readFileContent(resourcesPath + "/"+ fileName);
    }

    String readFileContent(String filename) {
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
                    for (int i = 0; i < changesArray.length(); i++) {
                        JsonObject node = changesArray.get(i);
                        String nodeId = node.get("node").asString();

                        if (node.hasKey("key") && node.getString("key").equals("payload")) {
                            // {"node":44,"type":"put","key":"payload","feat":0,"value":{"type":"@id","payload":"sendComment"}}
                            JsonObject payload = node.get("value");
                            if ("@id".equals(payload.getString("type"))) {
                                String cssId = payload.getString("payload");
                                nodeIdToCssIdMap.put(nodeId, cssId);
                                nodeIdToRequestFileNames.computeIfAbsent(nodeId, k -> new ArrayList<>());
                                nodeIdToRequestFileNames.get(nodeId).add(requestFilename);
                            }
                        }
                        if (node.hasKey("key") && node.getString("key").equals("label")) {
                            // {"node":39,"type":"put","key":"label","feat":1,"value":"Category"}
                            JsonString labelValue = node.get("value");
                            if (!Strings.isNullOrEmpty(labelValue.getString())) {
                                nodeIdToLabelMap.put(nodeId, labelValue.getString());
                                nodeIdToRequestFileNames.computeIfAbsent(nodeId, k -> new ArrayList<>());
                                nodeIdToRequestFileNames.get(nodeId).add(requestFilename);
                            }
                        }
                    }
                }
            }

            System.out.println("x");

        } catch (Exception e) {
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe(responseJson);
            Logger.getLogger(LoadTestConfigurator.class.getName()).severe("Failed to parse response json " + responseJson);
        }
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
}