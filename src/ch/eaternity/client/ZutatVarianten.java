package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;

import ch.eaternity.client.Search.SelectionStyle;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;
import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;
import ch.eaternity.shared.Zutat.Zustaende;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.maps.client.geocode.StatusCodes;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

public class ZutatVarianten extends  Composite{
	interface Binder extends UiBinder<Widget, ZutatVarianten> { }
	interface SelectionStyle extends CssResource {
		String selectedBlob();
	}
	private static final Binder binder = GWT.create(Binder.class);

	static double distance = 0;

	@UiField
	static SelectionStyle selectionStyle;
//	@UiField DisclosurePanel Labels;
	@UiField
	static ListBox Herkunft;
	@UiField
	static ToggleButton flugzeug;
	@UiField
	static ToggleButton bio;
	@UiField
	static ToggleButton treibhaus;
	@UiField
	static ToggleButton tiefgekühlt;
	@UiField
	static ToggleButton eingemacht;
	@UiField
	static ToggleButton getrocknet;
	@UiField
	static Label saison;
	@UiField HTMLPanel zustandHTML;
	@UiField HTMLPanel produktionHTML;
	@UiField HTMLPanel herkunftHTML;
	@UiField
	static HTMLPanel saisonHTML;

	private final static Geocoder geocoder = new Geocoder();
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

	
	public void setWerte(ZutatSpecification zutatSpec, Zutat zutat,int row){
//		TODO it is to expensive!
		// first get the distance ( this may take the longest)
//		SimpleDirectionsDemo(zutatSpec.getHerkunft().name(),TopPanel.clientLocation.getText(), null);

		
		// Menge Gramm
		InfoZutat.zutatMenge.setText(Integer.toString(zutatSpec.getMengeGramm()));
		
		Herkunft.clear();
		for(  Herkuenfte herkunft: zutat.getHerkuenfte() ){
			Herkunft.addItem(herkunft.name());
		}
		Herkunft.setSelectedIndex(zutat.getHerkuenfte().indexOf(zutatSpec.getHerkunft()));
		
		Produktionen produktion = zutatSpec.getProduktion();
		bio.setDown(false);
		treibhaus.setDown(false);
		switch (produktion){
			case biologisch: bio.setDown(true);
			case Treibhaus: treibhaus.setDown(true);
		}
			
		Zustaende zustand = zutatSpec.getZustand();
		tiefgekühlt.setDown(false);
		eingemacht.setDown(false);
		getrocknet.setDown(false);
		switch (zustand){
			case tiefgekühlt: tiefgekühlt.setDown(true);
			case eingemacht: eingemacht.setDown(true);
			case getrocknet: getrocknet.setDown(true);
		}
		
		Transportmittel transport = zutatSpec.getTransportmittel();
		flugzeug.setDown(false);
		switch (transport){
			case Flugzeug: flugzeug.setDown(true);
		}
		
//		ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
//				 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
//				zutat.getStdTransportmittel());
//		zutatSpecification.setHerkunft(zutat.getStdHerkunft());
//		zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
//		zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
//		zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());
		
		InfoZutat.zutat = zutatSpec;
		updateSaison();

		

	}
	
	public void setStdWerte(Zutat zutat){
//	TODO still to expensive!	
		// first get the distance ( this may take the longest)
//		SimpleDirectionsDemo(zutat.getStdHerkunft().name(),TopPanel.clientLocation.getText(), null);

		
		// Menge Gramm
		InfoZutat.zutatMenge.setText(Integer.toString(zutat.getStdMengeGramm()));
		
		Herkunft.clear();
		for(  Herkuenfte herkunft: zutat.getHerkuenfte() ){
			Herkunft.addItem(herkunft.name());
		}
		Herkunft.setSelectedIndex(zutat.getHerkuenfte().indexOf(zutat.getStdHerkunft()));
		
		Produktionen produktion = zutat.getStdProduktion();
		bio.setDown(false);
		treibhaus.setDown(false);
		switch (produktion){
			case biologisch: bio.setDown(true);
			case Treibhaus: treibhaus.setDown(true);
		}
			
		Zustaende zustand = zutat.getStdZustand();
		tiefgekühlt.setDown(false);
		eingemacht.setDown(false);
		getrocknet.setDown(false);
		switch (zustand){
			case tiefgekühlt: tiefgekühlt.setDown(true);
			case eingemacht: eingemacht.setDown(true);
			case getrocknet: getrocknet.setDown(true);
		}
		
		Transportmittel transport = zutat.getStdTransportmittel();
		flugzeug.setDown(false);
		switch (transport){
			case Flugzeug: flugzeug.setDown(true);
		}
		
		ZutatSpecification zutatSpecification = new ZutatSpecification(zutat.getId(), zutat.getSymbol(),
				 new Date(),zutat.getStdZustand(), zutat.getStdProduktion(), 
				zutat.getStdTransportmittel());
		zutatSpecification.setHerkunft(zutat.getStdHerkunft());
		zutatSpecification.setMengeGramm(zutat.getStdMengeGramm());
		zutatSpecification.setSeason(zutat.getStdStartSeason(), zutat.getStdStopSeason());
		zutatSpecification.setNormalCO2Value(zutat.getCO2eWert());
		
		InfoZutat.zutat = zutatSpecification;
		updateSaison();

		

	}
	
	private static void styleLabel( HTMLPanel panel, boolean selected) {
		
			String style = selectionStyle.selectedBlob();

			if (selected) {
				panel.addStyleName(style);
			} else {
				panel.removeStyleName(style);
			}
		
	}
	
	public static void updateSaison() {
		
		Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
		// In Tagen
//		String test = InfoZutat.zutat.getStartSeason();
		Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( InfoZutat.zutat.getStartSeason() );		
		Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( InfoZutat.zutat.getStopSeason() );
		
		if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
				dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
			
			saison.setText("Diese Zutat hat Saison");
			styleLabel(saisonHTML,true);
			
			InfoZutat.styleHinweis(false);
			InfoZutat.hinweisPanel.setText("Angaben sind koherent.");
			
		} else {
			saison.setText("Diese Zutat hat keine Saison");
			styleLabel(saisonHTML,false);
			
			// unvollständig:
			
			InfoZutat.styleHinweis(true);
			InfoZutat.hinweisPanel.setText("Angaben sind unvollständig.");
		}
		
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		
		//TODO uncomment this:
		
//		InfoZutat.updateZutatCO2(zutatSpec,EaternityRechner.selectedRow);
		
	}

	public ZutatVarianten() {
		initWidget(binder.createAndBindUi(this));

//		Labels.setAnimationEnabled(true);
	}

	public static ZutatSpecification getZutatSpecification(ZutatSpecification zutatSpec) {

//		herkunft.clear();
//		geocoder.getLatLng( Herkunft.getItemText( Herkunft.getSelectedIndex()), geocodeResultListener  );
//		zutatSpec.setHerkunft(herkunft );
		
		//  I put that away
//		ZutatSpecification zutatSpec = new ZutatSpecification();
//		zutatSpec.setZutat_id(zutat.getId());
//		zutatSpec.setName(zutat.getSymbol());
//		zutatSpec.setNormalCO2Value( zutat.getCO2eWert());
		
		zutatSpec.setMengeGramm(Integer.valueOf(InfoZutat.zutatMenge.getText()));
//		AjaxLoader.init();
//
//		  AjaxLoader.loadApi("maps", "2.s", new Runnable() {
//		      public void run() {
//		        mapsLoaded();
//		      }
//		    }, null);
//		  AjaxLoaderOptions options = AjaxLoaderOptions.newInstance();
		zutatSpec.setHerkunft( Herkuenfte.valueOf(Herkunft.getItemText( Herkunft.getSelectedIndex())) );
		
//TODO to the distance calculation call here
		zutatSpec.setDistance(distance);


		Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
		zutatSpec.setCookingDate( date );

		Transportmittel transportmittel;
		if(flugzeug.isDown()){
			transportmittel = Transportmittel.Flugzeug;
		} else {
			//TODO oversea
			transportmittel = Transportmittel.LKW;
		}
		zutatSpec.setTransportmittel(transportmittel);
		Produktionen produktion = Produktionen.konventionell;
		if(treibhaus.isDown()){
			produktion = Produktionen.Treibhaus;
		}
		if(bio.isDown()){
			produktion = Produktionen.biologisch;
		}
		zutatSpec.setProduktion(produktion);

		Zustaende zustand = Zustaende.frisch;
		if(eingemacht.isDown()){
			zustand = Zustaende.eingemacht;			
		}
		if(getrocknet.isDown()){
			zustand = Zustaende.getrocknet;			
		}
		if(tiefgekühlt.isDown()){
			zustand = Zustaende.tiefgekühlt;			
		}

		zutatSpec.setZustand(zustand);
		

		return zutatSpec;
	}

	protected void mapsLoaded() {
		// TODO Auto-generated method stub
		
		
	}

	@UiHandler("bio")
	void onBioButtonClick(ClickEvent event) {
		treibhaus.setDown(false);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("treibhaus")
	void onTreibhausButtonClick(ClickEvent event) {
		bio.setDown(false);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}

	@UiHandler("tiefgekühlt")
	void onTiefButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		eingemacht.setDown(false);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("getrocknet")
	void onTrockButtonClick(ClickEvent event) {
		eingemacht.setDown(false);
		tiefgekühlt.setDown(false);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	@UiHandler("eingemacht")
	void onEingButtonClick(ClickEvent event) {
		getrocknet.setDown(false);
		tiefgekühlt.setDown(false);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	
	@UiHandler("flugzeug")
	void onFlugButtonClick(ClickEvent event) {
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
	}
	
	@UiHandler("Herkunft")
	void onChange(ChangeEvent event) {
		String location;
		//		getDistance(Herkunft.getItemText(Herkunft.getSelectedIndex()),TopPanel.clientLocation.getText());
		if(TopPanel.currentLocation != null){
			location = TopPanel.currentLocation.getAddress();
		} else {
			location = TopPanel.clientLocation.getText();
		}
		
		SimpleDirectionsDemo(Herkunft.getItemText(Herkunft.getSelectedIndex()),location, null);
		ZutatSpecification zutatSpec = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec, EaternityRechner.selectedRow);
		
	}
	
	
	static void getDistance(final String start, final String stop ) {
		
		geocoder.getLocations(stop, new LocationCallback() {
		      public void onFailure(int statusCode) {
		    	  Window.alert("We cannot resolve this address to");
		      }

		      public void onSuccess(final JsArray<Placemark> locationsStop) {
		    	
		    	 if(TopPanel.currentLocation != null){
		    		 Placemark placeStop = locationsStop.get(0);
		    		 Placemark placeStart = TopPanel.currentLocation;
		    		 getDistanceEstimateToCurrent(placeStart.getPoint().toString(),placeStop.getPoint().toString(),null);
		    	 } else {
				  		geocoder.getLocations(start, new LocationCallback() {
						      public void onFailure(int statusCode) {
						    	  Window.alert("We cannot resolve this address from");
						      }

						      public void onSuccess(JsArray<Placemark> locationsStart) {
						    	  
						    	  Placemark placeStart = locationsStart.get(0);
						    	  Placemark placeStop = locationsStop.get(0);
						    	  getDistanceEstimateToCurrent(placeStart.getPoint().toString(),placeStop.getPoint().toString(),null);
						    	  TopPanel.currentLocation = placeStart;
						      }
						    });
		    	 }
		    	  
		      }
		    });
		
	}
	
	
	
	static void showAddress(final String address) {
		if (address.length() > 1) { 
	    geocoder.getLocations(address, new LocationCallback() {
	      public void onFailure(int statusCode) {
	        TopPanel.locationLabel.setText("Wir können die Adresse nicht finden: ");
	      }

	      public void onSuccess(JsArray<Placemark> locations) {
	    	  Placemark place = locations.get(0);
	    	  TopPanel.locationLabel.setText("Sie befinden sich in der Mitte von: " +place.getAddress() +"  ");
	    	  TopPanel.currentLocation = place;
	      }
	    });
		} else {
			TopPanel.locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
		}
	}
	
	static void getDistanceEstimateToCurrent(final String from, final String to, final ZutatSpecification zutatSpec) {
		if (TopPanel.currentLocation == null) { 
			TopPanel.locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein: ");
	    
		} else {
			geocoder.getLocations(from, new LocationCallback() {
			      public void onFailure(int statusCode) {
			        Window.alert("We cannot resolve this address from");
			      }

			      public void onSuccess(final JsArray<Placemark> locations1) {
			    	  
						geocoder.getLocations(to, new LocationCallback() {
						      public void onFailure(int statusCode) {
						        Window.alert("We cannot resolve this address to");
						      }

						      public void onSuccess(JsArray<Placemark> locations) {
						    	  Placemark place = locations.get(0);

						    	  distance = locations1.get(0).getPoint().distanceFrom(place.getPoint());
						    	  updateDistance(distance,zutatSpec);
						      }
						    });
			      }
			    });
		}
	}
	
	protected static void updateDistance(double distance2, ZutatSpecification zutatSpec) {
//		Window.alert(Double.toString(distance2)+" ist eine zu kurze Distanz");
		
		//TODO uncomment this:
		//if(EaternityRechner.zutatImMenu.contains(zutatSpec)){
		//int index = EaternityRechner.zutatImMenu.indexOf(zutatSpec);
		//zutatSpec.setDistance(distance2);
		//EaternityRechner.zutatImMenu.set(index,zutatSpec);
		//}
		//InfoZutat.distance =  distance2;
		
		ZutatSpecification zutatSpec2 = getZutatSpecification(InfoZutat.zutat) ;
		//TODO uncomment this:
		//InfoZutat.updateZutatCO2(zutatSpec2, EaternityRechner.selectedRow);
		
	}

	public static void SimpleDirectionsDemo(final String from, final String to, final ZutatSpecification zutatSpec) {
		
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
	        Window.alert("Schauen sie nach Schreibfehlern, in ihren Ortsbeschreibungen.");
	      }

	      public void onSuccess(DirectionResults result) {
//	    	Window.alert(Double.toString(result.getDistance().inMeters())+"m from "+from+" to "+to);
	        GWT.log("Successfully loaded directions.", null);
	        distance = result.getDistance().inMeters();
	        if (distance == 0){
	        	getDistanceEstimateToCurrent(from,to,zutatSpec);
	        } else {
	        	updateDistance(distance,zutatSpec);
	        }
	      }
	    });
	    
	    
	  }

}

