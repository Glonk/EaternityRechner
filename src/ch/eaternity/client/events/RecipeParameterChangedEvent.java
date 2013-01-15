package ch.eaternity.client.events;

import ch.eaternity.shared.Recipe;

import com.google.gwt.event.shared.GwtEvent;

public class RecipeParameterChangedEvent extends GwtEvent<RecipeParameterChangedEventHandler> {
  public static Type<RecipeParameterChangedEventHandler> TYPE = new Type<RecipeParameterChangedEventHandler>();
  
  public Recipe recipe;
  
  public RecipeParameterChangedEvent(Recipe recipe) {
	  this.recipe = recipe;
  }
  
  @Override
  public Type<RecipeParameterChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipeParameterChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
