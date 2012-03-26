package org.netomi.tracker.util;

import com.vaadin.Application;

public class ApplicationHolder {
	
	private static ThreadLocal<Application> app = new ThreadLocal<Application>();
	
	/**
	 * Hide constructor for this utility class.
	 */
	private ApplicationHolder() {}
	
	public static void setApplication(final Application application) {
		app.set(application);
	}
	
	public static void resetApplication() {
		app.remove();
	}
	
    public static Application getApplication() {
		return app.get();
	}

}
