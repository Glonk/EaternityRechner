package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecipeAddedEventHandler extends EventHandler {
	
	void onEvent(RecipeAddedEvent event);

}
