package ch.eaternity.client.events;

import ch.eaternity.shared.Ingredient;

import com.google.gwt.event.shared.GwtEvent;

public class IngredientsChangedEvent extends GwtEvent<IngredientsChangedEventHandler> {
  public static Type<IngredientsChangedEventHandler> TYPE = new Type<IngredientsChangedEventHandler>();
  
  public Ingredient ing;
  
  public IngredientsChangedEvent(Ingredient ing) {
	  this.ing = ing;
  }
  
  @Override
  public Type<IngredientsChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(IngredientsChangedEventHandler handler) {
    handler.onEvent(this);
  }
}
