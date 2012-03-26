package org.netomi.tracker.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class DateColumnGenerator implements Table.ColumnGenerator {
  private SimpleDateFormat dateFormat;
  
  public DateColumnGenerator() {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  public Component generateCell(Table source, Object itemId, Object columnId) {
    // Get the object stored in the cell as a property
    Property prop = source.getItem(itemId).getItemProperty(columnId);
    if (prop.getType().equals(Date.class) && prop.getValue() != null) {
      Date d = (Date) prop.getValue();
      String text = dateFormat.format(d);
      Label label = new Label(text, Label.CONTENT_TEXT);
      label.setSizeUndefined();
      return label;
    } else {
      Label label = new Label("");
      label.setSizeUndefined();
      return label;
    }
  }
}
