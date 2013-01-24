package ch.eaternity.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import ch.eaternity.client.ui.RechnerView;
import ch.eaternity.client.ui.GoodbyeView;
import ch.eaternity.client.ui.HelloView;
import ch.eaternity.client.ui.KlimaZmittagView;
import ch.eaternity.client.ui.MenuPreviewView;

public interface ClientFactory {
	
	// the eventbus and placecontrollers
	EventBus getEventBus();
	PlaceController getPlaceController();
    
    // the views
	HelloView getHelloView();
	RechnerView getRechnerView();
	GoodbyeView getGoodbyeView();
	MenuPreviewView getMenuPreviewView();
	KlimaZmittagView getKlimaZmittagView();
	
    // services
    DataServiceAsync getDataServiceRPC();
	DataController getDataController();

}
