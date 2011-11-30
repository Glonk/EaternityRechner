package ch.eaternity.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.HelloPlace;
import ch.eaternity.client.ui.HelloView;
import ch.eaternity.client.ui.MenuPreviewView;

public class HelloActivity extends AbstractActivity implements
		HelloView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "Hello,"
	private String name;
	private MenuPreviewView dialogBox;
	
	public HelloActivity(HelloPlace place, ClientFactory clientFactory) {
		this.name = place.getPlaceName();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		HelloView helloView = clientFactory.getHelloView();
		helloView.setName(name);
		helloView.setPresenter(this);
		containerWidget.setWidget(helloView.asWidget());
		
		dialogBox = clientFactory.getMenuPreviewView();
		helloView.setMenuPreviewDialog(dialogBox);
		
	    Timer t = new Timer() {
			public void run() {	
				goTo(new EaternityRechnerPlace(name));
			}
		};
		t.schedule(6000);
	}

	/**
	 * Ask user before stopping this activity
	 */
//	@Override
//	public String mayStop() {
//		return "Please hold on. This activity is stopping.";
//	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public DialogBox getDialogBox() {
		return dialogBox;
	}
}
