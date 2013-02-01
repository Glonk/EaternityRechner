
package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoadedDataEventHandler;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.MonthChangedEventHandler;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.events.UpdateRecipeViewEventHandler;
import ch.eaternity.client.ui.widgets.TooltipListener;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SeasonDate;
import ch.eaternity.shared.comparators.NameComparator;
import ch.eaternity.shared.comparators.ValueComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author aurelianjaggi
 *
 */
public class SearchIngredients extends Composite {
	interface Binder extends UiBinder<Widget, SearchIngredients> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	
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
	@UiField public static SuggestBox SearchInput;
	
	// Display Results in:
	@UiField DockLayoutPanel displayResultsPanel;
	
	// Search results Tables
	@UiField static FlexTable table;

	// CSS reference for the alternating row coloring
	@UiField static MarkingStyle markingStyle;
	@UiField static SelectionStyle selectionStyle;
	@UiField static EvenStyleRow evenStyleRow;
	
	// sorting of the tables:
	@UiField Anchor saisonOrder;
	@UiField Anchor co2Order;
	@UiField Anchor alphOrder;
	
	
	// ---------------------- Class Interfaces ---------------------
	
	 //Call-back when items are selected. 
	public interface Listener { // Call-back for ingredient click
		void onItemSelected(Ingredient item);
	}

	public interface ListenerMeals { // Call-back for menu click
		void onItemSelected(Recipe item);
	}


	interface MarkingStyle extends CssResource {
		String markedRow();
	}
	
	
	//TODO check why everything crashes for selectedRow = -1
	interface SelectionStyle extends CssResource {
		String selectedRow();
	}
	
	// Color the rows alternating
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}
			
	// ---------------------- Class Variables ----------------------
	
	private RechnerActivity presenter;
	private DataController dco;
	
	public List<Ingredient> foundIngredients = new ArrayList<Ingredient>();
	public List<Ingredient> foundAlternativeIngredients = new ArrayList<Ingredient>();
	
	public String searchString = "";
		
	// choose this sorting method
	static int sortMethod = 1;
	
	private Listener listener;
	private ListenerMeals listenerMeals;
	
	// CSS of rows
	static int markedRow = 0;
	static int selectedRow = 0;
	
	// ---------------------- public Methods -----------------------
	
	public SearchIngredients() {
		initWidget(uiBinder.createAndBindUi(this));
		// we have to wait till the database is loaded:
		SearchInput.setText("wird geladen...");
		SearchInput.setFocus(true);

		initToolTips();
	}
	

	private void bind() {
		
		
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(LoadedDataEvent.TYPE, new LoadedDataEventHandler() {
			@Override
			public void onLoadedData(LoadedDataEvent event) {
				updateResults("");
			}
		});
		presenter.getEventBus().addHandler(MonthChangedEvent.TYPE, new MonthChangedEventHandler() {
			@Override
			public void onEvent(MonthChangedEvent event) {
				displayResults();
			}
		});
	}
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		initTable(); // just the size
		bind();
	}
	
	// ---------------------- UI Handlers ----------------------
	
	@UiHandler("co2Order")
	void onCo2Clicked(ClickEvent event) {
		sortMethod = 1;
		sortResults(sortMethod);
		displayResults();
	}

	@UiHandler("alphOrder")
	void onAlphClicked(ClickEvent event) {
		sortMethod = 5;
		sortResults(sortMethod);
		displayResults();
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
				markedRow = 0;
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

	

		// ---------------------------------------------------------------
		
		
		/**
		 * The search algorithm
		 */
		
		// TODO this is getting called twice all the time...
		public void updateResults(String searchString) {
			SearchInput.setText(searchString);
			
			foundIngredients.clear();
			foundAlternativeIngredients.clear();
			
			// Get data from Data Controller
			dco.searchIngredients(searchString, foundIngredients, foundAlternativeIngredients);
	
			// Display Results
			//TODO: Special Display for alternatives, now still done in displayIngredient
			foundIngredients.addAll(foundAlternativeIngredients);
	
			displayResults();
			
			// Correct mark adjustements
			int numOfIngredientsFound = foundIngredients.size();
			if (markedRow <= 0)
				changeMarkedRow(0);
			else if(markedRow >= numOfIngredientsFound)
				changeMarkedRow(numOfIngredientsFound-1);
			else
				changeMarkedRow(markedRow);
			
			if (searchString.equals(""))
				changeMarkedRow(0);
			
		}
		
		/**
		 * The sorting functions
		 * 
		 * Call displayResults for showing effect
		 *
		*/
		public void sortResults(int sortMethod) {
			this.sortMethod = sortMethod;
			
			switch(sortMethod){
			case 1:{
				//"co2-value"
				Collections.sort(foundIngredients,new ValueComparator());
				Collections.sort(foundAlternativeIngredients,new ValueComparator());
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
				
				Collections.sort(foundIngredients,new NameComparator());
				Collections.sort(foundAlternativeIngredients,new NameComparator());
			}
			}
		}
		
	
		
		// ----------------------------- private Methods -------------------------------------------
		
		private void displayResults() {
			table.removeAllRows();
			if(foundIngredients != null){
				// display all noALternative Ingredients
				for (final Ingredient item : foundIngredients){
					if (item.getNoAlternative())
						displayIngredient(item);
				}
			
					
				// display all alternative Ingredients (sorted as well)
				// boolean textlabeladded = false;
				for (final Ingredient item : foundAlternativeIngredients){
					if (!item.getNoAlternative())
					{
						/* alternative dividing section *
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
		 * the displaying functions for ingredients
		 */
		private void displayIngredient(final Ingredient ingredient) {
			int row = table.getRowCount();
	
	
			HTML icon = new HTML();
	
			if(ingredient.getCo2eValue() < 400){
				icon.setStyleName("base-icons");
				icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
				icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>");
			} else	if(ingredient.getCo2eValue() < 1200){
				icon.setStyleName("base-icons");	
				icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>");
	
			}
	
			if(ingredient.getHasSeason() != null && ingredient.getHasSeason()){
				SeasonDate date = new SeasonDate(dco.getCurrentMonth(),1);
				SeasonDate dateStart = ingredient.getStartSeason();		
				SeasonDate dateStop =  ingredient.getStopSeason();
				
				if( date.after(dateStart) && date.before(dateStop) ){
					icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
				} 
			}
	
			if(ingredient.getNoAlternative()){
			icon.setHTML(icon.getHTML()+"<div class='ingText'>"+ingredient.getSymbol()+"</div>");
			
			} else {
				icon.setHTML(icon.getHTML()+"(alt): " +ingredient.getSymbol());
			}
	
			icon.setHTML(icon.getHTML()+"<div class='putRight'>ca "+Integer.toString((int) ingredient.getCo2eValue()/10).concat(" g*")+"</div>");
	
			table.setWidget(row,0,icon);
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
		
		private void initTable() {
			// this is just basic design stuff
			table.getColumnFormatter().setWidth(0, "120px");
		}
	
		
	
		private void selectRow(final int row) {
			Ingredient item;
			
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
			
			if (foundIngredients.size() < row){
				return;
			}
			
			if (row >= 0 && row < foundIngredients.size())
			{
				item = foundIngredients.get(row);
			}
			/*
			else if (row >= presenter.getDAO().foundIngredient.size() && row < numOfIngredientsFound)
			{
				item = presenter.getDAO().foundAlternativeIngredients.get(row - presenter.getDAO().foundIngredient.size());
			}
			*/
			else return;
	
	
	
			if (item == null) {
				return;
			}
	
			styleRow(selectedRow, false);
			styleRow(row, true);
	
	
			Timer t = new Timer() {
				public void run() {
					styleRow(row, false);
				}
			};
	
			dco.addIngredientToMenu(item, grams);
	
			t.schedule(200);
			selectedRow = row;
			markedRow = 0;
	
			if (listener != null) {
				listener.onItemSelected(item);
			}
		}
		
		private void changeMarkedRow(int row)
		{
			if (row >= 0 && row < foundIngredients.size())
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
		
		private void styleMarkedRow(int row, boolean marked) {
			String style = markingStyle.markedRow();
	
			if (marked) {
				table.getRowFormatter().addStyleName(row, style);
			} else {
				table.getRowFormatter().removeStyleName(row, style);
			}
		}

}



