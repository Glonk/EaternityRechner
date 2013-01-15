package ch.eaternity.client.events;

import ch.eaternity.shared.Recipe;

import com.google.gwt.event.shared.GwtEvent;

public class RecipeAddedEvent extends GwtEvent<RecipeAddedEventHandler> {
  public static Type<RecipeAddedEventHandler> TYPE = new Type<RecipeAddedEventHandler>();
  
  public Recipe recipe;
  
  public RecipeAddedEvent(Recipe recipe) {
	  this.recipe = recipe;
  }
  
  @Override
  public Type<RecipeAddedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipeAddedEventHandler handler) {
    handler.onEvent(this);
  }
}
