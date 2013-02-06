package ch.eaternity.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.HelloPlace;
import ch.eaternity.client.ui.HelloView;
import ch.eaternity.client.ui.MenuPreviewView;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Staff;

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
		
		final HelloView helloView = clientFactory.getHelloView();
		helloView.setName(name);
		helloView.setPresenter(this);
		containerWidget.setWidget(helloView.asWidget());
		
		clientFactory.getDataServiceRPC().login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
			}

			public void onSuccess(final LoginInfo loginInfo) {
				if (loginInfo.isLoggedIn()) {
					clientFactory.getDataServiceRPC().getEnabledUsers(new AsyncCallback<List<Staff>>() {
						public void onFailure(Throwable error) {
							Window.alert("Call to the server failed. Enabled Users couldnt get loaded. Try again.");
						}
	
						public void onSuccess(List<Staff> staffs) {
							boolean userEnabled = false;
							if (loginInfo.isAdmin())
								userEnabled = true;
							else
								for (Staff staff : staffs) {
									if (staff.userEmail.equals(loginInfo.getEmailAddress()) ) {
										userEnabled = true;
										
									}
								}
							
							if (userEnabled){
								goTo(new EaternityRechnerPlace(""));
							}
							else {
								helloView.setStatusInfo("Du bist eingelogt als: " + loginInfo.getEmailAddress() + "<br /><br />Du bist kein freigeschalteter Benutzer.");
								helloView.setLoginUrl(loginInfo.getLogoutUrl());
								helloView.setButtonText("Logout");
								helloView.setVisibility(true);
							}
			
						}
						});
				}
				else {
					helloView.setLoginUrl(loginInfo.getLoginUrl());
					helloView.setVisibility(true);
				}
			}
			});
		

		
		

		
//	    Timer t = new Timer() {
//			public void run() {	
//				goTo(new EaternityRechnerPlace(name));
//			}
//		};
//		t.schedule(6000);
		
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
