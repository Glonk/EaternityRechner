package ch.eaternity.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.activity.EaternityRechnerActivity;
import ch.eaternity.client.activity.GoodbyeActivity;
import ch.eaternity.client.activity.HelloActivity;
import ch.eaternity.client.activity.KlimaZmittagActivity;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.place.HelloPlace;
import ch.eaternity.client.place.KlimaZmittagPlace;

public class AppActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

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
		GWT.log("Place called: " + place);
		
		// This is begging for GIN
		if (place instanceof HelloPlace)
			return new HelloActivity((HelloPlace) place, clientFactory);
		else if (place instanceof EaternityRechnerPlace)
			return new EaternityRechnerActivity((EaternityRechnerPlace) place, clientFactory);
		else if (place instanceof GoodbyePlace)
			return new GoodbyeActivity((GoodbyePlace) place, clientFactory);
		else if (place instanceof KlimaZmittagPlace)
			return new KlimaZmittagActivity((KlimaZmittagPlace) place, clientFactory);

		return null;
	}

}
