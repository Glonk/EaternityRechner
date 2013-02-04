package ch.eaternity.client.events;

import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.event.shared.GwtEvent;

public class IngredientAddedEvent extends GwtEvent<IngredientAddedEventHandler> {
  public static Type<IngredientAddedEventHandler> TYPE = new Type<IngredientAddedEventHandler>();
  
  public IngredientSpecification ing;
  
  public IngredientAddedEvent(IngredientSpecification ing) {
	  this.ing = ing;
  }
  
  @Override
  public Type<IngredientAddedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(IngredientAddedEventHandler handler) {
    handler.onEvent(this);
  }
}
