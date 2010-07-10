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
import java.util.List;
import java.util.Set;


import java.util.TreeSet;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
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
		void onItemSelected(Zutat item);
	}

	interface Binder extends UiBinder<Widget, Search> { }
	interface SelectionStyle extends CssResource {
		String selectedRow();
	}



	private static final Binder binder = GWT.create(Binder.class);
	static final int VISIBLE_EMAIL_COUNT = 4;

	@UiField
	static FlexTable table;
	@UiField
	static FlexTable tableMeals;
//	@UiField Button SearchButton;
	@UiField SuggestBox SearchBox2;
	@UiField DockLayoutPanel SearchBox;
	@UiField
	static SelectionStyle selectionStyle;
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField
	static DockLayoutPanel leftSplitPanel;
	//@UiField
	//static InfoZutat infoZutat;
	
	
	private static Data clientData = new Data();
	private static ArrayList<Rezept> FoundRezepte = new ArrayList<Rezept>();
	private static ArrayList<Zutat> FoundZutaten = new ArrayList<Zutat>();

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
	@UiHandler("SearchBox2")
	public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {

			

			// simulate button press
//			SearchButton.setEnabled(false);
			Timer t = new Timer() {
				public void run() {
//					SearchButton.setEnabled(true);
					updateResults(SearchBox2.getText());
				}
			};
			t.schedule(200);
		}
	}



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
		table.getColumnFormatter().setWidth(0, "36px");
		table.getColumnFormatter().setWidth(1, "102px");
		
	}

	/**
	 * Selects the given row (relative to the current page).
	 * 
	 * @param row the row to be selected
	 */
	private void selectRow(int row) {

		
		//TODO uncomment this:
		//leftSplitPanel.setWidgetMinSize(infoZutat, 448);
		//    Zutat item = MailItems.getMailItemName(table.getText(row, 1));
		if (FoundZutaten.size() < row){
			return;
		}
		
		Zutat item = FoundZutaten.get(row);

		if (item == null) {
			return;
		}

		
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



		styleRow(selectedRow, false);
		
		//TODO uncomment this:
		//if(EaternityRechner.MenuTable.getRowCount() > 0){
		//	EaternityRechner.styleRow(EaternityRechner.selectedRow,false);
		//	EaternityRechner.selectedRow = -1;
		//}
		
		styleRow(row, true);

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
		
		FoundZutaten.clear();
		FoundRezepte.clear();
		
			if ((getClientData().getZutaten() != null) ){
				
				// Zutaten
				if(searchString.trim().length() != 0){
					
				String[] searches = searchString.split(" ");
				
				for(String search : searches){
	
					// Zutaten
					for(Zutat zutat : getClientData().getZutaten())
						if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
						//if(,search) < 3){
							//Window.alert(zutat.getSymbol().substring(0, search.trim().length()));
							if(!FoundZutaten.contains(zutat)){
								FoundZutaten.add(zutat);
								displayZutat(zutat);
							}
							for(Long alternativen_id : zutat.getAlternativen()){
								for(Zutat zutat2 : getClientData().getZutaten()){
									if(zutat2.getId().equals(alternativen_id)){
										if(!FoundZutaten.contains(zutat2)){
											FoundZutaten.add(zutat2);
											displayZutat(zutat2);
										}
									}
								}

							}
							
						}
					}
				// Rezepte
				List<Rezept> allRezepte = getClientData().getPublicRezepte();
				if(	getClientData().getYourRezepte() != null){
					allRezepte.addAll(getClientData().getYourRezepte());
				}
				
				for(Rezept rezept : allRezepte){
					if(rezept != null){
						if( getLevenshteinDistance(rezept.getSymbol(),searchString) < 5){
							if(!FoundRezepte.contains(rezept)){
								// Rezept zu Rezeptsuche
								FoundRezepte.add(rezept);
								displayRezept(rezept);
								
								// Zutaten des Rezept Ergerbnis Liste der Zutaten
								for(ZutatSpecification zutatSpSuche : rezept.getZutaten() ){
									for(Zutat zutatSuche : getClientData().getZutaten() ){
										if(zutatSpSuche.getZutat_id().equals(zutatSuche.getId() )){
											if(!FoundZutaten.contains(zutatSuche)){
												FoundZutaten.add(zutatSuche);
												displayZutat(zutatSuche);
											}
										}

									}

								}
							}
						}

						List<ZutatSpecification> zutatenRezept = rezept.getZutaten();
						if(zutatenRezept != null){
							for(ZutatSpecification ZutatImRezept : zutatenRezept ){
								if(ZutatImRezept != null){
									int i = 0;
									for(String search2 : searches){
										if (getLevenshteinDistance(ZutatImRezept.getName(),search2) < 2){
											i++;
										}
									}
									if(i == searches.length){
										if(!FoundRezepte.contains(rezept)){
											FoundRezepte.add(rezept);
											displayRezept(rezept);
										}
									}
								}

							}
						}
					}
				}
				
				} 
				 else {
				
					for(Zutat zutat : getClientData().getZutaten()){
						if(!FoundZutaten.contains(zutat)){
							FoundZutaten.add(zutat);
						displayZutat(zutat);
						}
					}
				}
			}
			}

	
	
	private void updateResults_old(String searchString) {
		// Clear any remaining slots.
		table.removeAllRows();
		tableMeals.removeAllRows();
		//	
		//    for (int a= 0; a < table.getRowCount(); ++a) {
		//      table.removeRow(table.getRowCount() - 1);
		//    }

		//	if(getClientData().getOrcaleIndex().contains(search)){
//		ArrayList<Rezept> FoundRezepte = new ArrayList<Rezept>();
//		ArrayList<Zutat> FoundOnes = new ArrayList<Zutat>();
		FoundZutaten.clear();
		FoundRezepte.clear();
		
		if(getClientData().getZutaten() != null){

			String[] searches = searchString.split(" ");
			for(String search : searches){

				// Zutaten
				for(Zutat zutat : getClientData().getZutaten()){
					if( getLevenshteinDistance(zutat.getSymbol(),search) < 3){
						if(!FoundZutaten.contains(zutat)){
							FoundZutaten.add(zutat);
							displayZutat(zutat);
						}
						for(Long alternativen_id : zutat.getAlternativen()){
							for(Zutat zutat2 : getClientData().getZutaten()){
								if(zutat2.getId().equals(alternativen_id)){
									if(!FoundZutaten.contains(zutat2)){
										FoundZutaten.add(zutat2);
										displayZutat(zutat2);
									}
								}
							}

						}
					}
				}

				// Rezepte
				List<Rezept> allRezepte = getClientData().getPublicRezepte();
				if(	getClientData().getYourRezepte() != null){
					allRezepte.addAll(getClientData().getYourRezepte());
				}
				
				for(Rezept rezept : allRezepte){
					if(rezept != null){
						if( getLevenshteinDistance(rezept.getSymbol(),searchString) < 5){
							if(!FoundRezepte.contains(rezept)){
								// Rezept zu Rezeptsuche
								FoundRezepte.add(rezept);
								displayRezept(rezept);
								
								// Zutaten des Rezept Ergerbnis Liste der Zutaten
								for(ZutatSpecification zutatSpSuche : rezept.getZutaten() ){
									for(Zutat zutatSuche : getClientData().getZutaten() ){
										if(zutatSpSuche.getZutat_id().equals(zutatSuche.getId() )){
											if(!FoundZutaten.contains(zutatSuche)){
												FoundZutaten.add(zutatSuche);
												displayZutat(zutatSuche);
											}
										}

									}

								}
							}
						}

						List<ZutatSpecification> zutatenRezept = rezept.getZutaten();
						if(zutatenRezept != null){
							for(ZutatSpecification ZutatImRezept : zutatenRezept ){
								if(ZutatImRezept != null){
									int i = 0;
									for(String search2 : searches){
										if (getLevenshteinDistance(ZutatImRezept.getName(),search2) < 2){
											i++;
										}
									}
									if(i == searches.length){
										if(!FoundRezepte.contains(rezept)){
											FoundRezepte.add(rezept);
											displayRezept(rezept);
										}
									}
								}

							}
						}
					}
				}


			}
		}


//		if(FoundOnes != null){
//			for (final Zutat item : FoundOnes){
//				displayZutat(item);
//			}
//		}
//
//		if(FoundRezepte != null){
//			for (final Rezept item : FoundRezepte){
//				displayRezept(item);
//			}
//		}

	}

	public static void displayRezept(final Rezept rezept) {
		int row = tableMeals.getRowCount();
		tableMeals.setText(row,1,rezept.getSymbol());

		Button AddRezeptButton = new Button(" + ");
		AddRezeptButton.addStyleDependentName("gwt-Button");
		AddRezeptButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

//				Rezept test = rezept;
				
				EaternityRechner.ShowRezept(rezept);
				//		    int removedIndex = item.indexOf(item.sender);
				//		    item.remove(removedIndex);
				//		    table.removeRow(removedIndex + 1);
			}
		});
		tableMeals.setWidget(row, 0, AddRezeptButton);
	}


	public static void displayZutat(final Zutat zutat) {
		int row = table.getRowCount();
		table.setText(row,1,zutat.getSymbol());
		table.setText(row, 2, "ca. "+Integer.toString((int) zutat.getCO2eWert()).concat("g CO₂-Äquivalent pro 100g"));



		Button AddZutatButton = new Button(" + ");
		AddZutatButton.addStyleDependentName("gwt-Button");
		AddZutatButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = EaternityRechner.AddZutatZumMenu(zutat);
				// TODO uncomment this:
				//EaternityRechner.selectRow(row);
			}
		});
		table.setWidget(row, 0, AddZutatButton);

	}

	

	public void setFoundRezepte(ArrayList<Rezept> foundRezepte) {
		FoundRezepte = foundRezepte;
	}

	public ArrayList<Rezept> getFoundRezepte() {
		return FoundRezepte;
	}

	public void setFoundZutaten(ArrayList<Zutat> foundZutaten) {
		FoundZutaten = foundZutaten;
	}

	public ArrayList<Zutat> getFoundZutaten() {
		return FoundZutaten;
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
}
