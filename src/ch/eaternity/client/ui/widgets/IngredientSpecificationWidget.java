package ch.eaternity.client.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.ui.RecipeEdit;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IngredientSpecificationWidget extends Composite {
	interface Binder extends UiBinder<Widget, IngredientSpecificationWidget> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField Label NameLabel;
	@UiField Button closeButton;
	@UiField Label SeasonLabel;
	
	@UiField Label ExtractionLabel;
	@UiField ListBox ExtractionList;
	@UiField TextBox UnknownExtractionTextBox;
	
	@UiField Label TransportationLabel;
	@UiField ListBox TransportationList;
	@UiField HTML kmHTML;
	@UiField Label ProductionLabel;
	@UiField ListBox ProductionList;
	@UiField Label ConditionLabel;
	@UiField ListBox ConditionList;
	
	@UiField Label CostLabel;
	@UiField TextBox CostTextBox;
	@UiField Label CurrencyLabel;
	@UiField Label CostErrorLabel;
	
	@UiField HTML CoherenceHTML;
	
	private IngredientSpecification ingSpec;
	private RecipeEdit recipeEdit;
	private DataController dco;
	
	private static final String calulateAnchor = "<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>";
	
	// ---------------------- public Methods -----------------------
	
	public IngredientSpecificationWidget(RecipeEdit recipeEdit, IngredientSpecification ingSpec, DataController dco) {
		this.ingSpec = ingSpec;
		this.recipeEdit = recipeEdit;
		this.dco = dco;
		setFields();
	}
	
	// ---------------------- private Methods -----------------------

	private void setFields() {
		UnknownExtractionTextBox.setVisible(false);
		
	}
	
	private void changeExtraction() {
		//old triggerHerkunftChange
	}
	
	private void unknownExtractionSelected(){
		int width = ExtractionList.getOffsetWidth();
		ExtractionList.setVisible(false);
		
		UnknownExtractionTextBox.setWidth(Integer.toString(width)+"px");
		UnknownExtractionTextBox.setVisible(true);
		UnknownExtractionTextBox.setText("");
		
		kmHTML.setHTML(calulateAnchor);
		
		UnknownExtractionTextBox.setFocus(true);
	}
	
	private void dontAddExtraction() {
			UnknownExtractionTextBox.setVisible(false);
			ExtractionList.setSelectedIndex(i);
			ExtractionList.setVisible(true);
			ExtractionList.setFocus(true);
			changeExtraction();
	}
	
	private void processNewExtraction() {
  		String processedLocation= dco.getDist().strProcessLocation(UnknownExtractionTextBox.getText());
  		
  		if (processedLocation != null) {
  			double distance = dco.getDist().getDistance(processedLocation, "");
  			List<Extraction> extractions = new ArrayList<Extraction>();
  			boolean foundInList = false;
  			
  			for (Extraction extraction : extractions) {
  				if (extraction.symbol.equals(processedLocation) ){
	  		  		UnknownExtractionTextBox.setVisible(false);
	  				ExtractionList.setSelectedIndex(i);
	  				ExtractionList.setVisible(true);
	  				ExtractionList.setFocus(true);
  		  			foundInList = true;
  				}
  			}
  			
  		  	for (int i = 0; i < ExtractionList.getItemCount(); i++) {
  		  		if (ExtractionList.getValue(i).equals(processedLocation)) {
	  		  		UnknownExtractionTextBox.setVisible(false);
	  				ExtractionList.setSelectedIndex(i);
	  				ExtractionList.setVisible(true);
	  				ExtractionList.setFocus(true);
  		  			foundInList = true;
  		  		}
  			}	
	     
  		  	//don't add new extraction in ingredients list if already exists
	    	if (foundInList == false)
	    		ExtractionList.insertItem(processedLocation, 0);
	    		ingSpec.getIngredient().getExtractions().add(0, new Extraction(processedLocation));
	    	  
	    	changeExtraction();
  		}
  		else {
  			kmHTML.setHTML("Adresse nicht auffindbar!");
	    	  Timer t = new Timer() {
	    		  public void run() {
	    			  kmHTML.setHTML(calulateAnchor);
	    		  }
	    	  };
	    	  t.schedule(1000);
  		}		
	}
	
	// ---------------------- UI Handlers ----------------------
	
	@UiHandler("ExtractionList")
	public void onExtractionChange(ChangeEvent event) {
		int count = ExtractionList.getItemCount();
		int selected = ExtractionList.getSelectedIndex();
		if(count-1 == selected){
			// we have selected the "andere" item
			unknownExtractionSelected();
			
		} else {
			changeExtraction();
		}
	}
	
	@UiHandler("CostTextBox")
	public void onBlur(BlurEvent event) {
		String text = CostTextBox.getText();
		try { 
			if ("".equals(text)) {}
			else {
				//DecimalFormat df = new DecimalFormat();
				//df.setMaximumFractionDigits(2);
				//zutatSpec.setCost(df.parse(text).doubleValue());
				CostErrorLabel.setText("");
				double cost = Double.parseDouble(text);
				if (cost >= 0.0)
					ingSpec.setCost(cost);
				else
					CostErrorLabel.setText("Wert ungueltig");
			}
		}
		catch (NumberFormatException nfe) {
			CostErrorLabel.setText("Wert ungueltig");
		}
	}
	
	@UiHandler("UnknownExtractionTextBox")
	public void onKeyDown(KeyDownEvent event) {
		if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
			processNewExtraction();
		}
	}
	
	@UiHandler("kmHTML")
	public void onClick(ClickEvent event) {
		processNewExtraction();
	}
	
	
	
	
}
