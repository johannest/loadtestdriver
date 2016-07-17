package org.vaadin.johannest.loadtestdriver;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import scala.collection.mutable.Map;
import scala.collection.mutable.StringBuilder;

public class LoadTestDriver extends PhantomJSDriver {
		
	private Recorder recorder;
	private boolean recording;
	
	private int concurrentUsers;
	private int rampUpTime;
	private int repeats;
	private int proxyPort;
	private String proxyHost;
	private String tempFilePath;
	private boolean testRefactoringEnabled;
	private boolean staticResourcesIngnoringEnabled;
	

	public LoadTestDriver(DesiredCapabilities capabilities) {
		super(capabilities);		
	}

	public void startRecording() {
		recorder = new Recorder(getProxyPort(),getProxyHost(),getTempFilePath(),staticResourcesIngnoringEnabled);
		recording = true;
		recorder.start();
	}
	
	public String stopAndSaveRecording() {
		return recorder.stopAndSave();
	}
	
	@Override
	public void get(String url) {
		startRecording();
		super.get(url);
	}

	@Override
	public void close() {
		if (recording) {
			stopRecordingAndSaveResults();
			super.close();
			postRecordingTasks();
		}
	}
	
	@Override
	protected void stopClient() {
		if (recording) {
			stopRecordingAndSaveResults();
			super.stopClient();
			postRecordingTasks();
		}
	}

	@Override
	public void quit() {
		if (recording) {
			stopRecordingAndSaveResults();
			super.quit();
			postRecordingTasks();
		}
	}

	private void stopRecordingAndSaveResults() {
		recording = false;
		stopAndSaveRecording();
	}
	
	private void postRecordingTasks() {
		configureTestFile();
		//compileTestFile();
		//showLoadTestMonitor();
		//runLoadTest();
		//showResultRaport();
	}

	private void configureTestFile() {
		boolean syncIdsInitialized = false;
		String fileName = recorder.getTempFilePath()+"/"+recorder.getClassName()+".scala";
		Logger.getLogger(Recorder.class.getName()).info("Configuring test file: "+fileName);
		try {
			File file = new File(fileName);
			FileReader fr = new FileReader(file);
	        BufferedReader br = new BufferedReader(fr);
	       
	        String line,newLine;
	        List<String> lines = new ArrayList<String>();
	        
	        boolean resourcesHandled = true;
	        
	        while((line=br.readLine()) != null) {
	            if(line != null) {
	                newLine = line;
	                
	                if (testRefactoringEnabled && newLine.contains("val scn")) {
            			insertSyncIdInits(lines);
            		}
	                
	                if (newLine.contains(".exec(http(") && !syncIdsInitialized) {
	                	lines.add("\t\t.exec(initSyncAndClientIds)");
	                	syncIdsInitialized = true;
	                }
	                	
	            	if (testRefactoringEnabled && newLine.contains(".resources(http(")) {
	            		resourcesHandled = false;

	            		newLine = newLine.replaceFirst("\\.resources\\(http\\(", ").pause(0)\n\t\t.exec(http(");
	            		if (newLine.contains("RawFileBody")) {
	            			newLine = replaceWithStringBody(newLine);
	            		}	            		
	            		lines.add(newLine);	            		
	            		
	            		while(!resourcesHandled) {
	            			newLine=br.readLine();
	            			
	            			if (newLine.endsWith("),")) {
	            				newLine = newLine.replaceFirst("\\),", ")\n\t\t).pause(0)\n\t\t.exec(");	            				
	            			}
	            			
	            			newLine = newLine.replaceFirst("\\s+http\\(","\t\t\thttp(");
	            			
	            			resourcesHandled = newLine.endsWith(")))");
	            			if (resourcesHandled) {
	            				newLine = newLine.replaceFirst("\\)\\)\\)","))");
	            			}
	            			
	            			if (newLine.contains("RawFileBody")) {
		            			newLine = replaceWithStringBody(newLine);
		            			Logger.getLogger(Recorder.class.getName()).info(newLine);
		            		}
	            			
	            			lines.add(newLine);
	            		}
	            		
	            	} else {
	            		if (newLine.contains("RawFileBody")) {
	            			newLine = replaceWithStringBody(newLine);
	            			Logger.getLogger(Recorder.class.getName()).info(newLine);
	            		}
	            		
	            		newLine = newLine.replaceFirst("inject\\(atOnceUsers\\(1\\)\\)", "inject(rampUsers("+concurrentUsers+") over ("+rampUpTime+" seconds))");
	            		lines.add(newLine);
	            	}
	            }
	        }
	        br.close();
	        
	        if (testRefactoringEnabled) {
		        for (int i=0; i<lines.size(); i++) {
		        	String aline = lines.get(i);
		        	if (aline.contains(".post(") && aline.contains("/UIDL/?v-uiId=")) {
		        		lines.add(i+2, "\t\t\t.check(regex(\"\"\"syncId\": ([0-9]*),\"\"\").saveAs(\"syncIdPlaceholder\"))");
		        		lines.add(i+3, "\t\t\t.check(regex(\"\"\"clientId\": ([0-9]*),\"\"\").saveAs(\"clientIdPlaceholder\"))");
		        	}
		        }
	        }
	        
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        for (String s : lines) {
	        	bw.write(s+"\n");
	        }
	        
	        bw.flush();	       
	        bw.close();
	        
		} catch (FileNotFoundException e) {
			Logger.getLogger(Recorder.class.getName()).severe("Failed to found file: "+fileName);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(Recorder.class.getName()).severe("Failed to access file: "+fileName);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String replaceWithStringBody(String newLine) {
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		Matcher matcher = pattern.matcher(newLine);
		if (matcher.find()) {
			String fileName = matcher.group(1);
			Logger.getLogger(Recorder.class.getName()).info(fileName);
			String requesBody = readFileContent(recorder.getTempFilePath()+"/bodies/"+fileName);
			requesBody = requesBody.replaceFirst("syncId\":[0-9]+", Matcher.quoteReplacement("syncId\":${syncIdPlaceholder}"));
			requesBody = requesBody.replaceFirst("clientId\":[0-9]+", Matcher.quoteReplacement("clientId\":${clientIdPlaceholder}"));
			requesBody = requesBody.replaceAll("\"", "\\\\\"");
			newLine = newLine.replaceFirst("RawFileBody","StringBody");
			newLine = newLine.replaceFirst("\"(.*?)\"", Matcher.quoteReplacement("\""+requesBody+"\""));
		}
		return newLine;
	}

	
	
	private String readFileContent(String filename) {
		String content ="";
		try {
			content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
			Logger.getLogger(Recorder.class.getName()).info(content);
		} catch (FileNotFoundException e) {
			Logger.getLogger(Recorder.class.getName()).severe("Failed to read request");
			e.printStackTrace();
		}
		return content;
	}

	private void insertSyncIdInits(List<String> lines) {
		lines.add("\tval initSyncAndClientIds = exec((session) => {");
		lines.add("\t\tsession.setAll(");
		lines.add("\t\t\t\"syncIdPlaceholder\" -> 0,");
		lines.add("\t\t\t\"clientIdPlaceholder\" -> 0");
		lines.add("\t\t)");
		lines.add("\t})");
		lines.add("\n");
	}

	public void setConcurrentUsers(int concurrentUsers) {
		this.concurrentUsers = concurrentUsers;
	}

	public void setRampUpTime(int rampUpTime) {
		this.rampUpTime = rampUpTime;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	
	public String getProxyHost() {
		return proxyHost;
	}
	
	public String getTempFilePath() {
		return tempFilePath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}
	
	public void setTestRefactoringEnabled(boolean testRefactoringEnabled) {
		this.testRefactoringEnabled = testRefactoringEnabled;
	}
	
	public void withStaticResourcesIngnoringEnabled(
			boolean staticResourcesIngnoringEnabled) {
		this.staticResourcesIngnoringEnabled = staticResourcesIngnoringEnabled;
	}
	
	private void compileTestFile() {
		Logger.getLogger(Recorder.class.getName()).info("Compiling test file");
    	try {
    		String classpath = System.getProperty("java.class.path");

    		StringBuilder cmd = new StringBuilder();
    		cmd.append("java -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 ");
    		cmd.append("-Xms512M -Xmx512M -Xmn100M -Xss10M ");
    		cmd.append("-cp "+classpath);
    		cmd.append(" io.gatling.compiler.ZincCompiler ");	
    		cmd.append("-ccp "+classpath);
    		cmd.append(" -sf ");
    		cmd.append(recorder.getTempFilePath());
    		cmd.append(" -bf ");
    		cmd.append(recorder.getTempFilePath());
//    		cmd.append(" -ro ");
//    		cmd.append(recorder.getTempFilePath()+"/results");
    		
    		Logger.getLogger(Recorder.class.getName()).info("Running ZincCompiler with comman: "+cmd.toString());
    		Process pro = Runtime.getRuntime().exec(cmd.toString());
			printLines(" stdout:", pro.getInputStream());
		    printLines(" stderr:", pro.getErrorStream());
		    pro.waitFor();
		    Logger.getLogger(Recorder.class.getName()).info(" exitValue() " + pro.exitValue());
		} catch (Exception e) {
			Logger.getLogger(Recorder.class.getName()).severe("Compilation failed");
			e.printStackTrace();
		}
	}
	
	private static void printLines(String name, InputStream ins) throws Exception {
	    String line = null;
	    BufferedReader in = new BufferedReader(
	        new InputStreamReader(ins));
	    while ((line = in.readLine()) != null) {
	    	Logger.getLogger(Recorder.class.getName()).info(name + " " + line);
	    }
	  }
	
	private void showLoadTestMonitor() {
		// TODO Auto-generated method stub
		
	}
	
	private void runLoadTest() {
		GatlingPropertiesBuilder propsBuilder = new GatlingPropertiesBuilder();
		propsBuilder.binariesDirectory(recorder.getTempFilePath()+"/test-classes");
		propsBuilder.outputDirectoryBaseName(recorder.getTempFilePath());
		propsBuilder.resultsDirectory(recorder.getTempFilePath()+"/results");
		propsBuilder.sourcesDirectory(recorder.getTempFilePath());
		propsBuilder.bodiesDirectory(recorder.getBodiesFolderPath());
		propsBuilder.dataDirectory(recorder.getDataFolderPath());
		Map<String, Object> propsMap = propsBuilder.build();
		propsMap.put("gatling.core.mute", true);
		//propsMap.put("gatling.core.directory.reportsOnly", recorder.getTempFilePath());
		Gatling.fromMap(propsMap);
	}

	private void showResultRaport() {
		try {
			Desktop.getDesktop().browse(findReportFile().toURI());
		} catch (IOException e) {
			Logger.getLogger(Recorder.class.getName()).severe(e.getLocalizedMessage());
			Logger.getLogger(Recorder.class.getName()).severe("Failed to open raport");
		}
	}
	
	private File findReportFile() {
		Logger.getLogger(Recorder.class.getName()).info("findReportFile");
		File dir = new File(recorder.getTempFilePath()+"/..");
		FileFilter fileFilter = new WildcardFileFilter("gatling-*");
		File[] files = dir.listFiles(fileFilter);
		
		File newest = null;
		long newestTimeStamp = 0;
		
		for (File file : files) {
			Logger.getLogger(Recorder.class.getName()).info(file.getName());
			String timeStamp = file.getName().split("-")[1];
			long ts = Long.parseLong(timeStamp);
			if (ts>newestTimeStamp) {
				newest = file;
				newestTimeStamp = ts;
			}
		}
		Logger.getLogger(Recorder.class.getName()).info("Report file "+newest.getName());
		return new File(newest.getPath()+"/index.html");
	}

    public static String getLocalIpAddress() {
    	try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			Logger.getLogger(LoadTestDriver.class.getName()).warning("Failed to find localhost ip - using 127.0.0.1 instead: "+e.getMessage());
			return "127.0.0.1";
		}
    }

	public static class LoadTestDriverBuilder  {
	
		private static final int DEFAULT_PROXY_PORT = 8888;
		
		private String ipaddress;
		private String proxyHost;
		private String path;
		private int concurrentUsers;
		private int repeats;
		private int rampUpTime;
		private int proxyPort = DEFAULT_PROXY_PORT;
		private boolean testRefactoringEnabled;
		private boolean staticResourcesIngnoringEnabled;
		
		public LoadTestDriverBuilder() {
			this.ipaddress = "127.0.0.1";
		}
		
		public LoadTestDriverBuilder withIpAddress(String ipaddress) {
			this.ipaddress  = ipaddress;
			return this;
		}
		
		public LoadTestDriverBuilder withNumberOfConcurrentUsers(int concurrentUsers) {
			this.concurrentUsers = concurrentUsers;
			return this;
		}
		
		public LoadTestDriverBuilder withRepeats(int repeats) {
			this.repeats = repeats;
			return this;
		}
		
		public LoadTestDriverBuilder withRampUpTimeInSeconds(int rampUpTime) {
			this.rampUpTime = rampUpTime;
			return this;
		}
		
		public LoadTestDriverBuilder withProxyPort(int proxyPort) {
			this.proxyPort  = proxyPort;
			return this;
		}
		
		public LoadTestDriverBuilder withProxyHost(String proxyHost) {
			this.proxyHost  = proxyHost;
			return this;
		}
		
		public LoadTestDriverBuilder withPath(String path) {
			this.path  = path;
			return this;
		}
		
		public LoadTestDriverBuilder withTestRefactoring() {
			this.testRefactoringEnabled = true;
			return this;
		}
		
		public LoadTestDriverBuilder withStaticResourcesIngnoring() {
			this.staticResourcesIngnoringEnabled  = true;
			return this;
		} 
		
		public WebDriver build() {
			ArrayList<String> cliArgsCap = new ArrayList<String>();
			cliArgsCap.add("--web-security=false");
	    	cliArgsCap.add("--load-images=false");
	    	cliArgsCap.add("--ignore-ssl-errors=true");
	    	cliArgsCap.add("--debug=true");
			cliArgsCap.add("--proxy="+ipaddress+":"+proxyPort);
	    	cliArgsCap.add("--proxy-type=http");
	    	
	    	ArrayList<String> cliArgsCap2 = new ArrayList<String>();
	    	cliArgsCap2.add("--logLevel=INFO");
	    	
	    	DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
	    	capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
	    	capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, cliArgsCap2);
	    	LoadTestDriver driver = new LoadTestDriver(capabilities);
	    	driver.setConcurrentUsers(concurrentUsers);
	    	driver.setRepeats(repeats);
	    	driver.setRampUpTime(rampUpTime);
	    	driver.setProxyPort(proxyPort);
	    	driver.setProxyHost(proxyHost);
	    	driver.setTempFilePath(path);
	    	driver.setTestRefactoringEnabled(testRefactoringEnabled);
	    	driver.withStaticResourcesIngnoringEnabled(staticResourcesIngnoringEnabled);
			return driver;
		}
	}


}
