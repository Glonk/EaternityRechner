
package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RecipeIngredientsChangedEvent extends GwtEvent<RecipeIngredientsChangedEventHandler> {
  public static Type<RecipeIngredientsChangedEventHandler> TYPE = new Type<RecipeIngredientsChangedEventHandler>();
  

  
  public RecipeIngredientsChangedEvent() {

  }
  
  @Override
  public Type<RecipeIngredientsChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipeIngredientsChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
