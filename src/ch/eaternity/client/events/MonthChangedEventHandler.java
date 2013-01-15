package ch.eaternity.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface MonthChangedEventHandler extends EventHandler {
	
	void onEvent(MonthChangedEvent event);

}
