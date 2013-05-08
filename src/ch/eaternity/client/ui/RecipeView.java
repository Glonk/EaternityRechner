package ch.eaternity.client.ui;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.AlertEvent;
import ch.eaternity.client.events.AlertEventHandler;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.ui.cells.RecipeCell;
import ch.eaternity.shared.RecipeInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

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
	@UiField Button addToCollectionButton;
	@UiField Button generateReportButton;
	
	@UiField ScrollPanel recipeScrollPanel;
	
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	// Create a data provider.
    ListDataProvider<RecipeInfo> recipeDataProvider;
	
	// ---------------------- public Methods -----------------------
	
	public RecipeView() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	public void setPresenter(final RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		this.recipeDataProvider = dco.getRecipeDataProvider();
		
		// this shoud be in css
		addToCollectionButton.setVisible(false);
		generateReportButton.setVisible(false);
		
		// initialize a key provider to refer to the same selection states
		ProvidesKey<RecipeInfo> keyProvider = new ProvidesKey<RecipeInfo>() {
		      public Object getKey(RecipeInfo item) {
		        // Always do a null check.
		        return (item == null) ? null : item.getId();
		      }
		};
		    
		// Create a cell to render each value in the list.
	    RecipeCell recipeCell = new RecipeCell(presenter);
	    
	    // Create a CellList that uses the cell.
	    CellList<RecipeInfo> cellList = new CellList<RecipeInfo>(recipeCell, keyProvider);
	    
	    setupOnePageList(cellList);
	    
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<RecipeInfo> selectionModel = new SingleSelectionModel<RecipeInfo>(keyProvider);
	    
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	    	public void onSelectionChange(SelectionChangeEvent event) {
	    		RecipeInfo selected = selectionModel.getSelectedObject();
		        if (selected != null) {
		        	//presenter.goTo(new RechnerRecipeEditPlace(selected.getId().toString()));
		        	selectionModel.setSelected(selected, false);
		        }
	    	}
	    });
	    
	    // Connect the list to the data provider.
	    recipeDataProvider.addDataDisplay(cellList);
	    
	    recipeDataProvider.setList(dco.getRecipeInfos());
	    
	    // Add it to the display panel.
	    recipeScrollPanel.setWidget(cellList);
		
		bind();
	}
	
	public static void setupOnePageList(final AbstractHasData<?> cellTable) {
	    cellTable.addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
	        @Override
	        public void onRowCountChange(RowCountChangeEvent event) {
	            cellTable.setVisibleRange(new Range(0, event.getNewRowCount()));
	        }
	    });
	}

	
	@UiHandler("addRecipeButton")
	public void onAddRecipeButtonPress(ClickEvent event) {
		
		presenter.goTo(new RechnerRecipeEditPlace("new"));
	}
	
		
	private void bind() {
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(UpdateRecipeViewEvent.TYPE,
				new UpdateRecipeViewEventHandler() {
					@Override
					public void onEvent(UpdateRecipeViewEvent event) {
						//updateList();
					}
				});
		presenter.getEventBus().addHandler(AlertEvent.TYPE,
				new AlertEventHandler() {
					@Override
					public void onEvent(final AlertEvent event) {
						if (event.destination == AlertEvent.Destination.VIEW || event.destination == AlertEvent.Destination.BOTH) {
							//alertPanel.insert(event.alert, 0);
							
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
	/*
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
	*/
	// ---------------------- UI Handlers ----------------------
	
	
	// ---------------------- private Methods ----------------------
	
}
