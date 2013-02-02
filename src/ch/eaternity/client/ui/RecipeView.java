package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class RecipeView extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	@UiField Button addRecipeButton;
	@UiField FlexTable recipeList;
	@UiField Button addToCollectionButton;
	@UiField Button generateReportButton;
	
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	// ---------------------- public Methods -----------------------
	
	public RecipeView() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1600px");
		bind();
	}

	private void openRecipeEdit() {
		dco.setEditRecipe();
		presenter.goTo(new RechnerRecipeEditPlace("RecipeName clicked"));
	}
	
	@UiHandler("addRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		dco.createRecipe();
		// does the RechnerRecipeEditPlace live without a recipe?
		// the design pattern to encouple everything makes sense -> "strict separation of concerns."
		presenter.goTo(new RechnerRecipeEditPlace(null));
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
	
	// ---------------------- UI Handlers ----------------------
	
	// ---------------------- private Methods ----------------------
	
}
