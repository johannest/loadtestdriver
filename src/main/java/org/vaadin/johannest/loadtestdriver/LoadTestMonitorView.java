package org.vaadin.johannest.loadtestdriver;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsSeries;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.management.MBeanServerConnection;

public class LoadTestMonitorView extends LoadTestMonitor {

	private final DataSeries memory = new DataSeries("Used memory, MB");
	private final DataSeries heap = new DataSeries("Heap size, MB");
	private final DataSeries sessions = new DataSeries("Active sessions");
	private final DataSeries classes = new DataSeries("Number of classes");
	private final DataSeries threads = new DataSeries("Number of threads");

	private OperatingSystemMXBean osMBean;
	private NumberFormat formatter = new DecimalFormat("#0.00");    
	
	private double previousUsedMemory = 0;
	private int previousSessionCount = 0;
	
	public LoadTestMonitorView() {
		super();
		initializeListeners();
		createCharts();
		initInstrumentation();
	}
	
	@Override
	public void attach() {
		super.attach();
		previousUsedMemory = getUsedMemoryMB();
	}

	private void initializeListeners() {
		garbageCollectButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Runtime.getRuntime().gc();
			}
		});
		quitButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
			}
		});
		sessionSizeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				double usedMem = (double)getUsedMemoryMB();
				int sessionCount = SessionListener.getActiveHttpSessions();
				
				double deltaMem = usedMem-previousUsedMemory;
				double deltaSes = sessionCount-previousSessionCount;
				
				sessionSizeLabel.setValue("E. session size: "+formatter.format(deltaMem/deltaSes));
				
				previousSessionCount = sessionCount;
				previousUsedMemory = usedMem;
			}
		});
	}

	private void createCharts() {
		Chart memoryChart = new Chart(ChartType.LINE);
		Chart sessionsChart = new Chart(ChartType.LINE);
		Chart classesChart = new Chart(ChartType.LINE);
		Chart threadsChart = new Chart(ChartType.LINE);

		firstChartLayout.removeAllComponents();
		firstChartLayout.addComponents(memoryChart, classesChart);

		secondChartLayout.removeAllComponents();
		secondChartLayout.addComponents(sessionsChart, threadsChart);

		memoryChart.getConfiguration().addSeries(memory);
		memoryChart.getConfiguration().addSeries(heap);
		sessionsChart.getConfiguration().addSeries(sessions);
		classesChart.getConfiguration().addSeries(classes);
		threadsChart.getConfiguration().addSeries(threads);

		configureChart(memoryChart, "Heap size and Used memory, MB");
		configureChart(sessionsChart, "Active HTTP session");
		configureChart(classesChart, "Number of classes");
		configureChart(threadsChart, "Number of threads");
	}

	private void initInstrumentation() {
		MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

		try {
			osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc,
					ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
					OperatingSystemMXBean.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void configureChart(Chart chart, String caption) {
		chart.setCaption(caption);
		chart.setSizeFull();

		chart.getConfiguration().setCredits(new Credits(false));
		chart.getConfiguration().getLegend().setEnabled(false);
		chart.getConfiguration().getxAxis().setType(AxisType.DATETIME);
		chart.getConfiguration().getyAxis().setTitle("");
		chart.getConfiguration().setTitle("");
		chart.getConfiguration().getChart()
				.setBackgroundColor(new SolidColor(0, 0, 0, 0));
		PlotOptionsSeries po = new PlotOptionsSeries();
		po.setMarker(new Marker(false));
		chart.getConfiguration().setPlotOptions(po);
	}

	public void update() {
		boolean shift = sessions.size() > 75;
		final long now = System.currentTimeMillis();

		memory.add(new DataSeriesItem(now, getUsedMemoryMB()), true, shift);
		heap.add(new DataSeriesItem(now, getHeapSizeMB()), true, shift);
		
		sessions.add(
				new DataSeriesItem(now, SessionListener.getActiveHttpSessions()),
				true, shift);

		classes.add(new DataSeriesItem(now, getNumberOfClasses()), true, shift);

		threads.add(new DataSeriesItem(now, getNumberOfThreads()), true, shift);

		sysLoadLabel.setValue("Avg. load: "+getCPULoad());
		coresLabel.setValue("CPUs/cores: "+getNumberOfCPUs());
		maxHeapSizeLabel.setValue("-Xmx (MB): "+getMaxHeapSizeMB());
		
		UI.getCurrent().push();
	}

	private String getNumberOfCPUs() {
		return Integer.toString(Runtime.getRuntime().availableProcessors());
	}

	private String getMaxHeapSizeMB() {
		return Long.toString((Runtime.getRuntime().maxMemory()) / (1024 * 1024));
	}

	private String getCPULoad() {
		return formatter.format(osMBean.getSystemLoadAverage());
	}

	private long getUsedMemoryMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) / (1024 * 1024);
	}

	private long getHeapSizeMB() {
		return (Runtime.getRuntime().totalMemory()) / (1024 * 1024);
	}

	private Number getNumberOfClasses() {
		Field f;
		try {
			f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);
			Vector<Class> classes = (Vector<Class>) f.get(ClassLoader
					.getSystemClassLoader());
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes.size();
	}

	private Number getNumberOfThreads() {
		return Thread.activeCount();
	}

}
