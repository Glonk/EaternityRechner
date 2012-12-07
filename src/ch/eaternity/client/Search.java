/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.comparators.NameComparator;
import ch.eaternity.shared.comparators.RezeptNameComparator;
import ch.eaternity.shared.comparators.RezeptValueComparator;
import ch.eaternity.shared.comparators.ValueComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox; // TODO check why we need a suggestBox!
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * A composite that displays a list of ingredients that can be selected.
 */
public class Search<T> extends ResizeComposite {
	
	// GWT UI-Binder (to display the search class)
	interface Binder extends UiBinder<Widget, Search> { }
	private static final Binder binder = GWT.create(Binder.class);

	/**
	 * Call-back when items are selected. 
	 */
	public interface Listener { // Call-back for ingredient click
		void onItemSelected(Ingredient item);
	}

	public interface ListenerMeals { // Call-back for menu click
		void onItemSelected(Recipe item);
	}

	private Listener listener;
	private ListenerMeals listenerMeals;
	
	static int markedRow = 0;
	interface MarkingStyle extends CssResource {
		String markedRow();
	}
	
	// CSS of selected row
	static int selectedRow = 0;
	//TODO check why everything crashes for selectedRow = -1
	interface SelectionStyle extends CssResource {
		String selectedRow();
	}
	
	// Color the rows alternating
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}


	/**
	 * User interface elements
	 */
	
	// Legend and informations:
	@UiField HTMLPanel panelSouth;
	@UiField HTMLPanel legendPanel;
	
	@UiField Anchor legendAnchor;
	@UiField Anchor legendAnchorClose;
	
	@UiField Image imageCarrot;
	@UiField Image imageSmiley1;
	@UiField Image imageSmiley2;
	@UiField Image imageSmiley3;
	@UiField Image imageRegloc;
	@UiField Image imageBio;
	
	
	// Search Panel (Box and Button)
	@UiField DockLayoutPanel SearchBox;
	@UiField HTML SearchLabel;
	@UiField
	public
	static HTML yourRecipesText;
	@UiField
	public
	static SuggestBox SearchInput;
	
	
	// Display Results in:
	@UiField DockLayoutPanel displayResultsPanel;
	static
	@UiField HTMLPanel yourMealsPanel;
	@UiField SplitLayoutPanel mealsSplitPanels;
	@UiField SplitLayoutPanel subMealsSplitPanels;
	@UiField HTMLPanel scrollAbleHtml;

	
	// Search results Tables
	@UiField
	static FlexTable table;
	@UiField
	static FlexTable tableMeals;
	@UiField
	static FlexTable tableMealsYours;


	// CSS reference for the alternating row coloring
	@UiField
	static MarkingStyle markingStyle;
	@UiField
	static SelectionStyle selectionStyle;
	@UiField
	static EvenStyleRow evenStyleRow;
	
	
	// sorting of the tables:
	@UiField Anchor co2Order;
	@UiField Anchor alphOrder;
	
	/**
	 * User sorting handlers
	 */
	
	// choose this sorting method
	static int sortMethod = 1;
	
	
	//---------------- UI HANDLERS ----------------
	
	@UiHandler("co2Order")
	void onCo2Clicked(ClickEvent event) {
		sortMethod = 1;
		sortResults();
	}

	@UiHandler("alphOrder")
	void onAlphClicked(ClickEvent event) {
		sortMethod = 5;
		sortResults();
	}
	
	// Handle search input
	private int numKeyPressed;
	@UiHandler("SearchInput")
	public void onKeyUp(KeyUpEvent event) {
		// this matches up to 2 words!
		numKeyPressed++;
		// only update on text change
		if (numKeyPressed % 2 == 0)
		{
			if( !SearchInput.getText().trim().equals(searchString)){
				searchString = SearchInput.getText().trim();
				updateResults(searchString);
			}
		}
	}

	
	// Handle Enter Key to add new ingredient
	//ugly workaround for catching double firing of events from suggestbox (http://code.google.com/p/google-web-toolkit/issues/detail?id=3533)
	private int numEnterKeyPressed;
	private int numDownKeyPressed;
	private int numUpKeyPressed;
	
	@UiHandler("SearchInput")
	public void onKeyDown(KeyDownEvent event) {
		if(KeyCodes.KEY_ENTER == event.getNativeKeyCode())
		{
			numEnterKeyPressed++;
			if (numEnterKeyPressed % 2 == 0)
			{
				selectRow(markedRow);
				SearchInput.setText("");
				updateResults("");
			}
		}
		if(KeyCodes.KEY_DOWN == event.getNativeKeyCode())
		{
			numDownKeyPressed++;
			if (numDownKeyPressed % 2 == 0)
				changeMarkedRow(markedRow + 1);
		}
		if(KeyCodes.KEY_UP == event.getNativeKeyCode())
		{
			numUpKeyPressed++;
			if (numUpKeyPressed % 2 == 0)
				changeMarkedRow(markedRow - 1);
		}
	}
	
	/* ALternative Way: select the textbox behind, doesn't worked
	SearchInput.getTextBox().addKeyPressHandler(new KeyPressHandler() {      
		public void onKeyPress(KeyPressEvent event) {
			if (KeyCodes.KEY_ENTER == event.getCharCode()) //getNativeKeyCode())
				selectRow(markedRow);
	    }
	});
	*/

	
	@UiHandler("legendAnchor")
	public void onLegendClick(ClickEvent event) {

		legendPanel.setStyleName("legend1");
		displayResultsPanel.setWidgetSize(panelSouth, 220);
		displayResultsPanel.forceLayout();

	}

	@UiHandler("legendAnchorClose")
	public void onLegendCloseClick(ClickEvent event) {

		legendPanel.setStyleName("legend2");
		displayResultsPanel.setWidgetSize(panelSouth, 80);
		displayResultsPanel.forceLayout();

	}

	@UiHandler("table")
	void onTableClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = table.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			selectRow(row);
		}
	}

	@UiHandler("tableMeals")
	void onTableMealsClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = tableMeals.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			selectRowMeals(row);
		}
	}

	@UiHandler("tableMealsYours")
	void onTableMealsYoursClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = tableMealsYours.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			selectRowMealsYours(row);
		}
	}


	
	
	/**
	 * Important Variables
	 */

	// here is the database of all data pushed to....
	public Data clientData = new Data();
	
	public  List<Recipe> selectedKitchenRecipes = new ArrayList<Recipe>();
	
	private ArrayList<Recipe> FoundRezepte = new ArrayList<Recipe>();
	private ArrayList<Recipe> FoundRezepteYours = new ArrayList<Recipe>();
	private ArrayList<Ingredient> FoundIngredient = new ArrayList<Ingredient>();
	private ArrayList<Ingredient> FoundAlternativeIngredients = new ArrayList<Ingredient>();
	
	// re-check this list
	private ArrayList<Recipe> FoundRezepteHasDesc = new ArrayList<Recipe>();
	private ArrayList<Recipe> FoundRezepteYoursHasDesc = new ArrayList<Recipe>();

	public String searchString = "";

	private Presenter<T> presenter;
	public void setPresenter(Presenter<T> presenter){
		this.presenter = presenter;
		initTable(); // just the size
	}
	private EaternityRechnerView superDisplay;
	public void setSuperDisplay(EaternityRechnerView superDisplay){
		this.superDisplay = superDisplay;
	}

	public Search() {
		
		// bind and display the Search
		initWidget(binder.createAndBindUi(this));

		// we have to wait till the database is loaded:
		SearchInput.setText("wird geladen...");
	
		SearchInput.setFocus(true);
		

		subMealsSplitPanels.addStyleName("noSplitter");
		setVDraggerHeight("0px");

		initToolTips();

	}

	// Select the KitchenRecipes
	// Always call updateResults after for propper loading!
	public void updateKitchenRecipesForSearch(Long id) {
		selectedKitchenRecipes.clear();
		for(Recipe recipe : clientData.KitchenRecipes){
			for(Long kitchenId : recipe.kitchenIds){
				if(kitchenId.equals(id))
				{
					selectedKitchenRecipes.add(recipe);
				}
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private void initToolTips() {
		
		imageCarrot.setUrl("pixel.png");
		imageSmiley1.setUrl("pixel.png");
		imageSmiley2.setUrl("pixel.png");
		imageSmiley3.setUrl("pixel.png");
		imageRegloc.setUrl("pixel.png");
		imageBio.setUrl("pixel.png");
		imageCarrot.setPixelSize(20, 20);
		imageSmiley1.setPixelSize(20, 20);
		imageSmiley2.setPixelSize(20, 20);
		imageSmiley3.setPixelSize(20, 20);
		imageRegloc.setPixelSize(20, 20);
		imageBio.setPixelSize(20, 20);

		imageCarrot.addMouseListener(
				new TooltipListener(
						"ausgezeichnet klimafreundlich", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		imageSmiley1.addMouseListener(
				new TooltipListener(
						"CO₂-Äq. Wert unter besten 20%", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		imageSmiley2.addMouseListener(
				new TooltipListener(
						"CO₂-Äq. Wert über Durchschnitt", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		imageSmiley3.addMouseListener(
				new TooltipListener(
						"Angaben unvollständig", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		imageRegloc.addMouseListener(
				new TooltipListener(
						"saisonale und regionale Ware", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		imageBio.addMouseListener(
				new TooltipListener(
						"biologische Zutat / Recipe", 5000 /* timeout in milliseconds*/,"yourcssclass",-6,-42));
		
		
		
		SearchLabel.addMouseListener(
				new TooltipListener(
						"Suche nach Zutaten und Rezepten hier.", 5000 /* timeout in milliseconds*/,"yourcssclass",5,-34));
		co2Order.addMouseListener(
				new TooltipListener(
						"Sortiere Suchergebnisse nach CO₂-Äquivalent Wert.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));
		alphOrder.addMouseListener(
				new TooltipListener(
						"Sortiere Suchergebnisse alphabetisch.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));

	}

	
	// what is this thing for?
	public void setVDraggerHeight (String height)
	{
		//	  SplitLayoutPanel p = (SplitLayoutPanel) this.getWidget ();
		SplitLayoutPanel p = 	subMealsSplitPanels;
		int widgetCount = p.getWidgetCount ();
		for (int i = 0; i < widgetCount; i++) {
			Widget w = p.getWidget (i);
			if (w.getStyleName ().equals ("gwt-SplitLayoutPanel-VDragger")) {
				w.setHeight (height);
			}
		}
	}

	
	private void initTable() {
		// this is just basic design stuff
		table.getColumnFormatter().setWidth(0, "120px");

		tableMeals.getColumnFormatter().setWidth(0, "120px");

		tableMealsYours.getColumnFormatter().setWidth(0, "120px");
		tableMealsYours.getColumnFormatter().setWidth(1, "18px");
		
		if(presenter.getLoginInfo() != null && presenter.getLoginInfo().isAdmin()){
			tableMeals.getColumnFormatter().setWidth(1, "18px");
			tableMeals.getColumnFormatter().setWidth(2, "10px");
			
			tableMealsYours.getColumnFormatter().setWidth(2, "10px");
		}
		
	}

	
	private void selectRowMeals(final int row) {

		if (FoundRezepte.size() < row){
			return;
		}
		Recipe item = FoundRezepte.get(row);
		if (item == null) {
			return;
		}

		styleRowMeals(selectedRow, false);
		styleRowMeals(row, true);

		Timer t = new Timer() {
			public void run() {
				styleRowMeals(row, false);
			}
		};

		superDisplay.cloneRecipe(item);
		t.schedule(200);

		selectedRow = row;
		if (listenerMeals != null) {
			listenerMeals.onItemSelected(item);
		}

	}

	private void selectRowMealsYours(final int row) {

		if (FoundRezepteYours.size() < row){
			return;
		}
		Recipe item = FoundRezepteYours.get(row);
		if (item == null) {
			return;
		}

		styleRowMealsYours(selectedRow, false);
		styleRowMealsYours(row, true);

		Timer t = new Timer() {
			public void run() {
				styleRowMealsYours(row, false);
			}
		};

		superDisplay.cloneRecipe(item);
		t.schedule(200);

		selectedRow = row;
		if (listenerMeals != null) {
			listenerMeals.onItemSelected(item);
		}

	}

	private void selectRow(final int row) {

		// get the grams from the input
		// if 2 valid numbers exist, take the first valid one
		int grams = 0;
		
		searchString = SearchInput.getText().trim();
		String[] searches = searchString.split(" ");
		
		for(String search : searches)
		{
			try {
			    int x = Integer.parseInt(search);
			    grams = x;
			    break;
			}
			catch(NumberFormatException nFE) {
			}
		}
		
		if (FoundIngredient.size() + FoundAlternativeIngredients.size() < row){
			return;
		}
		
		Ingredient item;
		if (row >= 0 && row < FoundIngredient.size())
		{
			item = FoundIngredient.get(row);
		}
		else if (row >= FoundIngredient.size() && row < FoundIngredient.size() + FoundAlternativeIngredients.size())
		{
			item = FoundAlternativeIngredients.get(row - FoundIngredient.size());
		}
		else return;



		if (item == null) {
			return;
		}

		styleRow(selectedRow, false);
		styleRow(row, true);

		//TODO uncomment this: idea is to animate some effects
		//infoZutat.setZutat(item);

		//		leftSplitPanel.setHeight("500px");
		//		leftSplitPanel.addSouth( new InfoZutat(), 500);

		//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 0, PX);
		//		leftSplitPanel.forceLayout();
		//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 2, EM);
		//		leftSplitPanel.animate(500);
		
		//TODO uncomment this: idea is to provide a tooltip with more informations
		
		//infoZutat.stylePanel(true);
		
		// infotext über angeklickte zutat
		// Groß:Name, Foto, Liste:Labels: Text:Beschreibung Wikipedia klein:Alternativen
		//


		Timer t = new Timer() {
			public void run() {
				styleRow(row, false);
			}
		};

		final RecipeView rezeptView;
		int selectedRecipe = superDisplay.getSelectedRecipeNumber();
		if(selectedRecipe != -1){
			rezeptView = superDisplay.getSelectedRecipeView();
		} else {
			rezeptView = superDisplay.createNewRecipeView();
		}
		superDisplay.addOneIngredientToMenu(item,rezeptView, grams);
		rezeptView.showRezept(rezeptView.recipe);
		superDisplay.displayRecipeEditView(rezeptView);
		
		superDisplay.adjustStickyEditLayout();

		t.schedule(200);

		selectedRow = row;

		if (listener != null) {
			listener.onItemSelected(item);
		}
	}
	
	private void changeMarkedRow(int row)
	{
		if (row >= 0 && row < FoundIngredient.size() + FoundAlternativeIngredients.size())
		{
			styleMarkedRow(markedRow, false);
			styleMarkedRow(row, true);
			markedRow = row;
		}
		
	}

	static void styleRow(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRow();

			if (selected) {
				table.getRowFormatter().addStyleName(row, style);
			} else {
				table.getRowFormatter().removeStyleName(row, style);
			}
		}
	}

	static void styleRowMeals(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRow();

			if (selected) {
				tableMeals.getRowFormatter().addStyleName(row, style);
			} else {
				tableMeals.getRowFormatter().removeStyleName(row, style);
			}
			
		}
	}

	static void styleRowMealsYours(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRow();

			if (selected) {
				tableMealsYours.getRowFormatter().addStyleName(row, style);
			} else {
				tableMealsYours.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	
	private void styleMarkedRow(int row, boolean marked) {
		String style = markingStyle.markedRow();

		if (marked) {
			table.getRowFormatter().addStyleName(row, style);
		} else {
			table.getRowFormatter().removeStyleName(row, style);
		}
	}
	
	
	


	
	
	/**
	 * The search algorithm
	 */
	
	// TODO this is getting called twice all the time...
	
	public void updateResults(String searchString) {
		table.removeAllRows();
		tableMeals.removeAllRows();
		tableMealsYours.removeAllRows();

		FoundIngredient.clear();
		FoundAlternativeIngredients.clear();
		FoundRezepte.clear();
		FoundRezepteYours.clear();
		
		FoundRezepteHasDesc.clear();
		FoundRezepteYoursHasDesc.clear();

		
		if(	getYourRecipes() != null && getYourRecipes().size() != 0){
			// then we have at least one recipe...
			yourMealsPanel.setVisible(true);
		} else {
			yourMealsPanel.setVisible(false);
		}

		if ((clientData.getIngredients() != null) ){

			// Zutaten
			// when the search string has a length 
			if(searchString.trim().length() != 0){

				String[] searches = searchString.split(" ");

				// consider strings with whitespaces, ssek for each word individually
				for(String search : searches)
				{
					// Zutaten
					// TODO this search algorithm is extremely slow, make faster
					for(Ingredient zutat : clientData.getIngredients()){
						if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
							//if(,search) < 3){
							//Window.alert(zutat.getSymbol().substring(0, search.trim().length()));
							if(!FoundIngredient.contains(zutat)){
								zutat.noAlternative = true;
								FoundIngredient.add(zutat);
//								displayIngredient(zutat);
							}
						}
					}
					// only look for alternatives, if there is only 1 result
					// TODO mark the alternatives as Special!
					if(FoundIngredient.size() == 1){
						for(Ingredient zutat :FoundIngredient){
							if(zutat.getAlternatives() != null){
								for(Long alternativen_id : zutat.getAlternatives()){
									for(Ingredient zutat2 : clientData.getIngredients()){
										if(zutat2.getId().equals(alternativen_id)){
											if(!FoundAlternativeIngredients.contains(zutat2)){
												zutat2.noAlternative = false;
												FoundAlternativeIngredients.add(zutat2);
//												displayIngredient(zutat2);
											}
										}
									}
								}
							}
							break;
						}
					}
				}
				// Rezepte
				if(	getYourRecipes() != null){
					searchRezept(searchString, getYourRecipes(), searches,true);
				}

				if(	clientData.getPublicRezepte() != null){
					searchRezept(searchString, clientData.getPublicRezepte(), searches,false);
				}
			} 
			// the search string was empty (so just display everything!)
			// TODO yet a little slow...
			else {
				for(Ingredient zutat : clientData.getIngredients()){
//					if(!FoundIngredient.contains(zutat)){ // not necessary, as we are getting anyway all of them (no alternatives...)
						FoundIngredient.add(zutat);
						zutat.noAlternative = true;
//						displayIngredient(zutat);
//					}
				}

				if(	getYourRecipes() != null && getYourRecipes().size() != 0){
					yourMealsPanel.setVisible(true);
					for(Recipe recipe : getYourRecipes()){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							if(!FoundRezepteHasDesc.contains(recipe) && !FoundRezepteYoursHasDesc.contains(recipe)){
								
								if(recipe.getDirectDescandentID() != null){
									FoundRezepteYoursHasDesc.add(recipe);
								} else {
									FoundRezepteYours.add(recipe);
								}
	//							displayRecipeItemCheck(recipe,true);
							}
						}
					}
				} else {
					yourMealsPanel.setVisible(false);
				}

				if(	clientData.getPublicRezepte() != null){
					for(Recipe recipe : clientData.getPublicRezepte()){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							if(!FoundRezepteHasDesc.contains(recipe) && !FoundRezepteYoursHasDesc.contains(recipe)){
								if(recipe.getDirectDescandentID() != null){
									FoundRezepteHasDesc.add(recipe);
								} else {
									FoundRezepte.add(recipe);
								}
	//							displayRecipeItemCheck(recipe,false);
							}
						}
					}
				}

			}
			// all found items are now displayed
			
			// display recipes if there is no descendant of them in the list
			displayUnDescendantedRecipes(FoundRezepteHasDesc,FoundRezepte);
			displayUnDescendantedRecipes(FoundRezepteYoursHasDesc,FoundRezepteYours);

			// sort and display results
			sortResults();
			
			// mark top entry
			changeMarkedRow(0);
			
			
		}	
	}



	private List<Recipe> getYourRecipes() {
		// TODO Auto-generated method stub
		if(presenter.getTopPanel().isNotInKitchen){
			return clientData.getYourRezepte();
		} else {
			// this should only return the selected Kitchen ones
			return selectedKitchenRecipes;
		}
		
	}

	private void searchRezept(String searchString,
			List<Recipe> allRezepte, String[] searches, boolean yours) {
		if(allRezepte != null){
			for(Recipe recipe : allRezepte){
				if(recipe != null){
					if( getLevenshteinDistance(recipe.getSymbol(),searchString) < 5){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							if(!FoundRezepteHasDesc.contains(recipe) && !FoundRezepteYoursHasDesc.contains(recipe)){
								// Recipe zu Rezeptsuche
								if(yours){
									if(recipe.getDirectDescandentID() != null){
										FoundRezepteYoursHasDesc.add(recipe);
									} else {
										FoundRezepteYours.add(recipe);
									}
								} else {
									if(recipe.getDirectDescandentID() != null){
										FoundRezepteHasDesc.add(recipe);
									} else {
										FoundRezepte.add(recipe);
									}
								}
	//							displayRecipeItem(recipe,yours);
							}
						}
					}

					List<IngredientSpecification> zutatenRezept = recipe.getZutaten();
					if(zutatenRezept != null){
						int i = 0;
						for(IngredientSpecification ZutatImRezept : zutatenRezept ){
							if(ZutatImRezept != null){

								for(String search2 : searches){
									if( search2.trim().length() <= ZutatImRezept.getName().length() &&  ZutatImRezept.getName().substring(0, search2.trim().length()).compareToIgnoreCase(search2) == 0){
										//if (getLevenshteinDistance(ZutatImRezept.getName(),search2) < 2){
										i++;
									}
								}
								if(i == searches.length){
									if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
										if(!FoundRezepteHasDesc.contains(recipe) && !FoundRezepteYoursHasDesc.contains(recipe)){
											if(yours){
												if(recipe.getDirectDescandentID() != null){
													FoundRezepteYoursHasDesc.add(recipe);
												} else {
													FoundRezepteYours.add(recipe);
												}
											} else {
												if(recipe.getDirectDescandentID() != null){
													FoundRezepteHasDesc.add(recipe);
												} else {
													FoundRezepte.add(recipe);
												}
											}
	//										displayRecipeItem(recipe,yours);
										}
									}
								}
							}

						}
					}
				}
			}
		}
	}

	/**
	 * The filtering function
	 */
	private static void displayUnDescendantedRecipes(List<Recipe> possibleRecipes, List<Recipe> alreadyFound) {
		
		// check if descendant is also in the own list
		Iterator<Recipe> iterator = possibleRecipes.iterator();
		while(iterator.hasNext()){
			Recipe recipeHasDesc = iterator.next();
			for( Recipe recipeIsPossibleDesc :possibleRecipes){
				// is the descendant in the own list
				if(recipeHasDesc.getDirectDescandentID().contains(recipeIsPossibleDesc.getId())){
					// remove recipeHasDesc
					iterator.remove();
					break;
//					possibleRecipes.remove(recipeHasDesc);
				}
			}
		}
	
		
		// check if descendant is also in the public list
		Iterator<Recipe> iteratorAgain = possibleRecipes.iterator();
		while(iteratorAgain.hasNext()){
			Recipe recipeHasDescAgain = iteratorAgain.next();
			for( Recipe recipeIsPossibleDesc :alreadyFound){
				// is the descendant in the own list
				if(recipeHasDescAgain.getDirectDescandentID().contains(recipeIsPossibleDesc.getId())){
					// remove recipeHasDesc
//					possibleRecipes.remove(recipeHasDescAgain);
					iteratorAgain.remove();
					break;
				}
			}
		}
		
		
		//if not display itself
		for(Recipe addRecipe : possibleRecipes){
			alreadyFound.add(addRecipe);
		}

		// (also mark ancestors as old... by displaying the descendant)
		//TODO ( also display the old versions, but only in RecipeView)

	}
	
	/**
	 * The sorting functions
	 */

	private void sortResults() {
		switch(sortMethod){
		case 1:{
			//"co2-value"
			Collections.sort(FoundIngredient,new ValueComparator());
			displayIngredients();
			
			Collections.sort(FoundAlternativeIngredients,new ValueComparator());
			displayIngredients();	

			Collections.sort(FoundRezepte,new RezeptValueComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Recipe item : FoundRezepte){
					displayRecipeItem(item,false);
				}
			}

			Collections.sort(FoundRezepteYours,new RezeptValueComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Recipe item : FoundRezepteYours){
					displayRecipeItem(item,true);
				}
			}

			break;
		}
		case 2:{
			// "popularity"

		}
		case 3:{
			//"saisonal"

		}
		case 4:{
			//"kategorisch"
			// vegetarisch
			// vegan
			// etc.
		}
		case 5:{
			//"alphabetisch"
			
			// could there be a better method to do this? like that:
			//			   ComparatorChain chain = new ComparatorChain();
			//			    chain.addComparator(new NameComparator());
			//			    chain.addComparator(new NumberComparator()
			
			Collections.sort(FoundIngredient,new NameComparator());
			displayIngredients();
			
			Collections.sort(FoundAlternativeIngredients,new NameComparator());
			displayIngredients();

			Collections.sort(FoundRezepte,new RezeptNameComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Recipe item : FoundRezepte){
					displayRecipeItem(item,false);
				}
			}

			Collections.sort(FoundRezepteYours,new RezeptNameComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Recipe item : FoundRezepteYours){
					displayRecipeItem(item,true);
				}
			}
			break;
		}
		}
	}
	
	private void displayIngredients()
	{
		table.removeAllRows();
		if(FoundIngredient != null){
			// display all noALternative Ingredients
			for (final Ingredient item : FoundIngredient){
				if (item.noAlternative)
					displayIngredient(item);
			}
		
				
			// display all alternative Ingredients (sorted as well)
			boolean textlabeladded = false;
			for (final Ingredient item : FoundAlternativeIngredients){
				if (!item.noAlternative)
				{
					/* alternative dividing section
					if (!textlabeladded)
					{
						int row = table.getRowCount();
						HTML textALternatives = new HTML();
						//textALternatives.setHTML("<div style='color:red; margin:auto; width:70px;'> Alternativen: </div>");
						textALternatives.setHTML("alternativen:");
						table.setWidget(row,0,textALternatives);
						textlabeladded = true;
					}*/
					
					displayIngredient(item);
				}
			}
		}
	}
	


	/**
	 * the displaying functions for recipes
	 */
	
	public void displayRecipeItem(final Recipe recipe, boolean yours) {
		if(yours){
			final int row = tableMealsYours.getRowCount();


			Button removeRezeptButton = new Button(" x ");
			removeRezeptButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					final ConfirmDialog dlg = new ConfirmDialog("Hiermit werden Sie das...");
					dlg.statusLabel.setText("Rezept löschen.");
					
					//  recheck user if he really want to do this...
					dlg.yesButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							presenter.removeRecipe(recipe);
							tableMealsYours.removeCells(row, 0, tableMealsYours.getCellCount(row));
							dlg.hide();
							dlg.clear();
						}
					});
					dlg.show();
					dlg.center();


				}
			});
			// remove button is 1
			tableMealsYours.setWidget(row, 1, removeRezeptButton);


			HTML item = new HTML();

			if(recipe.eaternitySelected != null && recipe.eaternitySelected){
				item.setHTML(item.getHTML()+"<img src='pixel.png' height=1 width=20 />");
				item.setStyleName("base-icons carrot");	
			}
			if(recipe.regsas != null && recipe.regsas){
				item.setHTML(item.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
			}
			if(recipe.bio != null && recipe.bio){
				item.setHTML(item.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
			}

			item.setHTML(item.getHTML()+"<div class='ingText'>"+recipe.getSymbol()+"</div>");
			// Text and CO2 is 0
			tableMealsYours.setWidget(row,0,item);

			recipe.setCO2Value();
			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
			item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g*</div>");



			if(presenter.getLoginInfo().isAdmin()){
				// This is ugly, but that's the way it is...
				if(!recipe.isOpen()){
					//					if(recipe.openRequested){
					// this should be a link to make it open
					Anchor openThis = new Anchor("o");
					openThis.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							presenter.recipeApproval(recipe,true);
						}
					});
					tableMealsYours.setWidget(row, 2,openThis);
//					item.setHTML(openThis+" "+item.getHTML());
					//					}
				} else {
					// this should be a link to make it close
					Anchor closeThis = new Anchor("c");
					closeThis.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							presenter.recipeApproval(recipe,false);
						}
					});
					tableMealsYours.setWidget(row, 2,closeThis);
//					item.setHTML(closeThis+" "+item.getHTML());
				}
			} else {

//				 how to show, that this recipe is public??
				
//				if(recipe.isOpen()){
//					tableMealsYours.setText(row, 2,"o");
//				} else if(recipe.openRequested){
//					tableMealsYours.setText(row, 2,"c");
//				}
				
				//TODO better show in the menu itself

			}
			
			//TODO should this not be called after the sort?
			if ((row % 2) == 1) {
				String style = evenStyleRow.evenRow();
				tableMealsYours.getRowFormatter().addStyleName(row, style);
			}

		}else{
			final int row = tableMeals.getRowCount();
			HTML item = new HTML();

			if(recipe.eaternitySelected != null && recipe.eaternitySelected){
				item.setHTML(item.getHTML()+"<img src='pixel.png' height=1 width=20 />");
				item.setStyleName("base-icons carrot");	
			}
			if(recipe.regsas != null && recipe.regsas){
				item.setHTML(item.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
			}
			if(recipe.bio != null && recipe.bio){
				item.setHTML(item.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
			}

			item.setHTML(item.getHTML()+"<div class='ingText'>"+recipe.getSymbol()+"</div>");

			// Text and CO2 is 0
			tableMeals.setWidget(row,0,item);

			recipe.setCO2Value();
			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
			item.setHTML(item.getHTML()+"<div class='putRight'>ca "+formatted+ " g*</div>");

			if(presenter.getLoginInfo() != null && presenter.getLoginInfo().isAdmin()){
				
				Anchor removeRezeptButton = new Anchor(" x ");
				removeRezeptButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						final ConfirmDialog dlg = new ConfirmDialog("Sie wollen dieses...");
						dlg.statusLabel.setText("Rezept löschen?");
						// TODO recheck user if he really want to do this...
						dlg.yesButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								presenter.removeRecipe(recipe);
								tableMeals.removeCells(row, 0, tableMealsYours.getCellCount(row));
								dlg.hide();
								dlg.clear();
							}
						});
						dlg.show();
						dlg.center();
					}
				});
				
				// remove button is 1
				tableMeals.setWidget(row, 1, removeRezeptButton);
//				item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g* ("+removeRezeptButton+")</div>");

				if(!recipe.isOpen()){
					if(recipe.openRequested){
						// TODO this should be a link to make it open
						Anchor openThis = new Anchor("o");
						openThis.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								presenter.recipeApproval(recipe,true);
//								 initTable();
								// why does the layout suck after this button press?????
							}
						});
						tableMeals.setWidget(row, 2,openThis);
//						item.setHTML(openThis+" "+item.getHTML());
					}
				} else {
					// TODO this should be a link to make it close
					Anchor closeThis = new Anchor("c");
					closeThis.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							presenter.recipeApproval(recipe,false);
//							 initTable();
						}
					});
					tableMeals.setWidget(row, 2,closeThis);
//					item.setHTML(closeThis+" "+item.getHTML());
				}
			}

			//TODO should this not be called after the sort?
			if ((row % 2) == 1) {
				String style = evenStyleRow.evenRow();
				tableMeals.getRowFormatter().addStyleName(row, style);
			}
		}


	}


	/**
	 * the displaying functions for ingredients
	 */
	
	
	
	public void displayIngredient(final Ingredient ingredient) {
		int row = table.getRowCount();

		/*if ((row % 2) == 1) {
			String style = evenStyleRow.evenRow();
			table.getRowFormatter().addStyleName(row, style);
		}*/

		HTML icon = new HTML();

		//		icon.addMouseListener(
		//			    new TooltipListener(
		//			      "add", 15000 /* timeout in milliseconds*/,"bla",400,10));

		// these value (400,1200) are based on the 0.2 and 0.5 quantile of all ingredients
		if(ingredient.getCo2eValue() < 400){
//			icon.setHTML(icon.getHTML()+"<img src='pixel.png' height=1 width=20 />");
			icon.setStyleName("base-icons");
			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>");
		} else	if(ingredient.getCo2eValue() < 1200){
//			icon.setHTML(icon.getHTML()+"<img src='pixel.png' height=1 width=20 />");
			icon.setStyleName("base-icons");	
			icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");

		}

		if(ingredient.hasSeason != null && ingredient.hasSeason){
//			Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
//			presenter
			Date date = null;
			if (presenter != null) {
				date = DateTimeFormat.getFormat("MM").parse(Integer.toString(presenter.getSelectedMonth()));
			}
			
			// In Tagen
			//		String test = InfoZutat.zutat.getStartSeason();
			Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( ingredient.stdExtraction.startSeason);		
			Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( ingredient.stdExtraction.stopSeason );

			if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
					dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
				icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
			} 
		}

		if(ingredient.noAlternative){
		icon.setHTML(icon.getHTML()+"<div class='ingText'>"+ingredient.getSymbol()+"</div>");
		
		} else {
			icon.setHTML(icon.getHTML()+"(alt): " +ingredient.getSymbol());
		}

		icon.setHTML(icon.getHTML()+"<div class='putRight'>ca "+Integer.toString((int) ingredient.getCo2eValue()/10).concat(" g*")+"</div>");

		table.setWidget(row,0,icon);


	}



	public void setFoundRezepte(ArrayList<Recipe> foundRezepte) {
		FoundRezepte = foundRezepte;
	}

	public ArrayList<Recipe> getFoundRezepte() {
		return FoundRezepte;
	}

	
	/**
	 * This function may proof to be useful for a more fuzzy matching!
	 * This is used for a matching in the recipes names
	 */

	private static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
       The difference between this impl. and the previous is that, rather 
       than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
       we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
       is the 'current working' distance array that maintains the newest distance cost
       counts as we iterate through the characters of String s.  Each time we increment
       the index of String t we are comparing, d is copied to p, the second int[].  Doing so
       allows us to retain the previous cost counts as required by the algorithm (taking 
       the minimum of the cost count to the left, up one, and diagonally up and to the left
       of the current cost count being calculated).  (Note that the arrays aren't really 
       copied anymore, just switched...this is clearly much better than cloning an array 
       or doing a System.arraycopy() each time  through the outer loop.)

       Effectively, the difference between the two implementations is this one does not 
       cause an out of memory condition when calculating the LD over two very large strings.
		 */

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}

		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}

	public void setFoundIngredient(ArrayList<Ingredient> foundIngredient) {
		FoundIngredient = foundIngredient;
	}

	public ArrayList<Ingredient> getFoundIngredient() {
		return FoundIngredient;
	}
	
	
}

