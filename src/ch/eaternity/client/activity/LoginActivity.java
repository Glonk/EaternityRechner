package ch.eaternity.client.activity;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.place.LoginPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.LoginView;
import ch.eaternity.shared.UserInfo;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;



public class LoginActivity extends AbstractActivity {

	private ClientFactory clientFactory;
	private DataServiceAsync dataRpcService;
	private String name;

	public LoginActivity(LoginPlace place, ClientFactory clientFactory) {
		this.name = place.getPlaceName();
		this.clientFactory = clientFactory;
		this.dataRpcService = clientFactory.getDataServiceRPC();
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {

		final LoginView loginView = clientFactory.getLoginView();
		loginView.setName(name);
		loginView.setPresenter(this);
		containerWidget.setWidget(loginView.asWidget());

		dataRpcService.getUserInfo(GWT.getHostPageBaseURL(), new AsyncCallback<UserInfo>() {
			public void onFailure(Throwable error) {
				loginView.setStatusInfo("Error during load of login occured. plz try again later or contact system administrator.");
			}

			public void onSuccess(final UserInfo userInfo) {
				if (userInfo.isLoggedIn()) {
					if (userInfo.isEnabled())
						goTo(new RechnerRecipeViewPlace(""));
					else {
						loginView.setStatusInfo("Du bist eingelogt als: " + userInfo.getEmailAddress() + "<br /><br />Du bist kein freigeschalteter Benutzer.");
						loginView.setLoginUrl(userInfo.getLogoutUrl());
						loginView.setButtonText("Logout");
						loginView.setVisibility(true);
					}
				}
				else {
					loginView.setLoginUrl(userInfo.getLoginUrl());
					loginView.setVisibility(true);
				}
			}
		});
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}


}