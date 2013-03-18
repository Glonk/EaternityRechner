package ch.eaternity.client.ui.widgets;

import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Season;
import org.eaticious.common.SeasonDate;
import org.eaticious.common.SeasonDateImpl;
import org.eaticious.common.Unit;

import ch.eaternity.client.DataController;
import ch.eaternity.client.ui.RecipeEdit;
import ch.eaternity.shared.Ingredient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

public class IngredientWidget extends Composite {
	interface Binder extends UiBinder<Widget, IngredientWidget> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField HTML dragHandle;
	@UiField TextBox amountBox;
	@UiField Label nameLabel;
	@UiField HTMLPanel iconsPanel;
	@UiField HTMLPanel ratingPanel;
	@UiField Label co2valueLabel;
	@UiField Button closeButton;
	
	private DataController dco;
	private Ingredient ingredient;
	private RecipeEdit recipeEdit;
	private int month;
	
	public IngredientWidget() {}
			
	public IngredientWidget(DataController cdo, Ingredient ingSpec, RecipeEdit recipeEdit, int month) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.dco = dco;
		this.ingredient = ingSpec;
		this.recipeEdit = recipeEdit;
		
		amountBox.setAlignment(TextAlignment.RIGHT);
		amountBox.setText(Integer.toString(ingSpec.getWeight().getAmount().intValue()));
		amountBox.setWidth("36px");
		
		nameLabel.setText(ingSpec.getProduct().getName(dco.getLocale()));
		updateCO2Value();
		
		updateIcons();
	}

	
	public void updateCO2Value() {
		co2valueLabel.setText("" + ((int)ingredient.getCalculatedCO2Value()) +"g");
	}

	
	public void updateIcons() {
			
			// Saisonal
			HTML seasonIcon = new HTML();
			if(ingredient.getCondition() != null && ingredient.getCondition().symbol.equalsIgnoreCase("frisch") && ingredient.getDistance().convert(Unit.KILOMETER).getAmount() < 500){
				Season season = ingredient.getProduct().getSeason();
				if(season != null){

					SeasonDate date = new SeasonDateImpl(month,1);
					
					if( date.after(season.getBeginning()) && date.before(season.getEnd()) ){
						seasonIcon.setHTML(seasonIcon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
					} else if (!ingredient.getCondition().symbol.equalsIgnoreCase("frisch") && !ingredient.getProduction().symbol.equalsIgnoreCase("GH") && ingredient.getDistance().convert(Unit.KILOMETER).getAmount() < 500) {
						seasonIcon.setHTML(seasonIcon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
					} else if (ingredient.getProduction().symbol.equalsIgnoreCase("GH")) {} 
			} 
			
			// rating
			HTML ratingIcon = new HTML();
			if(ingredient.getProduct().getCo2eValue().convert(Unit.KILOGRAM).getAmount() < .4){
				ratingIcon.setHTML("<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>");
		
			} else if(ingredient.getProduct().getCo2eValue().convert(Unit.KILOGRAM).getAmount() < 1.2){
				ratingIcon.setHTML("<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
		
			} else {
				ratingIcon.setHTML("<div class='extra-icon smiley3'><img src='pixel.png' height=1 width=20 /></div>");
			}

			// BIO
			HTML bioIcon = new HTML();
			if(ingredient.getProduction() != null && ingredient.getProduction().symbol.equalsIgnoreCase("bio")){
				bioIcon.setHTML("<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
			}

			
			iconsPanel.add(seasonIcon);
			iconsPanel.add(bioIcon);
			iconsPanel.add(seasonIcon);
		}
	}
	
	@UiHandler("closeButton")
	public void oncloseButtonPress(ClickEvent event) {	
		recipeEdit.removeIngredient(this);
	}
	

	public Ingredient getIngredient() {
		return this.ingredient;
	}
	
	public HTML getDragHandle() {
		return this.dragHandle;
	}
	
	
	@UiHandler("amountBox")
	public void onKeyUp(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();
		if ((!Character.isDigit((char) keyCode)) && (keyCode != KeyCodes.KEY_TAB)
				&& (keyCode != KeyCodes.KEY_BACKSPACE)
				&& (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) 
				&& (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
				&& (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
				&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN)) {
			// TextBox.cancelKey() suppresses the current keyboard event.
			amountBox.cancelKey();
		} else {
			String MengeZutatWert;
			if(!amountBox.getText().equalsIgnoreCase("")){
				MengeZutatWert = amountBox.getText().trim();
				try {
					ingredient.setWeight(new QuantityImpl(Double.valueOf(MengeZutatWert), Unit.GRAM));
					updateCO2Value();
				}
				catch (NumberFormatException nfe) {
					amountBox.setText("");
				}
						
			} else {
				MengeZutatWert = "";
			}
		}
	}
	
	
	
}

