package ch.eaternity.client;

import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.Utils;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



import ch.eaternity.client.widgets.PhotoGallery;
import ch.eaternity.client.widgets.UploadPhoto;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.ZutatSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class RezeptView extends Composite {
	interface Binder extends UiBinder<Widget, RezeptView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField SelectionStyleRow selectionStyleRow;
	@UiField EvenStyleRow evenStyleRow;

	@UiField FlexTable MenuTable;
	@UiField HTMLPanel SaveRezeptPanel;
	@UiField HTMLPanel topStatusBar;
	@UiField
	public FlowPanel menuDecoInfo;
	@UiField Button RezeptButton;
	@UiField TextBox RezeptName;
	@UiField CheckBox makePublic;
	@UiField FlexTable SuggestTable;
	@UiField HorizontalPanel addInfoPanel;
	@UiField Button removeRezeptButton;
	@UiField HTMLPanel htmlRezept;
	@UiField HTMLPanel rezeptTitle;
	@UiField Label rezeptNameTop;
	@UiField Label rezeptSubTitleTop;
	@UiField HTML topIndikator;
	@UiField HTML bottomIndikator;
//	@UiField HorizontalPanel imageUploaderHP;
	@UiField TextArea cookingInstr;
	@UiField TextBox amountPersons;
	@UiField TextBox rezeptDetails;
	
	@UiField HTML titleHTML;
	@UiField HTML openHTML;
	@UiField HTML savedHTML;
	
	private FlowPanel panelImages = new FlowPanel();
	private PhotoGallery galleryWidget;
	private UploadPhoto uploadWidget;
	
	HTML htmlCooking;
	
	HandlerRegistration klicky;
	
	boolean saved;
	
	private Listener listener;
	int  selectedRow = 0;
	int  selectedRezept = -1;
	Rezept rezept;

	
//	static ArrayList<ZutatSpecification> zutatImMenu = new ArrayList<ZutatSpecification>();
	
	
	public RezeptView(Rezept rezept) {
	    // does this need to be here?
	    initWidget(uiBinder.createAndBindUi(this));
	    setRezept(rezept);
	    saved = true;
	    initTable();
	    
	    RezeptName.setVisible(false);
	    rezeptDetails.setVisible(false);
	    cookingInstr.setVisible(false);
	    
//	    galleryWidget = new PhotoGallery(this);
////	    addInfoPanel.insert(galleryWidget,0);
//	    menuDecoInfo.add(galleryWidget);
	    
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			uploadWidget = new UploadPhoto(EaternityRechner.loginInfo, this);
			uploadWidget.setStyleName("notInline");
			
			// Bind it to event so uploadWidget can refresh the gallery
//			uploadWidget.addGalleryUpdatedEventHandler(galleryWidget);
//			addInfoPanel.insert(uploadWidget,0);
			menuDecoInfo.add(uploadWidget);
		}
		
		
	    
//	    imageUploaderHP.add(panelImages);
//	    MultiUploader defaultUploader = new MultiUploader();
//	    imageUploaderHP.add(defaultUploader);
//	    defaultUploader.avoidRepeatFiles(true);
//	    defaultUploader.setValidExtensions(new String[] { "jpg", "jpeg", "png", "gif" });
//	    defaultUploader.setMaximumFiles(1);
//	    defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

	    
		if(EaternityRechner.loginInfo.isLoggedIn()) {
			// TODO even more....
			SaveRezeptPanel.setVisible(true);
			topStatusBar.setVisible(true);
		} else   {
			SaveRezeptPanel.setVisible(false);
			topStatusBar.setVisible(false);
		}
	  }
	
	
	public interface Listener {
		void onItemSelected(ZutatSpecification item);
	}


	
	interface SelectionStyleRow extends CssResource {
		String selectedRow();
	}
	interface EvenStyleRow extends CssResource {
		String evenRow();
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	@UiHandler("amountPersons")
	void onKeyUp(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();
		if ((Character.isDigit((char) keyCode)) 
				|| (keyCode == KeyCodes.KEY_BACKSPACE)
				|| (keyCode == KeyCodes.KEY_DELETE) ) {
			// TextBox.cancelKey() suppresses the current keyboard event.
			Long persons = 1l;
			rezept.setPersons(persons);
			if(!amountPersons.getText().isEmpty()){
				persons = Long.parseLong(amountPersons.getText().trim());
				if(persons > 0){
					rezept.setPersons(persons);
					updateSuggestion();
				} else {
//					amountPersons.setText("1");
				}
			} else {
//				amountPersons.setText("1");
			}
			
			
		} else {
			amountPersons.cancelKey();
		}
	}


	@UiHandler("rezeptNameTop")
	void onMouseOver(MouseOverEvent event) {
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			RezeptName.setVisible(true);
			rezeptNameTop.setVisible(false);
		}
	}
	
	@UiHandler("RezeptName")
	void onEdit(KeyUpEvent event) {
		if(RezeptName.getText() != ""){
			rezeptNameTop.setText(RezeptName.getText());
			rezept.setSymbol(RezeptName.getText());
		}
	}
	
	@UiHandler("RezeptName")
	void onMouseOut(MouseOutEvent event) {
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			RezeptName.setVisible(false);
			rezeptNameTop.setVisible(true);
		}
	}
	
	@UiHandler("cookingInstr")
	void onEditCook(KeyUpEvent event) {
		if(cookingInstr.getText() != ""){
			htmlCooking.setText(cookingInstr.getText());
			rezept.setCookInstruction(cookingInstr.getText());
		}
	}
	
	@UiHandler("cookingInstr")
	void onMouseOutCook(MouseOutEvent event) {
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			cookingInstr.setVisible(false);
			htmlCooking.setVisible(true);
		}
	}
	

	@UiHandler("rezeptSubTitleTop")
	void onMouseOverSub(MouseOverEvent event) {
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			rezeptDetails.setVisible(true);
			rezeptSubTitleTop.setVisible(false);
		}
	}
	
	@UiHandler("rezeptDetails")
	void onEditSub(KeyUpEvent event) {
		if(rezeptDetails.getText() != ""){
			rezeptSubTitleTop.setText(rezeptDetails.getText());
			rezept.setSubTitle(rezeptDetails.getText());
		}
	}
	
	@UiHandler("rezeptDetails")
	void onMouseOutSub(MouseOutEvent event) {
		if (EaternityRechner.loginInfo.isLoggedIn()) {
			rezeptDetails.setVisible(false);
			rezeptSubTitleTop.setVisible(true);
		}
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
		final RezeptView test = this;
		if(saved){
			int row = getWidgetRow(test , EaternityRechner.rezeptList);
			EaternityRechner.rezeptList.remove(test);
			EaternityRechner.rezeptList.removeRow(row);
			EaternityRechner.selectedRezept = -1;
			EaternityRechner.suggestionPanel.clear();
		} else {
		final ConfirmDialog dlg = new ConfirmDialog("Diese Zusammenstellungen wurde noch nicht gespeichert!");
		dlg.statusLabel.setText("Zusammenstellung trotzdem ausblenden?");
		// TODO recheck user if he really want to do this...
		
		dlg.executeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = getWidgetRow(test , EaternityRechner.rezeptList);
				EaternityRechner.rezeptList.remove(test);
				EaternityRechner.rezeptList.removeRow(row);
				EaternityRechner.selectedRezept = -1;
				EaternityRechner.suggestionPanel.clear();
				dlg.hide();
			}
		});
		dlg.show();
		dlg.center();
		}
	}
	


	
	private void initTable() {
		MenuTable.getColumnFormatter().setWidth(0, "40px");
		MenuTable.getColumnFormatter().setWidth(1, "140px");
		MenuTable.setCellPadding(1);
		
	    if(rezept.getCookInstruction() != null){
	    	htmlCooking = new HTML(rezept.getCookInstruction());
	    } else {
	    	htmlCooking = new HTML("Kochanleitung.");
	    }
	    	htmlCooking.addStyleName("cookingInstr");
	    	menuDecoInfo.insert(htmlCooking,0);
	    	
	    	
	    	htmlCooking.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					// TODO Auto-generated method stub
					if (EaternityRechner.loginInfo.isLoggedIn()) {
						cookingInstr.setVisible(true);
						htmlCooking.setVisible(false);
					}
				}
			});
	    	
	    
	}
	
	public void setRezept(Rezept rezept){
		this.rezept = rezept;
//		showRezept(rezept);
	}

	public Rezept getRezept(){
		return this.rezept;
	}	
	
	public void showRezept(final Rezept rezept) {

			if(rezept.getPersons() != null){
				amountPersons.setText(rezept.getPersons().toString());
			} else {
				amountPersons.setText("4");
				Long persons = Long.parseLong(amountPersons.getText());
				rezept.setPersons(persons);
			}
			final RezeptView rezeptView = this;
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
						// TODO warn that it wasn't saved in the other case
						amountPersons.setText(rezept.getPersons().toString());
//						Speichere Rezept ab. 
//						Rezept rezeptSave = new Rezept(RezeptName.getText());
//						rezeptSave.setOpen(makePublic.getValue());
//						rezeptSave.addZutaten(rezept.getZutaten());
//						EaternityRechner.addRezept(rezeptSave);
						rezept.setSymbol(RezeptName.getText());
						if(rezeptDetails.getText() != ""){
							rezept.setSubTitle(rezeptDetails.getText());
						} else {
							rezept.setSubTitle("just like that");
						}
						rezept.openRequested = !makePublic.getValue();
						rezept.open = false;
						rezept.setCookInstruction(cookingInstr.getText()); 
						
						EaternityRechner.addRezept(rezept,rezeptView);
					}
				}
			});
		
	}

	
	

	 void selectRow(int row) {
		
		//TODO uncomment this:
		//Search.leftSplitPanel.setWidgetMinSize(Search.infoZutat, 448);
//		Window.alert(Integer.toString(row));
		
		 saved = false;
		 
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
		Ingredient zutat = Search.getClientData().getIngredientByID(ParentZutatId);
		
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

	private void openSpecificationDialog(ZutatSpecification zutatSpec, Ingredient zutat,  TextBox amount,FlexTable MenuTable,int selectedRow) {
		// TODO Auto-generated method stub
		
//		if(addInfoPanel.getWidgetCount() ==2){
			addInfoPanel.remove(2);
//		}
		menuDecoInfo.setVisible(false);
		InfoZutatDialog infoZutat = new InfoZutatDialog(zutatSpec,zutat,amount,MenuTable,selectedRow,rezept,SuggestTable,this);
//		addInfoPanel.add(infoZutat);
		addInfoPanel.insert(infoZutat, 2);
//		addInfoPanel.add(new HTML("test"));
		
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
	


	
	private void displayZutatImMenu( ArrayList<ZutatSpecification> zutaten) {
	
	MenuTable.removeAllRows();;
	Integer row = MenuTable.getRowCount();
//	ArrayList<ZutatSpecification> zutatenNew = (ArrayList<ZutatSpecification>) zutaten.clone();
	for(final ZutatSpecification zutat : zutaten){
//		
	Button removeZutat = new Button("x");
//	removeZutat.addStyleName("style.gwt-Button");
//	removeZutat.addStyleDependentName("gwt-Button");
	removeZutat.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
			int removedIndex = rezept.Zutaten.indexOf(zutat);
			rezept.Zutaten.remove(removedIndex);
			MenuTable.removeRow(removedIndex);
//			rezept.removeZutat(removedIndex);
			
			// does this work to prevent the error?
			styleRow(removedIndex, false);
			
			if(selectedRow == removedIndex){
				if(addInfoPanel.getWidgetCount() ==2){
					addInfoPanel.remove(1);
				}
			} else {
				if(selectedRow > removedIndex){
					selectedRow = selectedRow-1;
					selectRow(selectedRow);
				}
			}
			
			// set the colors in the right order...
			String style = evenStyleRow.evenRow();
			for(Integer rowIndex = 0; rowIndex<MenuTable.getRowCount(); rowIndex++){
				if ((rowIndex % 2) == 1) {
					MenuTable.getRowFormatter().addStyleName(rowIndex, style);
				} else {
					MenuTable.getRowFormatter().removeStyleName(rowIndex, style);
				}
			}
			
			updateSuggestion();
		}
	});
	
	final TextBox MengeZutat = new TextBox();
	MengeZutat.setText(Integer.toString(zutat.getMengeGramm()));
	MengeZutat.setWidth("36px");
	
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
				if(!MengeZutat.getText().equalsIgnoreCase("")){
					MengeZutatWert = MengeZutat.getText().trim();
					zutat.setMengeGramm(Integer.valueOf(MengeZutatWert));
				} else {
					MengeZutatWert = "";
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
	

	if ((row % 2) == 1) {
		String style = evenStyleRow.evenRow();
		MenuTable.getRowFormatter().addStyleName(row, style);
	}
	MenuTable.setWidget(row, 0, MengeZutat);
	MenuTable.setText(row, 1, "g " + zutat.getName());
	MenuTable.setWidget(row, 5, removeZutat);
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
			String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
			MenuTable.setText(rezept.Zutaten.indexOf(zutatSpec),3,"ca "+formatted+"g *");
			MenuTable.setHTML(rezept.Zutaten.indexOf(zutatSpec), 4, "<div style='background:#A3C875;width:80px;height:1.1em;margin-right:5px;'><div style='background:#323533;height:1.1em;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/MaxMenuWert*80).concat("px'>.</div></div>")));
		}
		
		String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
		
		SuggestTable.setCellSpacing(2);
		SuggestTable.setText(0,0,"SUMME");
		SuggestTable.getColumnFormatter().setWidth(0, "175px");
		SuggestTable.setHTML(0,1,"ca <b>"+formatted+"g</b> *");
		SuggestTable.getColumnFormatter().setWidth(1, "180px");
		
		
		updtTopSuggestion();
		
		
		
	}


	public void updtTopSuggestion() {
		// TODO Algorithm for the Top Suggestions
		// Von jedem Gericht gibt es einen CO2 Wert für 4Personen (mit oder ohne Herkunft? oder aus der nächsten Distanz?), 
		// so wie sie gepeichert wurde.
		// Es werden bei der Anzeige Rezepte berücksichtigt, die: 
		// min 20% identische Zutaten ( Zutat*(Menge im Rezept)/StdMenge ) und das pro Zutat, und davon min 20%identisch
		// min +50% Zutaten die in den alternativen Vorkommen
		// hierbei wird die 2 passendsten Rezepte jeweils aus den nicht durch das markierte Rezept belegten Bereich angezeigt
		// Bereich sind 0-20%	20%-50%		50%-100%
		
		// Rezepte sollten sich bewerten lassen, und deren Popularität gemessen werden. ( Über die Zeit?)
		// 

		// diese Filter sollten in der Reihenfolge ausgeführt werden, in der sie am wenigsten Berechnungen benötigen:
		
		// TODO alle Rezepte für 4 Personen, sonst macht der Vergleich keinen Sinn
		
		// get Comparator
		ArrayList<ComparatorObject> comparator = comparator(rezept);
		Double maxScore = 0.0;
		for(ComparatorObject comparatorObject : comparator){
			maxScore = maxScore+comparatorObject.value;
		}
		
		
		// all Recipes
		List<Rezept> allRecipes = new ArrayList<Rezept>();
		allRecipes.clear();
		Rezept compare = rezept;
		compare.setSelected(true);
		if(rezept.getSymbol() == null){
			compare.setSymbol("Ihr Menu");
		}
		
		if(rezept.getSubTitle() == null){
			compare.setSubTitle("just like that");
		}
		
		allRecipes.add(compare);
		allRecipes.addAll( Search.getClientData().getPublicRezepte());
		if(Search.getClientData().getYourRezepte() != null){
			allRecipes.addAll(Search.getClientData().getYourRezepte());
		}

		
		
		// zuerst der Filter über die tatsächlichen Zutaten
		ArrayList<ComparatorRecipe> scoreMap = new ArrayList<ComparatorRecipe>();
		scoreMap.clear();
		Double MaxValueRezept = 0.0;
		Double MinValueRezept = 10000000.0;
		// first go over the Recipes in the Workspace
		for(Widget widget : EaternityRechner.rezeptList){
			RezeptView rezeptView = (RezeptView) widget;
			rezeptView.rezept.setCO2Value();
			if(rezeptView.rezept.getCO2Value()>MaxValueRezept){
				MaxValueRezept = rezeptView.rezept.getCO2Value();
			} 
			if(rezeptView.rezept.getCO2Value()<MinValueRezept){
				MinValueRezept = rezeptView.rezept.getCO2Value();
			}
		}
		
		for( Rezept compareRecipe : allRecipes){
			
			compareRecipe.setCO2Value();
			if(compareRecipe.getCO2Value()>MaxValueRezept){
				MaxValueRezept = compareRecipe.getCO2Value();
			} 
			if(compareRecipe.getCO2Value()<MinValueRezept){
				MinValueRezept = compareRecipe.getCO2Value();
			}
			
			ComparatorRecipe comparatorRecipe = new ComparatorRecipe();
			comparatorRecipe.key = compareRecipe.getId();
			comparatorRecipe.recipe = compareRecipe;
			comparatorRecipe.comparator = getExactScore(comparator,comparator(compareRecipe));
			Double error = 0.0;
			for(ComparatorObject comparatorObject : comparatorRecipe.comparator){
				error = error+Math.abs(comparatorObject.value);
			}
			comparatorRecipe.value = error;
			scoreMap.add(comparatorRecipe);
		}
		
		// dann der gröbere über die definierten Alternativen der Zutaten
		ArrayList<ComparatorRecipe> scoreMap2 = new ArrayList<ComparatorRecipe>();
		scoreMap2.clear();
		for(ComparatorRecipe compRecipe: scoreMap){
			// TODO 0.8 IS JUST A GUESS
			if((compRecipe.value/maxScore)<0.8){ // this is min. 20% identical
				Rezept compareRecipe = compRecipe.recipe;
				ComparatorRecipe comparatorRecipe = new ComparatorRecipe();
				comparatorRecipe.recipe = compareRecipe;
				comparatorRecipe.key = compareRecipe.getId();
				comparatorRecipe.value = getAltScore(compRecipe);
				scoreMap2.add(comparatorRecipe);
			}
		}

		
		// alles was jetzt noch da ist (alle errors  <0.6), werden verglichen, das heisst die Statistik ausgerechnet
		ArrayList<ComparatorRecipe> scoreMapFinal = new ArrayList<ComparatorRecipe>();
		scoreMapFinal.clear();
		for(ComparatorRecipe compRecipe: scoreMap2){
			// TODO 0.6 IS JUST A GUESS
			if((compRecipe.value/maxScore)<0.6){ // this is min. 20% identical and min. 60% alternative Identity
				
				compRecipe.recipe.setCO2Value();
				scoreMapFinal.add(compRecipe);
			}
		}
		
		Collections.sort(scoreMapFinal,new ComparatorComparator());

		// this is just a test
		if(scoreMapFinal.size()>0){
			EaternityRechner.suggestionPanel.clear();

			displayTops(scoreMapFinal, 0.8, 1.0);
			displayTops(scoreMapFinal, 0.5, 0.8);
			displayTops(scoreMapFinal, 0.0, 0.5);
			
		}
		
////		if(scoreMapFinal.size()>2){
//			rezept.setCO2Value();
//			double indikator = rezept.getCO2Value();
////			double stop = scoreMapFinal.get(0).recipe.getCO2Value();
//			double stop = MaxValueRezept;
////			double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
//			double start = MinValueRezept;
//			
//			Long indikatorLeft = Math.round(800/(stop-start)*(indikator-start));
//			String indikatorHTML = "<div style='padding-left:"+indikatorLeft.toString()+"px'>für 1ne Person: "+NumberFormat.getFormat("##").format(rezept.getCO2Value())+"g CO2</div>";
//			topIndikator.setHTML(indikatorHTML);
//			bottomIndikator.setHTML(indikatorHTML);
			
//		}
		

		//		double stop = scoreMapFinal.get(0).recipe.getCO2Value();
		double stop = MaxValueRezept;
		//		double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
		double start = MinValueRezept;


		// update all widgets bars!
		for(Widget widget : EaternityRechner.rezeptList){
			RezeptView rezeptView = (RezeptView) widget;
			rezeptView.rezept.setCO2Value();
			Long indikatorLeft = new Long(Math.round(620/(stop-start)*(rezeptView.rezept.getCO2Value()-start)));
			String indikatorHTML = new String("<div style='padding-left:"+indikatorLeft.toString()+"px'>für 1ne Person: "+NumberFormat.getFormat("##").format(rezeptView.rezept.getCO2Value())+"g *</div>");
			rezeptView.topIndikator.setHTML(indikatorHTML);
			rezeptView.bottomIndikator.setHTML(indikatorHTML);
		}

			
		
		// und die 2 Rezepte mit den höchsten Scores aus den entspr. Bereichen selektiert und angezeigt.
	}


	private void displayTops(ArrayList<ComparatorRecipe> scoreMapFinal,
			Double startDouble, Double stopDouble) {
		int beginRange = (int) Math.floor((scoreMapFinal.size())*startDouble);
		int stopRange = (int) Math.floor((scoreMapFinal.size())*stopDouble);

		List<ComparatorRecipe> selectionList =  scoreMapFinal.subList(beginRange, stopRange);
		if(stopRange-beginRange != 0){
			Double minValue = 100000.0;
			Rezept selectedMax = null;
			Iterator<ComparatorRecipe> iterator = selectionList.iterator();
			while(iterator.hasNext()){
				ComparatorRecipe takeMax = iterator.next();
				if(takeMax.value<minValue){
					selectedMax = takeMax.recipe;
					minValue = takeMax.value;
				}
			}
			final Rezept takeThisOne = selectedMax;
			
//			Double MenuLabelWert = new Double(0.0);
//			for (ZutatSpecification zutatSpec : selectedMax.Zutaten) { 
//				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
//			}
			selectedMax.setCO2Value();
			Double MenuLabelWert = selectedMax.getCO2Value();
			HTML suggestText = new HTML("<div style='cursor: pointer;cursor: hand;height:60px;width:230px;background:#F9C88C;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +"g</b></div>");
			HandlerRegistration handler = suggestText.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					// add receipe to the Worksheet Panel
					EaternityRechner.ShowRezept(takeThisOne);
				}
			});
			
			if(selectedMax.getSelected() != null){
				if(selectedMax.getSelected()){
					handler.removeHandler();
					suggestText = new HTML("<div style='height:60px;width:230px;background:#F48F28;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +"g</b></div>");
				}
			}
			//HTML suggestText = new HTML(selectedMax.getSymbol() + " hat " + NumberFormat.getFormat("##").format(MenuLabelWert)  +"g *");
//			suggestText.setHTML();
			EaternityRechner.suggestionPanel.add(suggestText);
		}
	}
	
	
	
	private Double getAltScore(ComparatorRecipe compRecipe) {
		// check for all the stuff thats negative: that means we had to much in the compareRecipe
		// and check for all the alternatives of the negative one, if there is something left in the positive
		ArrayList<ComparatorObject> resultComparator = null;

		boolean changed = true;
		while(changed){
			resultComparator = compRecipe.comparator;
			changed = false;
			for(ComparatorObject compObj: resultComparator){
				if(compObj.ingredient.getAlternatives() != null){
					if(Math.abs(compObj.value)>0.1){
						// we take 10% as a tolerance value
						for(Long altIngredientId :compObj.ingredient.getAlternatives()){
							for(ComparatorObject compObj2: resultComparator){
								if(altIngredientId.equals(compObj2.key)){
									if((compObj.value+compObj2.value)<0.1){
										// this means we have min. 10% improvement ( to have a converging situation)

										int subtractHere = compRecipe.comparator.indexOf(compObj2);
										int addHere = compRecipe.comparator.indexOf(compObj);
										compObj2.value = compObj2.value-compObj.value;
										compObj.value = compObj.value-compObj2.value;
										compRecipe.comparator.set(subtractHere, compObj2);						
										compRecipe.comparator.set(addHere, compObj);	

										changed = true;
									}
								}
							}
						}

					}
				}
			}
			
		}
		
		
		Double errorOrig = 0.0;
		for(ComparatorObject comparatorObject : compRecipe.comparator){
			errorOrig = errorOrig+Math.abs(comparatorObject.value);
		}
		
		Double errorHere = 0.0;
		for(ComparatorObject comparatorObject : resultComparator){
			errorHere = errorHere+Math.abs(comparatorObject.value);
		}
		

		// return value should be the absolut values but just 1/3 of the score...
		// TODO 1/3 IS JUST A GUESS
		return errorOrig+(errorOrig-errorHere)/3;
	}


	private ArrayList<ComparatorObject> getExactScore(ArrayList<ComparatorObject> recipeOrigin, ArrayList<ComparatorObject> recipeComparator) {
		
		ArrayList<ComparatorObject> resultComparator = new ArrayList<ComparatorObject>();
		
		// takes this Recipe from this RezeptView
		for(ComparatorObject comparatorObjectOrigin : recipeOrigin){
			// and compares every ingredient

			
			ComparatorObject comparatorResultObject = new ComparatorObject();
			// this is the positive error
			// we have something in the origin, but not in the compare
			double newValue = comparatorObjectOrigin.value;
			comparatorResultObject.key = comparatorObjectOrigin.key;
			comparatorResultObject.ingredient  = comparatorObjectOrigin.ingredient;
			
			// with the one from the database
			for(ComparatorObject comparatorObject :recipeComparator){
				
				// on match
				if(comparatorObject.key.equals(comparatorObjectOrigin.key)){	
					// calculate the error value
					newValue = comparatorObjectOrigin.value-comparatorObject.value;
					// if this is negative, we had too much of this in the compare
					break;
				}
			}
			
			// store the error value
			comparatorResultObject.value = newValue;
			resultComparator.add(comparatorResultObject);

		}
		
		// this is the case that we have more ingredients, then in the origin one.
		// they will be added as negative error... ( as it was also too much...)
		for(ComparatorObject comparatorObject :recipeComparator){
			boolean notFound = true;
			for(ComparatorObject resultCompObj : resultComparator){
				if(comparatorObject.key.equals(resultCompObj.key)){
					notFound = false;
				}
			}
			if(notFound){
				ComparatorObject comparatorResultObject = new ComparatorObject();
				comparatorResultObject.key = comparatorObject.key;
				comparatorResultObject.value = -comparatorObject.value;
				comparatorResultObject.ingredient  = comparatorObject.ingredient;
				resultComparator.add(comparatorResultObject);
			}
		
		}
		
		
		return resultComparator;
	}


	private ArrayList<ComparatorObject> comparator(Rezept rezept){
		// wtf is up with the Map() ???
//		Map<Long,Double> recipeComparator = Collections.emptyMap();
		// everything would have been so easy!!
		
		 ArrayList<ComparatorObject> recipeComparator = new  ArrayList<ComparatorObject>();
	
		
		for(ZutatSpecification zutatSpec : rezept.Zutaten){
			Ingredient zutat = Search.getClientData().getIngredientByID(zutatSpec.getZutat_id());
//			amount of Persons needs to be assigned always!
			Double amount = (1.0*zutatSpec.getMengeGramm()/zutat.stdAmountGramm)/rezept.getPersons();
			Double alreadyAmount = 0.0;
			int index = -1;
//			check if the indgredient is already in there...
			for(ComparatorObject comparatorObject :recipeComparator){
				if(comparatorObject.key.equals(zutat.getId())){
					alreadyAmount =  comparatorObject.value;
					index = recipeComparator.indexOf(comparatorObject);
					// if so take the found index
					break;
				}
			}
			
			ComparatorObject comparatorObject = new ComparatorObject();
			comparatorObject.ingredient = zutat;
			comparatorObject.key = zutat.getId();
			comparatorObject.value = amount+alreadyAmount;
			
			if(index!=-1){
				recipeComparator.set(index, comparatorObject);
			} else {
				recipeComparator.add(comparatorObject);
			}

		}

		return recipeComparator;
	}


	private void updateTable(int row,ZutatSpecification zutatSpec){
		saved = false;
		String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
		
		MenuTable.getColumnFormatter().setWidth(3, "80px");
		MenuTable.setText(row,3,": ca. "+formatted+"g CO₂-Äquivalent ");
		
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
	
	
	// here comes the Image Uploader:

   
 
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
	    public void onFinish(IUploader uploader) {
	      if (uploader.getStatus() == Status.SUCCESS) {
	    	 
//	    	 rezept.imageId = uploader.
	    	 GWT.log("Successfully uploaded image: "+  uploader.fileUrl(), null);
	        new PreloadedImage(uploader.fileUrl(), showImage);
	        
	        // The server can send information to the client.
	        // You can parse this information using XML or JSON libraries
//	        Document doc = XMLParser.parse(uploader.getServerResponse());
//	        String size = Utils.getXmlNodeValue(doc, "file-1-size");
//	        String type = Utils.getXmlNodeValue(doc, "file-1-type");
//	        System.out.println(size + " " + type);
	      }
	    }
	  };

	  // Attach an image to the pictures viewer
	  OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {
	    public void onLoad(PreloadedImage image) {
	      image.setWidth("125px");
	      panelImages.add(image);
	    }
	  };
	
	
	
	
	
}


class ComparatorObject{
	public Long key;
	public Double value;
	public Ingredient ingredient;
	public ComparatorObject(){
		
	}
}

class ComparatorRecipe{
	public Long key;
	public Double value;
	public Rezept recipe;
	public ArrayList<ComparatorObject> comparator;
	public ComparatorRecipe(){
		
	}
}

class ComparatorComparator implements Comparator<ComparatorRecipe> {
	  public int compare(ComparatorRecipe z1, ComparatorRecipe z2) {
		  Double o1 = z1.recipe.getCO2Value();
		  Double o2 = z2.recipe.getCO2Value();
			return Double.valueOf(o2).compareTo(Double.valueOf(o1));
	  }
	}