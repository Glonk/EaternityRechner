package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class MonthChangedEvent extends GwtEvent<MonthChangedEventHandler> {
  public static Type<MonthChangedEventHandler> TYPE = new Type<MonthChangedEventHandler>();
  
  public int month;
  
  public MonthChangedEvent(int month) {
	  this.month = month;
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
