package ch.eaternity.client;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;
import ch.eaternity.shared.Zutat.Herkuenfte;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DistancesDialog extends DialogBox{
	interface Binder extends UiBinder<Widget, DistancesDialog> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	private final static Geocoder geocoder = new Geocoder();

	@UiField Button closeButton;
	@UiField Button executeButton;
	@UiField
	static FlexTable mapsTable;
	@UiField Button locationButton;
	@UiField TextBox clientLocationDialog;

	public DistancesDialog() {
		setWidget(binder.createAndBindUi(this));
		
		setText("Versuche Routen zu berechnen");
		setAnimationEnabled(true);
		setGlassEnabled(true);
	}


	
	final static LatLngCallback geocodeResultListener = new
	LatLngCallback()
	{
		public void onSuccess(LatLng latlng)
		{

//			herkunft.add(latlng.getLatitude());
//			herkunft.add(latlng.getLongitude());
			Window.alert("- " + latlng.getLongitude() + " -");
			
		}

		public void onFailure()
		{
			Window.alert("Unable to geocode");
		}

	};

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent preview) {
		super.onPreviewNativeEvent(preview);

		NativeEvent evt = preview.getNativeEvent();
		if (evt.getType().equals("keydown")) {
			// Use the popup's key preview hooks to close the dialog when either
			// enter or escape is pressed.
			switch (evt.getKeyCode()) {
			case KeyCodes.KEY_ENTER:
					saveDistances();
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}

	private void saveDistances() {
		// TODO Auto-generated method stub
		
	}
	private  void calculateDistances(String string) {
		TreeMap<String, Double> distances = new TreeMap<String,Double>();
		
		  List<Zutat> zutaten = Search.getClientData().getZutaten();
		  for( Zutat zutat : zutaten){
			  for(Herkuenfte herkunft : zutat.getHerkuenfte()){
				  if(!distances.containsKey( herkunft.toString())){
					  getDistance( string, herkunft.toString());
					  distances.put(herkunft.toString(), 0.0);
					  
				  }
			  }
			  
		  }
	}

	@UiHandler("closeButton")
	void onClicked(ClickEvent event) {
		showTable();
	}

	private static void showTable() {
		mapsTable.removeAllRows();
	     Set<String> iter = TopPanel.allDistances.keySet();
//	    while(iter.hasNext()){
	     for(String key : iter){
	    	String formatted = NumberFormat.getFormat("##").format( TopPanel.allDistances.get(key)/1000 );
	    	mapsTable.setText(mapsTable.getRowCount(), 0, "nach " + key +" : ca. " + formatted + "km");
	  	}
	}
	
	@UiHandler("executeButton")
	void onOkayClicked(ClickEvent event) {
		saveDistances();
		hide();
	}
	
	@UiHandler("locationButton")
	void onLocClicked(ClickEvent event) {
		processAddress(clientLocationDialog.getText());
	}


	void processAddress(final String address) {
		if (address.length() > 1) { 
	    geocoder.getLocations(address, new LocationCallback() {
	      public void onFailure(int statusCode) {
	        TopPanel.locationLabel.setText("Wir können diese Adresse nicht finden: ");
	      }

	      public void onSuccess(JsArray<Placemark> locations) {
	    	  Placemark place = locations.get(0);
	    	  TopPanel.locationLabel.setText("Sie befinden sich in der Mitte von: " +place.getAddress() +"  ");
//	    	  TopPanel.currentLocation = place;
	    	  TopPanel.ddlg.setText("Berechne alle Routen von: " + place.getAddress());
	    	  TopPanel.ddlg.clientLocationDialog.setText(place.getAddress());
	    	  calculateDistances(place.getAddress());
	    	  
	      }
	    });
		} else {
			TopPanel.locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
		}
		
	}
	



	
	
	
	void getDistance(final String from, final String to ) {
		
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
		
	}
	

	public void simpleDirectionsDemo(final JsArray<Placemark> locationFrom, final JsArray<Placemark> locationTo) {
		Placemark placeFrom = locationFrom.get(0);
		Placemark placeTo = locationTo.get(0);
		String from = placeFrom.getAddress();
		final String to = placeTo.getAddress();
			
	    MapWidget map = null;
		DirectionsPanel directionsPanel = null;
		DirectionQueryOptions opts = new DirectionQueryOptions(map, directionsPanel);
	    opts.setLocale("de_DE");
	    opts.setPreserveViewport(true);
	    opts.setRetrievePolyline(false);
	    opts.setRetrieveSteps(false);
	    
	    String query = "from: "+from+" to: "+to;
	    Directions.load(query, opts, new DirectionsCallback() {

	      public void onFailure(int statusCode) {
	        Window.alert("Es ist ein Fehler aufgetreten...");
	        getDistanceEstimateToCurrent(locationFrom,locationTo,to);
	      }

	      public void onSuccess(DirectionResults result) {
//	    	Window.alert(Double.toString(result.getDistance().inMeters())+"m from "+from+" to "+to);
	        GWT.log("Successfully loaded directions.", null);
	        double distance = result.getDistance().inMeters();
	        if (distance == 0){
	        	getDistanceEstimateToCurrent(locationFrom,locationTo,to);
	        } else {
	        	TopPanel.allDistances.put(to, distance);
	        	showTable();
//	        	updateDistance(distance,zutatSpec);
	        }
	      }
	    });
	    
	    
	  }

	static void getDistanceEstimateToCurrent(JsArray<Placemark> locations, JsArray<Placemark> locations1, String to) {

		Placemark place = locations.get(0);
		double distance = locations1.get(0).getPoint().distanceFrom(place.getPoint());
		TopPanel.allDistances.put(to, distance);
		showTable();
		//	updateDistance(distance,zutatSpec);
	}

}

