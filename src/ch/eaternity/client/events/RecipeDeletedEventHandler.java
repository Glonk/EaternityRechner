package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecipeDeletedEventHandler extends EventHandler {
	
	void onEvent(RecipeDeletedEvent event);

}
