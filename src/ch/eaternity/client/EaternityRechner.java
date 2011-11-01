package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;



import ch.eaternity.client.widgets.ImageOverlay;
import ch.eaternity.shared.Converter;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.Kitchen;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;
import ch.eaternity.shared.IngredientSpecification;


import com.google.api.gwt.client.impl.ClientGoogleApiRequestTransport;
import com.google.api.gwt.services.urlshortener.shared.Urlshortener;
import com.google.api.gwt.shared.GoogleApiRequestTransport;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;


/**
 * This application demonstrates how to construct a relatively complex user
 * interface, similar to many common email readers. It has no back-end,
 * populating its components with hard-coded data.
 */

public class EaternityRechner implements EntryPoint {

	public static LoginInfo loginInfo = null;
	private final static DataServiceAsync dataService = GWT.create(DataService.class);
	private List<RecipeView> worksheet = new ArrayList<RecipeView>();
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
	@UiField
	static FlexTable rezeptEditList;
	@UiField
	static FlexTable MenuTable;
	@UiField
	static FlexTable SuggestTable;
	@UiField
	static HorizontalPanel addInfoPanel;
	@UiField
	static HTML titleHTML;
	@UiField DockLayoutPanel topSticky;
	@UiField HTMLPanel panelNorth;
	@UiField HTMLPanel htmlRezept;
	@UiField AbsolutePanel dragArea;
	
	@UiField ScrollPanel scrollWorkspace;
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
		initializeUrlshortener();

		// Inject global styles.
		GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();
		// Create the UI defined in EaternityRechner.ui.xml.
		DockLayoutPanel outer = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");


		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		
		// TODO Move cursor focus to the Search box.
		// TODO adjust this with something that makes more sense / what would that be?
		rezeptList.getColumnFormatter().setWidth(1, "750px");
		rezeptEditList.getColumnFormatter().setWidth(1, "750px");

		
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
					// are you even an admin?
					if(adminHandler != null){
						adminHandler.removeHandler();
						ingredientHandler.removeHandler();
					}
				}
			}
		});

	}

	

	static void addRezept(final Recipe recipe, final RecipeEditView rezeptView) {
		if(!TopPanel.leftKitchen){
			recipe.kitchenId = TopPanel.selectedKitchen.id;
		}
		dataService.addRezept(recipe, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Long id) {
				
// why would i want to display the menu after it is saved?
//				Search.displayRezept(recipe);
				
				// when this is your first one... so show the panel...
				Search.yourMealsPanel.setVisible(true);
				recipe.setId(id);
	
				Search.clientData.getYourRezepte().add(recipe);
				Search.updateResults(Search.SearchInput.getText());
				rezeptView.saved = true;
				
				// TODO make same sense out of this
				// this is just a test functionality...
				rezeptView.codeImage.setHTML(
						"<a href="
						+ GWT.getHostPageBaseURL()
						+ "view.jsp?pid="
						+ Converter.toString(recipe.getId(), 34)
						+ " ><img src=http://chart.apis.google.com/chart?cht=qr&amp;chs=84x84&amp;chld=M|0&amp;chl="
						+ recipe.ShortUrl.substring(7, recipe.ShortUrl.length())
						+ " width=42 height=42 /></a>");
				
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
	
	// TODO who needs this??? please clean
	private static void undisplayRezept(String rezept_id) {

	}

	private void loadLogin() {
		// Assemble login panel.
		topPanel.signInLink.setHref(loginInfo.getLoginUrl());
		
		// TODO only show when logged in:
		// do I really want only for logged in people to save this?
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
	
	// e.g., Buzz.class, Moderator.class, Shopping.class, etc...
	static Urlshortener urlshortener = GWT.create(Urlshortener.class);

	private void initializeUrlshortener() {
	  new ClientGoogleApiRequestTransport()
	      .setApiAccessKey("AIzaSyAkdIvs2SM0URQn5656q9NugoU-3Ix2LYg")
	      .setApplicationName("eaternityrechner")
	      .create(new Receiver<GoogleApiRequestTransport>() {
	        @Override
	        public void onSuccess(GoogleApiRequestTransport transport) {
	          urlshortener.initialize(new SimpleEventBus(), transport);

	          // Now that your service is initialized, you can make a request.
	          // It may be better to publish a "ready" event on the eventBus
	          // and listen for it to make requests elsewhere in your code.
//	          makeRequest();
	          //TODO block creating a new recipe until this event has fired! (or do this event bus stuff)
	          
	        }

	        @Override
	        public void onFailure(ServerFailure error) {
	          Window.alert("Failed to initialize Url-shortener!");
	        }
	      });
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
		// what are the other recipes?
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
		// what are the other recipes?
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
		// This is basically right now a clone procedure!
		// which is okay, if you don't own that recipe already...
		// otherwise you would want to edit the old one (or at least signal the 
		// clone procedure by pressing a "Duplicate this Menu" Button.
		
		// and it only gets called when pressing the new recipe button?
		
		styleRezept(selectedRezept, false);
		
		// hier wird zurückgestzt, da sonst kein neues gemacht wird
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
		
		rezeptView.RezeptName.setText(recipe.getSymbol());
		rezeptView.titleHTML.setText(recipe.getSymbol());
		
		rezeptView.rezeptDetails.setText(recipe.getSubTitle());
//		rezeptView.makePublic.setValue(!recipe.openRequested);
		
		rezeptView.openHTML.setHTML("Nicht veröffentlicht.");
		if(recipe.isOpen()){
			rezeptView.openHTML.setHTML("Veröffentlicht.");
		} else if(recipe.openRequested){
			rezeptView.openHTML.setHTML("Veröffentlichung angefragt.");
		}
		
		if(recipe.getCookInstruction() != null){
			rezeptView.cookingInstr.setText(recipe.getCookInstruction());
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
	    	
	    	// TODO check if the following is legacy code...
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
					rezeptView.cookingInstr.setVisible(true);
//					rezeptView.htmlCooking.setVisible(true);
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
	    if(loginInfo.isAdmin() && recipe.getEmailAddressOwner() != null ) {
	    	rezeptView.savedHTML.setHTML("gespeichert von "+recipe.getEmailAddressOwner());
		} else {
			rezeptView.savedHTML.setHTML("gespeichert");
		}
	    
	    
		
	}
	
	// some local variables for the scrolling behavior
	boolean reset = true;
	int displayHeight = 120;
	
	@UiHandler("scrollWorkspace")
    public void onScroll(ScrollEvent event) { 
		
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
		Recipe recipe = new Recipe();
		
		// TODO I don't want those to be set here... those are the standarts, and should be set elsewhere
		recipe.setSymbol("Ihr Menu");
		recipe.setSubTitle("Menu Beschreibung");
		recipe.setCookInstruction("Kochanleitung.");
		
		recipe.open = false;
		recipe.openRequested = true;
		
		ShowRezept(recipe);	
		adjustStickyEdit();
	}
	
	@UiHandler("rezeptList")
	void onRezeptClicked(ClickEvent event) {
		Cell cell = rezeptList.getCellForEvent(event);
		if (cell != null) {
			
			Widget rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RecipeView rezeptViewOld = (RecipeView) rezeptViewWidget;
			rezeptViewOld.isSelected = false;
			
			styleRezept(selectedRezept, false);
			selectedRezept = cell.getRowIndex();
			styleRezept(selectedRezept, true);
			
			rezeptViewWidget = rezeptList.getWidget(selectedRezept, 1);
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
			rezeptView.isSelected = true;
			
			// put this recipe into the edit panel...
			if(rezeptEditList.getRowCount() == 0){
				rezeptEditList.insertRow(0);
			}
			rezeptEditView = new RecipeEditView(rezeptView.recipe);
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
	
	public static void styleRezept(int row, boolean selected) {
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
	
	public static int AddZutatZumMenu( Ingredient item) {
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

	static RecipeEditView rezeptEditView;
	
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
			rezeptView = new RecipeView(newRezept);
			rezeptList.setWidget(selectedRezept, 1, rezeptView);
			rezeptList.getRowFormatter().setStyleName(0, "recipe");
			styleRezept(selectedRezept, true);
			
			// the edit form
			rezeptEditView = new RecipeEditView(newRezept);
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
	

	@UiHandler("MenuTable")
	void onClick(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = MenuTable.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			rezeptEditView.selectRow(row);
		}
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
//		Window.alert(error.getMessage()  +" "+error.getLocalizedMessage());
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
			RecipeView rezeptView = (RecipeView) rezeptViewWidget;
			rezeptView.updateSaison();
			for(IngredientSpecification zutat : rezeptView.recipe.getZutaten()){
				rezeptView.changeIcons(rezeptView.recipe.getZutaten().indexOf(zutat), zutat);
			}
		}
	}
	
	

}


