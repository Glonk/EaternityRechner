package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * less an event, actually more lose coupling of components, used by different senders
 * @author aurelianjaggi
 *
 */
public class UpdateRecipeViewEvent extends GwtEvent<UpdateRecipeViewEventHandler> {
  public static Type<UpdateRecipeViewEventHandler> TYPE = new Type<UpdateRecipeViewEventHandler>();
  
  
  public UpdateRecipeViewEvent() {
	  
  }
  
  @Override
  public Type<UpdateRecipeViewEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(UpdateRecipeViewEventHandler handler) {
    handler.onEvent(this);
  }
}
