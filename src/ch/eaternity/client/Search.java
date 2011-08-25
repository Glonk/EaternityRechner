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
import java.util.List;

import ch.eaternity.client.comparators.NameComparator;
import ch.eaternity.client.comparators.RezeptNameComparator;
import ch.eaternity.client.comparators.RezeptValueComparator;
import ch.eaternity.client.comparators.ValueComparator;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
public class Search extends ResizeComposite {
	
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
	
	
	/**
	 * Ui-Handlers
	 * 
	 * those who handle the interactions
	 */
	
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
	

	@UiHandler("SearchInput")
	public void onKeyUp(KeyUpEvent event) {
		// this matches up to 2 words!
		
		// only update on text change
		if( !SearchInput.getText().trim().equals(searchString)){
			searchString = SearchInput.getText().trim();
			updateResults(searchString);
		}
	}

	
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
	public static Data clientData = new Data();
	private static ArrayList<Recipe> FoundRezepte = new ArrayList<Recipe>();
	private static ArrayList<Recipe> FoundRezepteYours = new ArrayList<Recipe>();
	private static ArrayList<Ingredient> FoundIngredient = new ArrayList<Ingredient>();

	public static String searchString = "";



	public Search() {

		// bind and display the Search
		initWidget(binder.createAndBindUi(this));

		// we have to wait till the database is loaded:
		SearchInput.setText("wird geladen...");
		initTable();
		SearchInput.setFocus(true);
		yourMealsPanel.setVisible(false);
		
		subMealsSplitPanels.addStyleName("noSplitter");
		setVDraggerHeight("0px");

		initToolTips();

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



	/**
	 * Sets the listener that will be notified when an item is selected.
	 */
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void setMealListener(ListenerMeals listener) {
		this.listenerMeals = listener;
	}


	
	private static void initTable() {
		// this is just basic design stuff
		table.getColumnFormatter().setWidth(0, "120px");

		tableMeals.getColumnFormatter().setWidth(0, "120px");

		tableMealsYours.getColumnFormatter().setWidth(0, "120px");
				tableMealsYours.getColumnFormatter().setWidth(1, "18px");
//				tableMealsYours.getColumnFormatter().setWidth(2, "10px");
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

		EaternityRechner.ShowRezept(item);
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

		EaternityRechner.ShowRezept(item);
		t.schedule(200);

		selectedRow = row;
		if (listenerMeals != null) {
			listenerMeals.onItemSelected(item);
		}

	}

	private void selectRow(final int row) {

		if (FoundIngredient.size() < row){
			return;
		}

		Ingredient item = FoundIngredient.get(row);


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

		EaternityRechner.AddZutatZumMenu(item);

		t.schedule(200);

		selectedRow = row;

		if (listener != null) {
			listener.onItemSelected(item);
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

	
	
	/**
	 * The search algorithm
	 */
	
	// TODO this is getting called twice all the time...
	
	static void updateResults(String searchString) {
		table.removeAllRows();
		tableMeals.removeAllRows();
		tableMealsYours.removeAllRows();

		FoundIngredient.clear();
		FoundRezepte.clear();
		FoundRezepteYours.clear();

		if(	getYourRecipes() != null && getYourRecipes().size() != 0){
			yourMealsPanel.setVisible(true);

		} else {
			yourMealsPanel.setVisible(false);
		}

		if ((clientData.getIngredients() != null) ){

			// Zutaten
			
			// when the search string has a length 
			if(searchString.trim().length() != 0){

				String[] searches = searchString.split(" ");

				for(String search : searches){

					// Zutaten
					for(Ingredient zutat : clientData.getIngredients()){
						if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
							//if(,search) < 3){
							//Window.alert(zutat.getSymbol().substring(0, search.trim().length()));
							if(!FoundIngredient.contains(zutat)){
								zutat.noAlternative = true;
								FoundIngredient.add(zutat);
								displayIngredient(zutat);
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
											if(!FoundIngredient.contains(zutat2)){
												zutat2.noAlternative = false;
												FoundIngredient.add(zutat2);
												displayIngredient(zutat2);
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
						displayIngredient(zutat);
//					}
				}

				if(	getYourRecipes() != null && getYourRecipes().size() != 0){
					yourMealsPanel.setVisible(true);
					for(Recipe recipe : getYourRecipes()){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							FoundRezepteYours.add(recipe);
							displayRecipe(recipe,true);
						}
					}
				} else {
					yourMealsPanel.setVisible(false);
				}

				if(	clientData.getPublicRezepte() != null){
					for(Recipe recipe : clientData.getPublicRezepte()){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							FoundRezepte.add(recipe);
							displayRecipe(recipe,false);
						}
					}
				}

			}
			// all found items are now displayed

			sortResults();
			// and sorted
			
		}	
	}

	private static List<Recipe> getYourRecipes() {
		// TODO Auto-generated method stub
		if(TopPanel.leftKitchen){
			return clientData.getYourRezepte();
		} else {
			return clientData.KitchenRecipes;
		}
		
	}

	private static void searchRezept(String searchString,
			List<Recipe> allRezepte, String[] searches, boolean yours) {
		if(allRezepte != null){
			for(Recipe recipe : allRezepte){
				if(recipe != null){
					if( getLevenshteinDistance(recipe.getSymbol(),searchString) < 5){
						if(!FoundRezepte.contains(recipe) && !FoundRezepteYours.contains(recipe)){
							// Recipe zu Rezeptsuche
							if(yours){
								FoundRezepteYours.add(recipe);
							} else {
								FoundRezepte.add(recipe);
							}
							displayRecipe(recipe,yours);
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
										if(yours){
											FoundRezepteYours.add(recipe);
										} else {
											FoundRezepte.add(recipe);
										}
										displayRecipe(recipe,yours);
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
	 * The sorting functions
	 */

	private static void sortResults() {


		switch(sortMethod){
		case 1:{
			//"co2-value"
			Collections.sort(FoundIngredient,new ValueComparator());
			table.removeAllRows();
			if(FoundIngredient != null){
				for (final Ingredient item : FoundIngredient){
					displayIngredient(item);
				}
			}

			Collections.sort(FoundRezepte,new RezeptValueComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Recipe item : FoundRezepte){
					displayRecipe(item,false);
				}
			}

			Collections.sort(FoundRezepteYours,new RezeptValueComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Recipe item : FoundRezepteYours){
					displayRecipe(item,true);
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
			//			   ComparatorChain chain = new ComparatorChain();
			//			    chain.addComparator(new NameComparator());
			//			    chain.addComparator(new NumberComparator()
			Collections.sort(FoundIngredient,new NameComparator());
			table.removeAllRows();
			if(FoundIngredient != null){
				for (final Ingredient item : FoundIngredient){
					displayIngredient(item);
				}
			}

			Collections.sort(FoundRezepte,new RezeptNameComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Recipe item : FoundRezepte){
					displayRecipe(item,false);
				}
			}

			Collections.sort(FoundRezepteYours,new RezeptNameComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Recipe item : FoundRezepteYours){
					displayRecipe(item,true);
				}
			}

			break;



		}
		}

	}


	/**
	 * the displaying functions for recipes
	 */

	public static void displayRecipe(final Recipe recipe, boolean yours) {


		if(yours){
			final int row = tableMealsYours.getRowCount();


			Button removeRezeptButton = new Button(" x ");
			removeRezeptButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					final ConfirmDialog dlg = new ConfirmDialog("Sie wollen dieses Recipe...");
					dlg.statusLabel.setText("löschen?");
					
					//  recheck user if he really want to do this...
					dlg.executeButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							EaternityRechner.removeRezept(recipe);
							tableMealsYours.removeCells(row, 0, tableMealsYours.getCellCount(row));
							dlg.hide();
							dlg.clear();
						}
					});
					dlg.show();
					dlg.center();


				}
			});
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
			tableMealsYours.setWidget(row,0,item);

			recipe.setCO2Value();
			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
			item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g*</div>");



			if(EaternityRechner.loginInfo.isAdmin()){
				if(!recipe.isOpen()){
					//					if(recipe.openRequested){
					// this should be a link to make it open
					Anchor openThis = new Anchor("o");
					openThis.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							EaternityRechner.rezeptApproval(recipe,true);
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
							EaternityRechner.rezeptApproval(recipe,false);
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

			tableMeals.setWidget(row,0,item);
			//			tableMeals.setWidget(row, 2, AddRezeptButton);

//			double MenuLabelWert = 0.0;
//			for (IngredientSpecification zutatSpec : recipe.Zutaten) { 
//				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
//			}
			recipe.setCO2Value();
			String formatted = NumberFormat.getFormat("##").format(recipe.getCO2Value());
//			String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
			
			//			tableMeals.setText(row, 2,  "ca "+formatted+"g CO₂-Äquivalent");
			//			tableMeals.setText(row, 1,  "ca "+formatted+" g*");
			
			item.setHTML(item.getHTML()+"<div class='putRight'>ca "+formatted+ " g*</div>");

			if(EaternityRechner.loginInfo != null && EaternityRechner.loginInfo.isAdmin()){
				

//				Button removeRezeptButton = new Button(" - ");
//				removeRezeptButton.addStyleDependentName("gwt-Button");
				Anchor removeRezeptButton = new Anchor(" - ");
				removeRezeptButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						final ConfirmDialog dlg = new ConfirmDialog("Sie wollen dieses Recipe...");
						dlg.statusLabel.setText("löschen?");
						// TODO recheck user if he really want to do this...
						dlg.executeButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								EaternityRechner.removeRezept(recipe);
								tableMeals.removeCells(row, 0, tableMealsYours.getCellCount(row));
								dlg.hide();
								dlg.clear();
							}
						});
						dlg.show();
						dlg.center();


					}
				});
				tableMeals.setWidget(row, 1, removeRezeptButton);
//				item.setHTML(item.getHTML()+"<div class='putRight2'>ca "+formatted+ " g* ("+removeRezeptButton+")</div>");

				if(!recipe.isOpen()){
					if(recipe.openRequested){
						// TODO this should be a link to make it open
						Anchor openThis = new Anchor("o");
						openThis.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								EaternityRechner.rezeptApproval(recipe,true);
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
							EaternityRechner.rezeptApproval(recipe,false);
//							 initTable();
						}
					});
					tableMeals.setWidget(row, 2,closeThis);
//					item.setHTML(closeThis+" "+item.getHTML());
				}
			}


			if ((row % 2) == 1) {
				String style = evenStyleRow.evenRow();
				tableMeals.getRowFormatter().addStyleName(row, style);
			}
		}

	} 

	/**
	 * the displaying functions for ingredients
	 */
	
	
	public static void displayIngredient(final Ingredient ingredient) {
		int row = table.getRowCount();

		if ((row % 2) == 1) {
			String style = evenStyleRow.evenRow();
			table.getRowFormatter().addStyleName(row, style);
		}

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
			Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
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
			icon.setHTML(icon.getHTML()+"#: " +ingredient.getSymbol());
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

	public static void setFoundIngredient(ArrayList<Ingredient> foundIngredient) {
		FoundIngredient = foundIngredient;
	}

	public static ArrayList<Ingredient> getFoundIngredient() {
		return FoundIngredient;
	}
	
	
}


/**
 * Some Comparator classes to match Recipes
 */

// TODO check if there is already another class!

//class NameComparator implements Comparator<Ingredient> {
//	  public int compare(Ingredient z1, Ingredient z2) {
//		  String o1 = z1.getSymbol();
//		  String o2 = z2.getSymbol();
//	    if(o1 instanceof String && o2 instanceof String) {
//	      String s1 = (String)o1;
//	      String s2 = (String)o2;
//	      s1 = s1.substring(0, 1);
//	      s2 = s2.substring(0, 1);
//	      return s1.compareToIgnoreCase(s2);
//	    }
//	    return 0;
//	  }
//	}

//	class ValueComparator implements Comparator<Ingredient> {
//	  public int compare(Ingredient z1, Ingredient z2) {
//		  long o1 = z1.getCo2eValue();
//		  long o2 = z2.getCo2eValue();
//		  
//	    return -Long.valueOf(o2).compareTo(Long.valueOf(o1));
//	  }
//	}

//	class RezeptValueComparator implements Comparator<Recipe> {
//		public int compare(Recipe r1, Recipe r2) {
//			Double o1 = getRezeptCO2(r1.getZutaten());
//			Double o2 = getRezeptCO2(r2.getZutaten());
//
//			return -Double.valueOf(o2).compareTo(Double.valueOf(o1));
//
//		}
//
//		private Double getRezeptCO2(List<IngredientSpecification> Zutaten) {
//			Double MenuLabelWert = 0.0;
//			for (IngredientSpecification zutatSpec : Zutaten) { 
//				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
//
//			}
//			return MenuLabelWert;
//		}
//	}
//	
//	class RezeptNameComparator implements Comparator<Recipe> {
//		  public int compare(Recipe z1, Recipe z2) {
//			  String o1 = z1.getSymbol();
//			  String o2 = z2.getSymbol();
//		    if(o1 instanceof String && o2 instanceof String) {
//		      String s1 = (String)o1;
//		      String s2 = (String)o2;
//		      s1 = s1.substring(0, 1);
//		      s2 = s2.substring(0, 1);
//		      return s1.compareToIgnoreCase(s2);
//		    }
//		    return 0;
//		  }
//		}