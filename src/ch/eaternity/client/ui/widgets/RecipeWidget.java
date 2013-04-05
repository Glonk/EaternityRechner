package ch.eaternity.client.ui.widgets;

import ch.eaternity.client.DataController;
import ch.eaternity.shared.Recipe;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;



public class RecipeWidget extends Composite {
	interface Binder extends UiBinder<Widget, RecipeWidget> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField HTML recipeImage;
	
	@UiField Label recipeTitle;
	@UiField Label recipeSubTitle;
	@UiField HTML recipeRating;
	@UiField Label recipeCo2Value;
	
	@UiField Button deleteButton;
	
	private Recipe recipe;
	private DataController dco;
	
	public RecipeWidget() {}
	
	public RecipeWidget(Recipe recipe, DataController dco) {
		initWidget(uiBinder.createAndBindUi(this));
		this.recipe = recipe;
		this.dco = dco;
		
		if(recipe.getImage() !=null){
			recipeImage.setHTML("<img src='" + recipe.getImage().getServingUrl() + "=s80-c' />");
		} else {
			recipeImage.setHTML("<img src='http://placehold.it/80x80' />");
		}
		
		recipeTitle.setText(recipe.getTitle());
		recipeSubTitle.setText(recipe.getSubTitle());
		recipeCo2Value.setText("" + recipe.getCO2Value());
		
		if (recipe.getCO2Value() < 700) {
			recipeRating.setHTML("<img src='/images/rating_bars.png' />");
		}
		else if (recipe.getCO2Value() > 700 && recipe.getCO2Value() < 1500) {
			recipeRating.setHTML("<img src='/images/rating_bars.png' />");
		}
		else {
			recipeRating.setHTML("<img src='/images/rating_bars.png' />");
		}
	}
	
	@UiHandler("deleteButton")
	public void onDeleteClicked(ClickEvent event) {
		dco.deleteRecipe(recipe.getId());
	}
	
	public Long getRecipeId() {
		return recipe.getId();
	}
	
}