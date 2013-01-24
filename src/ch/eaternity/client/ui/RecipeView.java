package ch.eaternity.client.ui;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.place.RechnerRecipeEditPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RecipeView extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	RechnerActivity presenter;
	ClientFactory clientFactory;
	DataController dco;
	
	public RecipeView(RechnerActivity presenter, ClientFactory clientFactory) {
		this.presenter = presenter;
		this.clientFactory = clientFactory;
		this.dco = presenter.getDCO();
	}

	private void openRecipeEdit() {
		dco.openEditRecipe();
		presenter.goTo(new RechnerRecipeEditPlace("RecipeName clicked"));
	}
	
	@UiHandler("addRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		dco.createRecipe();		
	}
	
}
