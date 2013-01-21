package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.gwt.user.client.ui.FlexTable;

public class Distance {
	
	// Home (to), From, Distance
	private Map<String,Map<String,Double>> distMM;
	private final static Geocoder geocoder = new Geocoder();
	
	// how many seconds to wait for the distance request call
	Integer timeToWait = 1;
	
	public Distance() {
		distMM = new HashMap<String,Map<String,Double>>();
	}
	
	/**
	 * This method doesnt make an RPC call, just searches the current distances data
	 * @param from
	 * @param to
	 * @return null if distance doesnt exist
	 */
	public double getDistance(String from, String to) {
		Double dist = null;
		HashMap<String,Double> toMap = (HashMap<String, Double>) distMM.get(to);
		if (toMap != null) {
			dist = toMap.get(from);
		}
		return dist;
	}
	
	/**
	 * Checks weather location can be found in Google Maps
	 * @return Returns "failed" if location wasnt found, translated location otherwise
	 */
	public String strProcessLocation(final String location) {
		Placemark processedLocation = processLocation(location);
		
		if (processedLocation == null)
			return "failed";
		else {
			return processedLocation.getAddress();
		}
	}
	
	/**
	 * Checks weather location can be found in Google Maps
	 * @param location
	 * @return null if location couldn't get processed
	 */
	public Placemark processLocation(final String location) {
		final List<Placemark> processedLocation = new ArrayList<Placemark>();
		geocoder.setBaseCountryCode("ch");
		geocoder.getLocations(location, new LocationCallback() {
			 
			public void onFailure(int statusCode) {}
			public void onSuccess(JsArray<Placemark> locations) {
				processedLocation.add(locations.get(0));
			}
		});
	
		if (processedLocation.size() == 0)
			return null;
		else
			return processedLocation.get(0);
	}
	
	
	public void writeDistances(String homeLocation, List<IngredientSpecification> ingSpecs, final FlexTable mapsTable) {
		double dist;
		HashMap<String,Double> homeMap = (HashMap<String, Double>) distMM.get(homeLocation);
		if (homeMap == null) {
			homeMap = new HashMap<String,Double>();
		}
		else {
			for (IngredientSpecification ingSpec : ingSpecs) {
				String from = ingSpec.getExtraction().symbol;
				if (homeMap.containsKey(from)) {
					ingSpec.setDistance(homeMap.get(from));
				}
				else {
					dist = requestDistance(from, homeLocation, mapsTable);
					homeMap.put(from, dist);
					ingSpec.setDistance(dist);
				}				
			}
		}
	}
	
	public List<SingleDistance> calculateDistances(String homeLocation, List<String> froms, final FlexTable mapsTable) {
		double dist;
		List<SingleDistance> distances = new ArrayList<SingleDistance>();
		HashMap<String,Double> homeMap = (HashMap<String, Double>) distMM.get(homeLocation);
		if (homeMap == null) {
			homeMap = new HashMap<String,Double>();
		}
		else {
			for (String from : froms) {
				if (homeMap.containsKey(from)) {
					
				}
				else {
					dist = requestDistance(from, homeLocation, mapsTable);
					homeMap.put(from, dist);
					distances.add(new SingleDistance(homeLocation, from, dist));
				}				
			}
		}
		return distances;
	}
	
	public Double requestDistance(final String from, final String to, final FlexTable mapsTable) {
		final List<Double> distance = new ArrayList<Double>();
		
		Timer t = new Timer() {
			public void run() {
				final Placemark fromPlace = processLocation(from);
				final Placemark toPlace = processLocation(to);
				String fromString = fromPlace.getAddress();
				String toString = toPlace.getAddress();
				
				if (fromString.equals("failed"))
					Window.alert("Diese Zutat hat einen falsche Herkunft: "+ to);
				if (toString.equals("failed"))
					Window.alert("Wir können Ihre Adresse nicht zuordnen: " + from);
				timeToWait = timeToWait - 1000;

				MapWidget map = new MapWidget(fromPlace.getPoint(), 15);
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
						distance.add(toPlace.getPoint().distanceFrom(fromPlace.getPoint()));
					}
		
					public void onSuccess(DirectionResults result) {
						GWT.log("Successfully loaded directions. Von " + from + " nach " + to +".", null);
						distance.add(result.getDistance().inMeters());			
					}
				});
			}
		};
	
		t.schedule(timeToWait);
		timeToWait = timeToWait + 1000;
		return distance.get(0);
	}
	
}
