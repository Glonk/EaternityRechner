package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
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
import com.googlecode.objectify.annotation.*;

@Entity
public class HomeDistances implements Serializable {
	
	private static final long serialVersionUID = 3172640409035191495L;
	
	@Id private Long id;
	
	// Home (to), From, Distance
	@Serialize
	private Map<String, Route> distancesMap;
	
	@Index
	private String homeLocation;
	
	private final static Geocoder geocoder = new Geocoder();
	
	// how many seconds to wait for the distance request call
	private static Integer timeToWait = 1;
	
	private HomeDistances() {
		distancesMap = new HashMap<String, Route>();
	}
	
	public HomeDistances(String homelocation) {
		this();
		this.homeLocation = homelocation;
	}
	
	/**
	 * This method searches the current HomeDistances data or makes an RPC call if not in available data
	 * @param from must be a verified location 
	 * @param to must be a verified location
	 * @return the requested route, null if from or to where invalid parameters or request failed
	 */
	public Route getRoute(String from, String to) {
		if (to.equals(homeLocation)) {
			Route route = distancesMap.get(from);
			return route;
		}
		else return null;
		/*
		if (route == null) {
			Double distance = requestAirDistance(from, to);
			if (distance == null) return null;
			else {
				route = new Route(from, to, new QuantityImpl(distance, Unit.METER));
				distancesMap.put(from, route);

			}
		}
		return route; 
		*/
	}	
	
	/**
	 * Checks weather location can be found in Google Maps
	 * @return Returns null if location wasnt found, translated location otherwise
	 */
	public String strProcessLocation(final String location) {
		Placemark processedLocation = processLocation(location);
		
		if (processedLocation == null)
			return null;
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
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param mapsTable
	 * @return distance in meters or null if invalid arguments
	 */
	private Double requestAirDistance(final String from, final String to) {
		
		final Placemark fromPlace = processLocation(from);
		final Placemark toPlace = processLocation(to);
		Double distance;
		if (fromPlace != null && toPlace != null) {
			distance = toPlace.getPoint().distanceFrom(fromPlace.getPoint());
			return distance;
		}
		else
			return null;
	}
	
	/*
	public void writeDistances(String homeLocation, List<Ingredient> ingSpecs, final FlexTable mapsTable) {
		double dist;
		HashMap<String,Double> homeMap = (HashMap<String, Double>) distanceMap.get(homeLocation);
		if (homeMap == null) {
			homeMap = new HashMap<String,Double>();
		}
		else {
			for (Ingredient ingSpec : ingSpecs) {
				String from = ingSpec.getExtraction().symbol;
				if (homeMap.containsKey(from)) {
					ingSpec.setDistance(new QuantityImpl(homeMap.get(from), Unit.METER));
				}
				else {
					dist = requestDistance(from, homeLocation, mapsTable);
					homeMap.put(from, dist);
					ingSpec.setDistance(new QuantityImpl(dist, Unit.METER));
				}				
			}
		}
	}
	
	*/
	
	public List<Route> calculateDistances(String homeLocation, List<String> froms, final FlexTable mapsTable) {
		/*
		double dist;
		List<SingleDistance> distances = new ArrayList<SingleDistance>();
		HashMap<String,Quantity> homeMap = (HashMap<String, Quantity>) distanceMap.get(homeLocation);
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
		*/ return null;
	}
	

	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param mapsTable
	 * @return distance in meters
	 */
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
