package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * less an event, actually more lose coupling of components, used by different senders
 * @author aurelianjaggi
 *
 */
public class IngredientsLoadedEvent extends GwtEvent<IngredientsLoadedEventHandler> {
  public static Type<IngredientsLoadedEventHandler> TYPE = new Type<IngredientsLoadedEventHandler>();
  
  
  public IngredientsLoadedEvent() {
	  
  }
  
  @Override
  public Type<IngredientsLoadedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(IngredientsLoadedEventHandler handler) {
    handler.onEvent(this);
  }
}
