

package ch.eaternity.client.events;

import ch.eaternity.shared.Recipe;

import com.google.gwt.event.shared.GwtEvent;

public class RecipePublicityChangedEvent extends GwtEvent<RecipePublicityChangedEventHandler> {
  public static Type<RecipePublicityChangedEventHandler> TYPE = new Type<RecipePublicityChangedEventHandler>();
  
  public Recipe recipe;
  
  public RecipePublicityChangedEvent(Recipe recipe) {
	  this.recipe = recipe;
  }
  
  @Override
  public Type<RecipePublicityChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipePublicityChangedEventHandler handler) {
    handler.onEvent(this);
  }
}

