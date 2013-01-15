package ch.eaternity.client.ui.widgets;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import ch.eaternity.client.DataService;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;

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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DistancesDialog<T> extends DialogBox{
	interface Binder extends UiBinder<Widget, DistancesDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	private final static Geocoder geocoder = new Geocoder();
	private final DataServiceAsync distancesService = GWT.create(DataService.class);

	@UiField Button executeButton;
	@UiField FlexTable mapsTable;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable summaryTable;
	@UiField Button locationButton;
	@UiField TextBox clientLocationDialog;

	ArrayList<SingleDistance> allDistances = new ArrayList<SingleDistance>();
	String currentLocation;

	Integer TimeToWait = 1;
	
	private Presenter<T> presenter;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
	}
	private EaternityRechnerView superDisplay;
	
	public DistancesDialog(String string, EaternityRechnerView superDisplay) {
		this.superDisplay = superDisplay;		
		processAddress(string,true);
	}

	void processAddress(final String address, final boolean firstTime) {
		if (address.length() > 1) { 
			geocoder.setBaseCountryCode("ch");
			geocoder.getLocations(address, new LocationCallback() {
				public void onFailure(int statusCode) {
					presenter.getTopPanel().locationLabel.setText("Wir können diese Adresse nicht finden: ");
				}

				public void onSuccess(JsArray<Placemark> locations) {
					Placemark place = locations.get(0);
					presenter.getTopPanel().locationLabel.setText("Sie befinden sich in: " +place.getAddress() +"  ");
					
					currentLocation = place.getAddress();
					setText("Berechne alle Routen von: " + place.getAddress());
					TopPanel.currentHerkunft = place.getAddress();

					calculateDistances(place.getAddress(),firstTime);

				}
			});
		} else {
			presenter.getTopPanel().locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
		}

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


	private void openDialog() {
		// Dialog only opens if distances need to be fetched
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
		show();
		scrollPanel.setHeight("400px");
		center();
		clientLocationDialog.setText(currentLocation);
		setText("Es werden die Routen berechnet.");

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
				mapsTable.removeAllRows();
				processAddress(clientLocationDialog.getText(),false);
				break;
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}


	private void saveDistances() {

		if(!allDistances.isEmpty()){
			distancesService.addDistances(allDistances, new AsyncCallback<Integer>() {
				public void onFailure(Throwable error) {
					Window.alert("Fehler : "+ error.getMessage());
				}
				public void onSuccess(Integer ignore) {
					presenter.getClientData().getDistances().addAll(allDistances);
					updateAllZutatenInRecipes(); 
					//				Window.alert(Integer.toString(ignore) + " Distanzen gespeichert.");
				}
			});
		}

		hide();
	}



	private void updateAllZutatenInRecipes() {
		
		for(Widget widget : superDisplay.getRezeptList()){
			RecipeView rezeptView = ((RecipeView) widget);
			List<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
			zutaten.addAll(rezeptView.getRezept().ingredients);
			for(IngredientSpecification zutatSpec : zutaten ){
				int index = rezeptView.getRezept().ingredients.indexOf(zutatSpec);
				for(SingleDistance singleDistance : presenter.getClientData().getDistances()){
					if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
							singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){

						zutatSpec.setDistance(singleDistance.getDistance());
						
						// and this must be propagated forward to the recipeEditView
						rezeptView.getRezept().ingredients.set(index, zutatSpec);

						break;
					}

				}
			}
			
			// from this everything should be propagated and updated:
			rezeptView.updateSuggestion();

			// the infoPanel should be updated by recipeEditView not here
//			if(rezeptView.addInfoPanel.getWidgetCount() ==2){
//				rezeptView.addInfoPanel.remove(1);
//			}
			
		}
		
		updateOriginOfAllZutaten();
	}


	private void updateOriginOfAllZutaten() {
		Iterator<Ingredient> ingredientIterator =  presenter.getClientData().getIngredients().iterator();
		while(ingredientIterator.hasNext()) {

			/*
			 * Um es richtig zu machen, müsste man mit der geocode abfrage das Land bestimmen.
			 * Dann entsprechend schauen, ob man zu Zutat dieses Land schon gespeichert hat.
			 * Diese Informationen dann übernehmen.
			 * 
			 * Wenn man zur Zutat noch nichts gespeichert hat, muss man diese Information manuell nachtragen...
			 * D.h. es braucht eine Benachrichtigung, dass sie noch fehlt...
			 */
			
			Ingredient ingredient = ingredientIterator.next(); 
			ingredient.stdExtractionSymbol = TopPanel.currentHerkunft;
			ingredient.stdExtraction.symbol = TopPanel.currentHerkunft;
			ingredient.getExtractions().add(0,ingredient.stdExtraction); // add to the top of the List... (to complicated :)

		} 
		
	}

	private void showTable() {

		summaryTable.removeAllRows();
		//	    while(iter.hasNext()){
		for(SingleDistance singleDistance : allDistances){
			String to = singleDistance.getTo();
			String formatted = NumberFormat.getFormat("##").format( singleDistance.getDistance()/100000 );
			if(formatted.contentEquals("0")){
				summaryTable.setText(summaryTable.getRowCount(), 0, "nach " + to +" : ca. " + formatted + "km");
			}else{
				summaryTable.setText(summaryTable.getRowCount(), 0, "nach " + to +" : ca. " + formatted + "00km");
			}
		}

	}

	@UiHandler("executeButton")
	void onOkayClicked(ClickEvent event) {
		saveDistances();
		hide();
	}

	@UiHandler("locationButton")
	void onLocClicked(ClickEvent event) {
		mapsTable.removeAllRows();
		processAddress(clientLocationDialog.getText(),false);
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

