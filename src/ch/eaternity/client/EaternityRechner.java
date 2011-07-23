package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;



import ch.eaternity.client.widgets.ImageOverlay;
import ch.eaternity.client.widgets.PhotoGallery;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;


/**
 * This application demonstrates how to construct a relatively complex user
 * interface, similar to many common email readers. It has no back-end,
 * populating its components with hard-coded data.
 */

public class EaternityRechner implements EntryPoint {

	public static LoginInfo loginInfo = null;
	private final static DataServiceAsync dataService = GWT.create(DataService.class);
	private List<RezeptView> worksheet = new ArrayList<RezeptView>();
	// private VerticalPanel loginPanel = new VerticalPanel();
	// private Label loginLabel = new Label("Please sign in to your Google Account to access the eaternity Rechner application.");
	// private Anchor signInLink = new Anchor("Sign In");
	//private Anchor signOutLink = new Anchor("Sign Out");
	private static Data clientData = new Data();
	
	interface Binder extends UiBinder<DockLayoutPanel, EaternityRechner> { }
	
	interface SelectionStyle extends CssResource {
		String selectedRezept();
	}

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField TopPanel topPanel;
	@UiField Search search;  
	@UiField
	static FlexTable rezeptList;
	@UiField Button addRezeptButton;
	@UiField
	static SelectionStyle selectionStyle;
	@UiField static HorizontalPanel suggestionPanel;
	
	
	static int selectedRezept = -1;
	
	private HandlerRegistration adminHandler;
	private HandlerRegistration ingredientHandler;
	static String styleNameOverlap = "overlap";
	
	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		
		// now load the data

		loadData();

		// Inject global styles.
		GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();
		// Create the UI defined in EaternityRechner.ui.xml.
		DockLayoutPanel outer = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");



		// Listen for item selection, displaying the currently-selected item in
		// the detail area.
		//	    mailList.setListener(new MailList.Listener() {
		//	      public void onItemSelected(MailItem item) {
		//	        mailDetail.setItem(item);
		//	      }
		//	    });

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		// mainPanel.add(signOutLink);
		
		// TODO Move cursor focus to the Search box.
		// TODO remove this with something that makes more sense
		rezeptList.getColumnFormatter().setWidth(1, "750px");

		
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		
		suggestionPanel.setVisible(false);
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		//TODO uncomment this when ready!
		
		
		//	  
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					loadYourRechner();
					if(loginInfo.isAdmin()) {
						adminHandler = loadAdmin();
					} 
				} else   {
					loadLogin();
					if(adminHandler != null){
						adminHandler.removeHandler();
						ingredientHandler.removeHandler();
					}
				}
			}
		});
		


	}

	

	static void addRezept(final Recipe recipe, final RezeptView rezeptView) {
		if(!TopPanel.leftKitchen){
			recipe.kitchenId = TopPanel.selectedKitchen.id;
		}
		dataService.addRezept(recipe, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Long id) {
//Window.alert("good");
//				Search.displayRezept(recipe);
				Search.yourMealsPanel.setVisible(true);
				recipe.setId(id);
				Search.clientData.getYourRezepte().add(recipe);
				Search.updateResults(Search.SearchInput.getText());
				rezeptView.saved = true;
				
				
			}
		});
	}
	static void removeRezept(final Recipe recipe) {
		dataService.removeRezept(recipe.getId(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				Search.clientData.getYourRezepte().remove(recipe);
				if(recipe.isOpen()){
				Search.clientData.getPublicRezepte().remove(recipe);
				}
			}
		});
	}
	
	static void rezeptApproval(final Recipe recipe, final Boolean approve) {
		dataService.approveRezept(recipe.getId(), approve,new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
// here happens some graphics clinch... or somewhere else...
				Search.clientData.getPublicRezepte().remove(recipe);
				recipe.open = approve;
				Search.clientData.getPublicRezepte().add(recipe);
				
				Search.updateResults(Search.SearchInput.getText());
			}
		});
	}
	
	private static void undisplayRezept(String rezept_id) {

	}

	private void loadLogin() {
		// Assemble login panel.
		topPanel.signInLink.setHref(loginInfo.getLoginUrl());
		
		// TODO only show when logged in:
		//SaveRezeptPanel.setVisible(false);
	}

	private void loadYourRechner() {
		topPanel.signOutLink.setHref(loginInfo.getLogoutUrl());
		topPanel.signInLink.setVisible(false);
		topPanel.signOutLink.setVisible(true);
		
		topPanel.loginLabel.setText("Willkommen "+ loginInfo.getNickname() +".");
		
		// load your kitchens
		// loadYourKitchens();
		
		if(loginInfo.getInKitchen()){
//			topPanel.stepIn.setVisible(false);
//			topPanel.stepOut.setVisible(true);
			topPanel.location.setVisible(false);
//			topPanel.kitchen.setVisible(true);
		} else {
//			topPanel.stepIn.setVisible(true);
//			topPanel.stepOut.setVisible(false);	
			topPanel.location.setVisible(true);
//			topPanel.kitchen.setVisible(false);
		}
		
		//load your personal recipes
		loadYourRezepte();



	}
	private HandlerRegistration loadAdmin() {
		
		ingredientHandler = topPanel.ingredientLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				IngredientsDialog dlg = new IngredientsDialog();
				dlg.show();
				dlg.center();
			}
		});
		topPanel.ingredientLink.setVisible(true);
		
		// Always display the Kitchen Dialog of all Customers...
		// this means, pull all kitchens from the server
		
		// TODO get all the other recipes
		dataService.getAdminRezepte(new AsyncCallback<List<Recipe>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Recipe> rezepte) {
				Data data = Search.clientData;
				data.setPublicRezepte(rezepte);
				Search.clientData = data;
				Search.updateResults(" ");
			}
		});
		
		
		// TODO get all the other recipes
		dataService.getAdminKitchens(new AsyncCallback<List<Kitchen>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			
			public void onSuccess(List<Kitchen> result) {
				Data data = Search.clientData;
				
				if(result.size() != 0){ // there must be somthing!
				data.kitchens.addAll(result);
				}
				// this shouldn't be necessary, as we are working with pointers:
//				Search.setClientData(data);
				
				// is there anything to update?
//				Search.updateResults(" ");
				
			}
		});
		
		return adminHandler;
	}

	public static void ShowRezept(final Recipe recipe) {
		// create a new one
		
		styleRezept(selectedRezept, false);
		
		selectedRezept = -1;
		ArrayList<IngredientSpecification> zutaten = new ArrayList<IngredientSpecification>();
		zutaten.clear();
		for(IngredientSpecification zutatNew : recipe.getZutaten()){
			
			// TODO that nothing is missing
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
		final RezeptView rezeptView = (RezeptView) rezeptList.getWidget(selectedRezept,1);
		
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
			rezeptView.recipe.setSubTitle("Recipe Untertitel");
		} else {
			rezeptView.recipe.setSubTitle(recipe.getSubTitle());
		}
		
		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
		rezeptView.recipe.setSymbol("Ihr " + recipe.getSymbol());
		
		rezeptView.rezeptNameTop.setText("Ihr " + recipe.getSymbol());
		rezeptView.titleHTML.setText(recipe.getSymbol());
		
		rezeptView.rezeptSubTitleTop.setText(recipe.getSubTitle());
		rezeptView.makePublic.setValue(!recipe.openRequested);
		
		rezeptView.openHTML.setHTML("nicht veröffentlicht");
		if(recipe.isOpen()){
			rezeptView.openHTML.setHTML("veröffentlicht");
		} else if(recipe.openRequested){
			rezeptView.openHTML.setHTML("Veröffentlichung angefragt");
		}
		
		if(recipe.getCookInstruction() != null){
			rezeptView.htmlCooking.setHTML(recipe.getCookInstruction());
			rezeptView.recipe.setCookInstruction(recipe.getCookInstruction());
		}
		
		rezeptView.showImageRezept = new Image();
		
		if(loginInfo.isLoggedIn()){
    	rezeptView.bildEntfernen = new Anchor("Bild entfernen");
    	rezeptView.bildEntfernen.addStyleName("platzrechts");
    	rezeptView.bildEntfernen.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				rezeptView.menuDecoInfo.remove(rezeptView.showImageRezept);
				rezeptView.uploadWidget.setVisible(true);
				rezeptView.bildEntfernen.setVisible(false);
				rezeptView.recipe.image = null;
			}
    	});
    	
    	rezeptView.menuDecoInfo.add(rezeptView.bildEntfernen);
    	rezeptView.bildEntfernen.setVisible(false);
		}
    	
	    if(recipe.image != null){
	    	rezeptView.getRezept().image = recipe.image;
	    	rezeptView.showImageRezept.setUrl(rezeptView.getRezept().image.getServingUrl()+"=s150-c");
//	    	setHTML("<img src='" +GWT.getModuleBaseURL()+ recipe.image.getServingUrl() + "' />"+recipe.getCookInstruction());
//	    	rezeptView.imageUploaderHP.add(showImage);
	    	
	    	rezeptView.imagePopUpHandler = rezeptView.showImageRezept.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ImageOverlay imageOverlay = new ImageOverlay(rezeptView.getRezept().image, loginInfo);
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
	    	if(loginInfo.isLoggedIn()){
	    	rezeptView.bildEntfernen.setVisible(true);
	    	}
	    }
	    final Anchor mehrDetails = new Anchor("mehr Details");
	    mehrDetails.setStyleName("floatRight");
	    rezeptView.askForLess = false;
	    rezeptView.askForLess2 = true;
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
//					rezeptView.cookingInstr.setVisible(true);
					rezeptView.htmlCooking.setVisible(true);
					mehrDetails.setText("mehr Details");
					rezeptView.askForLess = false;
					if(rezeptView.showImageHandler != null){
						rezeptView.showImageHandler.removeHandler();
						rezeptView.showImageHandler = null;
					}
					
				} else {
					rezeptView.detailText.setHTML("<img src='pixel.png' style='float:right' width=360 height="+ Integer.toString(rezeptView.overlap)+" />"+recipe.getCookInstruction());
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
					rezeptView.htmlCooking.setVisible(false);
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
	    if(loginInfo.isAdmin() && recipe.getEmailAddressOwner() != null ) {
	    	rezeptView.savedHTML.setHTML("gespeichert von "+recipe.getEmailAddressOwner());
		} else {
			rezeptView.savedHTML.setHTML("gespeichert");
		}
		
	}
	
	@UiHandler("addRezeptButton")
	public void onButtonPress(ClickEvent event) {
		Recipe recipe = new Recipe();
		recipe.setSymbol("unbenanntes Recipe");
		recipe.setSubTitle(" ");
		recipe.setCookInstruction("keine Kochanleitung.");
		recipe.open = false;
		recipe.openRequested = true;
		ShowRezept(recipe);	
	}
	
	@UiHandler("rezeptList")
	void onRezeptClicked(ClickEvent event) {
		Cell cell = rezeptList.getCellForEvent(event);
		if (cell != null) {
			styleRezept(selectedRezept, false);
			selectedRezept = cell.getRowIndex();
			styleRezept(selectedRezept, true);
			
			Widget rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RezeptView rezeptView = (RezeptView) rezeptViewWidget;
			suggestionPanel.clear();
			suggestionPanel.add(new HTML("Es gibt hier noch keinen Vergleich"));
			rezeptView.updtTopSuggestion();

		}
	}
	
	public static void styleRezept(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRezept();

			if (selected) {
				rezeptList.getRowFormatter().addStyleName(row, style);
			} else {
				rezeptList.getRowFormatter().removeStyleName(row, style);
			}
		}
	}
	
	public static int AddZutatZumMenu( Ingredient item) {
		// convert zutat to ZutatSpex and call the real method
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

	static int AddZutatZumMenu(final ArrayList<IngredientSpecification> zutatenNew) {
		ArrayList<IngredientSpecification> zutaten = (ArrayList<IngredientSpecification>) zutatenNew.clone();
		ListIterator<IngredientSpecification> iterator = zutaten.listIterator();
		while(iterator.hasNext()){
			IngredientSpecification zutatSpec = iterator.next();
			for(SingleDistance singleDistance : Search.clientData.getDistances()){
				if(singleDistance.getFrom().contentEquals(TopPanel.currentHerkunft) && 
						singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
					
					zutatSpec.setDistance(singleDistance.getDistance());
					iterator.set(zutatSpec);
					break;
				}

			}
		}
		RezeptView rezeptView;
		if (selectedRezept == -1){
			// create new Recipe
			Recipe newRezept = new Recipe();
			selectedRezept = 0;
			rezeptList.insertRow(0);
			
			
			//same as below
			newRezept.addZutaten(zutaten);
			rezeptView = new RezeptView(newRezept);
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			rezeptList.getRowFormatter().setStyleName(0, "recipe");
			styleRezept(selectedRezept, true);
			
			// is it necessary to have a worksheet or is rezeptList already containing everything?
			// worksheet.add(rezeptView);
			
		} else {
			// get the old one
			rezeptView = (RezeptView) rezeptList.getWidget(selectedRezept,1);
			Recipe recipe = rezeptView.getRezept();
			
			// maybe same as above
			//zutaten.addAll(zutaten);
			recipe.addZutaten(zutaten);
			rezeptView.setRezept(recipe);
//			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			//worksheet.set(selectedRezept, rezeptView);
		}
		// is this necessary?
		rezeptView.showRezept(rezeptView.recipe);
		
		return selectedRezept;
	}
	


	



	private void loadData() {
		// here all the Data is loaded.
		// is it necessary to have a seccond request for the admin, I don't think so. Yet it is still implemented
		
		dataService.getData(new AsyncCallback<Data>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Data data) {
				// the data objects holds all the data
				
//				displayZutaten(data.getZutaten());
//				displayRezepte(data.getPublicRezepte());
//				displayRezepte(data.getYourRezepte());
//				topPanel.loadingLabel.setText(" ");
//				setClientData(data);
				
				// the search interface gets all the recipes and ingredients
				Search.clientData =data;
				Search.SearchInput.setText("");
				Search.updateResults(" ");
				
				// the top panel grabs all the existing distances also from the search interface
				topPanel.locationButton.setEnabled(true);
				
				
				// the kitchens must be listed in the Kitchen Dialog
				if(data.kitchens.size() == 0 && (loginInfo == null || !loginInfo.isAdmin() )){
					// what happens here?
//					TopPanel.kDlg.kitchens.addItem("beliebige Zürcher Küche");
				} else {
					// he may edit the kitchen stuff
					TopPanel.editKitchen.setVisible(true);
					
					
					for(int i = 0;i<data.kitchens.size();i++){
						// this should also not work
//						TopPanel.kDlg.kitchens.addItem(data.kitchens.get(i).getSymbol());
					}
				}
	
				
				if(data.kitchens.size() > 0){
//					if(Search.clientData.lastKitchen == 0){
//						EaternityRechner.loginInfo.setLastKitchen(0);
//					}
					int lastkitchen = Search.clientData.lastKitchen;
					String kitchenName = data.kitchens.get(lastkitchen).getSymbol();
					TopPanel.isCustomerLabel.setText("Sie sind in der Küche: "+kitchenName+" ");
					TopPanel.location.setVisible(false);
					TopPanel.leftKitchen = false;
					TopPanel.selectedKitchen = data.kitchens.get(lastkitchen);
				} else {
					if(loginInfo == null || !loginInfo.isAdmin()){ 
						TopPanel.isCustomer.setVisible(false);
						
					}
				}
				
				
				//TODO ist the oracle of need?
				//Set<String> itemIndex = data.getOrcaleIndex();
				//Search.initializeOracle(itemIndex);
				
				
			}
		});
	}
	

	private void loadYourRezepte() {
		dataService.getYourRezepte(new AsyncCallback<List<Recipe>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Recipe> rezepte) {
				
				addClientDataRezepte(rezepte);
//				displayRezepte(rezepte);
			}
		});
	}

	private void displayRezepte(List<Recipe> rezepte) {
		for (Recipe recipe : rezepte) {
			if(recipe != null){ //why can it be 0?
//				TODO wtf is this?
				Search.displayRecipe(recipe,false);
			}
		}
	}
	
	private void displayZutaten(List<Ingredient> zutaten) {
		for (Ingredient zutat : zutaten) {
			if(zutat != null){ //why can it be 0?
				Search.displayIngredient(zutat);
			}
		}
		
	}


	private static void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	public void setAdminHandler(HandlerRegistration adminHandler) {
		this.adminHandler = adminHandler;
	}

	public HandlerRegistration getAdminHandler() {
		return adminHandler;
	}

	public static void setClientData(Data clientData) {
		EaternityRechner.clientData = clientData;
	}
	
	public static void addClientDataRezepte(List<Recipe> yourRezepte) {
		EaternityRechner.clientData.setYourRezepte(yourRezepte);
	}





	public static void updateSaisonAndMore() {
		for( Widget rezeptViewWidget : rezeptList){
			RezeptView rezeptView = (RezeptView) rezeptViewWidget;
			rezeptView.updateSaison();
			for(IngredientSpecification zutat : rezeptView.recipe.getZutaten()){
				rezeptView.changeIcons(rezeptView.recipe.getZutaten().indexOf(zutat), zutat);
			}
		}
	}
	
	

}


