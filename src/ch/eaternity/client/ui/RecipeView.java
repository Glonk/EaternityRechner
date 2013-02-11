package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.AlertEvent;
import ch.eaternity.client.events.AlertEventHandler;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.ui.widgets.RecipeWidget;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Util.RecipeScope;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class RecipeView extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	@UiField VerticalPanel alertPanel;
	@UiField Button addRecipeButton;
	@UiField FlexTable recipeList;
	@UiField Button addToCollectionButton;
	@UiField Button generateReportButton;
	
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	private List<Recipe> recipes = new ArrayList<Recipe>();
	
	// ---------------------- public Methods -----------------------
	
	public RecipeView() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.setHeight("1000px");
		bind();
	}

	
	@UiHandler("addRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		
		presenter.goTo(new RechnerRecipeEditPlace("new"));
	}
	
	@UiHandler("recipeList")
	void onRecipeClicked(ClickEvent event) {
		Cell cell = recipeList.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			RecipeWidget recipeWidget = (RecipeWidget) recipeList.getWidget(row, 1);
			presenter.goTo(new RechnerRecipeEditPlace(recipeWidget.getRecipeId().toString()));
		}
	}
		
	private void bind() {
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
						updateList();
					}
				});
		presenter.getEventBus().addHandler(AlertEvent.TYPE,
				new AlertEventHandler() {
					@Override
					public void onEvent(final AlertEvent event) {
						if (event.destination == AlertEvent.Destination.VIEW) {
							alertPanel.insert(event.alert, 0);
							
							Timer t = new Timer() {
								public void run() {
									event.alert.close();
								}
							};
							if (event.timeDisplayed != null)
								t.schedule(event.timeDisplayed);
						}
					}
				});
	}
	
	private void updateList() {
		RecipeScope recipeScope = dco.getRecipeScope();
		
		if (recipeScope == RecipeScope.USER ) {
			if (dco.getLoginInfo().isLoggedIn()){
				recipes = dco.getUserRecipes();
			}
			else {
				presenter.getEventBus().fireEvent(new AlertEvent("Your not logged in. Login and try again.", AlertType.WARNING, AlertEvent.Destination.VIEW));
				recipes.clear();
			}
		}
		else if (recipeScope == RecipeScope.KITCHEN) {
			if (dco.getCurrentKitchen() != null)
				recipes = dco.getCurrentKitchenRecipes();
			else {
				presenter.getEventBus().fireEvent(new AlertEvent("Your not in a kitchen. Try to enter a kitchen again.", AlertType.WARNING, AlertEvent.Destination.VIEW));	
				recipes.clear();
			}
		}
		else if (recipeScope == RecipeScope.PUBLIC)
			recipes = dco.getPublicRecipes();
		else 
			recipes = dco.getPublicRecipes();
		
		recipeList.removeAllRows();
		int row = 0;
		for (Recipe recipe : recipes) {
			RecipeWidget recipeWidget = new RecipeWidget(recipe,dco);
			recipeList.insertRow(row);
			recipeList.setWidget(row,0,recipeWidget);
			row = row + 1;
		}
	}
	
	// ---------------------- UI Handlers ----------------------
	
	
	// ---------------------- private Methods ----------------------
	
}
