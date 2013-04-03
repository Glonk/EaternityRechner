package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecipeLoadedEventHandler extends EventHandler {
	
	void onEvent(RecipeLoadedEvent event);

}
