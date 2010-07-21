package ch.eaternity.client;

import java.util.ArrayList;
import java.util.List;


import ch.eaternity.shared.Data;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.Zutat;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class RezeptView extends Composite {
	interface Binder extends UiBinder<Widget, RezeptView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField SelectionStyleRow selectionStyleRow;

	@UiField FlexTable MenuTable;
	@UiField HTMLPanel SaveRezeptPanel;
	@UiField Button RezeptButton;
	@UiField TextBox RezeptName;
	@UiField CheckBox makePublic;
	@UiField FlexTable SuggestTable;
	@UiField HorizontalPanel addInfoPanel;
	@UiField Button removeRezeptButton;
	@UiField HTMLPanel htmlRezept;
	@UiField Label rezeptNameTop;
	@UiField Button acceptZutatButton;
	HandlerRegistration klicky;

	
//	static ArrayList<ZutatSpecification> zutatImMenu = new ArrayList<ZutatSpecification>();
	
	
	public RezeptView(Rezept rezept) {
	    // does this need to be here?
	    initWidget(uiBinder.createAndBindUi(this));
	    setRezept(rezept);
	    initTable();
	    
	    acceptZutatButton.setVisible(false);
		if(EaternityRechner.loginInfo.isLoggedIn()) {
			SaveRezeptPanel.setVisible(true);
		} else   {
			SaveRezeptPanel.setVisible(false);
		}
	  }
	
	
	public interface Listener {
		void onItemSelected(ZutatSpecification item);
	}


	
	interface SelectionStyleRow extends CssResource {
		String selectedRow();
	}
	private Listener listener;
	int  selectedRow = 0;
	int  selectedRezept = -1;
	private Rezept rezept;
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	
	@UiHandler("MenuTable")
	void onTableClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = MenuTable.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			selectRow(row);
		}
	}
	
	@UiHandler("removeRezeptButton")
	void onRemoveClicked(ClickEvent event) {
		
		final ConfirmDialog dlg = new ConfirmDialog("Sie wollen diese Zusammenstellung...");
		dlg.statusLabel.setText("entfernen?");
		// TODO recheck user if he really want to do this...
		final RezeptView test = this;
		dlg.executeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = getWidgetRow(test , EaternityRechner.rezeptList);
				EaternityRechner.rezeptList.remove(test);
				EaternityRechner.rezeptList.removeRow(row);
				EaternityRechner.selectedRezept = -1;
				dlg.hide();
				dlg.clear();
			}
		});
		dlg.show();
		dlg.center();
		

		

	}
	


	
	private void initTable() {
		MenuTable.getColumnFormatter().setWidth(0, "40px");
		MenuTable.getColumnFormatter().setWidth(1, "76px");
		
	}
	
	public void setRezept(Rezept rezept){
		this.rezept = rezept;
		showRezept(rezept);
	}

	public Rezept getRezept(){
		return this.rezept;
	}	
	
	public void showRezept(final Rezept rezept) {
			displayZutatImMenu(rezept.Zutaten);
			updateSuggestion();
//			zutatImMenu.clear();
			
//			int row = AddZutatZumMenu(rezept.getZutaten());
			// add Speicher Rezept Button
			if(klicky != null){
				klicky.removeHandler();
			}
			
			klicky = RezeptButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if(RezeptName.getText() != ""){
//						Speichere Rezept ab. 
//						Rezept rezeptSave = new Rezept(RezeptName.getText());
//						rezeptSave.setOpen(makePublic.getValue());
//						rezeptSave.addZutaten(rezept.getZutaten());
//						EaternityRechner.addRezept(rezeptSave);
						rezept.setSymbol(RezeptName.getText());
						rezept.setOpen(makePublic.getValue());
						
						EaternityRechner.addRezept(rezept);
					}
				}
			});
		
	}

	
	

	 void selectRow(int row) {
		
		//TODO uncomment this:
		//Search.leftSplitPanel.setWidgetMinSize(Search.infoZutat, 448);
//		Window.alert(Integer.toString(row));
		

		 
		 if(selectedRow != -1 && addInfoPanel.getWidgetCount() ==2){
			 InfoZutatDialog infoDialog = (InfoZutatDialog)(addInfoPanel.getWidget(1));
			 ZutatSpecification zutatSpec2 = infoDialog.getZutatSpec();
//			 int index = zutatImMenu.indexOf(zutatSpec);
//			 zutatImMenu = (ArrayList<ZutatSpecification>) rezept.getZutaten();
			 rezept.Zutaten.set(selectedRow , zutatSpec2);

		 }
		 
		ZutatSpecification zutatSpec = rezept.Zutaten.get(row);

		if (zutatSpec == null) {
			return;
		}
		
		Long ParentZutatId = zutatSpec.getZutat_id();
		Zutat zutat = Search.getClientData().getZutatByID(ParentZutatId);
		
		openSpecificationDialog(zutatSpec,zutat, (TextBox) MenuTable.getWidget(row, 0), MenuTable,row);
		//InfoZutat.setZutat(item, clientDataHere.getZutatByID(ParentZutatId),row);
//
//		infoZutat.stylePanel(true);

		styleRow(selectedRow, false);
		
//		Search.styleRow(Search.selectedRow,false);
		Search.selectedRow = -1;
		
		styleRow(row, true);

		selectedRow = row;

		if (listener != null) {
			listener.onItemSelected(zutatSpec);
		}
		
		updateSuggestion();
	}

	private void openSpecificationDialog(ZutatSpecification zutatSpec, Zutat zutat,  TextBox amount,FlexTable MenuTable,int selectedRow) {
		// TODO Auto-generated method stub
		
		if(addInfoPanel.getWidgetCount() ==2){
			addInfoPanel.remove(1);
		}
		InfoZutatDialog infoZutat = new InfoZutatDialog(zutatSpec,zutat,amount,MenuTable,selectedRow);
		addInfoPanel.add(infoZutat);
		acceptZutatButton.setVisible(true);
		
	}
	
	@UiHandler("acceptZutatButton")
	void onAcceptClicked(ClickEvent event) {
		styleRow(selectedRow, false);
		if(addInfoPanel.getWidgetCount() ==2){
			addInfoPanel.remove(1);
		}
		acceptZutatButton.setVisible(false);
		updateSuggestion();
	}
	

	//TODO do the same for Search BUtton Press
	void styleRow(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyleRow.selectedRow();

			if (selected) {
				MenuTable.getRowFormatter().addStyleName(row, style);
			} else {
				MenuTable.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	


	
	private void displayZutatImMenu( List<ZutatSpecification> zutaten) {
	
	MenuTable.removeAllRows();;
	int row = MenuTable.getRowCount();
	
	for(final ZutatSpecification zutat : zutaten){

	Button removeZutat = new Button("x");
//	removeZutat.addStyleName("style.gwt-Button");
//	removeZutat.addStyleDependentName("gwt-Button");
	removeZutat.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
			int removedIndex = rezept.Zutaten.indexOf(zutat);
			rezept.Zutaten.remove(removedIndex);
			MenuTable.removeRow(removedIndex);
//			rezept.removeZutat(removedIndex);
			updateSuggestion();
		}
	});
	
	final TextBox MengeZutat = new TextBox();
	MengeZutat.setText(Integer.toString(zutat.getMengeGramm()));
	MengeZutat.setWidth("35px");
	
	MengeZutat.addKeyUpHandler( new KeyUpHandler() {
		public void onKeyUp(KeyUpEvent event) {
			int keyCode = event.getNativeKeyCode();
			if ((!Character.isDigit((char) keyCode)) && (keyCode != KeyCodes.KEY_TAB)
					&& (keyCode != KeyCodes.KEY_BACKSPACE)
					&& (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) 
					&& (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
					&& (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
					&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN)) {
				// TextBox.cancelKey() suppresses the current keyboard event.
				MengeZutat.cancelKey();
			} else {
				String MengeZutatWert;
				int rowhere = getWidgetRow(MengeZutat,MenuTable);
				if(MengeZutat.getText() != ""){
					MengeZutatWert = MengeZutat.getText();
					zutat.setMengeGramm(Integer.valueOf(MengeZutatWert));
				} else {
					MengeZutatWert = "0";
				}
				updateTable(rowhere,zutat);
//				int length = (int)  Math.round(Double.valueOf(MengeZutatWert).doubleValue() *0.001);
//				MenuTable.setText(rowhere,3,"ca. "+ Double.toString(zutatSpec.getCalculatedCO2Value()).concat("g CO₂-Äquivalent"));
//				MenuTable.setHTML(rowhere, 4, "<div style='background:#ff0;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/1000).concat("px'>.</div>")));
//				updateSuggestion();
			}


		}


	});

	//Name
	MenuTable.setWidget(row, 0, MengeZutat);
	MenuTable.setText(row, 1, "g " + zutat.getName());
	MenuTable.setWidget(row, 6, removeZutat);
	// Remove Button

	


	
	
//	int length = (int) Math.round(zutatSpec.getCalculatedCO2Value());
//	//	Menge CO2 Äquivalent
//	MenuTable.setText(row,3,Integer.toString(length).concat("g CO₂-Äquivalent"));
//
//	MenuTable.setHTML(row, 4, "<div style='background:#ff0;width:".concat(Integer.toString(length/1000)).concat("px'>.</div>"));
	
	updateTable(row,zutat);
//	MenuTable.setText(row,3,"ca. "+ Double.toString(zutatSpec.getCalculatedCO2Value()).concat("g CO₂-Äquivalent"));
//	MenuTable.setHTML(row, 4, "<div style='background:#ff0;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/1000).concat("px'>.</div>")));
//	updateSuggestion();
	row = row+1;
	}
}
	void updateSuggestion() {

		Double MenuLabelWert = 0.0;
		Double MaxMenuWert = 0.0;

		if(rezept.Zutaten.isEmpty()){
			if(addInfoPanel.getWidgetCount() ==2){
				addInfoPanel.remove(1);
			}
		}
		
		for (ZutatSpecification zutatSpec : rezept.Zutaten) { 
			MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
			if(zutatSpec.getCalculatedCO2Value()>MaxMenuWert){
				MaxMenuWert = zutatSpec.getCalculatedCO2Value();
			}
			
		}
		for (ZutatSpecification zutatSpec : rezept.Zutaten) { 	
			MenuTable.setHTML(rezept.Zutaten.indexOf(zutatSpec), 8, " <div style='background:#ff0;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/MaxMenuWert*100).concat("px'>.</div>")));
		}
		
		String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
		
		SuggestTable.setWidth("300px");
		SuggestTable.setText(0,0," alles zusammen: ca "+formatted+"g CO₂-Äquivalent");

	}
	
	private void updateTable(int row,ZutatSpecification zutatSpec){
		String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
		
		MenuTable.getColumnFormatter().setWidth(4, "180px");
		MenuTable.setText(row,4,": ca. "+formatted+"g CO₂-Äquivalent ");
//		MenuTable.setHTML(row, 8, " <div style='background:#ff0;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/100).concat("px'>.</div>")));
		updateSuggestion();
	}
	
	private static int getWidgetRow(Widget widget, FlexTable table) {
		for (int row = 0; row < table.getRowCount(); row++) {
			for (int col = 0; col < table.getCellCount(row); col++) {
				Widget w = table.getWidget(row, col);
				if (w == widget) {
					return row;
				}
			}
		}
		throw new RuntimeException("Unable to determine widget row");
	}


	public void updateSaison() {
		if(addInfoPanel.getWidgetCount() ==2){
			InfoZutatDialog infoZutat = (InfoZutatDialog) addInfoPanel.getWidget(1);
			 infoZutat.updateSaison(infoZutat.zutatSpec);
		 }
	}
}
