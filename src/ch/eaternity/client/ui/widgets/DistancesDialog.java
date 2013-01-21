package ch.eaternity.client.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.DataService;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Distance;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DistancesDialog extends DialogBox{
	interface Binder extends UiBinder<Widget, DistancesDialog> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Button executeButton;
	@UiField FlexTable mapsTable;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable summaryTable;
	@UiField Button locationButton;
	@UiField TextBox adressBox;
	@UiField Label statusInfo;
	@UiField InlineLabel locationLabel;

	private String currentLocation;
	private List<SingleDistance> allDistances = new ArrayList<SingleDistance>();
	
	private Presenter presenter;
	private DataController dco;
	private Distance distances;
	
	// --------------- UiHandlers ----------------
	
	
	@UiHandler("executeButton")
	void onOkayClicked(ClickEvent event) {
		dco.changeCurrentLocation(currentLocation);	
		hide();
	}

	@UiHandler("locationButton")
	void onLocClicked(ClickEvent event) {
		summaryTable.removeAllRows();
		processAddress(adressBox.getText());
	}
	
	// ---------------  public Methods--------------- 
	
	public DistancesDialog() {}
	
	public void setPresenter(Presenter presenter){
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.distances = dco.getDist();
		
		executeButton.setEnabled(false);
		
		if (dco.getCurrentLocation() == null) 
			locationLabel.setText("Bitte geben Sie hier Ihre Adresse ein");
		else
			adressBox.setText(dco.getCurrentLocation());
		openDialog(); 	
	}

	
	private void openDialog() {
		setWidget(binder.createAndBindUi(this));
		setAnimationEnabled(true);
		setGlassEnabled(true);
		show();
		scrollPanel.setHeight("400px");
		center();
		setText("Adresse eingeben.");
	}
	

	private void processAddress(final String address) {
		mapsTable.removeAllRows();
		
		String processedLocation = distances.strProcessLocation(address);
		if (processedLocation.equals("failed")) {
			statusInfo.setText("Wir können diese Adresse nicht finden");
			adressBox.selectAll();
		}
		else {
			setText("Berechne alle Routen von: " + processedLocation);
			locationLabel.setText("Sie befinden sich in der Mitte von: ");
			List<String> extractions = new ArrayList<String>();
			if (dco.getCurrentKitchen() != null ) {
				for (Recipe recipe : dco.getCurrentKitchenRecipes()){
					for (IngredientSpecification ingSpec : recipe.getZutaten())
						extractions.add(ingSpec.getExtraction().symbol);
				}
			}
			else {
				for (IngredientSpecification ingSpec : dco.getEditRecipe().getZutaten())
					extractions.add(ingSpec.getExtraction().symbol);
			}
			
			// add sandwatch here during waiting...
			allDistances = distances.calculateDistances(processedLocation, extractions, mapsTable);
			currentLocation = processedLocation;
			executeButton.setEnabled(true);
			showTable();
		}
	}


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
				processAddress(adressBox.getText());
				break;
			case KeyCodes.KEY_ESCAPE:
				hide();
				break;
			}
		}
	}


	private void showTable() {
		summaryTable.removeAllRows();
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

}

