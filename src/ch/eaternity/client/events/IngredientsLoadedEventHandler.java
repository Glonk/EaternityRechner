package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface IngredientsLoadedEventHandler extends EventHandler {
	
	void onEvent(IngredientsLoadedEvent event);

}
