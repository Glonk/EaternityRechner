//package ch.eaternity.client.ui.widgets;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import ch.eaternity.client.DataController;
//import ch.eaternity.client.activity.RechnerActivity;
//import ch.eaternity.client.events.KitchenChangedEvent;
//import ch.eaternity.client.events.KitchenChangedEventHandler;
//import ch.eaternity.client.events.LocationChangedEvent;
//import ch.eaternity.client.events.LocationChangedEventHandler;
//import ch.eaternity.client.events.MonthChangedEvent;
//import ch.eaternity.client.events.MonthChangedEventHandler;
//import ch.eaternity.client.ui.HelloView.Presenter;
//import ch.eaternity.client.ui.RechnerView;
//import ch.eaternity.client.ui.RecipeView;
//import ch.eaternity.shared.Ingredient;
//import ch.eaternity.shared.Recipe;
//import ch.eaternity.shared.SeasonDate;
//import ch.eaternity.shared.comparators.NameComparator;
//import ch.eaternity.shared.comparators.RezeptNameComparator;
//import ch.eaternity.shared.comparators.RezeptValueComparator;
//import ch.eaternity.shared.comparators.ValueComparator;
//
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.dom.client.KeyCodes;
//import com.google.gwt.event.dom.client.KeyDownEvent;
//import com.google.gwt.event.dom.client.KeyUpEvent;
//import com.google.gwt.i18n.client.NumberFormat;
//import com.google.gwt.resources.client.CssResource;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.uibinder.client.UiHandler;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.Anchor;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DockLayoutPanel;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.HTMLPanel;
//import com.google.gwt.user.client.ui.HTMLTable.Cell;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.ResizeComposite;
//import com.google.gwt.user.client.ui.SplitLayoutPanel;
//import com.google.gwt.user.client.ui.SuggestBox;
//import com.google.gwt.user.client.ui.Widget;
//
//
//
///**
// * A composite that displays a list of ingredients that can be selected.
// */
///*
//public class IngredientsResultWidget extends ResizeComposite {
//	
//	// GWT UI-Binder (to display the search class)
//	interface Binder extends UiBinder<Widget, IngredientsResultWidget> { }
//	private static final Binder binder = GWT.create(Binder.class);
//
//	/**
//	 * User interface elements
//	 
//	// Legend and informations:
//	@UiField HTMLPanel panelSouth;
//	@UiField HTMLPanel legendPanel;
//	
//	@UiField Anchor legendAnchor;
//	@UiField Anchor legendAnchorClose;
//	
//	@UiField Image imageCarrot;
//	@UiField Image imageSmiley1;
//	@UiField Image imageSmiley2;
//	@UiField Image imageSmiley3;
//	@UiField Image imageRegloc;
//	@UiField Image imageBio;
//	
//	// Search Panel (Box and Button)
//	@UiField DockLayoutPanel SearchBox;
//	@UiField HTML SearchLabel;
//	@UiField public static HTML yourRecipesText;
//	@UiField public static SuggestBox SearchInput;
//	
//	// Display Results in:
//	@UiField DockLayoutPanel displayResultsPanel;
//	@UiField static HTMLPanel yourMealsPanel;
//	@UiField SplitLayoutPanel mealsSplitPanels;
//	@UiField SplitLayoutPanel subMealsSplitPanels;
//	@UiField HTMLPanel scrollAbleHtml;
//	
//	// Search results Tables
//	@UiField static FlexTable table;
//	@UiField static FlexTable tableMeals;
//	@UiField static FlexTable tableMealsYours;
//
//	// CSS reference for the alternating row coloring
//	@UiField static MarkingStyle markingStyle;
//	@UiField static SelectionStyle selectionStyle;
//	@UiField static EvenStyleRow evenStyleRow;
//	
//	// sorting of the tables:
//	@UiField Anchor co2Order;
//	@UiField Anchor alphOrder;
//	
//	// ---------------------- Class Interfaces ---------------------
//	
//	 //Call-back when items are selected. 
//	public interface Listener { // Call-back for ingredient click
//		void onItemSelected(Ingredient item);
//	}
//
//	public interface ListenerMeals { // Call-back for menu click
//		void onItemSelected(Recipe item);
//	}
//
//
//	interface MarkingStyle extends CssResource {
//		String markedRow();
//	}
//	
//	
//	//TODO check why everything crashes for selectedRow = -1
//	interface SelectionStyle extends CssResource {
//		String selectedRow();
//	}
//	
//	// Color the rows alternating
//	interface EvenStyleRow extends CssResource {
//		String evenRow();
//	}
//	
//	// ---------------------- Class Variables ----------------------
//
//		private RechnerActivity presenter;
//		private DataController dco;
//		
//		public List<Ingredient> foundIngredients = new ArrayList<Ingredient>();
//		public List<Ingredient> foundAlternativeIngredients = new ArrayList<Ingredient>();
//		public List<Recipe> foundPublicRecipes = new ArrayList<Recipe>();
//		public List<Recipe> foundUserRecipes = new ArrayList<Recipe>();
//		
//		public String searchString = "";
//			
//		// choose this sorting method
//		static int sortMethod = 1;
//		
//		private Listener listener;
//		private ListenerMeals listenerMeals;
//		
//		// CSS of rows
//		static int markedRow = 0;
//		static int selectedRow = 0;
//		
//
//	// --------------------------- public Methods ----------------------------------
//
//	public void setPresenter(RechnerActivity presenter){
//		this.presenter = presenter;
//		this.dco = presenter.getDCO();
//		initTable(); // just the size
//	}
//	private RechnerView superDisplay;
//	public void setSuperDisplay(RechnerView superDisplay){
//		this.superDisplay = superDisplay;
//	}
//
//	public IngredientsResultWidget() {
//		
//		// bind and display the Search
//		initWidget(binder.createAndBindUi(this));
//
//		// we have to wait till the database is loaded:
//		SearchInput.setText("wird geladen...");
//	
//		SearchInput.setFocus(true);
//		
//
//		subMealsSplitPanels.addStyleName("noSplitter");
//		setVDraggerHeight("0px");
//
//		initToolTips();
//		
//		
//		bind();
//	}
//	
//	private void bind() {
//		presenter.getEventBus().addHandler(KitchenChangedEvent.TYPE, new KitchenChangedEventHandler() {
//			@Override
//			public void onKitchenChanged(KitchenChangedEvent event) {
//				if (event.id.equals(-1)) {
//					yourRecipesText.setHTML(" in eigenen Rezepten");
//					updateResults(SearchInput.getText());
//				}
//				else {
//					yourRecipesText.setHTML(" in " + dco.getCurrentKitchen().getSymbol() + " Rezepten");
//					updateResults(SearchInput.getText());
//				}
//					
//			}
//			});
//		presenter.getEventBus().addHandler(RecipePublicityChangedEvent.TYPE, new RecipePublicityChangedEventHandler() {
//			@Override
//			public void onEvent(RecipePublicityChangedEvent event) {
//					
//					// TODO show in public recipes, mark recipe as open
//			}
//			});
//		presenter.getEventBus().addHandler(LocationChangedEvent.TYPE, new LocationChangedEventHandler() {
//			@Override
//			public void onEvent(LocationChangedEvent event) {
//				// Could be faster with just delet list item, but not many times recipes are getting deleted...
//				updateResults(SearchInput.getText());
//			}
//			});
//		presenter.getEventBus().addHandler(MonthChangedEvent.TYPE, new MonthChangedEventHandler() {
//			@Override
//			public void onEvent(MonthChangedEvent event) {
//				displayIngredients();
//			}
//			});
//		
//	}
//	
//	//---------------- UI HANDLERS ----------------
//	
//	@UiHandler("co2Order")
//	void onCo2Clicked(ClickEvent event) {
//		sortMethod = 1;
//		sortResults(sortMethod);
//		displayResults();
//	}
//
//	@UiHandler("alphOrder")
//	void onAlphClicked(ClickEvent event) {
//		sortMethod = 5;
//		sortResults(sortMethod);
//		displayResults();
//	}
//	
//	// Handle search input
//	private int numKeyPressed;
//	@UiHandler("SearchInput")
//	public void onKeyUp(KeyUpEvent event) {
//		// this matches up to 2 words!
//		numKeyPressed++;
//		// only update on text change
//		if (numKeyPressed % 2 == 0)
//		{
//			if( !SearchInput.getText().trim().equals(searchString)){
//				searchString = SearchInput.getText().trim();
//				updateResults(searchString);
//			}
//		}
//	}
//
//	
//	// Handle Enter Key to add new ingredient
//	//ugly workaround for catching double firing of events from suggestbox (http://code.google.com/p/google-web-toolkit/issues/detail?id=3533)
//	private int numEnterKeyPressed;
//	private int numDownKeyPressed;
//	private int numUpKeyPressed;
//	
//	@UiHandler("SearchInput")
//	public void onKeyDown(KeyDownEvent event) {
//		if(KeyCodes.KEY_ENTER == event.getNativeKeyCode())
//		{
//			numEnterKeyPressed++;
//			if (numEnterKeyPressed % 2 == 0)
//			{
//				selectRow(markedRow);
//				SearchInput.setText("");
//				updateResults("");
//				markedRow = 0;
//			}
//		}
//		if(KeyCodes.KEY_DOWN == event.getNativeKeyCode())
//		{
//			numDownKeyPressed++;
//			if (numDownKeyPressed % 2 == 0)
//				changeMarkedRow(markedRow + 1);
//		}
//		if(KeyCodes.KEY_UP == event.getNativeKeyCode())
//		{
//			numUpKeyPressed++;
//			if (numUpKeyPressed % 2 == 0)
//				changeMarkedRow(markedRow - 1);
//		}
//	}
//	
//	/* ALternative Way: select the textbox behind, doesn't worked
//	SearchInput.getTextBox().addKeyPressHandler(new KeyPressHandler() {      
//		public void onKeyPress(KeyPressEvent event) {
//			if (KeyCodes.KEY_ENTER == event.getCharCode()) //getNativeKeyCode())
//				selectRow(markedRow);
//	    }
//	});
//	
//
//	
//	@UiHandler("legendAnchor")
//	public void onLegendClick(ClickEvent event) {
//
//		legendPanel.setStyleName("legend1");
//		displayResultsPanel.setWidgetSize(panelSouth, 220);
//		displayResultsPanel.forceLayout();
//
//	}
//
//	@UiHandler("legendAnchorClose")
//	public void onLegendCloseClick(ClickEvent event) {
//
//		legendPanel.setStyleName("legend2");
//		displayResultsPanel.setWidgetSize(panelSouth, 80);
//		displayResultsPanel.forceLayout();
//
//	}
//
//	@UiHandler("table")
//	void onTableClicked(ClickEvent event) {
//		// Select the row that was clicked (-1 to account for header row).
//		Cell cell = table.getCellForEvent(event);
//		if (cell != null) {
//			int row = cell.getRowIndex();
//			selectRow(row);
//		}
//	}
//
//	@UiHandler("tableMeals")
//	void onTableMealsClicked(ClickEvent event) {
//		// Select the row that was clicked (-1 to account for header row).
//		Cell cell = tableMeals.getCellForEvent(event);
//		if (cell != null) {
//			int row = cell.getRowIndex();
//			selectRowMeals(row);
//		}
//	}
//
//	@UiHandler("tableMealsYours")
//	void onTableMealsYoursClicked(ClickEvent event) {
//		// Select the row that was clicked (-1 to account for header row).
//		Cell cell = tableMealsYours.getCellForEvent(event);
//		if (cell != null) {
//			int row = cell.getRowIndex();
//			selectRowMealsYours(row);
//		}
//	}
//
//
//	// ---------------------------------------------------------------
//	
//	// what is this thing for?
//	public void setVDraggerHeight (String height)
//	{
//		//	  SplitLayoutPanel p = (SplitLayoutPanel) this.getWidget ();
//		SplitLayoutPanel p = 	subMealsSplitPanels;
//		int widgetCount = p.getWidgetCount ();
//		for (int i = 0; i < widgetCount; i++) {
//			Widget w = p.getWidget (i);
//			if (w.getStyleName ().equals ("gwt-SplitLayoutPanel-VDragger")) {
//				w.setHeight (height);
//			}
//		}
//	}
//	
//	/**
//	 * The search algorithm
//	 
//	
//	// TODO this is getting called twice all the time...
//	public void updateResults(String searchString) {
//		SearchInput.setText(searchString);
//		
//		foundIngredients.clear();
//		foundAlternativeIngredients.clear();
//		foundPublicRecipes.clear();
//		foundUserRecipes.clear();
//		
//		// Get data from Data Controller
//		dco.searchIngredients(searchString, foundIngredients, foundAlternativeIngredients);
//		foundPublicRecipes = dco.searchPublicRecipes(searchString);
//		if (dco.getCurrentKitchen() != null)
//			foundUserRecipes = dco.searchKitchenRecipes(searchString);
//		else 
//			foundUserRecipes = dco.searchUserRecipes(searchString);
//
//		
//		// Display Results
//		//TODO: Special Display for alternatives, now still done in displayIngredient
//		foundIngredients.addAll(foundAlternativeIngredients);
//
//		displayResults();
//		
//		// Correct mark adjustements
//		int numOfIngredientsFound = foundIngredients.size();
//		if (markedRow <= 0)
//			changeMarkedRow(0);
//		else if(markedRow >= numOfIngredientsFound)
//			changeMarkedRow(numOfIngredientsFound-1);
//		else
//			changeMarkedRow(markedRow);
//		
//		if (searchString.equals(""))
//			changeMarkedRow(0);
//		
//		// Show your Recipes panel
//		if (foundUserRecipes.size() > 0){
//			yourMealsPanel.setVisible(true);
//		} else {
//			yourMealsPanel.setVisible(false);
//		}
//		
//	}
//	
//	/**
//	 * The sorting functions
//	 * 
//	 * Call displayResults for showing effect
//	 *
//
//	public void sortResults(int sortMethod) {
//		this.sortMethod = sortMethod;
//		
//		switch(sortMethod){
//		case 1:{
//			//"co2-value"
//			Collections.sort(foundIngredients,new ValueComparator());
//			Collections.sort(foundAlternativeIngredients,new ValueComparator());
//			Collections.sort(foundPublicRecipes,new RezeptValueComparator());
//			Collections.sort(foundUserRecipes,new RezeptValueComparator());
//			break;
//		}
//		case 2:{
//			// "popularity"
//
//		}
//		case 3:{
//			//"saisonal"
//
//		}
//		case 4:{
//			//"kategorisch"
//			// vegetarisch
//			// vegan
//			// etc.
//		}
//		case 5:{
//			//"alphabetisch"
//			
//			// could there be a better method to do this? like that:
//			//			   ComparatorChain chain = new ComparatorChain();
//			//			    chain.addComparator(new NameComparator());
//			//			    chain.addComparator(new NumberComparator()
//			
//			Collections.sort(foundIngredients,new NameComparator());
//			Collections.sort(foundAlternativeIngredients,new NameComparator());
//			Collections.sort(foundPublicRecipes,new RezeptNameComparator());
//			Collections.sort(foundUserRecipes,new RezeptNameComparator());
//		}
//		}
//	}
//	
//
//	
//	// ----------------------------- private Methods -------------------------------------------
//	
//	private void displayResults() {
//		displayIngredients();
//		
//		tableMeals.removeAllRows();
//		if(foundPublicRecipes != null){
//			for (final Recipe item : foundPublicRecipes){
//				displayRecipeItem(item,false);
//			}
//		}
//		tableMealsYours.removeAllRows();
//		if(foundUserRecipes != null){
//			for (final Recipe item : foundUserRecipes){
//				displayRecipeItem(item,true);
//			}
//		}
//	}
//	
//	
//	private void displayIngredients()
//	{
//		table.removeAllRows();
//		if(foundIngredients != null){
//			// display all noALternative Ingredients
//			for (final Ingredient item : foundIngredients){
//				if (item.noAlternative)
//					displayIngredient(item);
//			}
//		
//				
//			// display all alternative Ingredients (sorted as well)
//			// boolean textlabeladded = false;
//			for (final Ingredient item : foundAlternativeIngredients){
//				if (!item.noAlternative)
//				{
//					/* alternative dividing section
//					if (!textlabeladded)
//					{
//						int row = table.getRowCount();
//						HTML textALternatives = new HTML();
//						//textALternatives.setHTML("<div style='color:red; margin:auto; width:70px;'> Alternativen: </div>");
//						textALternatives.setHTML("alternativen:");
//						table.setWidget(row,0,textALternatives);
//						textlabeladded = true;
//					}*
//					displayIngredient(item);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * the displaying functions for ingredients
//	 *
//	private void displayIngredient(final Ingredient ingredient) {
//		int row = table.getRowCount();
//
//
//		HTML icon = new HTML();
//
//		if(ingredient.getCo2eValue() < 400){
//			icon.setStyleName("base-icons");
//			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
//			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>");
//		} else	if(ingredient.getCo2eValue() < 1200){
//			icon.setStyleName("base-icons");	
//			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
//
//		}
//
//		if(ingredient.hasSeason != null && ingredient.hasSeason){
//			SeasonDate date = new SeasonDate(presenter.getCurrentMonth(),1);
//			SeasonDate dateStart = ingredient.startSeason;		
//			SeasonDate dateStop =  ingredient.stopSeason;
//			
//			if( date.after(dateStart) && date.before(dateStop) ){
//				icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
//			} 
//		}
//
//		if(ingredient.noAlternative){
//		icon.setHTML(icon.getHTML()+"<div class='ingText'>"+ingredient.getSymbol()+"</div>");
//		
//		} else {
//			icon.setHTML(icon.getHTML()+"(alt): " +ingredient.getSymbol());
//		}
//
//		icon.setHTML(icon.getHTML()+"<div class='putRight'>ca "+Integer.toString((int) ingredient.getCo2eValue()/10).concat(" g*")+"</div>");
//
//		table.setWidget(row,0,icon);
//	}
//	
//	private void displayUserRecipeItem() {}
//	private void displayPublicRecipeItem() {}
//	
//	/**
//	 * the displaying functions for recipes
//	 *
//	private void displayRecipeItem(final Recipe recipe, boolean yours) {
//		if(yours){
//			final int row = tableMealsYours.getRowCount();
//
//
//			Button removeRezeptButton = new Button(" x ");
//			removeRezeptButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					final ConfirmDialog dlg = new ConfirmDialog("Hiermit werden Sie das...");
//					dlg.statusLabel.setText("Rezept löschen.");
//					
//					//  recheck user if he really want to do this...
//					dlg.yesButton.addClickHandler(new ClickHandler() {
//						public void onClick(ClickEvent event) {
//							dco.deleteRecipe(recipe);
//							tableMealsYours.removeCells(row, 0, tableMealsYours.getCellCount(row));
//							dlg.hide();
//							dlg.clear();
//						}
//					});
//					dlg.show();
//					dlg.center();
//
//
//				}
//			});
//			// remove button is 1
//			tableMealsYours.setWidget(row, 1, removeRezeptButton);
//
//
//			HTML item = new HTML();
//
//			if(recipe.eaternitySelected != null && recipe.eaternitySelected){
//				item.setHTML(item.getHTML()+"<img src='pixel.png' height=1 width=20 />");
//				item.setStyleName("base-icons carrot");	
//			}
//			if(recipe.regsas != null && recipe.regsas){
//				item.setHTML(item.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
//			}
//			if(recipe.bio != null && recipe.bio){
//				item.setHTML(item.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
//			}
//
//			item.setHTML(item.getHTML()+"<div class='ingText'>"+recipe.getSymbol()+"</div>");
//			// Text and CO2 is 0
//			tableMealsYours.setWidget(row,0,item);
//
//			recipe.setCO2Value();
//			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
//			item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g*</div>");
//
//
//
//			if(presenter.getLoginInfo().isAdmin()){
//				// This is ugly, but that's the way it is...
//				if(!recipe.isOpen()){
//					//					if(recipe.openRequested){
//					// this should be a link to make it open
//					Anchor openThis = new Anchor("o");
//					openThis.addClickHandler(new ClickHandler() {
//						public void onClick(ClickEvent event) {
//							dco.approveRecipe(recipe,true);
//						}
//					});
//					tableMealsYours.setWidget(row, 2,openThis);
//				} 
//				else {
//					// this should be a link to make it close
//					Anchor closeThis = new Anchor("c");
//					closeThis.addClickHandler(new ClickHandler() {
//						public void onClick(ClickEvent event) {
//							dco.approveRecipe(recipe,false);
//						}
//					});
//					tableMealsYours.setWidget(row, 2,closeThis);
//
//				}
//			} 
//			
//			//TODO should this not be called after the sort?
//			if ((row % 2) == 1) {
//				String style = evenStyleRow.evenRow();
//				tableMealsYours.getRowFormatter().addStyleName(row, style);
//			}
//
//		}
//		else{
//			final int row = tableMeals.getRowCount();
//			HTML item = new HTML();
//
//			if(recipe.eaternitySelected != null && recipe.eaternitySelected){
//				item.setHTML(item.getHTML()+"<img src='pixel.png' height=1 width=20 />");
//				item.setStyleName("base-icons carrot");	
//			}
//			if(recipe.regsas != null && recipe.regsas){
//				item.setHTML(item.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
//			}
//			if(recipe.bio != null && recipe.bio){
//				item.setHTML(item.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
//			}
//
//			item.setHTML(item.getHTML()+"<div class='ingText'>"+recipe.getSymbol()+"</div>");
//
//			// Text and CO2 is 0
//			tableMeals.setWidget(row,0,item);
//
//			recipe.setCO2Value();
//			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
//			item.setHTML(item.getHTML()+"<div class='putRight'>ca "+formatted+ " g*</div>");
//
//			if(presenter.getLoginInfo() != null && presenter.getLoginInfo().isAdmin()){
//				
//				Anchor removeRezeptButton = new Anchor(" x ");
//				removeRezeptButton.addClickHandler(new ClickHandler() {
//					public void onClick(ClickEvent event) {
//						final ConfirmDialog dlg = new ConfirmDialog("Sie wollen dieses...");
//						dlg.statusLabel.setText("Rezept löschen?");
//						// TODO recheck user if he really want to do this...
//						dlg.yesButton.addClickHandler(new ClickHandler() {
//							public void onClick(ClickEvent event) {
//								dco.deleteRecipe(recipe);
//								tableMeals.removeCells(row, 0, tableMealsYours.getCellCount(row));
//								dlg.hide();
//								dlg.clear();
//							}
//						});
//						dlg.show();
//						dlg.center();
//					}
//				});
//				
//				// remove button is 1
//				tableMeals.setWidget(row, 1, removeRezeptButton);
////				item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g* ("+removeRezeptButton+")</div>");
//
//				if(!recipe.isOpen()){
//					if(recipe.openRequested){
//						// TODO this should be a link to make it open
//						Anchor openThis = new Anchor("o");
//						openThis.addClickHandler(new ClickHandler() {
//							public void onClick(ClickEvent event) {
//								dco.approveRecipe(recipe,true);
////								 initTable();
//								// why does the layout suck after this button press?????
//							}
//						});
//						tableMeals.setWidget(row, 2,openThis);
////						item.setHTML(openThis+" "+item.getHTML());
//					}
//				} else {
//					// TODO this should be a link to make it close
//					Anchor closeThis = new Anchor("c");
//					closeThis.addClickHandler(new ClickHandler() {
//						public void onClick(ClickEvent event) {
//							dco.approveRecipe(recipe,false);
////							 initTable();
//						}
//					});
//					tableMeals.setWidget(row, 2,closeThis);
////					item.setHTML(closeThis+" "+item.getHTML());
//				}
//			}
//
//			//TODO should this not be called after the sort?
//			if ((row % 2) == 1) {
//				String style = evenStyleRow.evenRow();
//				tableMeals.getRowFormatter().addStyleName(row, style);
//			}
//		}
//
//
//	}
//
//	
//	
//	@SuppressWarnings("deprecation")
//	private void initToolTips() {
//		
//		imageCarrot.setUrl("pixel.png");
//		imageSmiley1.setUrl("pixel.png");
//		imageSmiley2.setUrl("pixel.png");
//		imageSmiley3.setUrl("pixel.png");
//		imageRegloc.setUrl("pixel.png");
//		imageBio.setUrl("pixel.png");
//		imageCarrot.setPixelSize(20, 20);
//		imageSmiley1.setPixelSize(20, 20);
//		imageSmiley2.setPixelSize(20, 20);
//		imageSmiley3.setPixelSize(20, 20);
//		imageRegloc.setPixelSize(20, 20);
//		imageBio.setPixelSize(20, 20);
//
//		imageCarrot.addMouseListener(
//				new TooltipListener(
//						"ausgezeichnet klimafreundlich", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		imageSmiley1.addMouseListener(
//				new TooltipListener(
//						"CO₂-Äq. Wert unter besten 20%", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		imageSmiley2.addMouseListener(
//				new TooltipListener(
//						"CO₂-Äq. Wert über Durchschnitt", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		imageSmiley3.addMouseListener(
//				new TooltipListener(
//						"Angaben unvollständig", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		imageRegloc.addMouseListener(
//				new TooltipListener(
//						"saisonale und regionale Ware", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		imageBio.addMouseListener(
//				new TooltipListener(
//						"biologische Zutat / Recipe", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
//		
//		
//		
//		SearchLabel.addMouseListener(
//				new TooltipListener(
//						"Suche nach Zutaten und Rezepten hier.", 5000 /* timeout in milliseconds*/,"yourcssclass",5,-34));
//		co2Order.addMouseListener(
//				new TooltipListener(
//						"Sortiere Suchergebnisse nach CO₂-Äquivalent Wert.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));
//		alphOrder.addMouseListener(
//				new TooltipListener(
//						"Sortiere Suchergebnisse alphabetisch.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));
//
//	}
//	
//	private void initTable() {
//		// this is just basic design stuff
//		table.getColumnFormatter().setWidth(0, "120px");
//
//		tableMeals.getColumnFormatter().setWidth(0, "120px");
//
//		tableMealsYours.getColumnFormatter().setWidth(0, "120px");
//		tableMealsYours.getColumnFormatter().setWidth(1, "18px");
//		
//		if(presenter.getLoginInfo() != null && presenter.getLoginInfo().isAdmin()){
//			tableMeals.getColumnFormatter().setWidth(1, "18px");
//			tableMeals.getColumnFormatter().setWidth(2, "10px");
//			
//			tableMealsYours.getColumnFormatter().setWidth(2, "10px");
//		}
//		
//	}
//
//	
//	private void selectRowMeals(final int row) {
//
//		// some code here should be in the dataobject!
//		if (foundPublicRecipes.size() < row){
//			return;
//		}
//		Recipe item = foundPublicRecipes.get(row);
//		if (item == null) {
//			return;
//		}
//
//		styleRowMeals(selectedRow, false);
//		styleRowMeals(row, true);
//
//		Timer t = new Timer() {
//			public void run() {
//				styleRowMeals(row, false);
//			}
//		};
//
//		superDisplay.cloneRecipe(item);
//		t.schedule(200);
//
//		selectedRow = row;
//		if (listenerMeals != null) {
//			listenerMeals.onItemSelected(item);
//		}
//
//	}
//
//	private void selectRowMealsYours(final int row) {
//
//		if (foundUserRecipes.size() < row){
//			return;
//		}
//		Recipe item = foundUserRecipes.get(row);
//		if (item == null) {
//			return;
//		}
//
//		styleRowMealsYours(selectedRow, false);
//		styleRowMealsYours(row, true);
//
//		Timer t = new Timer() {
//			public void run() {
//				styleRowMealsYours(row, false);
//			}
//		};
//
//		superDisplay.cloneRecipe(item);
//		t.schedule(200);
//
//		selectedRow = row;
//		if (listenerMeals != null) {
//			listenerMeals.onItemSelected(item);
//		}
//
//	}
//
//	private void selectRow(final int row) {
//		Ingredient item;
//		
//		// get the grams from the input
//		// if 2 valid numbers exist, take the first valid one
//		int grams = 0;
//		
//		searchString = SearchInput.getText().trim();
//		String[] searches = searchString.split(" ");
//		
//		for(String search : searches)
//		{
//			try {
//			    int x = Integer.parseInt(search);
//			    grams = x;
//			    break;
//			}
//			catch(NumberFormatException nFE) {
//			}
//		}
//		
//		if (foundIngredients.size() < row){
//			return;
//		}
//		
//		if (row >= 0 && row < foundIngredients.size())
//		{
//			item = foundIngredients.get(row);
//		}
//		/*
//		else if (row >= presenter.getDAO().foundIngredient.size() && row < numOfIngredientsFound)
//		{
//			item = presenter.getDAO().foundAlternativeIngredients.get(row - presenter.getDAO().foundIngredient.size());
//		}
//		*/
//		else return;
//
//
//
//		if (item == null) {
//			return;
//		}
//
//		styleRow(selectedRow, false);
//		styleRow(row, true);
//
//		//TODO uncomment this: idea is to animate some effects
//		//infoZutat.setZutat(item);
//
//		//		leftSplitPanel.setHeight("500px");
//		//		leftSplitPanel.addSouth( new InfoZutat(), 500);
//
//		//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 0, PX);
//		//		leftSplitPanel.forceLayout();
//		//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 2, EM);
//		//		leftSplitPanel.animate(500);
//		
//		//TODO uncomment this: idea is to provide a tooltip with more informations
//		
//		//infoZutat.stylePanel(true);
//		
//		// infotext über angeklickte zutat
//		// Groß:Name, Foto, Liste:Labels: Text:Beschreibung Wikipedia klein:Alternativen
//		//
//
//
//		Timer t = new Timer() {
//			public void run() {
//				styleRow(row, false);
//			}
//		};
//
//		final RecipeView rezeptView;
//		int selectedRecipe = superDisplay.getSelectedRecipeNumber();
//		if(selectedRecipe != -1){
//			rezeptView = superDisplay.getSelectedRecipeView();
//		} else {
//			rezeptView = superDisplay.createNewRecipeView();
//		}
//		dco.addIngredientToMenu(item, grams);
//		// REFACTOR: 
//		rezeptView.showRezept(rezeptView.recipe);
//		superDisplay.displayRecipeEditView(rezeptView);
//		
//		superDisplay.adjustStickyEditLayout();
//
//		t.schedule(200);
//		selectedRow = row;
//		markedRow = 0;
//
//		if (listener != null) {
//			listener.onItemSelected(item);
//		}
//	}
//	
//	private void changeMarkedRow(int row)
//	{
//		if (row >= 0 && row < foundIngredients.size())
//		{
//			styleMarkedRow(markedRow, false);
//			styleMarkedRow(row, true);
//			markedRow = row;
//		}
//		
//	}
//
//	static void styleRow(int row, boolean selected) {
//		if (row != -1) {
//			String style = selectionStyle.selectedRow();
//
//			if (selected) {
//				table.getRowFormatter().addStyleName(row, style);
//			} else {
//				table.getRowFormatter().removeStyleName(row, style);
//			}
//		}
//	}
//
//	static void styleRowMeals(int row, boolean selected) {
//		if (row != -1) {
//			String style = selectionStyle.selectedRow();
//
//			if (selected) {
//				tableMeals.getRowFormatter().addStyleName(row, style);
//			} else {
//				tableMeals.getRowFormatter().removeStyleName(row, style);
//			}
//			
//		}
//	}
//
//	static void styleRowMealsYours(int row, boolean selected) {
//		if (row != -1) {
//			String style = selectionStyle.selectedRow();
//
//			if (selected) {
//				tableMealsYours.getRowFormatter().addStyleName(row, style);
//			} else {
//				tableMealsYours.getRowFormatter().removeStyleName(row, style);
//			}
//		}
//	}
//	
//	private void styleMarkedRow(int row, boolean marked) {
//		String style = markingStyle.markedRow();
//
//		if (marked) {
//			table.getRowFormatter().addStyleName(row, style);
//		} else {
//			table.getRowFormatter().removeStyleName(row, style);
//		}
//	}
//	
//	
//	
//	
//	
//	
//	
//}
//