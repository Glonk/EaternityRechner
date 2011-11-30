package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

import ch.eaternity.client.IngredientsDialog;
import ch.eaternity.client.RecipeEditView;
import ch.eaternity.client.RecipeView;
import ch.eaternity.client.Search;
import ch.eaternity.client.TopPanel;
import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.widgets.ImageOverlay;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;

public class EaternityRechnerViewImpl<T> extends Composite implements EaternityRechnerView<T>
{
	@UiTemplate("EaternityRechnerViewImpl.ui.xml")
	interface EaternityRechnerViewUiBinder extends UiBinder<DockLayoutPanel, EaternityRechnerViewImpl> {}
	private static EaternityRechnerViewUiBinder uiBinder =
		GWT.create(EaternityRechnerViewUiBinder.class);

	//	private static EaternityRechnerViewImplUiBinder uiBinder = GWT.create(EaternityRechnerViewImplUiBinder.class);
	//
	//	interface EaternityRechnerViewImplUiBinder extends UiBinder<DockLayoutPanel, EaternityRechnerViewImpl>
	//	{
	//	}

	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}


	@UiField TopPanel topPanel;
	@UiField Search searchPanel;  

	@UiField FlexTable rezeptList;
	@UiField FlexTable rezeptEditList;
	@UiField FlexTable MenuTable;
	@UiField FlexTable SuggestTable;
	@UiField HorizontalPanel addInfoPanel;
	@UiField HTML titleHTML;
	@UiField DockLayoutPanel topSticky;
	@UiField HTMLPanel panelNorth;
	@UiField HTMLPanel htmlRezept;
	@UiField AbsolutePanel dragArea;

	@UiField ScrollPanel scrollWorkspace;
	@UiField Button addRezeptButton;
	@UiField SelectionStyle selectionStyle;
	@UiField HorizontalPanel suggestionPanel;



	static int selectedRezept = -1;

	private HandlerRegistration adminHandler;
	private HandlerRegistration ingredientHandler;
	static String styleNameOverlap = "overlap";

	private Presenter<T> presenter;
	private String name;
	
	static RecipeEditView rezeptEditView;

	public EaternityRechnerViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");


		// TODO Move cursor focus to the Search box.
		// TODO adjust this with something that makes more sense / what would that be? 1024 width...
		rezeptList.getColumnFormatter().setWidth(1, "750px");
		rezeptEditList.getColumnFormatter().setWidth(1, "750px");

		//TODO Reactivate for later use
		suggestionPanel.setVisible(false);
	}
	
	public void loadLogin() {
		// Assemble login panel.
		topPanel.signInLink.setHref(presenter.getLoginInfo().getLoginUrl());
	}
	
	public void loadYourRechner() {
		topPanel.signOutLink.setHref(presenter.getLoginInfo().getLogoutUrl());
		topPanel.signInLink.setVisible(false);
		topPanel.signOutLink.setVisible(true);
		
		topPanel.loginLabel.setText("Willkommen "+ presenter.getLoginInfo().getNickname() +".");
		
		// load your kitchens
		// loadYourKitchens();
		
		if(presenter.getLoginInfo().getInKitchen()){
			topPanel.location.setVisible(false);
		} else {
			topPanel.location.setVisible(true);
		}
		
		//load your personal recipes
		presenter.loadYourRezepte();
	}

	
	public HandlerRegistration loadAdmin() {
		
		// um neue Zutaten hinzuzufügen (allein eine Funktion für den Admin)
		ingredientHandler = getTopPanel().ingredientLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				IngredientsDialog dlg = new IngredientsDialog();
				dlg.show();
				dlg.center();
			}
		});
		getTopPanel().ingredientLink.setVisible(true);
		
		return ingredientHandler;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}


	@Override
	public void setPresenter(Presenter<T> presenter)
	{
		this.presenter = presenter;
		searchPanel.setPresenter(presenter);
		topPanel.setPresenter(presenter);
	}

	@Override
	public TopPanel getTopPanel() {
		return topPanel;
	}
	
	@Override
	public Search getSearchPanel() {
		return searchPanel;
	}

	@Override
	public FlexTable getRezeptList() {
		return rezeptList;
	}

	@Override
	public FlexTable getRezeptEditList() {
		return rezeptEditList;
	}
	

	@Override
	public int getSelectedMonth() {
		return getTopPanel().Monate.getSelectedIndex()+1;
	}


	// some local variables for the scrolling behavior
	boolean reset = true;
	int displayHeight = 120;
	private MenuPreviewView menuPreview;
	private MenuPreviewView menuPreviewDialog;
	
	@UiHandler("scrollWorkspace")
    public void onScroll(ScrollEvent event) { 
		// here we still have an error, when the recipes differ in size...
		adjustStickyEdit();
    }



	private void adjustStickyEdit() {
		//		use this to check the scrolling events:
		//	titleHTML.setHTML("EditView: " + Integer.toString(rezeptEditView.getOffsetHeight()) + " scroll: " + Integer.toString(scrollWorkspace.getVerticalScrollPosition()));

		if(rezeptEditView.getOffsetHeight() < (scrollWorkspace.getVerticalScrollPosition()+displayHeight)){
			
			topSticky.setWidgetSize(panelNorth, 40+displayHeight);
			
			if(dragArea.getWidgetCount() > 0){
				dragArea.remove(0);
			}
			
			// TODO check if adding, means in deed, deleting it from the old place 
			// (and if anything is still correctly wired... )
			dragArea.add(rezeptEditView.htmlRezept);

			if(reset){
				scrollWorkspace.setScrollPosition(rezeptEditView.getOffsetHeight()+1);
				reset = false;
			}
		}
		
//		if(rezeptEditView.getOffsetHeight() > (scrollWorkspace.getVerticalScrollPosition()) && !reset){
		if(0 == (scrollWorkspace.getVerticalScrollPosition()) && !reset){
			
			topSticky.setWidgetSize(panelNorth, 40);
			rezeptEditView.dragArea.add(dragArea.getWidget(0));
			scrollWorkspace.setScrollPosition(rezeptEditView.getOffsetHeight()-displayHeight);
			reset = true;
			
		}
	} 

	
	@UiHandler("addRezeptButton")
	public void onAddRezeptButtonPress(ClickEvent event) {
		presenter.addNewRecipe();		
	}
	
	public void showRecipeClone(Recipe recipe) {
		// This is basically right now a clone procedure!
		// which is okay, if you don't own that recipe already...
		// otherwise you would want to edit the old one (or at least signal the 
		// clone procedure by pressing a "Duplicate this Menu" Button.
		
		// and it only gets called when pressing the new recipe button?
		
		// unstyle the old one
		styleRezept(selectedRezept, false);
		
		// hier wird zurückgesetzt, da sonst kein neues gemacht wird (this is a hack)
		selectedRezept = -1;
		
		ArrayList<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
		zutaten.clear();
		
		for(IngredientSpecification zutatNew : recipe.getZutaten()){
			
			// TODO check that nothing is missing
			final IngredientSpecification zutat = new IngredientSpecification(zutatNew.getId(), zutatNew.getName(),
					zutatNew.getCookingDate(),zutatNew.getZustand(),zutatNew.getProduktion(), 
					zutatNew.getTransportmittel());
			zutat.setDistance(zutatNew.getDistance());
			zutat.setHerkunft(zutatNew.getHerkunft());
			zutat.setMengeGramm(zutatNew.getMengeGramm());
			zutat.setSeason(zutatNew.getStartSeason(), zutatNew.getStopSeason());
			zutat.setZutat_id(zutatNew.getZutat_id());
			zutat.setNormalCO2Value(zutatNew.getNormalCO2Value());
			zutaten.add(zutat);
		}
		AddZutatZumMenu(zutaten);
		
		final RecipeView rezeptView = (RecipeView) rezeptList.getWidget(selectedRezept,1);
		
		if(!recipe.deviceSpecifications.isEmpty()){
		// add the devices...
			rezeptView.recipe.deviceSpecifications = new ArrayList<DeviceSpecification>(recipe.deviceSpecifications.size());
			for(DeviceSpecification devSpec:recipe.deviceSpecifications){
				DeviceSpecification devSpecClone = new DeviceSpecification(devSpec.deviceName,devSpec.deviceSpec, devSpec.kWConsumption, devSpec.duration);
				rezeptView.recipe.deviceSpecifications.add(devSpecClone);
			}
			String formatted = NumberFormat.getFormat("##").format( recipe.getDeviceCo2Value() );
			rezeptView.SuggestTable.setText(0,0,"Zubereitung");
			rezeptView.SuggestTable.setHTML(0,1,"ca <b>"+formatted+"g</b> *");
			rezeptView.PrepareButton.setText("Zubereitung bearbeiten");
		}
		
		rezeptView.RezeptName.setText(recipe.getSymbol());
		if(recipe.getSubTitle() == null){
			rezeptView.recipe.setSubTitle("Menu Beschriftung");
		} else {
			rezeptView.recipe.setSubTitle(recipe.getSubTitle());
		}
		
		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
		rezeptView.recipe.setSymbol(recipe.getSymbol());
		
		// here we set the ancestor if available
		if(recipe.getId() != null){
			rezeptView.recipe.setDirectAncestorID(recipe.getId());
		}
		
		rezeptView.RezeptName.setText(recipe.getSymbol());
		
		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
		rezeptView.makePublic.setValue(!recipe.openRequested);
		
		//TODO hinzufügen zu welchen Küchen das Rezept gehört.
		String kitchenString = "";
		Boolean gotOne = false;
		for(Long kitchenId : recipe.kitchenIds){
			Kitchen kitchen = presenter.getClientData().getKitchenByID(kitchenId);
			if(kitchen != null){
				kitchenString = kitchenString + " [" + kitchen.getSymbol() +"]";
				gotOne = true;
			}	
		}
		if(gotOne){
			kitchenString = " Ist den Küchen zugeordnet: " + kitchenString;
		}
		
		rezeptView.openHTML.setHTML("Nicht veröffentlicht."+kitchenString);
		if(recipe.isOpen()){
			rezeptView.openHTML.setHTML("Veröffentlicht."+kitchenString);
		} else if(recipe.openRequested){
			rezeptView.openHTML.setHTML("Veröffentlichung angefragt."+kitchenString);
		}
		
		if(recipe.getCookInstruction() != null){
			rezeptView.cookingInstr.setText(recipe.getCookInstruction());
			rezeptView.recipe.setCookInstruction(recipe.getCookInstruction());
		}
		
		rezeptView.showImageRezept = new Image();
		
		if(presenter.getLoginInfo().isLoggedIn()){
    	rezeptView.bildEntfernen = new Anchor("Bild entfernen");
    	rezeptView.bildEntfernen.addStyleName("platzrechts");
    	rezeptView.bildEntfernen.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				rezeptView.menuDecoInfo.remove(rezeptView.showImageRezept);
				rezeptView.uploadWidget.setVisible(true);
				rezeptView.bildEntfernen.setVisible(false);
				rezeptView.recipe.image = null;
//TODO				image should also be pulled out of the database to save space
			}
    	});
    	
    	rezeptView.menuDecoInfo.add(rezeptView.bildEntfernen);
    	rezeptView.bildEntfernen.setVisible(false);
		}
    	
	    if(recipe.image != null){
	    	rezeptView.getRezept().image = recipe.image;
	    	rezeptView.showImageRezept.setUrl(rezeptView.getRezept().image.getServingUrl()+"=s150-c");
	    	
	    	// TODO check if the following is legacy code...
//	    	setHTML("<img src='" +GWT.getModuleBaseURL()+ recipe.image.getServingUrl() + "' />"+recipe.getCookInstruction());
//	    	rezeptView.imageUploaderHP.add(showImage);
	    	
	    	rezeptView.imagePopUpHandler = rezeptView.showImageRezept.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ImageOverlay imageOverlay = new ImageOverlay(rezeptView.getRezept().image);
					imageOverlay.setLoginInfo(presenter.getLoginInfo());
//					imageOverlay.addGalleryUpdatedEventHandler(PhotoGallery.this);
					
					final PopupPanel imagePopup = new PopupPanel(true);
					imagePopup.setAnimationEnabled(true);
					imagePopup.setWidget(imageOverlay);
//					imagePopup.setGlassEnabled(true);
					imagePopup.setAutoHideEnabled(true);

					// TODO what is this???
					imagePopup.center();
					imagePopup.setPopupPosition(10, 10);
				}
			});
	    	rezeptView.showImageRezept.setStyleName("imageSmall");
	    	rezeptView.menuDecoInfo.insert(rezeptView.showImageRezept,0);
	    	if(rezeptView.uploadWidget != null){
	    		rezeptView.uploadWidget.setVisible(false);
	    	}
	    	if(presenter.getLoginInfo().isLoggedIn()){
	    	rezeptView.bildEntfernen.setVisible(true);
	    	}
	    }
	    
	    final Anchor mehrDetails = new Anchor("mehr Details");
	    mehrDetails.setStyleName("floatRight");
	    rezeptView.askForLess = false;
	    rezeptView.askForLess2 = true;
	    final String cookingInstructions = recipe.getCookInstruction();
	    
	    mehrDetails.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(rezeptView.askForLess){
					if(rezeptView.getRezept().image != null){
						rezeptView.showImageRezept.setUrl(rezeptView.getRezept().image.getServingUrl()+"=s150-c");
						rezeptView.showImageRezept.setWidth("150px");
						
						rezeptView.showImageRezept.setStyleName("imageSmall");
						
						
					}
					rezeptView.detailText.setVisible(false);
					rezeptView.cookingInstr.setVisible(true);
//					rezeptView.htmlCooking.setVisible(true);
					mehrDetails.setText("mehr Details");
					rezeptView.askForLess = false;
					if(rezeptView.showImageHandler != null){
						rezeptView.showImageHandler.removeHandler();
						rezeptView.showImageHandler = null;
					}
					
				} else {
					rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+cookingInstructions);
					if(rezeptView.getRezept().image != null){

//						rezeptView.overlap = Math.max(1,rezeptView.showImageRezept.getHeight() -  rezeptView.addInfoPanel.getOffsetHeight() +40);
//						rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+recipe.getCookInstruction());
						if(rezeptView.showImageHandler == null){
							rezeptView.showImageHandler = rezeptView.showImageRezept.addLoadHandler(new LoadHandler(){
								@Override
								public void onLoad(LoadEvent event) {
									if(rezeptView.askForLess2){
										rezeptView.overlap = Math.max(1,rezeptView.showImageRezept.getHeight() -  rezeptView.addInfoPanel.getOffsetHeight() +40);

										//				rezeptView.detailText.setHeight(height)
										rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+rezeptView.recipe.getCookInstruction());
										rezeptView.askForLess2 = false;
									}
								}
							});
						}
						rezeptView.showImageRezept.setUrl(rezeptView.getRezept().image.getServingUrl()+"=s800");
						rezeptView.showImageRezept.setWidth("340px");
						rezeptView.showImageRezept.setStyleName("imageBig");
//						rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+rezeptView.rezept.getCookInstruction());
						

					}
					rezeptView.detailText.setWidth("730px");
					rezeptView.detailText.setVisible(true);
					rezeptView.cookingInstr.setVisible(false);
//					rezeptView.htmlCooking.setVisible(false);
					mehrDetails.setText("weniger Details");
					rezeptView.askForLess = true;

				}
			}
	    	
	    });
	    
	    rezeptView.menuDecoInfo.insert(mehrDetails,1);
	   
	    if(recipe.getPersons() != null){
	    	 rezeptView.recipe.setPersons(recipe.getPersons());	    	
	    } else {
	    	rezeptView.recipe.setPersons(4l);
	    }
	    rezeptView.amountPersons.setText(rezeptView.recipe.getPersons().toString());
	    
	    rezeptView.cookingInstr.setText(recipe.getCookInstruction());
//	    rezeptView.showRezept(rezeptView.rezept);
	    rezeptView.showRezept(rezeptView.recipe);
	    rezeptView.saved = true;
	    if(presenter.getLoginInfo().isAdmin() && recipe.getEmailAddressOwner() != null ) {
	    	rezeptView.savedHTML.setHTML("gespeichert von "+recipe.getEmailAddressOwner());
		} else {
			rezeptView.savedHTML.setHTML("gespeichert");
		}
	    
	    adjustStickyEdit();
		
	}
	
	
	@UiHandler("rezeptList")
	void onRezeptClicked(ClickEvent event) {
		Cell cell = rezeptList.getCellForEvent(event);
		if (cell != null) {
			
			Widget rezeptViewWidget;
			
			if(selectedRezept != -1){
				rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
				RecipeView rezeptViewOld = (RecipeView) rezeptViewWidget;
				rezeptViewOld.isSelected = false;
			}
			
			styleRezept(selectedRezept, false);
			selectedRezept = cell.getRowIndex();
			styleRezept(selectedRezept, true);
			
			rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
			rezeptView.isSelected = true;
			
			
			rezeptEditList.removeRow(0);
			if(dragArea.getWidgetCount() > 0){
				dragArea.remove(0);
			}
			
			
			// put this recipe into the edit panel...
			if(rezeptEditList.getRowCount() == 0){
				rezeptEditList.insertRow(0);
			}
			rezeptEditView = new RecipeEditView(rezeptView.recipe, this);
			rezeptEditView.setPresenter(presenter);
			
			rezeptEditList.setWidget(0, 1, rezeptEditView);
			rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
			
			
			rezeptEditView.setRezept(rezeptView.recipe);
			rezeptEditView.showRezept(rezeptEditView.recipe);
			
			
			
			suggestionPanel.clear();
			suggestionPanel.add(new HTML("Es gibt hier noch keinen Vergleich"));
			rezeptView.updtTopSuggestion();
			
			adjustStickyEdit();

		}
	}
public int AddZutatZumMenu( Ingredient item) {
		
		// convert zutat to ZutatSpec and call the real method
		Extraction stdExtraction = null;
		for(Extraction extraction: item.getExtractions()){
			if(item.stdExtractionSymbol.equalsIgnoreCase(extraction.symbol)){
				stdExtraction = extraction;
			}
		}
		IngredientSpecification ingredientSpecification = new IngredientSpecification(item.getId(), item.getSymbol(),
				 new Date(),stdExtraction.stdCondition, stdExtraction.stdProduction, 
				 stdExtraction.stdMoTransportation);
		ingredientSpecification.setHerkunft(stdExtraction);
		ingredientSpecification.setMengeGramm(item.stdAmountGramm);
		ingredientSpecification.setSeason(stdExtraction.startSeason, stdExtraction.stopSeason);
		ingredientSpecification.setNormalCO2Value(item.getCo2eValue());
		ArrayList<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
		
		
		zutaten.add(ingredientSpecification);
		int row = AddZutatZumMenu(zutaten);
		
		return row;
	}
	
 int AddZutatZumMenu(final ArrayList<IngredientSpecification> zutatenNew) {
		
		ArrayList<IngredientSpecification> zutaten = (ArrayList<IngredientSpecification>) zutatenNew.clone();
		ListIterator<IngredientSpecification> iterator = zutaten.listIterator();
		while(iterator.hasNext()){
			IngredientSpecification zutatSpec = iterator.next();
			for(SingleDistance singleDistance : presenter.getClientData().getDistances()){
				if(singleDistance.getFrom().contentEquals(getTopPanel().currentHerkunft) && 
						singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
					
					zutatSpec.setDistance(singleDistance.getDistance());
					iterator.set(zutatSpec);
					break;
				}

			}
		}
		
		RecipeView rezeptView;
//		RecipeEditView rezeptEditView;
		
		// on case this is going to be a new one
		if (selectedRezept == -1){
			// create new Recipe
			Recipe newRezept = new Recipe();
			
			
			// what of the following becomes hence obsolete?
			// TODO check this:
			selectedRezept = 0;
			
//			both lists
			rezeptList.insertRow(0);
			if(rezeptEditList.getRowCount() == 0){
				rezeptEditList.insertRow(0);
			}
			
			//same as below
			newRezept.addZutaten(zutaten);
			
			// both get added
			rezeptView = new RecipeView(newRezept,this);
			rezeptView.setPresenter(presenter);
			
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			rezeptList.getRowFormatter().setStyleName(0, "recipe");
			styleRezept(selectedRezept, true);
			
			// the edit form
			rezeptEditView = new RecipeEditView(newRezept,this);
			rezeptEditView.setPresenter(presenter);
			
			rezeptEditList.setWidget(0, 1, rezeptEditView);
			rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
			
			
			// is it necessary to have a worksheet or is rezeptList already containing everything?
			// worksheet.add(rezeptView);
			
		} else {
			// get the old one
			rezeptView = (RecipeView) rezeptList.getWidget(selectedRezept,1);
			
			// there is only one recipe getting handled by both views!
			Recipe recipe = rezeptView.getRezept();
			recipe.addZutaten(zutaten);
			rezeptView.setRezept(recipe);

			
			// also manipulate the edit one...
			// this better should be mirrored
			rezeptEditView = (RecipeEditView) rezeptEditList.getWidget(0,1);
			rezeptEditView.setRezept(recipe);
			rezeptView.showRezept(rezeptView.recipe);
			
		}
		// this is necessary.
		rezeptEditView.showRezept(rezeptEditView.recipe);

		return selectedRezept;
	}
	
	public void updateSaisonAndMore() {

		rezeptEditView.updateSaison();
		for(IngredientSpecification zutat : rezeptEditView.recipe.getZutaten()){
			rezeptEditView.changeIcons(rezeptEditView.recipe.getZutaten().indexOf(zutat), zutat);
		}
		
		for( Widget rezeptViewWidget : rezeptList){
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
			rezeptView.updateSaison();
			for(IngredientSpecification zutat : rezeptView.recipe.getZutaten()){
				rezeptView.changeIcons(rezeptView.recipe.getZutaten().indexOf(zutat), zutat);
			}
		}
	}
	
	public void styleRezept(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRezept();

			if (selected) {
				// color the recipe
				rezeptList.getRowFormatter().addStyleName(row, style);
				// TODO maybe it makes sense to color even more elements in here
			} else {
				rezeptList.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	

	@UiHandler("MenuTable")
	void onTableClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		if (presenter != null) {
			FlexTable.Cell cell = MenuTable.getCellForEvent(event);

			if (cell != null) {
				int row = cell.getRowIndex();
				rezeptEditView.selectRow(row);
			}
		}
	}

	@Override
	public void setSelectedRezept(int rezeptPositionInList) {
		this.selectedRezept = rezeptPositionInList;
		
	}

	@Override
	public int getSelectedRezept() {
		return selectedRezept;
	}
	

	@Override
	public HorizontalPanel getSuggestionPanel() {
		return suggestionPanel;
	}

	@Override
	public AbsolutePanel getDragArea() {
		return dragArea;
	}

	@Override
	public void setTitleHTML(String string) {
		titleHTML.setHTML(string);
	}

	@Override
	public void setMenuPreviewDialog(MenuPreviewView menuPreviewDialog){
		this.menuPreviewDialog = menuPreviewDialog;
		menuPreviewDialog.setName(name);
	}

	@Override
	public void onResize() {
		// this is not getting called
		if(menuPreviewDialog != null){
			menuPreviewDialog.center();
			menuPreviewDialog.positionDialog();
		}
	}




}
