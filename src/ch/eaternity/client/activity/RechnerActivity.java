package ch.eaternity.client.activity;

import ch.eaternity.client.ClientFactory;
import ch.eaternity.client.DataController;
import ch.eaternity.client.DataServiceAsync;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.client.place.RechnerRecipeEditPlace;
import ch.eaternity.client.place.RechnerRecipeViewPlace;
import ch.eaternity.client.ui.RechnerView;
import ch.eaternity.client.ui.RecipeEdit;
import ch.eaternity.client.ui.RecipeView;
import ch.eaternity.client.ui.SearchIngredients;
import ch.eaternity.client.ui.SearchRecipes;
import ch.eaternity.shared.Util.RecipeScope;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SimplePanel;


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

	private final RechnerView rechnerView;
	private final SearchIngredients searchIngredients;
	private final SearchRecipes searchRecipes;
	private final RecipeView recipeView;
	private final RecipeEdit recipeEdit;
	
	private AcceptsOneWidget container;
	private Place place;
	
	
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	// just loaded once for both places (RecipeView, RecipeEdit)
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
		this.container = container;
		
		
				
		// now load the data (just once...)
		if (!dco.dataLoaded())
			dco.loadData();
	
		SimplePanel searchPanel = rechnerView.getSearchPanel();
		SimplePanel recipePanel = rechnerView.getRecipePanel();
		
		if (place instanceof RechnerRecipeViewPlace) {
			recipePanel.setWidget(recipeView);
			searchPanel.setWidget(searchRecipes);
			
			RecipeScope recipeScope = ((RechnerRecipeViewPlace) place).getRecipeScope();
			dco.changeRecipeScope(recipeScope);
			
			eventBus.fireEvent(new UpdateRecipeViewEvent());
		}
		else if (place instanceof RechnerRecipeEditPlace) {
			recipePanel.setWidget(recipeEdit);
			dco.setEditRecipe(((RechnerRecipeEditPlace) place).getID());
			searchPanel.setWidget(searchIngredients);
		}
		
		// set presenter again (lost because of new place...
		rechnerView.getTopPanel().setPresenter(this);
		
		container.setWidget(rechnerView);
	}
	
    /**
     * Save the EditRecipe before stopping this activity
     */
    @Override
    public String mayStop() {
    	if (place instanceof RechnerRecipeEditPlace && !recipeEdit.isSaved()){
    		recipeEdit.closeRecipeEdit();
    		return null;
    	}
        else
        	return null;
    }
    
    @Override
    public void onStop() {
    	Window.alert("Stop now");
    }
	
	public void setPlace(Place place) {
		this.place = place; 
		this.start(container, eventBus);
	}
	
	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		placeController.goTo(place);
	}
	
	public DataController getDCO() {
		return dco;
	}
	
	
	public EventBus getEventBus() {
		return this.eventBus;
	}
	
	public RecipeEdit getRecipeEdit() {
		return recipeEdit;
	}
	
}
