
package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;

import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoadedDataEventHandler;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.MonthChangedEventHandler;
import ch.eaternity.client.ui.cells.ProductCell;
import ch.eaternity.client.ui.widgets.TooltipListener;
import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Util.SortMethod;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
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
	@UiField Label SearchLabel;
	@UiField TextBox SearchInput;
	
	@UiField ScrollPanel ingredientDisplayWidget;
	
	// Display Results in:
	@UiField DockLayoutPanel displayResultsPanel;


	// CSS reference for the alternating row coloring
	@UiField static MarkingStyle markingStyle;
	@UiField static SelectionStyle selectionStyle;
	@UiField static EvenStyleRow evenStyleRow;
	
	// sorting of the tables:
	@UiField Anchor saisonOrder;
	@UiField Anchor co2Order;
	@UiField Anchor alphOrder;
	
	
	// ---------------------- Class Interfaces ---------------------

	interface MarkingStyle extends CssResource {
		String markedRow();
	}
	
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
	
	// Create a data provider.
	private ListDataProvider<FoodProductInfo> productDataProvider = new ListDataProvider<FoodProductInfo>();
    private CellList<FoodProductInfo> cellList;
	
    private List<FoodProductInfo> foundProducts  = new ArrayList<FoodProductInfo>();
	private List<FoodProductInfo> foundAlternativeProducts  = new ArrayList<FoodProductInfo>();
	
	private String searchString = "";
		
	private SortMethod lastSortMethod = SortMethod.NONE;
	
	private boolean[] reversSortArray = {false,false,false};
	
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
	
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		
		// this makes switching views (places) very slow... if it get's build each time again
		if(dco.editDataLoaded())
			updateResults("");
		
		this.setHeight("520px");
		panelSouth.setVisible(false);
		
		// initialize a key provider to refer to the same selection states
		ProvidesKey<FoodProductInfo> keyProvider = new ProvidesKey<FoodProductInfo>() {
		      public Object getKey(FoodProductInfo item) {
		        // Always do a null check.
		        return (item == null) ? null : item.getId();
		      }
		};
		    
		// Create a cell to render each value in the list.
	    ProductCell productCell = new ProductCell();
	    
	    // Create a CellList that uses the cell.
	    cellList = new CellList<FoodProductInfo>(productCell, keyProvider);
	    
	    setupOnePageList(cellList);
	    
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		
	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<FoodProductInfo> selectionModel = new SingleSelectionModel<FoodProductInfo>(keyProvider);
	    
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	    	public void onSelectionChange(SelectionChangeEvent event) {
	    		FoodProductInfo selected = selectionModel.getSelectedObject();
		        if (selected != null) {
		        	addFoodProduct(selected);
		        	selectionModel.setSelected(selected, false);
		        }
	    	}
	    });
	    
	    // Connect the list to the data provider.
	    productDataProvider.addDataDisplay(cellList);
	    cellList.redraw();
	    
	    // Add it to the display panel.
	    ingredientDisplayWidget.setWidget(cellList);
	    
		bind();
	}
	
	private void bind() {
		//  Listen to the EventBus 
		presenter.getEventBus().addHandler(LoadedDataEvent.TYPE, new LoadedDataEventHandler() {
			@Override
			public void onEvent(LoadedDataEvent event) {
				sortResults(SortMethod.ALPHABETIC, false);
				updateResults("");
			}
		});
		presenter.getEventBus().addHandler(MonthChangedEvent.TYPE, new MonthChangedEventHandler() {
			@Override
			public void onEvent(MonthChangedEvent event) {
				updateResults(SearchInput.getText().trim());
				sortResults(lastSortMethod, false);
			}
		});
	}
	
	public static void setupOnePageList(final AbstractHasData<?> cellTable) {
	    cellTable.addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
	        @Override
	        public void onRowCountChange(RowCountChangeEvent event) {
	            cellTable.setVisibleRange(new Range(0, event.getNewRowCount()));
	        }
	    });
	}
	
	// ---------------------- UI Handlers ----------------------
	
	@UiHandler("co2Order")
	void onCo2Clicked(ClickEvent event) {
		if (lastSortMethod == SortMethod.CO2VALUE)
			reversSortArray[1] = !reversSortArray[1];
		else
			reversSortArray[1] = false;
		sortResults(SortMethod.CO2VALUE, reversSortArray[1]);
	}

	@UiHandler("alphOrder")
	void onAlphClicked(ClickEvent event) {
		if (lastSortMethod == SortMethod.ALPHABETIC)
			reversSortArray[2] = !reversSortArray[2];
		else
			reversSortArray[2] = false;
		sortResults(SortMethod.ALPHABETIC, reversSortArray[2]);
	}
	
	@UiHandler("saisonOrder")
	void onSaisonClicked(ClickEvent event) {
		if (lastSortMethod == SortMethod.SEASON)
			reversSortArray[0] = !reversSortArray[0];
		else
			reversSortArray[0] = false;
		sortResults(SortMethod.SEASON, reversSortArray[0]);
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
				//selectRow(markedRow);
				SearchInput.setText("");
				updateResults("");
				markedRow = 0;
			}
		}
		if(KeyCodes.KEY_DOWN == event.getNativeKeyCode())
		{
			numDownKeyPressed++;
			if (numDownKeyPressed % 2 == 0);
				//changeMarkedRow(markedRow + 1);
		}
		if(KeyCodes.KEY_UP == event.getNativeKeyCode())
		{
			numUpKeyPressed++;
			if (numUpKeyPressed % 2 == 0);
				//changeMarkedRow(markedRow - 1);
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
		displayResultsPanel.setWidgetSize(panelSouth, 20);
		displayResultsPanel.forceLayout();

	}

	// ---------------------------------------------------------------
	

	public void updateResults(String searchString) {
		SearchInput.setText(searchString);
		
		foundProducts.clear();
		foundAlternativeProducts.clear();
		
		// Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<FoodProductInfo> productList = productDataProvider.getList(); 
	    
	    
		// Get data from Data Controller
		dco.searchIngredients(searchString, productList, foundAlternativeProducts);
	
		// Display Results
		//TODO: Special Display for alternatives, now still done in displayIngredient
		///foundIngredients.addAll(foundAlternativeIngredients);
	
		//displayResults();
		
	    /*
		// Correct mark adjustements
		int numOfIngredientsFound = foundProducts.size();
		if (markedRow <= 0)
			changeMarkedRow(0);
		else if(markedRow >= numOfIngredientsFound)
			changeMarkedRow(numOfIngredientsFound-1);
		else
			changeMarkedRow(markedRow);
		
		if (searchString.equals(""))
			changeMarkedRow(0);
		*/
	}
	
	/**
	 * The sorting functions
	*/
	public void sortResults(SortMethod sortMethod, final boolean reverse) {
		
		List<FoodProductInfo> productList = productDataProvider.getList();
		
		this.lastSortMethod = sortMethod;
		
		switch(sortMethod){
			case CO2VALUE:
				Collections.sort(productList,new Comparator<FoodProductInfo>() {
					@Override
					public int compare(FoodProductInfo z1, FoodProductInfo z2) {  
						if (reverse)
							return -Double.valueOf(z1.getCo2eValue()).compareTo(Double.valueOf(z2.getCo2eValue()));
						else
							return Double.valueOf(z1.getCo2eValue()).compareTo(Double.valueOf(z2.getCo2eValue()));
					}
				});
				break;
			case SEASON:
				Collections.sort(productList,new Comparator<FoodProductInfo>() {
					@Override
					public int compare(FoodProductInfo o1, FoodProductInfo o2) {  
						boolean v1 = o1.isInSeason();
					    boolean v2 = o2.isInSeason();
					    return (v1 ^ v2) ? ((v1 ^ reverse) ? 1 : -1) : 0;
					}
				});
				break;
			case ALPHABETIC:
				Collections.sort(productList, new Comparator<FoodProductInfo>() {
					@Override
					public int compare(FoodProductInfo z1, FoodProductInfo z2) {
						String o1 = z1.getName();
						String o2 = z2.getName();
						if(o1 instanceof String && o2 instanceof String) {
							String s1 = (String)o1;
							String s2 = (String)o2;
							s1 = s1.substring(0, 1);
							s2 = s2.substring(0, 1);
							if (reverse)
								return -s1.compareToIgnoreCase(s2);
							else
								return s1.compareToIgnoreCase(s2);
						}
						return 0;
					}
				});
				break;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private void initToolTips() {
		
		// do the tooltips with gwt-bootstrap
		
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
		
		/**
		*
		* SearchLabel.addMouseListener(
		*		new TooltipListener(
		*				"Suche nach Zutaten und Rezepten hier.", 5000 ,"yourcssclass",5,-34));
		*/
		
		co2Order.addMouseListener(
				new TooltipListener(
						"Sortiere Suchergebnisse nach CO₂-Äquivalent Wert.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));
		alphOrder.addMouseListener(
				new TooltipListener(
						"Sortiere Suchergebnisse alphabetisch.", 5000 /* timeout in milliseconds*/,"yourcssclass",0,-50));
	
	}
	
	
	
	
	private void addFoodProduct(FoodProductInfo product) {
		
		if (product == null) return;
		
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
			catch(NumberFormatException nFE) {}
		}
	
	
		//styleRow(selectedRow, false);
		//styleRow(row, true);
	
		/*
		Timer t = new Timer() {
			public void run() {
				styleRow(row, false);
			}
		};
		*/
		
		QuantityImpl weigth = null;
		if (grams != 0)
			weigth = new QuantityImpl((double)grams, Unit.GRAM);
		dco.addIngredientToMenu(product, weigth);
	
		/*
		t.schedule(200);
		selectedRow = row;
		markedRow = 0;
		*/
	
	}
		

}



