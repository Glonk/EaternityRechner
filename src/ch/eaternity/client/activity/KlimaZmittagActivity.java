package ch.eaternity.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.place.KlimaZmittagPlace;
import ch.eaternity.client.ui.KlimaZmittagView;
import ch.eaternity.shared.Commitment;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;

public class KlimaZmittagActivity extends AbstractActivity implements
	KlimaZmittagView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "Hello,"
	private String name;
	private final DataServiceAsync dataRpcService;

	
	public KlimaZmittagActivity(KlimaZmittagPlace place, ClientFactory clientFactory) {
		
		
		this.name = place.getPlaceName();
		this.clientFactory = clientFactory;
		this.dataRpcService = clientFactory.getDataServiceRPC();
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		
		final KlimaZmittagView klimaZmittagView = clientFactory.getKlimaZmittagView();
		klimaZmittagView.setName(name);
		klimaZmittagView.setPresenter(this);
		containerWidget.setWidget(klimaZmittagView.asWidget());
		
	
		dataRpcService.getOpenRecipe(new AsyncCallback<List<Recipe>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Recipe> recipes) {
				klimaZmittagView.populateRecipes(recipes);
			}
		});
		

		
	}


	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
	
	private static void handleError(Throwable error) {
		Window.alert(error.getMessage()  +" "+error.getLocalizedMessage());
		if (error instanceof NotLoggedInException) {
			// Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	@Override
	public void saveCommitment(Commitment commitment) {
		dataRpcService.addCommitment(commitment, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Long commitmentID) {
				triggerId( commitmentID);
			}

		});
		
	}
	
	private void triggerId(Long commitmentID) {
		Window.alert("Ihr Commitment wurde gespeichert"+Long.toString(commitmentID));
		
	}
	

}
