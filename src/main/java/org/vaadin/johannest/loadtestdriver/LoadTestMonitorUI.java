package org.vaadin.johannest.loadtestdriver;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

import java.util.Timer;
import java.util.TimerTask;

import org.openqa.jetty.html.Page;

import javax.servlet.annotation.WebServlet;

@Theme("valo")
@Widgetset("org.vaadin.johannest.loadtestdriver.LoadTestMonitorWidgetset")
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
public class LoadTestMonitorUI extends UI {

	private LoadTestMonitorView monitor;

	@Override
	protected void init(VaadinRequest request) {
		setContent(monitor = new LoadTestMonitorView());
		
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				LoadTestMonitorUI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                    	monitor.update();
                    }
                });
			}
		}, 0, 2000);
	}

	@WebServlet(urlPatterns = { "/monitor/*", "/VAADIN/*" }, name = "LoadTestMonitorUI", asyncSupported = true)
	@VaadinServletConfiguration(ui = LoadTestMonitorUI.class, productionMode = false)
	public static class MyMonitorUIServlet extends VaadinServlet {
	}

}
