package org.netomi.tracker.util;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;

public class ApplicationHelper {
	
	public static ApplicationContext getApplicationContext() {
		return getApplicationContext(getCurrentApplication());
	}
	
	public static ApplicationContext getApplicationContext(Application app) {
		ServletContext sc = ((WebApplicationContext)app.getContext()).getHttpSession().getServletContext();
		return WebApplicationContextUtils.getWebApplicationContext(sc);
	}
	
	public static WebBrowser getBrowser() {
		Application app = getCurrentApplication();
		return ((WebApplicationContext)app.getContext()).getBrowser();
	}

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(attrs != null) {
			return attrs.getRequest();
		}
		return null;
	}
	
	public static Application getCurrentApplication() {
		return ApplicationHolder.getApplication();
	}

	public static Locale getCurrentLocale() {
		return getCurrentApplication().getLocale();
	}
	
	public static String getMessage(String key) {
		return getVaadinApplicationObjectSupport().getMessage(key);
	}
	
	public static String getMessage(String key, String... args) {
		return getVaadinApplicationObjectSupport().getMessage(key, args);
	}

	public static VaadinApplicationObjectSupport getVaadinApplicationObjectSupport() {
		String names[] = getApplicationContext().getBeanNamesForType(VaadinApplicationObjectSupport.class);
		if(names != null && names.length == 1) {
			return (VaadinApplicationObjectSupport) getApplicationContext().getBean(names[0]);
		}
		return null;
	}
}
