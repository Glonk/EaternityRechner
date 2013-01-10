package ch.eaternity.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.api.gwt.client.impl.ClientGoogleApiRequestTransport;
import com.google.api.gwt.services.urlshortener.shared.Urlshortener;
import com.google.api.gwt.shared.GoogleApiRequestTransport;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.EaternityRechner;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.place.HelloPlace;
import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.HelloView;
import ch.eaternity.client.ui.MenuPreviewView;
import ch.eaternity.client.ui.widgets.ConfirmDialog;
import ch.eaternity.client.ui.widgets.ImageOverlay;
import ch.eaternity.client.ui.widgets.IngredientsDialog;
import ch.eaternity.client.ui.widgets.RecipeEditView;
import ch.eaternity.client.ui.widgets.RecipeView;
import ch.eaternity.client.ui.widgets.Search;
import ch.eaternity.client.ui.widgets.TopPanel;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.DeviceSpecification;
import ch.eaternity.shared.Extraction;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.SingleDistance;

public class EaternityRechnerActivity extends AbstractActivity implements
		EaternityRechnerView.Presenter {
	
	
	protected Data clientData;
	public static LoginInfo loginInfo = null;

	private final DataServiceAsync dataRpcService;
	private final EventBus eventBus;
	private final EaternityRechnerView display;
	private final EaternityRechnerPlace place;

	private PlaceController placeController;
	private DataController dao;
	private HandlerRegistration adminHandler;
	private MenuPreviewView menuPreview;
	
	
	
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	public EaternityRechnerActivity(EaternityRechnerPlace place, ClientFactory factory) {
		
		this.dataRpcService = factory.getDataServiceRPC();
		this.eventBus = factory.getEventBus();
		this.display = factory.getEaternityRechnerView();
		this.placeController = factory.getPlaceController();
		this.dao = factory.getDataController();
		this.place = place;
		
		this.menuPreview = factory.getMenuPreviewView();
		
	}

	
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		display.setName(place.getPlaceName());
		display.setPresenter(this);
		
		//REFACTOR: into display 
		display.getSearchPanel().setSuperDisplay(display);
		display.getTopPanel().setSuperDisplay(display);
		
//		container.setWidget(display.asWidget());
		container.setWidget(display);
		display.setMenuPreviewDialog(menuPreview);

		// now load the data
		loadData();
		initializeUrlshortener();

		// load login
		loadLoginData();
		
	}
	
	public void loadLoginData() {
		//	  
		// Check login status using login service.
		
		dataRpcService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					display.loadYourRechner();
					if(loginInfo.isAdmin()) {
						adminHandler = loadAdmin();
					} 
				} else   {
					display.loadLogin();
					// are you even an admin?
					if(adminHandler != null){
						adminHandler.removeHandler();
//						ingredientHandler.removeHandler();
					}
				}
			}
		});
	}

	private void loadData() {
		// here all the Data is loaded.
		// is it necessary to have a seccond request for the admin, I don't think so. Yet it is still implemented
		
		dataRpcService.getData(new AsyncCallback<Data>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Data data) {
				// the data objects holds all the data
				// the search interface gets all the data (recipes and ingredients)
				clientData = data;
				dao.clientData = clientData;
//				Search.clientData = data;
				
				eventBus.fireEvent(new LoadedDataEvent());

				// the top panel grabs all the existing distances also from the search interface
				
				//REFACTOR: eine Stufe tiefer (display)
				display.getTopPanel().locationButton.setEnabled(true);
				
				// is this necessary?:
				dao.isNotInKitchen = true;
				display.getTopPanel().location.setVisible(true);
				// it should not...
				
				// who may change the kitchen
				if(data.kitchens.size() == 0 && (loginInfo == null || !loginInfo.isAdmin() )){
					// there is no kitchen available and you are a normal user (or not logged in)
					display.getTopPanel().isCustomer.setVisible(false);
				} else {
					// otherwise may edit the kitchen stuff
					display.getTopPanel().editKitchen.setVisible(true);
				}

				
				// here is save the last kitchen thing
				if(data.kitchens.size() > 0){

					Long lastKitchenId = clientData.lastKitchen;
					if(lastKitchenId == null) { lastKitchenId = 0L; }
					
					Workgroup lastKitchen = null;
					for(Workgroup kitchIt : data.kitchens){
						if(kitchIt.id == lastKitchenId){
							lastKitchen = kitchIt;
						}
					}
					
					if(lastKitchenId != null && lastKitchen != null){
						String kitchenName = lastKitchen.getSymbol();
						display.getTopPanel().isCustomerLabel.setText("Sie sind in der Küche: "+kitchenName+" ");
						display.getTopPanel().location.setVisible(false);
						dao.isNotInKitchen = false;
						display.getTopPanel().selectedKitchen = lastKitchen;
						display.getSearchPanel().yourRecipesText.setHTML(" in " + kitchenName + " Rezepten");
						dao.updateKitchenRecipesForSearch(display.getTopPanel().selectedKitchen.id);

					} 
				} 
				
				display.getSearchPanel().SearchInput.setText("");
				display.getSearchPanel().updateResults(" ");
				menuPreview.hide();

			}
			
		});
	}
	
	//REFACTOR: in DataController und View (EVENT)
public void addRezept(final Recipe recipe, final RecipeView rezeptView) {
		
		// assign this recipe if necessary to a kitchen:
		if(!dao.isNotInKitchen){
			// then we are in a kitchen :-)
			// so this recipe belongs into this kitchen, so we add its id
			if(!recipe.kitchenIds.contains(getTopPanel().selectedKitchen.id)){
				recipe.kitchenIds.add(getTopPanel().selectedKitchen.id);
			}
			
		}
		
		// and then save it.
		dataRpcService.addRezept(recipe, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Long id) {

				// when this is your first one... so show the panel... should be automatic
//				Search.yourMealsPanel.setVisible(true);
				if(recipe.getDirectAncestorID() != null){
					for(Recipe recipeDesc : dao.clientData.getYourRezepte()){
						if(recipeDesc.getId().equals(recipe.getDirectAncestorID())){
							recipeDesc.addDirectDescandentID(id);
						}
					}
					for(Recipe recipeDesc : dao.clientData.KitchenRecipes){
						if(recipeDesc.getId().equals(recipe.getDirectAncestorID())){
							recipeDesc.addDirectDescandentID(id);
						}
					}

				}
				// equals() for recipe is not defined!!!
				
				// only add when it is not in there yet... (update)
				// and corresponds to the kitchen
				/*
				if(getTopPanel().isNotInKitchen){
					if(!dao.clientData.getYourRezepte().contains(recipe)){
						dao.clientData.getYourRezepte().add(recipe);
					}
				} else {
					if(!dao.clientData.KitchenRecipes.contains(recipe)){
						dao.clientData.KitchenRecipes.add(recipe);
					}
					if(!getSearchPanel().selectedKitchenRecipes.contains(recipe)){
						getSearchPanel().selectedKitchenRecipes.add(recipe);
					}
				}*/
				
				if(dao.isNotInKitchen){
					dao.clientData.getYourRezepte().add(recipe);
				} else {
					dao.clientData.KitchenRecipes.add(recipe);
					dao.selectedKitchenRecipes.add(recipe);
				}
				
				String searchString = Search.SearchInput.getText().trim();
				getSearchPanel().updateResults(searchString);
				
				rezeptView.setRecipeSavedMode(true);
				
				// there needs to be an automatic link between normal and editview...
				rezeptView.recipe.setId(id);
				
				// TODO make same sense out of this
				// this is just a test functionality...
				// but it could be displayed somewhere else...
//				rezeptView.codeImage.setHTML(
//						"<a href="
//						+ GWT.getHostPageBaseURL()
//						+ "view.jsp?pid="
//						+ Converter.toString(recipe.getId(), 34)
//						+ " ><img src=http://chart.apis.google.com/chart?cht=qr&amp;chs=84x84&amp;chld=M|0&amp;chl="
//						+ recipe.ShortUrl.substring(7, recipe.ShortUrl.length())
//						+ " width=42 height=42 /></a>");
				
			}
		});
	}
//REFACTOR: same as add
	void removeRezept(final Recipe recipe) {
		dataRpcService.removeRezept(recipe.getId(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				
				if(dao.clientData.getYourRezepte().contains(recipe)){
					dao.clientData.getYourRezepte().remove(recipe);
				}
				if(recipe.isOpen()){
					dao.clientData.getPublicRezepte().remove(recipe);
				}
				if(dao.clientData.KitchenRecipes.contains(recipe)){
					dao.clientData.KitchenRecipes.remove(recipe);
				}
				if(getTopPanel().selectedKitchen != null)
					dao.updateKitchenRecipesForSearch(getTopPanel().selectedKitchen.id);
				getSearchPanel().updateResults(Search.SearchInput.getText());
			}
		});
	}
	
	//REFACTOR: datacontroller
	void rezeptApproval(final Recipe recipe, final Boolean approve) {
		dataRpcService.approveRezept(recipe.getId(), approve,new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
// here happens some graphics clinch... or somewhere else...
				dao.clientData.getPublicRezepte().remove(recipe);
				recipe.open = approve;
				dao.clientData.getPublicRezepte().add(recipe);
				
				//TODO in the display of the recipes show, that is now public
				
				getSearchPanel().updateResults(Search.SearchInput.getText());
			}
		});
	}
	


	// registering the class is an essential procedure to access the api
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
	          //TODO block saving of a new recipe until this event has fired! (or do this event bus stuff - yeah, right, I have no idea howto :)
	          
	        }

	        @Override
	        public void onFailure(ServerFailure error) {
	          Window.alert("Failed to initialize Url-shortener!");
	        }
	      });
	}
	
	//REFACTOR: Entscheid Serverseitig ob Admin, je nachdem Adminrezepte mitschicken...
	private HandlerRegistration loadAdmin() {
		

		HandlerRegistration adminHandler = display.loadAdmin();
		
		// Always display the Kitchen Dialog of all Customers...
		
		// you are the admin, this means, pull all kitchens from the server
		
		dataRpcService.getAdminRezepte(new AsyncCallback<List<Recipe>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Recipe> rezepte) {
				// this shouldn't be necessary, as we are working with pointers:
				clientData = dao.clientData;
				// add all recipes to the public ones, this is an arbitrary choice...
				// TODO display this fact somewhere, to see which recipes are yours, and which are not.
				clientData.setPublicRezepte(rezepte);
				// this shouldn't be necessary, as we are working with pointers: 
				dao.clientData = clientData;
				getSearchPanel().updateResults(" ");
			}
		});
		
		
		dataRpcService.getAdminKitchens(new AsyncCallback<List<Workgroup>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			
			public void onSuccess(List<Workgroup> result) {
				// this shouldn't be necessary
				clientData = dao.clientData;
				
				if(result.size() != 0){ // there must be somthing!
					clientData.kitchens.addAll(result);
				}
				// this shouldn't be necessary, as we are working with pointers:
//				Search.setClientData(data);
				
				// is there anything to update?
//				Search.updateResults(" ");
				
			}
		});
		
		return adminHandler;
	}
	
	

	public void loadYourRezepte() {
		dataRpcService.getYourRezepte(new AsyncCallback<List<Recipe>>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(List<Recipe> rezepte) {
				
				if(rezepte != null && rezepte.size() > 0){
					addClientDataRezepte(rezepte);
				}
//				displayRezepte(rezepte);
			}
		});
	}
	
	public LoginInfo getLoginInfo(){
		return loginInfo;
	}
	
	public void setAdminHandler(HandlerRegistration adminHandler) {
		this.adminHandler = adminHandler;
	}

	public HandlerRegistration getAdminHandler() {
		return adminHandler;
	}

	public void setClientData(Data clientData) {
		this.clientData = clientData;
	}
	
	public void addClientDataRezepte(List<Recipe> yourRezepte) {
		if(clientData != null && !yourRezepte.isEmpty()){
			this.clientData.setYourRezepte(yourRezepte);
		}
	}







//	public void onAddButtonClicked() {
////		eventBus.fireEvent(new AddContactEvent());
//	}
//
//	public void onDeleteButtonClicked() {
////		deleteSelectedContacts();
//	}

//	public void onItemClicked(ContactDetails contactDetails) {
//		eventBus.fireEvent(new EditContactEvent(contactDetails.getId()));
//	}

//	public void onItemSelected(ContactDetails contactDetails) {
//		if (selectionModel.isSelected(contactDetails)) {
//			selectionModel.removeSelection(contactDetails);
//		}
//		else {
//			selectionModel.addSelection(contactDetails);
//		}
//	}
	
	


	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	
//	@Override
//	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
//		EaternityRechnerView eaternityRechnerView = clientFactory.getEaternityRechnerView();
////		eaternityRechnerView.setName(name);
//		eaternityRechnerView.setPresenter(this);
//		containerWidget.setWidget(eaternityRechnerView.asWidget());
//	}

	/**
	 * Ask user before stopping this activity
	 */
//	@Override
//	public String mayStop() {
//		return "Please hold on. This activity is stopping.";
//	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		placeController.goTo(place);
	}
	
	
	private static void handleError(Throwable error) {
		Window.alert(error.getMessage()  +" "+error.getLocalizedMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}
	

	public TopPanel getTopPanel() {
		return display.getTopPanel();
	}


	public Search getSearchPanel() {
		return display.getSearchPanel();
	}

	public int getSelectedMonth() {
		return getTopPanel().Monate.getSelectedIndex()+1;
	}
	
	public void addNewRecipe() {
		// create a new recipe
		Recipe recipe = new Recipe();
		
		// TODO I don't want those to be set here... those are the standards, and should be set elsewhere
		recipe.setSymbol("Ihr Menu");
		recipe.setSubTitle("Menu Beschreibung");
		
		String cookingIntructions = "Kochanleitung.";
		if(loginInfo != null && !loginInfo.isLoggedIn()){
			cookingIntructions = "Sie sind nicht angemeldet. Alle Änderungen am Rezept können nicht gespeichert werden.";
		}
		recipe.setCookInstruction(cookingIntructions);
		
		recipe.open = false;
		recipe.openRequested = true;
		//display the recipe
		display.cloneRecipe(recipe);
		
	}

	@Override
	public Urlshortener getUrlShortener() {
		return urlshortener;
	}

	@Override
	public void removeRecipe(Recipe recipe) {
		removeRezept(recipe);
	}
	
	@Override
	public void removeRecipeFromWorkplace(final RecipeView recipeToRemove)
	{
		if(recipeToRemove.saved)
			removeRecipeFromWorkplaceNoPrompt(recipeToRemove);
		else 
		{
			String saveText = recipeToRemove.recipe.getSymbol() + " ist noch nicht gespeichert!";
			if(!getLoginInfo().isLoggedIn()){
				saveText = "Sie verlieren alle Änderungen!";
		}
		final ConfirmDialog dlg = new ConfirmDialog(saveText);
		dlg.statusLabel.setText("Speichern?");
		// TODO recheck user if he really want to do this...
		
		dlg.yesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				recipeToRemove.saveThisRecipe();
				removeRecipeFromWorkplaceNoPrompt(recipeToRemove);
				dlg.hide();
			}
		});
		dlg.noButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeRecipeFromWorkplaceNoPrompt(recipeToRemove);
				dlg.hide();
			}
		});
		
		dlg.show();
		dlg.center();
		}
	}
	

	private void removeRecipeFromWorkplaceNoPrompt(RecipeView recipeToRemove)
	{
		int row = recipeToRemove.getWidgetRow(recipeToRemove , display.getRezeptList());
		
		display.getRezeptList().remove(recipeToRemove);
		display.getRezeptList().removeRow(row);

		if(display.getSelectedRecipeNumber() == row){
			display.setSelectedRecipeNumber(-1);
			display.getSuggestionPanel().clear();
			
			if(recipeToRemove.isSelected){
				//close also the Editview! ... or respectively the topview
				//TODO this fails on the top view...
				display.closeRecipeEditView();
			}
		}
	}
	
	@Override
	public void removeAllRecipesFromWorkplace()
	{
		  Iterator<Widget> it = display.getRezeptList().iterator();
		  while (it.hasNext())
		  {
			 RecipeView recipeToRemove = (RecipeView) it.next();
			 recipeToRemove.isSelected = true;
			 removeRecipeFromWorkplace(recipeToRemove);
		  }
	}

	@Override
	public void recipeApproval(Recipe recipe, boolean approve) {
		rezeptApproval(recipe, approve);
	}

	@Override
	public Data getClientData() {
		return clientData;
	}



	@Override
	public DataController getDAO() {
		
		// TODO Auto-generated method stub
		return dao;
	}
	
}
