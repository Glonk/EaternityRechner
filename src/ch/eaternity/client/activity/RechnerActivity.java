package ch.eaternity.client.activity;

import java.util.Iterator;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.RechnerView;
import ch.eaternity.client.ui.RecipeEdit;
import ch.eaternity.client.ui.RecipeView;
import ch.eaternity.client.ui.SearchIngredients;
import ch.eaternity.client.ui.SearchRecipes;
import ch.eaternity.client.ui.TopPanel;
import ch.eaternity.client.ui.widgets.ConfirmDialog;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the Activity and the main Presenter in One
 * @author aurelianjaggi
 *
 */
public class RechnerActivity extends AbstractActivity {
	
	private final ClientFactory clientFactory;
	private final DataServiceAsync dataRpcService;
	private final EventBus eventBus;
	private PlaceController placeController;
	private DataController dco;
	
	private Place place;

	private final RechnerView rechnerView;
	private final SearchIngredients searchIngredients;
	private final SearchRecipes searchRecipes;
	private final RecipeView recipeView;
	private final RecipeEdit recipeEdit;
	
	
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	public RechnerActivity(Place place, ClientFactory factory) {
		
		this.clientFactory = factory;
		this.dataRpcService = factory.getDataServiceRPC();
		this.eventBus = factory.getEventBus();
		this.rechnerView = factory.getRechnerView();
		this.placeController = factory.getPlaceController();
		this.dco = factory.getDataController();
		dco.setClientFactory(factory);
		this.place = place;
		
		rechnerView.setPresenter(this);
		
		recipeEdit = new RecipeEdit();
		recipeEdit.setPresenter(this);
		recipeView = new RecipeView();
		recipeView.setPresenter(this);
		
		searchIngredients = new SearchIngredients();
		searchIngredients.setPresenter(this);
		searchRecipes = new SearchRecipes();
		searchRecipes.setPresenter(this);
	}

	
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		
		SimplePanel searchPanel = rechnerView.getSearchPanel();
		SimplePanel recipePanel = rechnerView.getRecipePanel();
		// set presenter again (lost because of new place...
		rechnerView.getTopPanel().setPresenter(this);
		
		if (place instanceof RechnerRecipeViewPlace) {
			// Load RecipeView into recipecontainer
			recipePanel.setWidget(recipeView);
			// Load SearchRecipes into searchcontainer
			searchPanel.setWidget(searchRecipes);
			String recipeScope = ((RechnerRecipeViewPlace) place).getRecipeScope();
			if(recipeScope.equals("p") || recipeScope.equals("u") || recipeScope.equals("k"))
				dco.setRecipeScope(recipeScope);
			else
				dco.setRecipeScope("p");
		}
		else if (place instanceof RechnerRecipeEditPlace) {
			// Load RecipeEdit into recipecontainer
			recipePanel.setWidget(recipeEdit);
			// Load SearchIngredients into searchcontainer
			searchPanel.setWidget(searchIngredients);
		}
		
		
		container.setWidget(rechnerView);

		// now load the data (just once...)
		if (!dco.dataLoaded())
			dco.loadData();
	}
	
    /**
     * Save the EditRecipe before stopping this activity
     */
    @Override
    public String mayStop() {
    	if (place instanceof RechnerRecipeEditPlace && recipeEdit.hasChanged())
    		return "Rezept nocht nicht gespeichert. Trotzdem verlassen?";
        else
        	return null;
    }
	
	public void setPlace(Place place) {
		this.place = place; 
	}
	
	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		placeController.goTo(place);
	}

	/*
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
				}
				
				if(!dco.cdata.loginInfo.getIsInKitchen()){
					dco.cdata.userRecipes.add(recipe);
				} else {
					dco.cdata.kitchenRecipes.add(recipe);
					dco.cdata.currentKitchenRecipes.add(recipe);
				}
				
				String searchString = IngredientsResultWidget.SearchInput.getText().trim();
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
				getSearchPanel().updateResults(IngredientsResultWidget.SearchInput.getText());
			}
		});
	}
	
	
	


	
	public LoginInfo getLoginInfo(){
		return loginInfo;
	}




	
	
	private static void handleError(Throwable error) {
		Window.alert(error.getMessage()  +" "+error.getLocalizedMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}
	

	public TopPanel getTopPanel() {
		return rechnerView.getTopPanel();
	}


	public IngredientsResultWidget getSearchPanel() {
		return rechnerView.getSearchPanel();
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
		rechnerView.cloneRecipe(recipe);
		
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
		int row = recipeToRemove.getWidgetRow(recipeToRemove , rechnerView.getRezeptList());
		
		rechnerView.getRezeptList().remove(recipeToRemove);
		rechnerView.getRezeptList().removeRow(row);

		if(rechnerView.getSelectedRecipeNumber() == row){
			rechnerView.setSelectedRecipeNumber(-1);
			rechnerView.getSuggestionPanel().clear();
			
			if(recipeToRemove.isSelected){
				//close also the Editview! ... or respectively the topview
				//TODO this fails on the top view...
				rechnerView.closeRecipeEditView();
			}
		}
	}
	
	//REFACTOR: into View
	@Override
	public void removeAllRecipesFromWorkplace()
	{
		  Iterator<Widget> it = rechnerView.getRezeptList().iterator();
		  while (it.hasNext())
		  {
			 RecipeView recipeToRemove = (RecipeView) it.next();
			 recipeToRemove.isSelected = true;
			 removeRecipeFromWorkplace(recipeToRemove);
		  }
	}
	*/

	
	public DataController getDCO() {
		
		// TODO Auto-generated method stub
		return dco;
	}
	
	
	public EventBus getEventBus() {
		return this.eventBus;
	}
	
}
