package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LocationChangedEvent extends GwtEvent<LocationChangedEventHandler> {
  public static Type<LocationChangedEventHandler> TYPE = new Type<LocationChangedEventHandler>();
  
  public String location;
  
  public LocationChangedEvent(String location) {
	  this.location = location;
  }
  
  @Override
  public Type<LocationChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LocationChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
