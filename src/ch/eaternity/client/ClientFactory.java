package ch.eaternity.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.GoodbyeView;
import ch.eaternity.client.ui.HelloView;

public interface ClientFactory {
	
	// the eventbus and placecontrollers
	EventBus getEventBus();
	PlaceController getPlaceController();
    
    // the views
	HelloView getHelloView();
	EaternityRechnerView getEaternityRechnerView();
	GoodbyeView getGoodbyeView();
	
    // services
    DataServiceAsync getDataServiceRPC();

}
