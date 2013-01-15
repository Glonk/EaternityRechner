package ch.eaternity.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

import ch.eaternity.client.place.GoodbyePlace;
import ch.eaternity.client.ui.EaternityRechnerView.Presenter;
import ch.eaternity.client.ui.widgets.ImageOverlay;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.RecipeEditView;
import ch.eaternity.client.ui.widgets.RecipeView;
import ch.eaternity.client.ui.widgets.Search;
import ch.eaternity.client.ui.widgets.TopPanel;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;

public class EaternityRechnerViewImpl<T> extends SimpleLayoutPanel implements EaternityRechnerView<T>
{
	@UiTemplate("EaternityRechnerViewImpl.ui.xml")
	interface EaternityRechnerViewUiBinder extends UiBinder<DockLayoutPanel, EaternityRechnerViewImpl> {}
	private static EaternityRechnerViewUiBinder uiBinder =
		GWT.create(EaternityRechnerViewUiBinder.class);


	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}


	@UiField TopPanel topPanel;
	@UiField Search searchPanel;  

	@UiField FlexTable rezeptList;
	@UiField FlexTable rezeptEditList;
	@UiField HTML titleHTML;
	@UiField DockLayoutPanel topSticky;
	@UiField HTMLPanel panelNorth;
	@UiField AbsolutePanel topDragArea;
	@UiField HTMLPanel topOverflowArea;

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
		setWidget(uiBinder.createAndBindUi(this));
		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");


		// TODO Move cursor focus to the Search box.
		// TODO adjust this with something that makes more sense / what would that be? 1024 width...
		rezeptList.getColumnFormatter().setWidth(1, "750px");
		rezeptEditList.getColumnFormatter().setWidth(1, "750px");

	
	    Element topElem = topSticky.getWidgetContainerElement(panelNorth);
	    topElem.getStyle().setZIndex(2);
	    topElem.getStyle().setOverflow(Overflow.VISIBLE);
		
		//TODO Reactivate for later use
		suggestionPanel.setVisible(false);
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



	
	@UiHandler("scrollWorkspace")
    public void onScroll(ScrollEvent event) { 
		// here we still have an error, when the recipes differ in size...
		adjustStickyEditLayout();
    }

	// some local variables for the scrolling behavior
	public boolean editCoverActivated = false;
	int displayHeight = 120;
	private MenuPreviewView menuPreview;
	private MenuPreviewView menuPreviewDialog;
	
	HTML spaceholder = new HTML();
	Widget recipeEditObject;
	Integer saveHeight;
	
	public void setEditCoverActivated(boolean b){
		editCoverActivated = b;
	}

	public void adjustStickyEditLayout() {
//		titleHTML.setHTML("EditView: " + Integer.toString(rezeptEditView.getOffsetHeight()) + " scroll: " + Integer.toString(scrollWorkspace.getVerticalScrollPosition()));

		// all this procedure is only relevant, if the recipe is open
		// and this is the case, when a recipe is selected!
		if(selectedRezept != -1){

			if(!editCoverActivated && (rezeptEditView.getOffsetHeight() < (scrollWorkspace.getVerticalScrollPosition()+displayHeight))){

				recipeEditObject = rezeptEditView.dragArea.getWidget(0);
				saveHeight = rezeptEditView.getOffsetHeight();

				spaceholder.setHTML("<div style='height: " + Integer.toString(recipeEditObject.getOffsetHeight()) + "px;width:764px'></div>");

				//			rezeptEditView.dragArea.setHeight(Integer.toString(recipeEditObject.getOffsetHeight()));

				rezeptEditView.dragArea.remove(recipeEditObject);
				rezeptEditView.dragArea.add(spaceholder);


				topDragArea.add(recipeEditObject);
				panelNorth.setHeight("142px");
				topOverflowArea.setHeight("120px");

				editCoverActivated = true;

			} 

			if(editCoverActivated && (saveHeight >= (scrollWorkspace.getVerticalScrollPosition()+displayHeight))){
				Widget recipeEditObject = topDragArea.getWidget(0);
				//			topDragArea.add(new HTML("<div style='height: " + recipeEditObject.getOffsetHeight() + "px'></div>"));
				topDragArea.remove(recipeEditObject);
				rezeptEditView.dragArea.add(recipeEditObject);
				rezeptEditView.dragArea.remove(spaceholder);
				panelNorth.setHeight("22px");
				topOverflowArea.setHeight("0px");
				editCoverActivated = false;
			}

		}
	} 

	
	@UiHandler("addRezeptButton")
	public void onAddRezeptButtonPress(ClickEvent event) {
		presenter.getDAO().createRecipe();		
	}
	
	//REFACTOR: Recipe included
	public void cloneRecipe(Recipe recipe) {
		// This is basically right now a clone procedure!
		// which is okay, if you don't own that recipe already...
		// otherwise you would want to edit the old one (or at least signal the 
		// clone procedure by pressing a "Duplicate this Menu" Button.
		
		// this should actually be in the activity class
		
		final RecipeView rezeptView = createNewRecipeView();
	
		
		// clone zutaten in new list
		ArrayList<IngredientSpecification> zutatenNew = new ArrayList<IngredientSpecification>();
		zutatenNew.clear();
		
		for(IngredientSpecification zutatNew : recipe.getZutaten()){
			
			// TODO check that nothing is missing
			// we could use deep copying. actually, why do we copy at all?
			final IngredientSpecification zutat = new IngredientSpecification(zutatNew.getId(), zutatNew.getName(),
					zutatNew.getCookingDate(),zutatNew.getZustand(),zutatNew.getProduktion(), 
					zutatNew.getTransportmittel());
			zutat.setDistance(zutatNew.getDistance());
			zutat.setHerkunft(zutatNew.getHerkunft());
			zutat.setMengeGramm(zutatNew.getMengeGramm());
			zutat.setSeason(zutatNew.getStartSeason(), zutatNew.getStopSeason());
			zutat.setZutat_id(zutatNew.getZutat_id());
			zutat.setNormalCO2Value(zutatNew.getNormalCO2Value());
			zutat.setCost(zutatNew.getCost());
			zutat.update();
			zutatenNew.add(zutat);
		}
		
		addIngredientsToMenu(zutatenNew,rezeptView);
		
	
		
//		final RecipeView rezeptView = (RecipeView) rezeptList.getWidget(selectedRezept,1);
		
		if(!recipe.deviceSpecifications.isEmpty()){
		// add the devices...
			rezeptView.recipe.deviceSpecifications = new ArrayList<DeviceSpecification>(recipe.deviceSpecifications.size());
			for(DeviceSpecification devSpec:recipe.deviceSpecifications){
				DeviceSpecification devSpecClone = new DeviceSpecification(devSpec.deviceName,devSpec.deviceSpec, devSpec.kWConsumption, devSpec.duration);
				rezeptView.recipe.deviceSpecifications.add(devSpecClone);
			}
			String formatted = NumberFormat.getFormat("##").format( recipe.getDeviceCo2Value() );
//			rezeptView.SuggestTable.setText(0,0,"Zubereitung");
//			rezeptView.SuggestTable.setHTML(0,1,"ca <b>"+formatted+"g</b> *");
//			rezeptView.PrepareButton.setText("Zubereitung bearbeiten");
		}
		
		rezeptView.RezeptName.setText(recipe.getSymbol());
		if(recipe.getSubTitle() == null){
			rezeptView.recipe.setSubTitle("Menu Beschriftung");
		} else {
			rezeptView.recipe.setSubTitle(recipe.getSubTitle());
		}
		
		
//		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
		rezeptView.recipe.setSymbol(recipe.getSymbol());
		
		if(recipe.getCookInstruction() == null){
			String cookingIntructions = "Kochanleitung.";
			if(!presenter.getLoginInfo().isLoggedIn()){
				cookingIntructions = "Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.";
			}
			rezeptView.recipe.setCookInstruction(cookingIntructions);
		}
		
		// here we set the ancestor if available
		if(recipe.getId() != null){
			rezeptView.recipe.setDirectAncestorID(recipe.getId());
		}
		
		rezeptView.RezeptName.setText(recipe.getSymbol());
		
//		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
		rezeptView.makeNotPublic.setValue(!recipe.openRequested);
		
		//TODO hinzufügen zu welchen Küchen das Rezept gehört.
		String kitchenString = "";
		Boolean gotOne = false;
		for(Long kitchenId : recipe.kitchenIds){
			Workgroup kitchen = presenter.getClientData().getKitchenByID(kitchenId);
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
//			rezeptView.cookingInstr.setText(recipe.getCookInstruction());
			rezeptView.recipe.setCookInstruction(recipe.getCookInstruction());
		}
		
		//Date
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yy");
		Date date = recipe.cookingDate;
		if(date != null)
		{
			rezeptView.recipe.cookingDate = date;
			rezeptView.recipeDate.setText(dtf.format(date));
		}
		
		rezeptView.showImageRezept = new Image();
		
		if(presenter.getLoginInfo().isLoggedIn()){
    	rezeptView.bildEntfernen = new Anchor("Bild entfernen");
    	rezeptView.bildEntfernen.addStyleName("platzrechts");
    	rezeptView.bildEntfernen.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
//				rezeptView.menuDecoInfo.remove(rezeptView.showImageRezept);
//				rezeptView.uploadWidget.setVisible(true);
				rezeptView.bildEntfernen.setVisible(false);
				rezeptView.recipe.image = null;
//TODO				image should also be pulled out of the database to save space
			}
    	});
    	
//    	rezeptView.menuDecoInfo.add(rezeptView.bildEntfernen);
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
//	    	rezeptView.menuDecoInfo.insert(rezeptView.showImageRezept,0);
//	    	if(rezeptView.uploadWidget != null){
//	    		rezeptView.uploadWidget.setVisible(false);
//	    	}
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
//					rezeptView.detailText.setVisible(false);
//					rezeptView.cookingInstr.setVisible(true);
//					rezeptView.htmlCooking.setVisible(true);
					mehrDetails.setText("mehr Details");
					rezeptView.askForLess = false;
					if(rezeptView.showImageHandler != null){
						rezeptView.showImageHandler.removeHandler();
						rezeptView.showImageHandler = null;
					}
					
				} else {
//					rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+cookingInstructions);
					if(rezeptView.getRezept().image != null){

//						rezeptView.overlap = Math.max(1,rezeptView.showImageRezept.getHeight() -  rezeptView.addInfoPanel.getOffsetHeight() +40);
//						rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+recipe.getCookInstruction());
						if(rezeptView.showImageHandler == null){
							rezeptView.showImageHandler = rezeptView.showImageRezept.addLoadHandler(new LoadHandler(){
								@Override
								public void onLoad(LoadEvent event) {
									if(rezeptView.askForLess2){
//										rezeptView.overlap = Math.max(1,rezeptView.showImageRezept.getHeight() -  rezeptView.addInfoPanel.getOffsetHeight() +40);
//
//										//				rezeptView.detailText.setHeight(height)
//										rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+rezeptView.recipe.getCookInstruction());
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
//					rezeptView.detailText.setWidth("730px");
//					rezeptView.detailText.setVisible(true);
//					rezeptView.cookingInstr.setVisible(false);
//					rezeptView.htmlCooking.setVisible(false);
					mehrDetails.setText("weniger Details");
					rezeptView.askForLess = true;

				}
			}
	    	
	    });
	    
//	    rezeptView.menuDecoInfo.insert(mehrDetails,1);
	   
	    if(recipe.getPersons() != null){
	    	 rezeptView.recipe.setPersons(recipe.getPersons());	    	
	    } else {
	    	rezeptView.recipe.setPersons(4l);
	    }
	    
	    if(recipe.comments.size() > 0){
	    	rezeptView.recipe.comments.clear();
			for (int i = 0; i < recipe.comments.size(); i++) {
				rezeptView.recipe.comments.add(recipe.comments.get(i));
			}
	    }
	    
//	    rezeptView.amountPersons.setText(rezeptView.recipe.getPersons().toString());
	    
//	    rezeptView.cookingInstr.setText(recipe.getCookInstruction());
//	    rezeptView.showRezept(rezeptView.rezept);
//	    rezeptView.showRezept(rezeptView.recipe);

		
	    rezeptView.saved = true;
	    
	    if(presenter.getLoginInfo().isAdmin() && recipe.getEmailAddressOwner() != null ) {
	    	rezeptView.savedHTML.setVisible(true);
	    	rezeptView.savedHTML.setHTML("Autor: "+recipe.getEmailAddressOwner());
		} else {
//			rezeptView.savedHTML.setHTML("gespeichert");
			rezeptView.savedHTML.setVisible(false);
		}
	    


		// now show this recipe
		rezeptView.showRezept(rezeptView.recipe);

		displayRecipeEditView(rezeptView);
	    adjustStickyEditLayout();
	    
		
	}

	//REFACTOR: correct, kommuniziert mit seinen Views
	public RecipeView createNewRecipeView() {
		// unstyle the old recipe
		styleRezept(selectedRezept, false);
		
		
		Recipe newRezept = new Recipe();
		final RecipeView rezeptView = new RecipeView(newRezept, this);
		rezeptView.setPresenter(presenter);
		
		selectedRezept = 0;
		
//		This is a new recipe, so we add it to the list
		rezeptList.insertRow(0);
		

			
		rezeptList.setWidget(selectedRezept, 1, rezeptView);
		rezeptList.getRowFormatter().setStyleName(0, "recipe");
		styleRezept(selectedRezept, true);
		
		return rezeptView;
	
		
	}


	public void displayRecipeEditView(final RecipeView rezeptView) {
		// recipe edit view procedure 
		if(rezeptEditList.getRowCount() == 0){
			rezeptEditList.insertRow(0);
		}

		rezeptEditView = new RecipeEditView( rezeptView, rezeptView.recipe,this);
		rezeptEditView.setPresenter(presenter);
		rezeptEditView.showRezept(rezeptView.recipe);
		
		rezeptEditList.setWidget(0, 1, rezeptEditView);
		
		rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
		
//		rezeptEditView = (RecipeEditView) rezeptEditList.getWidget(0,1);
//		rezeptEditView.setRezept(rezeptView.recipe);
	
	}
	
	
	@UiHandler("rezeptList")
	void onRezeptClicked(ClickEvent event) {
		Cell cell = rezeptList.getCellForEvent(event);
		if (cell != null && selectedRezept != cell.getRowIndex()) {
			
			Widget rezeptViewWidget;
			
			if(selectedRezept > -1){
				rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
				RecipeView rezeptViewOld = (RecipeView) rezeptViewWidget;
				rezeptViewOld.isSelected = false;
			}
			
			
			// color the right recipe, and get the selected row index
			styleRezept(selectedRezept, false);
			selectedRezept = cell.getRowIndex();
			styleRezept(selectedRezept, true);
			
			// this is the new recipe
			rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
			rezeptView.isSelected = true;
			
			
			if(rezeptEditList.getRowCount() > 0){
			rezeptEditList.removeRow(0);
			}
			if(topDragArea.getWidgetCount() > 0){
				topDragArea.remove(0);
			}
			
			
			
			// put this recipe into the edit panel...
			if(rezeptEditList.getRowCount() == 0){
				rezeptEditList.insertRow(0);
			}
		
			
			rezeptEditView = new RecipeEditView(rezeptView, rezeptView.recipe, this);
			rezeptEditView.setPresenter(presenter);
			
			
			editCoverActivated = false;
			rezeptEditList.setWidget(0, 1, rezeptEditView);
			rezeptEditList.getRowFormatter().setStyleName(0, "recipe");
			
			rezeptEditView.setRezept(rezeptView.recipe, rezeptView);
			rezeptEditView.showRezept(rezeptEditView.recipe);
			
			
			
			suggestionPanel.clear();
			suggestionPanel.add(new HTML("Es gibt hier noch keinen Vergleich"));
			// rezeptView.updtTopSuggestion();
			
			adjustStickyEditLayout();

		}
	}
	
	//REFACTOR: direkt zum DataStore, Event
public void addOneIngredientToMenu(Ingredient item, RecipeView rezeptView, int grams) {
		
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
		if (grams == 0)
			ingredientSpecification.setMengeGramm(item.stdAmountGramm);
		else
			ingredientSpecification.setMengeGramm(grams);
		ingredientSpecification.setSeason(stdExtraction.startSeason, stdExtraction.stopSeason);
		ingredientSpecification.setNormalCO2Value(item.getCo2eValue());
		ArrayList<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
		
		
		zutaten.add(ingredientSpecification);
		addIngredientsToMenu(zutaten, rezeptView);
		
		
	}
	
 void addIngredientsToMenu(final ArrayList<IngredientSpecification> zutatenNew,	RecipeView rezeptView) {
		
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
		

//		RecipeEditView rezeptEditView;
		
		// on case this is going to be a new one
	
			// get the old one
//			rezeptView = (RecipeView) rezeptList.getWidget(selectedRezept,1);
			
			// there is only one recipe getting handled by both views!
			Recipe recipe = rezeptView.getRezept();
			recipe.addZutaten(zutaten);
			
			// wohooo, what did i do here... this needs to be much cleaner this procedure
			
			
			rezeptView.setRezept(recipe);
			 
			
			// also manipulate the edit one...
			// this better should be mirrored ... out now:
			editCoverActivated = false;	

	
	}
	
//REFACTOR: Event MonthChanged
	public void updateSaisonAndMore() {

		rezeptEditView.updateSaison();
		for(IngredientSpecification zutat : rezeptEditView.recipe.getZutaten()){
			rezeptEditView.changeIcons(rezeptEditView.recipe.getZutaten().indexOf(zutat), zutat);
		}
		
		for( Widget rezeptViewWidget : rezeptList){
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
//			rezeptView.updateSaison();
			for(IngredientSpecification zutat : rezeptView.recipe.getZutaten()){
//				rezeptView.changeIcons(rezeptView.recipe.getZutaten().indexOf(zutat), zutat);
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
	


	@Override
	public void setSelectedRecipeNumber(int rezeptPositionInList) {
		this.selectedRezept = rezeptPositionInList;
		
	}

	@Override
	public int getSelectedRecipeNumber() {
		return selectedRezept;
	}
	

	@Override
	public HorizontalPanel getSuggestionPanel() {
		return suggestionPanel;
	}

	@Override
	public AbsolutePanel getDragArea() {
		return topDragArea;
	}

	@Override
	public void setTitleHTML(String string) {
		titleHTML.setHTML(string);
	}

	@Override
	public void setMenuPreviewDialog(MenuPreviewView menuPreviewDialog){
		this.menuPreviewDialog = menuPreviewDialog;
//		menuPreviewDialog.setName(name);
	}

	@Override
	public void onResize() {
		// this is not getting called
		if(menuPreviewDialog != null){
//			menuPreviewDialog.center();
			menuPreviewDialog.positionDialog();
		}
	}

	
	//REFACTOR: correct
	@Override
	public void closeRecipeEditView() {

		//TODO we need to cover the case when the topCoverView is activated!!!
		if(editCoverActivated){
			
			// this is a replicate from the adjustStickyEdit Function!
			Widget recipeEditObject = topDragArea.getWidget(0);
			topDragArea.remove(recipeEditObject);
			rezeptEditView.dragArea.remove(spaceholder);
			panelNorth.setHeight("22px");
			topOverflowArea.setHeight("0px");
			editCoverActivated = false;

		} 
		else {
			getRezeptEditList().remove(rezeptEditView);
			if(getDragArea().getWidgetCount() > 0){
				getDragArea().remove(0);
			}
		}
		styleRezept(getSelectedRecipeNumber(), false);
		setSelectedRecipeNumber(-1);
		getSuggestionPanel().clear();

		setTitleHTML("Sie bearbeiten soeben kein Menu.");
		
	}

	@Override
	public RecipeView<?> getSelectedRecipeView() {
		RecipeView<?> recipeView = (RecipeView<?>) rezeptList.getWidget(selectedRezept,1);
		return recipeView;
	}




}
