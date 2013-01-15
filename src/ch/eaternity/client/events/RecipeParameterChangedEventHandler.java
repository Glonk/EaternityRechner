package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecipeParameterChangedEventHandler extends EventHandler {
	
	void onEvent(RecipeParameterChangedEvent event);

}

