package ch.eaternity.client.ui;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.place.RechnerRecipeEditPlace;

import com.google.gwt.user.client.ui.Composite;

public class RecipeView extends Composite {
	
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
	
}
