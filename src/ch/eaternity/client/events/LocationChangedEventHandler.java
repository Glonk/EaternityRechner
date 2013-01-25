package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface LocationChangedEventHandler extends EventHandler {
	
	void onEvent(LocationChangedEvent event);

}
