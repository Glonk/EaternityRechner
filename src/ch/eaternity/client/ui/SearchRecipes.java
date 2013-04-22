
package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.shared.RecipeSearchRepresentation;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class SearchRecipes extends Composite {
	interface Binder extends UiBinder<Widget, SearchRecipes> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------

	@UiField TextBox searchTextBox;
	@UiField Button searchButton;
	
	// ---------------------- Class Variables ----------------------
	
	RechnerActivity presenter;
	DataController dco;
	
	// ---------------------- public Methods -----------------------
	
	public SearchRecipes() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	

	private void bind() {
	
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
					}
				});
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		bind();
	}
	
	// ---------------------- UI Handlers ----------------------

	@UiHandler("searchButton")
	public void onSearchClick(ClickEvent event) {
		search();
	}
	
	@UiHandler("searchTextBox")
	public void onKeyDown(KeyDownEvent event) {
		if(KeyCodes.KEY_ENTER == event.getNativeKeyCode())
		{
			search();
		}
	}

	
	// ---------------------- private Methods ----------------------

	private void search() {
		RecipeSearchRepresentation recipeSearchRep = new RecipeSearchRepresentation(searchTextBox.getText(), dco.getRecipeScope());
		if (dco.getCurrentKitchen() != null)
			recipeSearchRep.setKitchenId(dco.getCurrentKitchen().getId());
		dco.searchRecipes(recipeSearchRep);
	}
	
}



