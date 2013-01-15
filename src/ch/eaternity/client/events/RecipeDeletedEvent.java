package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RecipeDeletedEvent extends GwtEvent<RecipeDeletedEventHandler> {
  public static Type<RecipeDeletedEventHandler> TYPE = new Type<RecipeDeletedEventHandler>();
  
  public Long id;
  
  public RecipeDeletedEvent(Long id) {
	  this.id = id;
  }
  
  @Override
  public Type<RecipeDeletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipeDeletedEventHandler handler) {
    handler.onEvent(this);
  }
}
