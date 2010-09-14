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
import java.util.Comparator;
import java.util.List;
import java.util.Set;



import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * A composite that displays a list of zutaten that can be selected.
 */
public class Search extends ResizeComposite {

	/**
	 * Callback when items are selected. 
	 */
	public interface Listener {
		void onItemSelected(Ingredient item);
	}

	interface Binder extends UiBinder<Widget, Search> { }
	interface SelectionStyle extends CssResource {
		String selectedRow();
	}
	
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}




	private static final Binder binder = GWT.create(Binder.class);
	static final int VISIBLE_EMAIL_COUNT = 4;

	@UiField
	static FlexTable table;
	@UiField
	static FlexTable tableMeals;
	@UiField
	static FlexTable tableMealsYours;
//	@UiField Button SearchButton;
	@UiField
	static SuggestBox SearchBox2;
	@UiField DockLayoutPanel SearchBox;
	@UiField
	static SelectionStyle selectionStyle;
	@UiField
	static EvenStyleRow evenStyleRow;
//	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField
	static DockLayoutPanel leftSplitPanel;
	@UiField Anchor co2Order;
	@UiField Anchor alphOrder;
	static
	@UiField HTMLPanel yourRezeptePanel;
	@UiField SplitLayoutPanel mealsSplitPanels;
	@UiField SplitLayoutPanel helpMealsSplitPanels;
	@UiField HTMLPanel scrollAbleHtml;

//	@UiField HTMLPanel scrollTriggerHtml;
	
	//@UiField
	//static InfoZutat infoZutat;
	
	
	private static Data clientData = new Data();
	private static ArrayList<Rezept> FoundRezepte = new ArrayList<Rezept>();
	private static ArrayList<Rezept> FoundRezepteYours = new ArrayList<Rezept>();
//	private static ArrayList<Zutat> FoundZutaten = new ArrayList<Zutat>();
	private static ArrayList<Ingredient> FoundIngredient = new ArrayList<Ingredient>();
	
	static int sortMethod = 1;

	public static Data getClientData() {
		return clientData;
	}

	public static void setClientData(Data clientData) {
		
		Search.clientData = clientData;
		
		
	}

	private Listener listener;
	//TODO check why everything crashes for selectedRow = -1
	static int  selectedRow = 0;
	
	
	private static MultiWordSuggestOracle oracle;
	//  private SearchBar navBar;
	

	public Search() {


		initWidget(binder.createAndBindUi(this));
		SuggestOracle soracle = SearchBox2.getSuggestOracle();
		oracle = (MultiWordSuggestOracle) soracle;

		initTable();
		//    updateResults("all");
		SearchBox2.setFocus(true);
		yourRezeptePanel.setVisible(false);
		helpMealsSplitPanels.addStyleName("noSplitter");
		setVDraggerHeight("0px");


	}

	public void setVDraggerHeight (String height)
	{
//	  SplitLayoutPanel p = (SplitLayoutPanel) this.getWidget ();
		SplitLayoutPanel p = 	helpMealsSplitPanels;
	  int widgetCount = p.getWidgetCount ();
	  for (int i = 0; i < widgetCount; i++) {
	    Widget w = p.getWidget (i);
	    if (w.getStyleName ().equals ("gwt-SplitLayoutPanel-VDragger")) {
	      w.setHeight (height);
	    }
	  }
	}
	
	public static void initializeOracle(Set<String> itemIndex){
		if (itemIndex != null){
			//TODO switch on Oracle...
			//oracle.addAll(itemIndex);
		}
	}

	/**
	 * Sets the listener that will be notified when an item is selected.
	 */
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	protected void onLoad() {
		// Select the first row if none is selected.
		if (selectedRow == -1) {
			selectRow(0);
		}
	}


//	@UiHandler("SearchButton")
//	public void onClick(ClickEvent event) {
//		updateResults(SearchBox2.getText());
//	}


	// Listen for keyboard events in the input box.
//	@UiHandler("SearchBox2")
//	public void onKeyPress(KeyPressEvent event) {
//		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
//
//			
//
//			// simulate button press
////			SearchButton.setEnabled(false);
//			Timer t = new Timer() {
//				public void run() {
////					SearchButton.setEnabled(true);
//					updateResults(SearchBox2.getText());
//				}
//			};
//			t.schedule(200);
//		}
//	}
	
//	
//	@UiHandler("mealsSplitPanels")
//	public void onDrag(MouseUpEvent change){
//		scrollAbleHtml.setHeight(Integer.toString(mealsSplitPanels.getOffsetHeight()));
//	}


	@UiHandler("SearchBox2")
	public void onKeyUp(KeyUpEvent event) {
		// this matches up to 2 words
		updateResults(SearchBox2.getText());
//		
//		Set<String> itemIndex = getClientData().getOrcaleIndex();
//		if(SearchBox2.getText().length() > 2 ){
//			if(itemIndex.contains( (SearchBox2.getText().substring(0, SearchBox2.getText().length()-1)).trim() ) & !SearchBox2.getText().endsWith(" ")){
//
//				Set<String> itemIndexLong = new TreeSet<String>();
//				for(String item : itemIndex){
//					if(!SearchBox2.getText().trim().contains(item)){
//						itemIndexLong.add( (SearchBox2.getText().substring(0, SearchBox2.getText().length()-1)).concat(item));
//					}
//				}
//				oracle.clear();
//				initializeOracle(itemIndexLong);
//			}
//		}
//
//		if(!SearchBox2.getText().trim().contains(" ") | SearchBox2.getText().endsWith(" ")){
//			oracle.clear();
//			initializeOracle(itemIndex);
//		}
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


	private void initTable() {
		table.getColumnFormatter().setWidth(0, "120px");
		table.getColumnFormatter().setWidth(1, "80px");
		table.getColumnFormatter().setWidth(2, "40px");
		
		tableMeals.getColumnFormatter().setWidth(0, "120px");
		tableMeals.getColumnFormatter().setWidth(1, "80px");
		tableMeals.getColumnFormatter().setWidth(2, "40px");
		
		tableMealsYours.getColumnFormatter().setWidth(0, "80px");
		tableMealsYours.getColumnFormatter().setWidth(1, "40px");
		tableMealsYours.getColumnFormatter().setWidth(2, "15px");
		tableMealsYours.getColumnFormatter().setWidth(3, "15px");
		tableMealsYours.getColumnFormatter().setWidth(4, "15px");
		
	}

	/**
	 * Selects the given row (relative to the current page).
	 * 
	 * @param row the row to be selected
	 */
	private void selectRow(final int row) {
		

		
		//TODO uncomment this:
		//leftSplitPanel.setWidgetMinSize(infoZutat, 448);
		//    Zutat item = MailItems.getMailItemName(table.getText(row, 1));
		if (FoundIngredient.size() < row){
			return;
		}
		
		Ingredient item = FoundIngredient.get(row);
		

		if (item == null) {
			return;
		}
		
		styleRow(selectedRow, false);
		styleRow(row, true);
		
		//TODO uncomment this:
		//infoZutat.setZutat(item);
		
//		leftSplitPanel.setHeight("500px");
//		leftSplitPanel.addSouth( new InfoZutat(), 500);
		
//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 0, PX);
//		leftSplitPanel.forceLayout();
//		leftSplitPanel.setWidgetTopHeight(infoZutat, 0, PX, 2, EM);
//		leftSplitPanel.animate(500);
		//TODO uncomment this:
		//infoZutat.stylePanel(true);

		// infotext über angeklickte zutat

		// Groß:Name, Foto, Liste:Labels: Text:Beschreibung Wikipedia klein:Alternativen



		

		
		//TODO uncomment this:
		//if(EaternityRechner.MenuTable.getRowCount() > 0){
		//	EaternityRechner.styleRow(EaternityRechner.selectedRow,false);
		//	EaternityRechner.selectedRow = -1;
		//}
		
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

	//TODO do the same for Search BUtton Press
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

	static void updateResults(String searchString) {
		table.removeAllRows();
		tableMeals.removeAllRows();
		tableMealsYours.removeAllRows();
		
		FoundIngredient.clear();
		FoundRezepte.clear();
		FoundRezepteYours.clear();
		
//		List<Rezept> allRezepte = getClientData().getPublicRezepte();
		if(	getClientData().getYourRezepte() != null && getClientData().getYourRezepte().size() != 0){
			yourRezeptePanel.setVisible(true);
//			for(Rezept rezept: getClientData().getYourRezepte()){
//				if(!allRezepte.contains(rezept)){
//					allRezepte.add(rezept);
//				}
//			}
//			allRezepte.addAll(getClientData().getYourRezepte());
		} else {
			yourRezeptePanel.setVisible(false);
		}
		
			if ((getClientData().getIngredients() != null) ){
				
				// Zutaten
				if(searchString.trim().length() != 0){
					
				String[] searches = searchString.split(" ");
				
				for(String search : searches){
	
					// Zutaten
					for(Ingredient zutat : getClientData().getIngredients()){
						if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
							//if(,search) < 3){
							//Window.alert(zutat.getSymbol().substring(0, search.trim().length()));
							if(!FoundIngredient.contains(zutat)){
								zutat.noAlternative = true;
								FoundIngredient.add(zutat);
								displayZutat(zutat);
							}
							


						}
					}
					// only look for alternatives, if there is only 1 result
					// TODO mark the alternatives as Special!
					if(FoundIngredient.size() == 1){
						for(Ingredient zutat :FoundIngredient){
							if(zutat.getAlternatives() != null){
								for(Long alternativen_id : zutat.getAlternatives()){
									for(Ingredient zutat2 : getClientData().getIngredients()){
										if(zutat2.getId().equals(alternativen_id)){
											if(!FoundIngredient.contains(zutat2)){
												zutat2.noAlternative = false;
												FoundIngredient.add(zutat2);
												displayZutat(zutat2);
											}
										}
									}
								}
							}
						}
					}
				}
				// Rezepte
				if(	getClientData().getYourRezepte() != null){
					searchRezept(searchString, getClientData().getYourRezepte(), searches,true);
				}
				
				if(	getClientData().getPublicRezepte() != null){
					searchRezept(searchString, getClientData().getPublicRezepte(), searches,false);
				}
				
				
				} 
				 else {
				
					for(Ingredient zutat : getClientData().getIngredients()){
						if(!FoundIngredient.contains(zutat)){
							FoundIngredient.add(zutat);
							zutat.noAlternative = true;
							displayZutat(zutat);
						}
					}
					
					if(	getClientData().getYourRezepte() != null && getClientData().getYourRezepte().size() != 0){
						yourRezeptePanel.setVisible(true);
						for(Rezept rezept : getClientData().getYourRezepte()){
							if(!FoundRezepte.contains(rezept) && !FoundRezepteYours.contains(rezept)){
								FoundRezepteYours.add(rezept);
							displayRezept(rezept,true);
							}
						}
					} else {
						yourRezeptePanel.setVisible(false);
					}
					
					if(	getClientData().getPublicRezepte() != null){
						for(Rezept rezept : getClientData().getPublicRezepte()){
							if(!FoundRezepte.contains(rezept) && !FoundRezepteYours.contains(rezept)){
								FoundRezepte.add(rezept);
							displayRezept(rezept,false);
							}
						}
					}
					

				}
				
				sortResults();
			}	
		}

	private static void searchRezept(String searchString,
			List<Rezept> allRezepte, String[] searches, boolean yours) {
		if(allRezepte != null){
		for(Rezept rezept : allRezepte){
			if(rezept != null){
				if( getLevenshteinDistance(rezept.getSymbol(),searchString) < 5){
					if(!FoundRezepte.contains(rezept) && !FoundRezepteYours.contains(rezept)){
						// Rezept zu Rezeptsuche
						if(yours){
							FoundRezepteYours.add(rezept);
						} else {
							FoundRezepte.add(rezept);
						}
						displayRezept(rezept,yours);
						
		// das ist unübersichtlich:
//						// Zutaten des Rezept Ergerbnis Liste der Zutaten
//						for(ZutatSpecification zutatSpSuche : rezept.getZutaten() ){
//							for(Ingredient zutatSuche : getClientData().getIngredients() ){
//								if(zutatSpSuche.getZutat_id().equals(zutatSuche.getId() )){
//									if(!FoundIngredient.contains(zutatSuche)){
//										FoundIngredient.add(zutatSuche);
//										displayZutat(zutatSuche);
//									}
//								}
//
//							}
//
//						}
					}
				}

				List<ZutatSpecification> zutatenRezept = rezept.getZutaten();
				if(zutatenRezept != null){
					int i = 0;
					for(ZutatSpecification ZutatImRezept : zutatenRezept ){
						if(ZutatImRezept != null){
							
							for(String search2 : searches){
								if( search2.trim().length() <= ZutatImRezept.getName().length() &&  ZutatImRezept.getName().substring(0, search2.trim().length()).compareToIgnoreCase(search2) == 0){
								//if (getLevenshteinDistance(ZutatImRezept.getName(),search2) < 2){
									i++;
								}
							}
							if(i == searches.length){
								if(!FoundRezepte.contains(rezept) && !FoundRezepteYours.contains(rezept)){
									if(yours){
										FoundRezepteYours.add(rezept);
									} else {
										FoundRezepte.add(rezept);
									}
									displayRezept(rezept,yours);
								}
							}
						}

					}
				}
			}
		}
		}
	}

	
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
	
	private static void sortResults() {
		
		
		switch(sortMethod){
		case 1:{
			//"co2-value"
			Collections.sort(FoundIngredient,new ValueComparator());
			table.removeAllRows();
			if(FoundIngredient != null){
				for (final Ingredient item : FoundIngredient){
					displayZutat(item);
				}
			}
			
			Collections.sort(FoundRezepte,new RezeptValueComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Rezept item : FoundRezepte){
					displayRezept(item,false);
				}
			}
			
			Collections.sort(FoundRezepteYours,new RezeptValueComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Rezept item : FoundRezepteYours){
					displayRezept(item,true);
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
					displayZutat(item);
				}
			}
			
			Collections.sort(FoundRezepte,new RezeptNameComparator());
			tableMeals.removeAllRows();
			if(FoundRezepte != null){
				for (final Rezept item : FoundRezepte){
					displayRezept(item,false);
				}
			}
			
			Collections.sort(FoundRezepteYours,new RezeptNameComparator());
			tableMealsYours.removeAllRows();
			if(FoundRezepteYours != null){
				for (final Rezept item : FoundRezepteYours){
					displayRezept(item,true);
				}
			}
			
			break;

			
			
		}
		}
		
	}

	

	public static void displayRezept(final Rezept rezept, boolean yours) {


		Button AddRezeptButton = new Button(" + ");
		AddRezeptButton.addStyleDependentName("gwt-Button");
		AddRezeptButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				EaternityRechner.ShowRezept(rezept);
			}
		});
		
		
		if(yours){
			final int row = tableMealsYours.getRowCount();
			
			Button removeRezeptButton = new Button(" - ");
			removeRezeptButton.addStyleDependentName("gwt-Button");
			removeRezeptButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					final ConfirmDialog dlg = new ConfirmDialog("Sie wollen dieses Rezept...");
					dlg.statusLabel.setText("löschen?");
					// TODO recheck user if he really want to do this...
					dlg.executeButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							EaternityRechner.removeRezept(rezept);
							tableMealsYours.removeCells(row, 0, tableMealsYours.getCellCount(row));
							dlg.hide();
							dlg.clear();
						}
					});
					dlg.show();
					dlg.center();

					
				}
			});
			
			tableMealsYours.setText(row,0,rezept.getSymbol());
			tableMealsYours.setWidget(row, 2, AddRezeptButton);
			tableMealsYours.setWidget(row, 3, removeRezeptButton);
			if(rezept.isOpen()){
				tableMealsYours.setText(row, 4,"o");
			}
			rezept.setCO2Value();
//			double MenuLabelWert = 0.0;
//			for (ZutatSpecification zutatSpec : rezept.Zutaten) { 
//				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
//			}
			String formatted = NumberFormat.getFormat("##").format(rezept.getCO2Value());
			tableMealsYours.setText(row, 1,  "ca "+formatted+"g *");
			
			if ((row % 2) == 1) {
				String style = evenStyleRow.evenRow();
				tableMealsYours.getRowFormatter().addStyleName(row, style);
			}
			
		}else{
			int row = tableMeals.getRowCount();
			tableMeals.setText(row,0,rezept.getSymbol());
			tableMeals.setWidget(row, 2, AddRezeptButton);
			
			double MenuLabelWert = 0.0;
			for (ZutatSpecification zutatSpec : rezept.Zutaten) { 
				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
			}
			String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
//			tableMeals.setText(row, 2,  "ca "+formatted+"g CO₂-Äquivalent");
			tableMeals.setText(row, 1,  "ca "+formatted+"g *");
			
			if ((row % 2) == 1) {
				String style = evenStyleRow.evenRow();
				tableMeals.getRowFormatter().addStyleName(row, style);
			}
		}
		
		


	}


	public static void displayZutat(final Ingredient zutat2) {
		int row = table.getRowCount();
		
		if ((row % 2) == 1) {
			String style = evenStyleRow.evenRow();
			table.getRowFormatter().addStyleName(row, style);
		}

		if(zutat2.noAlternative){
			table.setText(row,0,zutat2.getSymbol());
		} else {
			table.setText(row,0,"oder : " + zutat2.getSymbol());
		}
		table.setText(row, 1, "ca "+Integer.toString((int) zutat2.getCo2eValue()/10).concat("g *"));



//		Button AddZutatButton = new Button(" + ");
//		AddZutatButton.addStyleDependentName("gwt-Button");
//		AddZutatButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				int row = EaternityRechner.AddZutatZumMenu(zutat);
//				// TODO uncomment this:
//				//EaternityRechner.selectRow(row);
//			}
//		});
//		table.setWidget(row, 0, AddZutatButton);

	}

	

	public void setFoundRezepte(ArrayList<Rezept> foundRezepte) {
		FoundRezepte = foundRezepte;
	}

	public ArrayList<Rezept> getFoundRezepte() {
		return FoundRezepte;
	}

	
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

class NameComparator implements Comparator<Ingredient> {
	  public int compare(Ingredient z1, Ingredient z2) {
		  String o1 = z1.getSymbol();
		  String o2 = z2.getSymbol();
	    if(o1 instanceof String && o2 instanceof String) {
	      String s1 = (String)o1;
	      String s2 = (String)o2;
	      s1 = s1.substring(0, 1);
	      s2 = s2.substring(0, 1);
	      return s1.compareToIgnoreCase(s2);
	    }
	    return 0;
	  }
	}

	class ValueComparator implements Comparator<Ingredient> {
	  public int compare(Ingredient z1, Ingredient z2) {
		  long o1 = z1.getCo2eValue();
		  long o2 = z2.getCo2eValue();
		  
	    return -Long.valueOf(o2).compareTo(Long.valueOf(o1));
	  }
	}
	
	class RezeptValueComparator implements Comparator<Rezept> {
		public int compare(Rezept r1, Rezept r2) {
			Double o1 = getRezeptCO2(r1.getZutaten());
			Double o2 = getRezeptCO2(r2.getZutaten());

			return -Double.valueOf(o2).compareTo(Double.valueOf(o1));

		}

		private Double getRezeptCO2(List<ZutatSpecification> Zutaten) {
			Double MenuLabelWert = 0.0;
			for (ZutatSpecification zutatSpec : Zutaten) { 
				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();

			}
			return MenuLabelWert;
		}
	}
	
	class RezeptNameComparator implements Comparator<Rezept> {
		  public int compare(Rezept z1, Rezept z2) {
			  String o1 = z1.getSymbol();
			  String o2 = z2.getSymbol();
		    if(o1 instanceof String && o2 instanceof String) {
		      String s1 = (String)o1;
		      String s2 = (String)o2;
		      s1 = s1.substring(0, 1);
		      s2 = s2.substring(0, 1);
		      return s1.compareToIgnoreCase(s2);
		    }
		    return 0;
		  }
		}