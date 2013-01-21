package ch.eaternity.client.activity;

import java.util.Iterator;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.place.EaternityRechnerPlace;
import ch.eaternity.client.ui.EaternityRechnerView;
import ch.eaternity.client.ui.MenuPreviewView;
import ch.eaternity.client.ui.widgets.ConfirmDialog;
import ch.eaternity.client.ui.widgets.RecipeView;
import ch.eaternity.client.ui.widgets.Search;
import ch.eaternity.client.ui.widgets.TopPanel;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;

import com.google.api.gwt.client.impl.ClientGoogleApiRequestTransport;
import com.google.api.gwt.services.urlshortener.shared.Urlshortener;
import com.google.api.gwt.shared.GoogleApiRequestTransport;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class EaternityRechnerActivity extends AbstractActivity implements
		EaternityRechnerView.Presenter {
	
	public static LoginInfo loginInfo = null;
	
	private final ClientFactory clientFactory;

	private final DataServiceAsync dataRpcService;
	private final EventBus eventBus;
	private final EaternityRechnerView display;
	private final EaternityRechnerPlace place;

	private PlaceController placeController;
	private DataController dco;
	private MenuPreviewView menuPreview;
	
	
	
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	public EaternityRechnerActivity(EaternityRechnerPlace place, ClientFactory factory) {
		
		this.clientFactory = factory;
		this.dataRpcService = factory.getDataServiceRPC();
		this.eventBus = factory.getEventBus();
		this.display = factory.getEaternityRechnerView();
		this.placeController = factory.getPlaceController();
		this.dco = factory.getDataController();
		this.place = place;
		
		this.menuPreview = factory.getMenuPreviewView();
	}

	
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		display.setName(place.getPlaceName());
		display.setPresenter(this);
		
		//REFACTOR: into RechnerView
		display.getSearchPanel().setSuperDisplay(display);
		display.getTopPanel().setSuperDisplay(display);
		
		container.setWidget(display);
		display.setMenuPreviewDialog(menuPreview);

		// now load the data
		dco.loadData();
		
		initializeUrlshortener();	
	}

	
	//REFACTOR: in DataController und View (EVENT)
	public void addRezept(final Recipe recipe, final RecipeView rezeptView) {
		
		// assign this recipe if necessary to a kitchen:
		if(dco.cdata.loginInfo.getIsInKitchen()){
			// then we are in a kitchen :-)
			// so this recipe belongs into this kitchen, so we add its id
			if(!recipe.kitchenId.contains(getTopPanel().selectedKitchen.id)){
				recipe.kitchenId.add(getTopPanel().selectedKitchen.id);
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
					for(Recipe recipeDesc : dco.cdata.userRecipes){
						if(recipeDesc.getId().equals(recipe.getDirectAncestorID())){
							recipeDesc.addDirectDescandentID(id);
						}
					}
					for(Recipe recipeDesc : dco.cdata.kitchenRecipes){
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
				
				if(!dco.cdata.loginInfo.getIsInKitchen()){
					dco.cdata.userRecipes.add(recipe);
				} else {
					dco.cdata.kitchenRecipes.add(recipe);
					dco.cdata.currentKitchenRecipes.add(recipe);
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
//REFACTOR: same as add - Done
	void removeRezept(final Recipe recipe) {
		dataRpcService.removeRezept(recipe.getId(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				
				if(dco.cdata.userRecipes.contains(recipe)){
					dco.cdata.userRecipes.remove(recipe);
				}
				if(recipe.isOpen()){
					dco.cdata.publicRecipes.remove(recipe);
				}
				if(dco.cdata.kitchenRecipes.contains(recipe)){
					dco.cdata.kitchenRecipes.remove(recipe);
				}
				if(getTopPanel().selectedKitchen != null)
					dco.changeKitchenRecipes(getTopPanel().selectedKitchen.id);
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
	
	public LoginInfo getLoginInfo(){
		return loginInfo;
	}



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
	
	//REFACTOR: DataController - Done
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

	//REFACTOR: DataController
	@Override
	public void removeRecipe(Recipe recipe) {
		removeRezept(recipe);
	}
	
	//REFACTOR: probably in View
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
	
	//REFACTOR: into View
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
	
	//REFACTOR: into View
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
	public ClientData getClientData() {
		return dco.cdata;
	}



	@Override
	public DataController getDCO() {
		
		// TODO Auto-generated method stub
		return dco;
	}
	
	@Override 
	public EventBus getEventBus() {
		return this.eventBus;
	}
	
}
