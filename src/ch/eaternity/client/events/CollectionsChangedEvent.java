
package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class CollectionsChangedEvent extends GwtEvent<CollectionsChangedEventHandler> {
  public static Type<CollectionsChangedEventHandler> TYPE = new Type<CollectionsChangedEventHandler>();
  

  
  public CollectionsChangedEvent() {

  }
  
  @Override
  public Type<CollectionsChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CollectionsChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
