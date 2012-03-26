package org.netomi.tracker;

import org.netomi.tracker.ui.MainLayout;
import org.netomi.tracker.util.BaseApplication;
import org.netomi.tracker.util.VaadinApplicationObjectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@Component(value = "trackerApplication")
@Scope(value = "prototype")
public class TrackerApplication
    extends BaseApplication {

    private static final long serialVersionUID = 2970837123765522807L;

    private final static Logger logger = LoggerFactory.getLogger(TrackerApplication.class);

    @Autowired
    private transient VaadinApplicationObjectSupport app;

    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("init, locale: " + getLocale());
        }
        
        setTheme(Runo.THEME_NAME);

        final MainLayout layout = new MainLayout();
        final Window main = new Window(app.getMessage("application.name"), layout);
        main.setSizeFull();
        setMainWindow(main);
        
        if (logger.isDebugEnabled()) {
            logger.debug("initialization done");
        }
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        logger.error(event.getThrowable().getMessage(), event.getThrowable());
    }
}
