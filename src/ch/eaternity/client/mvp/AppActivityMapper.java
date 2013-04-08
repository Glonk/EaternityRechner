package ch.eaternity.client.mvp;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.activity.GoodbyeActivity;
import ch.eaternity.client.activity.LoginActivity;
import ch.eaternity.client.activity.KlimaZmittagActivity;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.place.LoginPlace;
import ch.eaternity.client.place.KlimaZmittagPlace;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;
	private RechnerActivity rechnerActivity;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param clientFactory
	 *            Factory to be passed to activities
	 */
	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(Place place) {
		
		//Log.info("Place called: " + place);
		
		// This is begging for GIN
		if (place instanceof LoginPlace)
			return new LoginActivity((LoginPlace) place, clientFactory);
		
		else if (place instanceof RechnerRecipeViewPlace) {
			if (rechnerActivity == null) 
				this.rechnerActivity = new RechnerActivity(place, clientFactory);
			else  {
				rechnerActivity.setPlace(place);		
			}
			return rechnerActivity;
		}
		
		else if (place instanceof RechnerRecipeEditPlace) {
			if (rechnerActivity == null) 
				this.rechnerActivity = new RechnerActivity(place, clientFactory);
			else  {
				rechnerActivity.setPlace(place);		
			}
			return rechnerActivity;
		}
		
		else if (place instanceof GoodbyePlace)
			return new GoodbyeActivity((GoodbyePlace) place, clientFactory);
		
		else if (place instanceof KlimaZmittagPlace)
			return new KlimaZmittagActivity((KlimaZmittagPlace) place, clientFactory);

		return null;
	}

}
