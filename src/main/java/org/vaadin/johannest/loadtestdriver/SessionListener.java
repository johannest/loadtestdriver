package org.vaadin.johannest.loadtestdriver;

import java.util.logging.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

	private static int activeHttpSessions = 0;
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		Logger.getLogger(SessionListener.class.getName()).info("Session created");
		++activeHttpSessions;
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		Logger.getLogger(SessionListener.class.getName()).info("Session destroyed");
		--activeHttpSessions;
	}

	public static int getActiveHttpSessions() {
		return activeHttpSessions;
	}
	
}
