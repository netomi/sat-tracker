package org.netomi.tracker.ui;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@Configurable(preConstruction = true)
public class WelcomeScreen
    extends VerticalLayout {

    private static final long serialVersionUID = -2655767648338406023L;

    public WelcomeScreen() {
        setSizeFull();
        setCaption("Welcome");

        Panel welcome = new Panel("Satellite Tracker");
        welcome.setSizeFull();
        welcome.addStyleName(Runo.PANEL_LIGHT);
        addComponent(welcome);
        setExpandRatio(welcome, 1);

        CssLayout margin = new CssLayout();
        margin.setMargin(true);
        margin.setWidth("100%");
        welcome.setContent(margin);

        HorizontalLayout texts = new HorizontalLayout();
        texts.setSpacing(true);
        texts.setWidth("100%");
        margin.addComponent(texts);

        Label text = new Label("<h3>Satellite Information</h3><p>The application gives you an overview of "
                + "(unclassified) satellites orbiting the earth.</p><p>Sample data is extracted from "
                + "<a href=\"http://www.space-track.org\">space-track.org</a> and <a href=\"http://www.celestrak.com\">celestrak.com</a>.</p>",
                Label.CONTENT_XHTML);
        
        texts.addComponent(text);
        texts.setExpandRatio(text, 1);

        // Spacer
        text = new Label("");
        text.setWidth("20px");
        texts.addComponent(text);

        text = new Label("<h3>Visibilities</h3><p>Gives you the opportunity to discover satellite sighting opportunities "
                + "at your ground location for a period of 5 days in advance.",
                Label.CONTENT_XHTML);
        texts.addComponent(text);
        texts.setExpandRatio(text, 1);

        // Spacer
        text = new Label("");
        text.setWidth("20px");
        texts.addComponent(text);

        text = new Label("<h3>Real-time Tracking</h3><p>Ever wanted to see the ISS moving around the earth? Take your chance.</p>",
                         Label.CONTENT_XHTML);
        texts.addComponent(text);
        texts.setExpandRatio(text, 1);

        addComponent(new Label("<hr />", Label.CONTENT_XHTML));

        texts = new HorizontalLayout();
        texts.addStyleName(Runo.LAYOUT_DARKER);
        texts.setSpacing(true);
        texts.setWidth("100%");
        texts.setMargin(true);
        addComponent(texts);

        text = new Label("<h4>About This Application</h4>This application is built entirely with open-source "
                         + "software. Click the links on the right to get more information.",
                         Label.CONTENT_XHTML);
        text.addStyleName(Runo.LABEL_SMALL);
        texts.addComponent(text);
        texts.setExpandRatio(text, 1);

        // Spacer
        text = new Label("");
        text.setWidth("20px");
        texts.addComponent(text);
        texts.setExpandRatio(text, 1);

        Link vaadinLink = new Link(null, new ExternalResource("http://www.vaadin.com"));
        vaadinLink.setIcon(new ExternalResource("images/vaadin-logo-white.png"));
        vaadinLink.setTargetName("_blank");
        texts.addComponent(vaadinLink);
        texts.setComponentAlignment(vaadinLink, Alignment.MIDDLE_CENTER);
        
        // Spacer
        text = new Label("");
        text.setWidth("20px");
        texts.addComponent(text);

        Link springLink = new Link(null, new ExternalResource("http://www.springsource.org/"));
        springLink.setIcon(new ExternalResource("images/spring-logo.jpg"));
        springLink.setTargetName("_blank");
        texts.addComponent(springLink);
        texts.setComponentAlignment(springLink, Alignment.MIDDLE_CENTER);

        // Spacer
        text = new Label("");
        text.setWidth("20px");
        texts.addComponent(text);

        Link orekitLink = new Link(null, new ExternalResource("http://www.orekit.org/"));
        orekitLink.setIcon(new ExternalResource("images/orekit-logo.png"));
        orekitLink.setTargetName("_blank");        
        texts.addComponent(orekitLink);
        texts.setComponentAlignment(orekitLink, Alignment.MIDDLE_CENTER);

    }
}
