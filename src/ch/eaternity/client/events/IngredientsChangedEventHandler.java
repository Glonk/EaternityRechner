package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface IngredientsChangedEventHandler extends EventHandler {
	
	void onEvent(IngredientsChangedEvent event);

}
