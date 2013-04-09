package ch.eaternity.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * @author Ikai Lan
 * 
 * Represents a GalleryUpdatedEvent to fire to the message bus for all listeners.
 * 
 *	Inspiration:  
 * 	http://stackoverflow.com/questions/2951621/gwt-custom-events
 *
 */
public class LoadedDataEvent extends GwtEvent<LoadedDataEventHandler> {
    public static Type<LoadedDataEventHandler> TYPE = new Type<LoadedDataEventHandler>();

    public LoadedDataEvent() {}
    
	@Override
	public Type<LoadedDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadedDataEventHandler handler) {
		handler.onEvent(this);
	}

}
