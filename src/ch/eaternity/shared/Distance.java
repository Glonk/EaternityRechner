package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.eaternity.client.ui.widgets.TopPanel;

import com.google.common.collect.Multimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class Distance {
	
	// Home (to), From, Distance
	private Map<String,Multimap<String,Double>> distMM;
	private final static Geocoder geocoder = new Geocoder();
	
	public Distance() {
		distMM = new HashMap<String,Multimap<String,Double>>();
	}
	
	public double getDistance(String from, String to) {
	double dist = 0;
	/*

		for(SingleDistance singleDistance : presenter.getDCO().cdata.distances){
			if(singleDistance.getFrom().contentEquals(getTopPanel().currentHerkunft) && 
					singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
				
				zutatSpec.setDistance(singleDistance.getDistance());
				iterator.set(zutatSpec);
				break;
			}

		}
	}*/
	return dist;
	}
	
	public List<SingleDistance> getDistancesToOneLocation(String home, List<String> from) {
		
		processLocation(location)
	}
	
	/**
	 * Checks weather location can be found in Google Maps
	 * 
	 * @return Returns "failed" if location wasnt found, translated location otherwise
	 */
	public String processLocation(final String location) {
		final List<String> codedLocation = new ArrayList<String>();
			geocoder.setBaseCountryCode("ch");
			geocoder.getLocations(location, new LocationCallback() {
				 
				public void onFailure(int statusCode) {
					codedLocation.add("failed");
				}
				public void onSuccess(JsArray<Placemark> locations) {
					Placemark place = locations.get(0);
					codedLocation.add(place.getAddress());
				}
			});
		
		return codedLocation.get(0);
	}
	
	private  void calculateDistances(String newHomeLocation, boolean firstTime) {
		// if we have them already in the datastore, take them...
		ArrayList<SingleDistance> distances = (ArrayList<SingleDistance>) presenter.getClientData().getDistances();
		ArrayList<String> distancesRequested = new ArrayList<String>();
		
		boolean notFound;
		List<Ingredient> zutaten = presenter.getClientData().getIngredients();
		for( Ingredient zutat : zutaten){
			for(Extraction herkunft : zutat.getExtractions()){

				notFound = true;
				
				for(SingleDistance singleDistance : distances){
					if(singleDistance.getFrom().contentEquals(newHomeLocation) && 
							singleDistance.getTo().contentEquals(herkunft.symbol)){
						notFound = false;
					}
				}
				if(notFound && !distancesRequested.contains(herkunft.symbol) ){
					// so we don't request them twice...
					distancesRequested.add(herkunft.symbol);
					
					// now, start the actual request!
					getDistance( newHomeLocation, herkunft.symbol);
				}


			}

		}
		
		if(!distancesRequested.isEmpty() && firstTime){
			// okay, we need to fetch them, so show what you fetch!
			openDialog();
		} else {
			// we got already all the data, so just update everything respectively
			updateAllZutatenInRecipes();
		}
	}
	
	void getDistance(final String from, final String to ) {

		Timer t = new Timer() {
			public void run() {



				geocoder.getLocations(to, new LocationCallback() {
					public void onFailure(int statusCode) {
						Window.alert("Diese Zutat hat einen falsche Herkunft: "+ to);
					}

					public void onSuccess(final JsArray<Placemark> locationsTo) {

						geocoder.getLocations(from, new LocationCallback() {
							public void onFailure(int statusCode) {
								Window.alert("Wir können Ihre Adresse nicht zuordnen: " + from);
							}

							public void onSuccess(JsArray<Placemark> locationsFrom) {

								simpleDirectionsDemo(locationsFrom,locationsTo);
							}
						});
					}
				});
				TimeToWait = TimeToWait - 1000;
			}
		};
		t.schedule(TimeToWait);
		TimeToWait = TimeToWait + 1000;
	}


	public void simpleDirectionsDemo(final JsArray<Placemark> locationFrom, final JsArray<Placemark> locationTo) {
		Placemark placeFrom = locationFrom.get(0);
		Placemark placeTo = locationTo.get(0);
		final String from = placeFrom.getAddress();
		final String to = placeTo.getAddress();

		MapWidget map = new MapWidget(locationFrom.get(0).getPoint(), 15);
		map.setHeight("300px");
		DirectionsPanel directionsPanel = new DirectionsPanel();
		DirectionQueryOptions opts = new DirectionQueryOptions(map, directionsPanel);
		int currentRow = mapsTable.getRowCount();
		mapsTable.setWidget(currentRow, 0, map);
		mapsTable.setWidget(currentRow, 1, directionsPanel);

		opts.setLocale("de_DE");
		opts.setPreserveViewport(true);
		opts.setRetrievePolyline(false);
		opts.setRetrieveSteps(false);

		String query = "from: "+from+" to: "+to;
		Directions.load(query, opts, new DirectionsCallback() {

			public void onFailure(int statusCode) {
				//	        Window.alert("Es ist ein Fehler aufgetreten...");
				getDistanceEstimateToCurrent(locationFrom,locationTo,to,true);
				// remove last row
				mapsTable.removeRow(mapsTable.getRowCount()-1);
			}

			public void onSuccess(DirectionResults result) {
				//	    	Window.alert(Double.toString(result.getDistance().inMeters())+"m from "+from+" to "+to);
				GWT.log("Successfully loaded directions. Von " + from + " nach " + to +".", null);
				double distance = result.getDistance().inMeters();
				SingleDistance singleDistance = new SingleDistance();
				singleDistance.setFrom(from);
				singleDistance.setTo(to);
				singleDistance.setRoad(true);
				singleDistance.setTriedRoad(true);
				singleDistance.setDistance(distance);

				allDistances.add(singleDistance);
				showTable();
				//	        	updateDistance(distance,zutatSpec);

			}
		});


	}

	void getDistanceEstimateToCurrent(JsArray<Placemark> locations, JsArray<Placemark> locations1, String to, Boolean tried) {

		Placemark place = locations.get(0);
		double distance = locations1.get(0).getPoint().distanceFrom(place.getPoint());

		SingleDistance singleDistance = new SingleDistance();
		singleDistance.setFrom(place.getAddress());
		singleDistance.setTo(to);
		singleDistance.setRoad(false);
		singleDistance.setTriedRoad(tried);
		singleDistance.setDistance(distance);

		allDistances.add(singleDistance);
		showTable();
		//	updateDistance(distance,zutatSpec);
	}
}
