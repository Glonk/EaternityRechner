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



import ch.eaternity.client.comparators.ComparatorComparator;
import ch.eaternity.client.comparators.ComparatorObject;
import ch.eaternity.client.comparators.ComparatorRecipe;
import ch.eaternity.client.widgets.PhotoGallery;
import ch.eaternity.client.widgets.UploadPhoto;
import ch.eaternity.shared.IngredientCondition;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.MoTransportation;
import ch.eaternity.shared.Production;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.gwt.user.client.ui.Image;
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

public class RecipeView extends Composite {
	interface Binder extends UiBinder<Widget, RecipeView> { }
	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField SelectionStyleRow selectionStyleRow;
	@UiField EvenStyleRow evenStyleRow;

	@UiField FlexTable MenuTable;
	@UiField HTMLPanel SaveRezeptPanel;
	@UiField HTMLPanel topStatusBar;
	@UiField
	public FlowPanel menuDecoInfo;
	@UiField Button RezeptButton;
	@UiField Anchor PrepareButton;
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
	@UiField VerticalPanel MenuTableWrapper;
	
	@UiField HTML titleHTML;
	@UiField HTML openHTML;
	@UiField HTML savedHTML;
	@UiField HTML detailText;
	
	private FlowPanel panelImages = new FlowPanel();
	private PhotoGallery galleryWidget;
	public UploadPhoto uploadWidget;
	public HandlerRegistration imagePopUpHandler = null;
	static int overlap = 0;
	
	HTML htmlCooking;
	Boolean askForLess;
	public Boolean askForLess2;
	public Image showImageRezept = new Image();
	public Anchor bildEntfernen;
	HandlerRegistration klicky;
	public HandlerRegistration showImageHandler = null;
	
	boolean saved;
	
	private Listener listener;
	int  selectedRow = 0;
	int  selectedRezept = -1;
	Recipe recipe;

	
//	static ArrayList<IngredientSpecification> zutatImMenu = new ArrayList<IngredientSpecification>();
	
	
	public RecipeView(Recipe recipe) {
	    // does this need to be here?
	    initWidget(uiBinder.createAndBindUi(this));
	    
	    if(TopPanel.leftKitchen){
	    	PrepareButton.setVisible(false);
	    }
	    setRezept(recipe);
	    saved = true;
	    initTable();
	    
	    RezeptName.setVisible(false);
	    rezeptDetails.setVisible(false);
	    cookingInstr.setVisible(false);
	    detailText.setVisible(false);
	    
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
		void onItemSelected(IngredientSpecification item);
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
			recipe.setPersons(persons);
			if(!amountPersons.getText().isEmpty()){
				persons = Long.parseLong(amountPersons.getText().trim());
				if(persons > 0){
					recipe.setPersons(persons);
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
			recipe.setSymbol(RezeptName.getText());
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
			recipe.setCookInstruction(cookingInstr.getText());
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
			recipe.setSubTitle(rezeptDetails.getText());
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
	
	@UiHandler("PrepareButton")
	void onPrepareClicked(ClickEvent event) {
		
		 // we changed something -> so it isn't saved anymore
		 saved = false;
		 
	
		 
		 
		 // what is this???
		 if(selectedRow != -1 && addInfoPanel.getWidgetCount() ==2){
			 InfoZutatDialog infoDialog = (InfoZutatDialog)(addInfoPanel.getWidget(1));
			 IngredientSpecification zutatSpec2 = infoDialog.getZutatSpec();
			 recipe.Zutaten.set(selectedRow , zutatSpec2);
		 }
		 
		 // the selected row in the recipe is not highlighted anymore
		 if (selectedRow != -1) {
			 styleRow(selectedRow, false);
			 Search.selectedRow = -1;
		 }
		 

		// remove window
		addInfoPanel.remove(2);
		
		// cooking instructions etc...
		menuDecoInfo.setVisible(false);
		
		InfoPreparationDialog infoPrepare = new InfoPreparationDialog(MenuTable,recipe,SuggestTable,this);
		
		addInfoPanel.insert(infoPrepare, 2);
		
		// is this necessary... it should be only on change...
		updateSuggestion();
		
	}
	
	@UiHandler("removeRezeptButton")
	void onRemoveClicked(ClickEvent event) {
		final RecipeView test = this;
		if(saved){
			int row = getWidgetRow(test , EaternityRechner.rezeptList);
			EaternityRechner.rezeptList.remove(test);
			EaternityRechner.rezeptList.removeRow(row);
			EaternityRechner.selectedRezept = -1;
			EaternityRechner.suggestionPanel.clear();
		} else {
		final ConfirmDialog dlg = new ConfirmDialog("Zusammenstellungen ist noch nicht gespeichert!");
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
		MenuTable.getColumnFormatter().setWidth(1, "180px");
		MenuTable.setCellPadding(1);
		
		

		
		
	    if(recipe.getCookInstruction() != null){
	    	htmlCooking = new HTML(recipe.getCookInstruction());
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
	
	public void setRezept(Recipe recipe){
		this.recipe = recipe;
//		showRezept(recipe);
	}

	public Recipe getRezept(){
		return this.recipe;
	}	
	
	public void showRezept(final Recipe recipe) {

			if(recipe.getPersons() != null){
				amountPersons.setText(recipe.getPersons().toString());
			} else {
				amountPersons.setText("4");
				Long persons = Long.parseLong(amountPersons.getText());
				recipe.setPersons(persons);
			}
			final RecipeView rezeptView = this;
			displayZutatImMenu(recipe.Zutaten);
			updateSuggestion();
//			zutatImMenu.clear();
			
//			int row = AddZutatZumMenu(recipe.getZutaten());
			// add Speicher Recipe Button
			if(klicky != null){
				klicky.removeHandler();
			}
			
			klicky = RezeptButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if(RezeptName.getText() != ""){
						// TODO warn that it wasn't saved in the other case
						amountPersons.setText(recipe.getPersons().toString());
//						Speichere Recipe ab. 
//						Recipe rezeptSave = new Recipe(RezeptName.getText());
//						rezeptSave.setOpen(makePublic.getValue());
//						rezeptSave.addZutaten(recipe.getZutaten());
//						EaternityRechner.addRezept(rezeptSave);
						recipe.setSymbol(RezeptName.getText());
						if(rezeptDetails.getText() != ""){
							recipe.setSubTitle(rezeptDetails.getText());
						} else {
							recipe.setSubTitle("Menü Beschreibung");
						}
						recipe.openRequested = !makePublic.getValue();
						recipe.open = false;
						recipe.setCookInstruction(cookingInstr.getText()); 
						
						EaternityRechner.addRezept(recipe,rezeptView);
					}
				}
			});
		
	}

	
	

	 void selectRow(int row) {
		 
		 PrepareButton.setVisible(true);
		//TODO uncomment this:
		//Search.leftSplitPanel.setWidgetMinSize(Search.infoZutat, 448);
//		Window.alert(Integer.toString(row));
		
		 saved = false;
		 
		 if(selectedRow != -1 && addInfoPanel.getWidgetCount() ==2){
			 InfoZutatDialog infoDialog = (InfoZutatDialog)(addInfoPanel.getWidget(1));
			 IngredientSpecification zutatSpec2 = infoDialog.getZutatSpec();
//			 int index = zutatImMenu.indexOf(zutatSpec);
//			 zutatImMenu = (ArrayList<IngredientSpecification>) recipe.getZutaten();
			 recipe.Zutaten.set(selectedRow , zutatSpec2);

		 }
		 
		IngredientSpecification zutatSpec = recipe.Zutaten.get(row);

		if (zutatSpec == null) {
			return;
		}
		
		Long ParentZutatId = zutatSpec.getZutat_id();
		Ingredient zutat = Search.clientData.getIngredientByID(ParentZutatId);
		
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

	private void openSpecificationDialog(IngredientSpecification zutatSpec, Ingredient zutat,  TextBox amount,FlexTable MenuTable,int selectedRow) {
		// TODO Auto-generated method stub
		
//		if(addInfoPanel.getWidgetCount() ==2){
			addInfoPanel.remove(2);
//		}
		menuDecoInfo.setVisible(false);
		InfoZutatDialog infoZutat = new InfoZutatDialog(zutatSpec,zutat,amount,MenuTable,selectedRow,recipe,SuggestTable,this);
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
	


	
	private void displayZutatImMenu( ArrayList<IngredientSpecification> zutaten) {
		if(askForLess != null){

			if(showImageHandler != null){
				showImageHandler.removeHandler();
				showImageHandler = null;
			}
			if(askForLess){
				if(detailText != null){
					overlap = Math.max(1,showImageRezept.getHeight() -  addInfoPanel.getOffsetHeight() +40);

					//				rezeptView.detailText.setHeight(height)
					detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(overlap)+" />"+recipe.getCookInstruction());
				}
			}
		}
		
	MenuTable.removeAllRows();;
	Integer row = MenuTable.getRowCount();
//	ArrayList<IngredientSpecification> zutatenNew = (ArrayList<IngredientSpecification>) zutaten.clone();
	for(final IngredientSpecification zutat : zutaten){
//		
	Button removeZutat = new Button("x");
//	removeZutat.addStyleName("style.gwt-Button");
//	removeZutat.addStyleDependentName("gwt-Button");
	removeZutat.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
			int removedIndex = recipe.Zutaten.indexOf(zutat);
			recipe.Zutaten.remove(removedIndex);
			MenuTable.removeRow(removedIndex);
//			recipe.removeZutat(removedIndex);
			
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
			if(askForLess != null){
				
					if(showImageHandler != null){
						showImageHandler.removeHandler();
						showImageHandler = null;
					}
					if(askForLess){
				if(detailText != null){
					overlap = Math.max(1,showImageRezept.getHeight() -  addInfoPanel.getOffsetHeight() +40 );

					//				rezeptView.detailText.setHeight(height)
					detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(overlap)+" />"+recipe.getCookInstruction());
				}
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
	
	changeIcons(row, zutat);
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

	void changeIcons(Integer row, final IngredientSpecification zutat) {
		HTML icon = new HTML();
		Boolean itsOkay = true;
		
		
		if(zutat.getZustand() != null){
			if(zutat.getZustand().symbol.equalsIgnoreCase("frisch") && zutat.getDistance() < 500000){
				if(zutat.getStartSeason() != null && zutat.getStopSeason() != null){
					Date date = DateTimeFormat.getFormat("MM").parse(Integer.toString(TopPanel.Monate.getSelectedIndex()+1));
					// In Tagen
					//		String test = InfoZutat.zutat.getStartSeason();
					Date dateStart = DateTimeFormat.getFormat("dd.MM").parse( zutat.getStartSeason());		
					Date dateStop = DateTimeFormat.getFormat("dd.MM").parse( zutat.getStopSeason() );

					if(		dateStart.before(dateStop)  && date.after(dateStart) && date.before(dateStop) ||
							dateStart.after(dateStop) && !( date.before(dateStart) && date.after(dateStop)  ) ){
						icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
					} else if (!zutat.getZustand().symbol.equalsIgnoreCase("frisch") && !zutat.getProduktion().symbol.equalsIgnoreCase("GH") && zutat.getDistance() < 500000) {
						icon.setHTML(icon.getHTML()+"<div class='extra-icon regloc'><img src='pixel.png' height=1 width=20 /></div>");
					} else if (zutat.getProduktion().symbol.equalsIgnoreCase("GH")) {
						// nothing
					} else {
						icon.setHTML(icon.getHTML()+"<div class='extra-icon smiley3'><img src='pixel.png' height=1 width=20 /></div>");
						itsOkay = false;
					}
				}
			} 
		}
		
		if (itsOkay) {
			if(zutat.getCalculatedCO2Value()/zutat.getMengeGramm() < .4){
				icon.setHTML("<div class='extra-icon smiley1'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());
				icon.setHTML(" g  <div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());

				//		icon.setHTML(icon.getHTML()+"<img src='pixel.png' height=1 width=20 />");
				//		icon.setStyleName("base-icons smiley1");			
			} else	if(zutat.getCalculatedCO2Value()/zutat.getMengeGramm() < 1.2){
				icon.setHTML(" g  <div class='extra-icon smiley2'><img src='pixel.png' height=1 width=20 /></div>"+icon.getHTML());
				//		icon.setHTML(icon.getHTML()+"<img src='pixel.png' height=1 width=20 />");
				//		icon.setStyleName("base-icons smiley2");			
			} else {
				icon.setHTML(" g  "+icon.getHTML());
			}
		} else {
			icon.setHTML(" g  "+icon.getHTML());
		}
		

		
		
		
		if(zutat.getProduktion() != null && zutat.getProduktion().symbol.equalsIgnoreCase("bio")){
			icon.setHTML(icon.getHTML()+"<div class='extra-icon bio'><img src='pixel.png' height=1 width=20 /></div>");
		}
		
		icon.setHTML(icon.getHTML()+zutat.getName());
		
		MenuTable.setWidget(row, 1,  icon);
	}
	
	void updateSuggestion() {

		Double MenuLabelWert = 0.0;
		Double MaxMenuWert = 0.0;

		if(recipe.Zutaten.isEmpty()){
			if(addInfoPanel.getWidgetCount() ==2){
				addInfoPanel.remove(1);
			}
		}
		
		for (IngredientSpecification zutatSpec : recipe.Zutaten) { 
			MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
			if(zutatSpec.getCalculatedCO2Value()>MaxMenuWert){
				MaxMenuWert = zutatSpec.getCalculatedCO2Value();
			}
			
		}
		for (IngredientSpecification zutatSpec : recipe.Zutaten) { 
			String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
			MenuTable.setText(recipe.Zutaten.indexOf(zutatSpec),3,"ca "+formatted+"g *");
			MenuTable.setHTML(recipe.Zutaten.indexOf(zutatSpec), 4, "<div style='background:#A3C875;width:40px;height:1.0em;margin-right:5px;'><div style='background:#323533;height:1.0em;width:".concat(Double.toString(zutatSpec.getCalculatedCO2Value()/MaxMenuWert*40).concat("px'>.</div></div>")));
		}
		
		String formatted = NumberFormat.getFormat("##").format(MenuLabelWert);
		
		SuggestTable.setCellSpacing(2);
		SuggestTable.setText(1,0,"SUMME");
		SuggestTable.getColumnFormatter().setWidth(0, "215px");
		SuggestTable.setHTML(1,1,"ca <b>"+formatted+"g</b> *");
		SuggestTable.getColumnFormatter().setWidth(1, "140px");
		
		
		updtTopSuggestion();
		
		
		
	}


	public void updtTopSuggestion() {
		// TODO Algorithm for the Top Suggestions
		// Von jedem Gericht gibt es einen CO2 Wert für 4Personen (mit oder ohne Herkunft? oder aus der nächsten Distanz?), 
		// so wie sie gepeichert wurde.
		// Es werden bei der Anzeige Rezepte berücksichtigt, die: 
		// min 20% identische Zutaten ( Zutat*(Menge im Recipe)/StdMenge ) und das pro Zutat, und davon min 20%identisch
		// min +50% Zutaten die in den alternativen Vorkommen
		// hierbei wird die 2 passendsten Rezepte jeweils aus den nicht durch das markierte Recipe belegten Bereich angezeigt
		// Bereich sind 0-20%	20%-50%		50%-100%
		
		// Rezepte sollten sich bewerten lassen, und deren Popularität gemessen werden. ( Über die Zeit?)
		// 

		// diese Filter sollten in der Reihenfolge ausgeführt werden, in der sie am wenigsten Berechnungen benötigen:
		
		// TODO alle Rezepte für 4 Personen, sonst macht der Vergleich keinen Sinn
		
		// get Comparator
		ArrayList<ComparatorObject> comparator = comparator(recipe);
		// comparator returns for each ingedient:
		// (zutatSpec.getMengeGramm()/zutat.stdAmountGramm)/recipe.getPersons();
		
		Double maxScore = 0.0;
		for(ComparatorObject comparatorObject : comparator){
			maxScore = maxScore+comparatorObject.value;
		}
		// this is the sum over all those ingredient values
		// which equals about the number of different ingredients in the recipe...
		
		
		// all Recipes
		List<Recipe> allRecipes = new ArrayList<Recipe>();
		allRecipes.clear();
		// display context information...
		Recipe compare = recipe;
		compare.setSelected(true);
		if(recipe.getSymbol() == null){
			compare.setSymbol("Ihr Menu");
		}
		if(recipe.getSubTitle() == null){
			compare.setSubTitle("just like that");
		}
		
		// add your specific recipe to the others in the database
		allRecipes.add(compare);
		// and all the others also
		allRecipes.addAll( Search.clientData.getPublicRezepte());
		if(Search.clientData.getYourRezepte() != null){
			allRecipes.addAll(Search.clientData.getYourRezepte());
		}

		
		
		// zuerst der Filter über die tatsächlichen Zutaten
		ArrayList<ComparatorRecipe> scoreMap = new ArrayList<ComparatorRecipe>();
		scoreMap.clear();
		
		// Init first boundaries, for indicator
		Double MaxValueRezept = 0.0;
		Double MinValueRezept = 10000000.0;
		//  go over the Recipes in the Workspace
		for(Widget widget : EaternityRechner.rezeptList){
			RecipeView rezeptView = (RecipeView) widget;
			rezeptView.recipe.setCO2Value();
			if(rezeptView.recipe.getCO2Value()>MaxValueRezept){
				MaxValueRezept = rezeptView.recipe.getCO2Value();
			} 
			if(rezeptView.recipe.getCO2Value()<MinValueRezept){
				MinValueRezept = rezeptView.recipe.getCO2Value();
			}
		}
		
		// go over the recipes in the database ( here our special one is already included...)
		for( Recipe compareRecipe : allRecipes){
			
			// this is just to get the min and max values for the indicator
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
			// get the direct comparison score... the bigger the worse...
			comparatorRecipe.comparator = getExactScore(comparator,comparator(compareRecipe));
			
			// error is the max score of both added together, minus twice the disjunct region
			
			Double error = 0.0;
			Double errorNeg = 0.0;
			for(ComparatorObject comparatorObject : comparatorRecipe.comparator){
				error = error+Math.abs(comparatorObject.value);
				if(comparatorObject.value<0){
					errorNeg = errorNeg+Math.abs(comparatorObject.value);
				}
			}
			comparatorRecipe.value = error;
			comparatorRecipe.valueNeg = errorNeg;
			scoreMap.add(comparatorRecipe);
		}
		
		// dann der gröbere über die definierten Alternativen der Zutaten
		ArrayList<ComparatorRecipe> scoreMap2 = new ArrayList<ComparatorRecipe>();
		scoreMap2.clear();
		for(ComparatorRecipe compRecipe: scoreMap){
			// this is cool now!
			if((compRecipe.value/(maxScore+compRecipe.valueNeg))<0.8){ // this is min. 20% identical _____==overlap==-----
//			if((compRecipe.value)<0.8){ // this is min. 20% identical
				Recipe compareRecipe = compRecipe.recipe;
				ComparatorRecipe comparatorRecipe = new ComparatorRecipe();
				comparatorRecipe.recipe = compareRecipe;
				comparatorRecipe.key = compareRecipe.getId();
				// TODO here we got some error...
				comparatorRecipe.value = getAltScore(compRecipe,maxScore);
				Double errorNeg = 0.0;
				for(ComparatorObject comparatorObject : compRecipe.comparator){
					if(comparatorObject.value<0){
						errorNeg = errorNeg+Math.abs(comparatorObject.value);
					}
				}
				comparatorRecipe.valueNeg = errorNeg;
				
				scoreMap2.add(comparatorRecipe);
			}
		}

		
		// alles was jetzt noch da ist (alle errors  <0.6), werden verglichen, das heisst die Statistik ausgerechnet
		ArrayList<ComparatorRecipe> scoreMapFinal = new ArrayList<ComparatorRecipe>();
		scoreMapFinal.clear();
		for(ComparatorRecipe compRecipe: scoreMap2){
			// TODO 0.6 IS JUST A GUESS
//			if((compRecipe.value/maxScore)<0.6){ // this is min. 20% identical and min. 60% alternative Identity
			if((compRecipe.value/(maxScore+compRecipe.valueNeg))<0.6){ // this is min. 20% identical and min. 60% alternative Identity
				
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
//			recipe.setCO2Value();
//			double indikator = recipe.getCO2Value();
////			double stop = scoreMapFinal.get(0).recipe.getCO2Value();
//			double stop = MaxValueRezept;
////			double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
//			double start = MinValueRezept;
//			
//			Long indikatorLeft = Math.round(800/(stop-start)*(indikator-start));
//			String indikatorHTML = "<div style='padding-left:"+indikatorLeft.toString()+"px'>für 1ne Person: "+NumberFormat.getFormat("##").format(recipe.getCO2Value())+"g CO2</div>";
//			topIndikator.setHTML(indikatorHTML);
//			bottomIndikator.setHTML(indikatorHTML);
			
//		}
		

		//		double stop = scoreMapFinal.get(0).recipe.getCO2Value();
		double stop = MaxValueRezept;
		//		double start = scoreMapFinal.get(scoreMapFinal.size()-1).recipe.getCO2Value();
		double start = MinValueRezept;


		// update all widgets bars!
		for(Widget widget : EaternityRechner.rezeptList){
			RecipeView rezeptView = (RecipeView) widget;
			rezeptView.recipe.setCO2Value();
			Long indikatorLeft = new Long(Math.round(580/(stop-start)*(rezeptView.recipe.getCO2Value()-start)));
			String indikatorHTMLoben = new String("<div style='padding-left: 30px;display:inline;background:#000;background-image:url(eckeoben.png);margin-left:"+indikatorLeft.toString()+"px'>"+NumberFormat.getFormat("##").format(rezeptView.recipe.getCO2Value())+" g* (pro Person)</div>");
			String indikatorHTMLunten = new String("<div style='padding-left: 30px;display:inline;background:#000;background-image:url(eckeunten.png);margin-left:"+indikatorLeft.toString()+"px'>"+NumberFormat.getFormat("##").format(rezeptView.recipe.getCO2Value())+" g* (pro Person)</div>");
			rezeptView.topIndikator.setHTML(indikatorHTMLoben);
			rezeptView.bottomIndikator.setHTML(indikatorHTMLunten);
		}

			
		
		// und die 2 Rezepte mit den höchsten Scores aus den entspr. Bereichen selektiert und angezeigt.
	}


	private void displayTops(ArrayList<ComparatorRecipe> scoreMapFinal,
			Double startDouble, Double stopDouble) {
		// maybe it shouldn't be 20% of the best rezepte, but a another scaling...
		int beginRange = (int) Math.floor((scoreMapFinal.size())*startDouble);
		int stopRange = (int) Math.floor((scoreMapFinal.size())*stopDouble);

		List<ComparatorRecipe> selectionList =  scoreMapFinal.subList(beginRange, stopRange);
		if(stopRange-beginRange != 0){
			Double minValue = 100000.0;
			Recipe selectedMax = null;
			Iterator<ComparatorRecipe> iterator = selectionList.iterator();
			while(iterator.hasNext()){
				ComparatorRecipe takeMax = iterator.next();
				if(takeMax.value<minValue){
					selectedMax = takeMax.recipe;
					minValue = takeMax.value;
				}
			}
			final Recipe takeThisOne = selectedMax;
			
//			Double MenuLabelWert = new Double(0.0);
//			for (IngredientSpecification zutatSpec : selectedMax.Zutaten) { 
//				MenuLabelWert +=zutatSpec.getCalculatedCO2Value();
//			}
			selectedMax.setCO2Value();
			Double MenuLabelWert = selectedMax.getCO2Value();
			HTML suggestText = new HTML("<div style='cursor: pointer;cursor: hand;height:60px;width:230px;background:#F9C88C;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +" g</b></div>");
			HandlerRegistration handler = suggestText.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					// add receipe to the Worksheet Panel
					EaternityRechner.ShowRezept(takeThisOne);
				}
			});
			
			if(selectedMax.getSelected() != null){
				if(selectedMax.getSelected()){
					handler.removeHandler();
					suggestText = new HTML("<div style='height:60px;width:230px;background:#F48F28;margin-right:30px;border-radius: 3px;border: solid 2px #F48F28;'><div style='height:40px;width:200px;background:#323533;color:#fff;padding-left:5px;border-bottom-right-radius: 3px;border-top-right-radius: 3px;'><b>" + selectedMax.getSymbol() + "</b><br/>"+ selectedMax.getSubTitle() +"</div>CO2-Äq ca: <b>" + NumberFormat.getFormat("##").format(MenuLabelWert)  +" g</b></div>");
				}
			}
			//HTML suggestText = new HTML(selectedMax.getSymbol() + " hat " + NumberFormat.getFormat("##").format(MenuLabelWert)  +"g *");
//			suggestText.setHTML();
			EaternityRechner.suggestionPanel.add(suggestText);
		}
	}
	
	
	
	private Double getAltScore(ComparatorRecipe compRecipe, Double maxScore) {
		// check for all the stuff thats negative: that means we had to much in the compareRecipe
		// and check for all the alternatives of the negative one, if there is something left in the positive
		ArrayList<ComparatorObject> resultComparator = null;

		boolean changed = true;
		while(changed){
			resultComparator = compRecipe.comparator;
			changed = false;
			for(ComparatorObject compObj: resultComparator){
				if(compObj.ingredient.getAlternatives() != null){
					if((Math.abs(compObj.value)/(maxScore+compRecipe.valueNeg))>0.1){
						// we take 10% as a tolerance value
						for(Long altIngredientId :compObj.ingredient.getAlternatives()){
							for(ComparatorObject compObj2: resultComparator){
								if(altIngredientId.equals(compObj2.key)){
									if(Math.abs(compObj2.value)>0 && Math.abs(compObj.value+compObj2.value)<0.1){
										// this means we have min. 10% improvement ( to have a converging situation)

										int subtractHere = compRecipe.comparator.indexOf(compObj2);
										int addHere = compRecipe.comparator.indexOf(compObj);
										compObj2.value = compObj2.value+compObj.value;
										compObj.value = compObj2.value;
										resultComparator.set(subtractHere, compObj2);						
										resultComparator.set(addHere, compObj);	

										changed = true;
										break;
									}
								}
							}
						}

					}
				}
			}
			
		}
		
		
		Double errorOrig = compRecipe.value;
//		for(ComparatorObject comparatorObject : compRecipe.comparator){
//			errorOrig = errorOrig+Math.abs(comparatorObject.value);
//		}
//		
		Double errorHere = 0.0;
		Double errorNeg = 0.0;
		for(ComparatorObject comparatorObject : resultComparator){
			errorHere = errorHere+Math.abs(comparatorObject.value);
		}
		

		// return value should be the absolut values but 1/3 of the score...
		// TODO 1/3 IS JUST A GUESS
		return errorHere;
	}


	private ArrayList<ComparatorObject> getExactScore(ArrayList<ComparatorObject> recipeOrigin, ArrayList<ComparatorObject> recipeComparator) {
		
		ArrayList<ComparatorObject> resultComparator = new ArrayList<ComparatorObject>();
		resultComparator.clear();
		
		// takes this Recipe from this RezeptView
		for(ComparatorObject comparatorObjectOrigin : recipeOrigin){
			// and compares every ingredient

			
			ComparatorObject comparatorResultObject = new ComparatorObject();
			// this is the positive error
			// if we have something in the origin, but not in the compare
			// we start with the maximum error
			double newValue = comparatorObjectOrigin.value;
			comparatorResultObject.key = comparatorObjectOrigin.key;
			comparatorResultObject.ingredient  = comparatorObjectOrigin.ingredient;
			
			// with the one from the database
			for(ComparatorObject comparatorObject :recipeComparator){
				
				// on match - we substract this match...
				if(comparatorObject.key.equals(comparatorObjectOrigin.key)){	
					// calculate the error value
					newValue = comparatorObjectOrigin.value-comparatorObject.value;
					// if this is negative, we had too much of this in the compare
					
					// break for speed up ( as the map is injective)
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


	private ArrayList<ComparatorObject> comparator(Recipe recipe){
		// wtf is up with the Map() ???
//		Map<Long,Double> recipeComparator = Collections.emptyMap();
		// everything would have been so easy!!
		
		 ArrayList<ComparatorObject> recipeComparator = new  ArrayList<ComparatorObject>();
		 recipeComparator.clear();
		
		for(IngredientSpecification zutatSpec : recipe.Zutaten){
			Ingredient zutat = Search.clientData.getIngredientByID(zutatSpec.getZutat_id());
//			amount of Persons needs to be assigned always!
			Double amount = (1.0*zutatSpec.getMengeGramm()/zutat.stdAmountGramm)/recipe.getPersons();
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


	private void updateTable(int row,IngredientSpecification zutatSpec){
		saved = false;
		String formatted = NumberFormat.getFormat("##").format( zutatSpec.getCalculatedCO2Value() );
		
		MenuTable.getColumnFormatter().setWidth(3, "80px");
		MenuTable.setText(row,3,": ca. "+formatted+" g CO₂-Äquivalent ");
		
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
	    	 
//	    	 recipe.imageId = uploader.
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

//class ComparatorObject{
//	public Long key;
//	public Double value;
//	public Ingredient ingredient;
//	public ComparatorObject(){
//		
//	}
//}

//class ComparatorRecipe{
//	public Long key;
//	public Double value;
//	public Recipe recipe;
//	public ArrayList<ComparatorObject> comparator;
//	public ComparatorRecipe(){
//		
//	}
//}
//
//class ComparatorComparator implements Comparator<ComparatorRecipe> {
//	  public int compare(ComparatorRecipe z1, ComparatorRecipe z2) {
//		  Double o1 = z1.recipe.getCO2Value();
//		  Double o2 = z2.recipe.getCO2Value();
//			return Double.valueOf(o2).compareTo(Double.valueOf(o1));
//	  }
//	}