package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class KitchenChangedEvent extends GwtEvent<KitchenChangedEventHandler> {
  public static Type<KitchenChangedEventHandler> TYPE = new Type<KitchenChangedEventHandler>();
  
  public Long id;
  
  public KitchenChangedEvent(Long id) {
	  this.id = id;
  }
  
  @Override
  public Type<KitchenChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(KitchenChangedEventHandler handler) {
    handler.onKitchenChanged(this);
  }
}