package ch.eaternity.client.events;

import ch.eaternity.shared.Recipe;

import com.google.gwt.event.shared.GwtEvent;

/**
 * less an event, actually more lose coupling of components, used by different senders
 * @author aurelianjaggi
 *
 */
public class RecipeLoadedEvent extends GwtEvent<RecipeLoadedEventHandler> {
  public static Type<RecipeLoadedEventHandler> TYPE = new Type<RecipeLoadedEventHandler>();
  
  public Recipe recipe;
  
  public RecipeLoadedEvent(Recipe recipe) {
	  this.recipe = recipe;
  }
  
  @Override
  public Type<RecipeLoadedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RecipeLoadedEventHandler handler) {
    handler.onEvent(this);
  }
}
