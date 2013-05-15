
package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import ch.eaternity.client.DataController;
import ch.eaternity.client.activity.RechnerActivity;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoadedDataEventHandler;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.MonthChangedEventHandler;
import ch.eaternity.client.ui.cells.ProductCell;
import ch.eaternity.shared.FoodProductInfo;
import ch.eaternity.shared.Util.SortMethod;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * this class extends resize composite to propperly propagate down the onResize events to child widgets
 * @author aurelianjaggi and mklarmann
 *
 */
public class SearchIngredients extends ResizeComposite {
	interface Binder extends UiBinder<Widget, SearchIngredients> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	// ---------------------- User Interface Elements --------------
	
	// Search Panel (Box and Button)
	@UiField TextBox SearchInput;
	
	@UiField SimplePanel ingredientDisplayWidget;
	
	// sorting of the tables:
	@UiField Anchor saisonOrder;
	@UiField Anchor co2Order;
	@UiField Anchor alphOrder;
	
	// The Legend
	@UiField Anchor showDisclaimer;
	@UiField Modal disclaimerModal;
	@UiField Button closeDisclaimer;
	
	@UiField Anchor feedbackAnchor;
	@UiField Modal feedbackModal;
	@UiField Button closeButton;
	
	// ---------------------- Class Variables ----------------------
	
	// initialize a key provider to refer to the same selection states
	private final ProvidesKey<FoodProductInfo> keyProvider = new ProvidesKey<FoodProductInfo>() {
	      public Object getKey(FoodProductInfo item) {
	        // Always do a null check.
	        return (item == null) ? null : item.getId();
	      }
	};
	
	public interface CellListResource extends CellList.Resources
	{
	   public interface CellListStyle extends CellList.Style {};

	   @Source({"../resources/productCellList.css"})
	   CellListStyle cellListStyle();
	};
	
	CellListResource cellListResource = GWT.create(CellListResource.class);
	
	private RechnerActivity presenter;
	private DataController dco;
	
	// Create a data provider.
	private ListDataProvider<FoodProductInfo> productDataProvider = new ListDataProvider<FoodProductInfo>();
    private CellList<FoodProductInfo> cellList;
    // Add a selection model to handle user selection.
    private final SingleSelectionModel<FoodProductInfo> selectionModel = new SingleSelectionModel<FoodProductInfo>(keyProvider);
	
    private List<FoodProductInfo> foundProducts  = new ArrayList<FoodProductInfo>();
	private List<FoodProductInfo> foundAlternativeProducts  = new ArrayList<FoodProductInfo>();
	
	private String searchString = "";
		
	private SortMethod lastSortMethod = SortMethod.CO2VALUE;
	
	private boolean[] reversSortArray = {false,false,false};
	
	// CSS of rows
	private int selectedRow = 0;
	
	// ---------------------- public Methods -----------------------
	
	public SearchIngredients() {
		
		initWidget(uiBinder.createAndBindUi(this));
		feedbackAnchor.setTarget("_blank");
		// we have to wait till the database is loaded:
		SearchInput.setText("wird geladen...");
		SearchInput.setFocus(true);
	}
	
	
	public void setPresenter(RechnerActivity presenter) {
		this.presenter = presenter;
		this.dco = presenter.getDCO();
		
		if(dco.editDataLoaded()) {
			updateResults("");
			sortResults(SortMethod.CO2VALUE, false);
			markRow(0);
			
		}
		    
		// Create a cell to render each value in the list.
	    ProductCell productCell = new ProductCell(this);
	    
	    // Create a CellList that uses the cell.
	    cellList = new CellList<FoodProductInfo>(productCell, cellListResource, keyProvider);
	    setupOnePageList(cellList); 
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    cellList.setSelectionModel(selectionModel);
	    
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
				updateResults("");
				sortResults(SortMethod.CO2VALUE, false);
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
	
	// Legend
	@UiHandler("showDisclaimer")
	void onshowDisclaimer(ClickEvent event) {
		disclaimerModal.show();
	}
	
	@UiHandler("closeDisclaimer")
	void onCloseDisclaimer(ClickEvent event) {
		disclaimerModal.hide();
	}
	
	// Feedback
	@UiHandler("feedbackAnchor")
	public void onFeedbackClick(ClickEvent event) {
		feedbackModal.show();
	}
	@UiHandler("closeButton")
	public void onCloseClick(ClickEvent event) {
		feedbackModal.hide();
	}
	
	
	// Handle search input
	@UiHandler("SearchInput")
	public void onKeyUp(KeyUpEvent event) {
		if( !SearchInput.getText().trim().equals(searchString))
			updateResults(SearchInput.getText());
	}
	
	
	@UiHandler("SearchInput")
	public void onKeyDown(KeyDownEvent event) {
		if(KeyCodes.KEY_ENTER == event.getNativeKeyCode())
		{
			addFoodProduct(selectionModel.getSelectedObject());
			SearchInput.setText("");
			updateResults("");
			markRow(0);
			SearchInput.setFocus(true);
		}
		if(KeyCodes.KEY_DOWN == event.getNativeKeyCode())
		{
			markRow(selectedRow + 1);
		}
		if(KeyCodes.KEY_UP == event.getNativeKeyCode())
		{
			markRow(selectedRow - 1);
		}
	}
	
	public void markRow(int rowToMark) {
		int listSize = productDataProvider.getList().size();
		if (listSize > 0) {
			if (rowToMark <= 0)
				selectedRow = 0;
			else if(rowToMark >= listSize)
				selectedRow = listSize - 1;
			else
				selectedRow = rowToMark;
			
			selectionModel.setSelected(productDataProvider.getList().get(selectedRow), true);
		}
		
	}


	// ---------------------------------------------------------------
	

	public void updateResults(String searchString) {
		SearchInput.setText(searchString);
		this.searchString = searchString.trim();
		
		// Add the data to the data provider, which automatically pushes it to the widget.
	    List<FoodProductInfo> productList = productDataProvider.getList(); 
	    
		// Get data from Data Controller
		dco.searchIngredients(searchString, productList, foundAlternativeProducts);
	
		//TODO: Special Display for alternatives, now still done in displayIngredient
		///foundIngredients.addAll(foundAlternativeIngredients);
		
		markRow(selectedRow);
					
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
		// mark the previously selected row again
		markRow(selectedRow);
		
	}
	
	
	
	
	public void addFoodProduct(FoodProductInfo product) {
		
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



