package org.netomi.tracker.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;

public abstract class BaseApplication extends Application implements HttpServletRequestListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(BaseApplication.class);

	@Override
	public Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	@Override
	public void setLocale(Locale locale) {
		LocaleContextHolder.setLocale(locale);
	}

	public final void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		if (logger.isTraceEnabled()) {
			logger.trace("onRequestStart()");
		}
		ApplicationHolder.setApplication(this);
		requestStart(request, response);
	}

	public final void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		if (logger.isTraceEnabled()) {
			logger.trace("onRequestEnd()");
		}
		try {
			requestEnd(request, response);
		} finally {
			ApplicationHolder.resetApplication();
		}
	}

	public void requestEnd(HttpServletRequest request, HttpServletResponse response) {
	}

	public void requestStart(HttpServletRequest request, HttpServletResponse response) {
	}
}
