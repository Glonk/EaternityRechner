
package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecipeIngredientsChangedEventHandler extends EventHandler {
	
	void onEvent(RecipeIngredientsChangedEvent event);

}

