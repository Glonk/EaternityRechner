package ch.eaternity.client.ui.widgets;

import java.util.List;

import org.eaticious.common.Quantity;
import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.MonthChangedEventHandler;
import ch.eaternity.client.ui.RecipeEdit;
import ch.eaternity.shared.Condition;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.SeasonDate;
import ch.eaternity.shared.Transportation;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
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
	@UiField HTML SeasonHTML;
	
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
	
	private Ingredient ingredient;
	private FoodProduct product;
	private RecipeEdit recipeEdit;
	private DataController dco;
	
	private static final String calulationAnchor = "<a style='margin-left:3px;cursor:pointer;cursor:hand;'>berechnen</a>";
	
	private boolean presenterSetted = false;
	
	// ---------------------- public Methods -----------------------
	
	public IngredientSpecificationWidget() {
		 initWidget(uiBinder.createAndBindUi(this));
		 this.setVisible(false);
	}
	
	public void setPresenter(RechnerActivity presenter, Ingredient ingredient) {
		this.ingredient = ingredient;
		this.product = ingredient.getFoodProduct();
		this.recipeEdit = presenter.getRecipeEdit();
		this.dco = presenter.getDCO();
		setFields();
		
		presenter.getEventBus().addHandler(MonthChangedEvent.TYPE,
				new MonthChangedEventHandler() {
					@Override
					public void onEvent(MonthChangedEvent event) {
						updateSeasonCoherency();
					}
				});
		presenterSetted = true;
		
	}
	
	
	
	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
		this.product = ingredient.getFoodProduct();
		setFields();
	}
	
	public boolean isPresenterSetted(){
		return presenterSetted;
	}
	
	// ---------------------- private Methods -----------------------

	private void setFields() {
		UnknownExtractionTextBox.setVisible(false);
		
		NameLabel.setText(product.getName());
		
		ExtractionList.clear();
		for (Extraction extraction : product.getExtractions()) {
			ExtractionList.addItem(extraction.getName());
		}
		TransportationList.clear();
		for (Transportation transport : product.getTransportations()) {
			TransportationList.addItem(transport.getSymbol());
		}
		ProductionList.clear();
		for (Production production : product.getProductions()) {
			ProductionList.addItem(production.getSymbol());
		}
		ConditionList.clear();
		for (Condition condition : product.getConditions()) {
			ConditionList.addItem(condition.getSymbol());
		}
	}
	
	private void changeExtraction() {
		//old triggerHerkunftChange
	}
	
	private void switchToUnknownExtraction(){
		int width = ExtractionList.getOffsetWidth();
		ExtractionList.setVisible(false);
		
		UnknownExtractionTextBox.setWidth(Integer.toString(width)+"px");
		UnknownExtractionTextBox.setVisible(true);
		UnknownExtractionTextBox.setText("");
		
		kmHTML.setHTML(calulationAnchor);
		
		UnknownExtractionTextBox.setFocus(true);
	}
	

	private void switchToKnownExtractions() {
		changeExtraction();
		UnknownExtractionTextBox.setVisible(false);
		ExtractionList.setVisible(true);
		ExtractionList.setFocus(true);
	}
	
	private void updateExtractionList(Extraction selectedExtraction) {
		int i = 0;
		ExtractionList.clear();
		// find the corresponding item in the ListBox
		for (Extraction extraction : product.getExtractions()) {
			ExtractionList.addItem(extraction.getName());
			
	  		if (extraction.getName().equals(selectedExtraction.getName())) 
	  			ExtractionList.setSelectedIndex(i);
	  		i++;
		}
	}
	
	private void processNewExtraction() {
  		String processedLocation= dco.getDist().strProcessLocation(UnknownExtractionTextBox.getText());
  		
  		if (processedLocation != null) {
  			QuantityImpl distance = dco.getDist().getDistance(processedLocation, dco.getCurrentLocation());
  			ingredient.setDistance(distance);
  			
  			List<Extraction> extractions = product.getExtractions();
  			boolean foundInList = false;
  			
  			for (Extraction extraction : extractions) {
  				if (extraction.symbol.equals(processedLocation) ){
  					updateExtractionList(extraction);
  					switchToKnownExtractions();
  		  			foundInList = true;
  				}
  			}	
	     
  		  	//don't add new extraction in ingredients list if already exists
	    	if (foundInList == false) {
	    		Extraction extraction = new Extraction(processedLocation);
	    		ExtractionList.insertItem(processedLocation, 0);
	    		product.getExtractions().add(0, extraction);
	    		ingredient.setExtraction(extraction);
	    	}
  		}
  		else {
  			kmHTML.setHTML("Adresse nicht auffindbar!");
	    	  Timer t = new Timer() {
	    		  public void run() {
	    			  kmHTML.setHTML(calulationAnchor);
	    		  }
	    	  };
	    	  t.schedule(1000);
  		}		
	}
	
	private void updateSeasonCoherency() {
		if (product.getSeason() != null) {
			String seasonText = "von " + product.getSeason().getBeginning().toMonthString() + " bis " + product.getSeason().getEnd().toMonthString();
			
			SeasonDate begin = product.getSeason().getBeginning();
			SeasonDate end = product.getSeason().getEnd();
			SeasonDate date = new SeasonDate(dco.getCurrentMonth(),1);
			
			if( date.after(begin) && date.before(end)) {
				seasonText.concat("<br />Diese Zutat ist saisonal");
				CoherenceHTML.setText("Angaben sind kohärent. <br /> Es ist möglich die Zutat frisch und lokal zu beziehen.");
			}
			else {
				seasonText.concat("<br />Diese Zutat ist nicht saisonal");
				if (ingredient.getDistance().convert(Unit.KILOMETER).getAmount() < 100 
						&& !ingredient.getProduction().getSymbol().equalsIgnoreCase("GH")
						&& ingredient.getCondition().getSymbol().equalsIgnoreCase("frisch")) {
					CoherenceHTML.setText("Angaben sind unvollständig. <br >" +
							"Bitte geben Sie an ob die Zutat importiert, konserviert oder im Gewächaus produziert wurde.");
				}
				
			}
		}
		else {
			SeasonLabel.setVisible(false);
			SeasonHTML.setVisible(false);
			CoherenceHTML.setVisible(false);
		}		
	}
	
	// ---------------------- UI Handlers ----------------------
	
	@UiHandler("closeButton") 
	public void onCloseClick(ClickEvent event) {
		this.setVisible(false);
	}
	
	
	@UiHandler("ExtractionList")
	public void onExtractionChange(ChangeEvent event) {
		int count = ExtractionList.getItemCount();
		int selected = ExtractionList.getSelectedIndex();
		if(count-1 == selected){
			// we have selected the "andere" item
			switchToUnknownExtraction();
			
		} else {
			changeExtraction();
		}
		recipeEdit.updateIngredientValue(ingredient);
	}
	
	@UiHandler("TransportationList")
	public void onTransportationChange(ChangeEvent event) {
		ingredient.setTransportation(product.getTransportation(TransportationList.getItemText(TransportationList.getSelectedIndex())));
		recipeEdit.updateIngredientValue(ingredient);
	}
	
	@UiHandler("ProductionList")
	public void onProductionChange(ChangeEvent event) {
		ingredient.setProduction(product.getProduction(ProductionList.getItemText(ProductionList.getSelectedIndex())));
		recipeEdit.updateIngredientValue(ingredient);
		updateSeasonCoherency();
	}
	
	@UiHandler("ConditionList")
	public void onConditionChange(ChangeEvent event) {
		ingredient.setCondition(product.getCondition(ConditionList.getItemText(ConditionList.getSelectedIndex())));
		recipeEdit.updateIngredientValue(ingredient);
		updateSeasonCoherency();
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
					ingredient.setCost(cost);
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
