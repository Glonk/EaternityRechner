package ch.eaternity.client.events;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

public class MonthChangedEvent extends GwtEvent<MonthChangedEventHandler> {
  public static Type<MonthChangedEventHandler> TYPE = new Type<MonthChangedEventHandler>();
  
  public Date date;
  
  public MonthChangedEvent(Date date) {
	  this.date = date;
  }
  
  @Override
  public Type<MonthChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MonthChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
