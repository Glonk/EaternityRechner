package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.eaternity.client.events.CollectionsChangedEvent;
import ch.eaternity.client.events.IngredientAddedEvent;
import ch.eaternity.client.events.KitchenChangedEvent;
import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.client.events.LoginChangedEvent;
import ch.eaternity.client.events.MonthChangedEvent;
import ch.eaternity.client.events.UpdateRecipeViewEvent;
import ch.eaternity.shared.ClientData;
import ch.eaternity.shared.Distance;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.LoginInfo;
import ch.eaternity.shared.NotLoggedInException;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Util;
import ch.eaternity.shared.Util.RecipeScope;
import ch.eaternity.shared.Workgroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Manages clientside data object, keeps it in sync with cloud and fires events
 */
public class DataController {
	
	// ---------------------- Class Variables ----------------------
	
	private ClientFactory clientFactory;
	private DataServiceAsync dataRpcService;
	private EventBus eventBus;
	
	// here is the database of all data pushed to....
	private ClientData cdata = new ClientData();
	private boolean dataLoaded = false;

	// ---------------------- public Methods ----------------------
	
	public DataController () {}
	
	public void setClientFactory(ClientFactory factory) {
		this.clientFactory = factory;
		this.dataRpcService = factory.getDataServiceRPC();
		this.eventBus = factory.getEventBus();
	}
	
	public boolean dataLoaded() {
		return dataLoaded;
	}
	
	public void loadData() {
		dataRpcService.getData(GWT.getHostPageBaseURL(), new AsyncCallback<ClientData>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(ClientData data) {
				// the data objects holds all the data
				cdata = data;
				
				// Load currentKitchen via ID
				boolean kitchenLoaded = true;
				if (cdata.loginInfo != null && cdata.loginInfo.getIsInKitchen() == true && cdata.loginInfo.getLastKitchen() != null)
				{
					cdata.currentKitchen = cdata.getKitchenByID(cdata.loginInfo.getLastKitchen());
					if (cdata.currentKitchen == null)
						kitchenLoaded = false;
				}
				else
					kitchenLoaded = false;
				
				if (kitchenLoaded) {
					changeKitchenRecipes(cdata.currentKitchen.id);
					eventBus.fireEvent(new KitchenChangedEvent(cdata.currentKitchen.id));
				}
				
				
				// Load current Month or Kitchen Month
				Date date = new Date();
				cdata.currentMonth = date.getMonth() + 1;
				eventBus.fireEvent(new MonthChangedEvent(cdata.currentMonth));
				
				if (cdata.loginInfo != null)
					eventBus.fireEvent(new LoginChangedEvent(cdata.loginInfo));
				
				eventBus.fireEvent(new LoadedDataEvent());
			}
			
		});
		dataLoaded = true;
	}
	
	// --------------------- Methods accessed by SubViews --------------------- 
	
	public void createRecipe() {
		Recipe recipe = new Recipe();
		setRecipeParameters(recipe);
	}
	
	public void cloneRecipe(Recipe toclone) {
		Recipe recipe = new Recipe(toclone);
		setRecipeParameters(recipe);
	}
	
	private void setRecipeParameters(Recipe recipe) {
		recipe.setCreateDate(new Date());
		
		if (cdata.currentKitchen != null) {
			recipe.setKitchenId(cdata.currentKitchen.id);
			cdata.kitchenRecipes.add(recipe);
			cdata.currentKitchenRecipes.add(recipe);
		}
		else {
			cdata.userRecipes.add(recipe);
		}
		
		if (cdata.loginInfo != null) {
			recipe.setEmailAddressOwner(cdata.loginInfo.getEmailAddress());
			recipe.setUserID(cdata.loginInfo.getId());
		}
		
		recipe.setPopularity(0L);
		recipe.setHits(0L);
		
		cdata.editRecipe = recipe;
	}
	
	
	
	public void saveRecipe(final Recipe recipe) { 
		dataRpcService.saveRecipe(recipe, new AsyncCallback<Long>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Long id) {
				recipe.setId(id);
				if (recipe.getKitchenId() == null) {
					if (cdata.getUserRecipeByID(id) == null) {
						cdata.userRecipes.add(recipe);
					}
				}
				else{
					if (recipe.getKitchenId() == cdata.currentKitchen.id)
						cdata.currentKitchenRecipes.add(recipe);
					cdata.kitchenRecipes.add(recipe);
				}
				// show status info that the recipe got saved...
			}
		});
		eventBus.fireEvent(new UpdateRecipeViewEvent());
	}
	
	public void deleteRecipe(final Recipe recipe) {
		dataRpcService.deleteRecipe(recipe.getId(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				cdata.userRecipes.remove(recipe);
				cdata.publicRecipes.remove(recipe);
				cdata.kitchenRecipes.remove(recipe);
				cdata.currentKitchenRecipes.remove(recipe);
				if (cdata.editRecipe == recipe)
					cdata.editRecipe = null;
				eventBus.fireEvent(new UpdateRecipeViewEvent());
			}
		});
	}
	
	
	
	public void addIngredientToMenu(Ingredient ingredient, int weight) {

		IngredientSpecification ingSpec = new IngredientSpecification(ingredient);
		if (weight == 0) {
			ingSpec.setWeight(ingredient.getStdWeight());
		}
		else
			ingSpec.setWeight(weight);
		//ingSpec.setDistance(cdata.distances.getDistance(ingSpec.getExtraction().symbol, cdata.currentLocation));
		
		if (cdata.editRecipe != null)
			cdata.editRecipe.addIngredient(ingSpec);
		else
			Window.alert("Edit Recipe is null");

		eventBus.fireEvent(new IngredientAddedEvent(ingSpec));
	}
	
	// probably other place?
	public void changeMonth(int month) {
		cdata.currentMonth = month;
		eventBus.fireEvent(new MonthChangedEvent(month));
	}
	
	public void changeKitchen(Workgroup kitchen) {
		// maybee kitchen = null not necessary...
		if (kitchen == null) {
			cdata.currentKitchen = null;
			cdata.currentKitchenRecipes = null;
			eventBus.fireEvent(new KitchenChangedEvent(-1L));
		}
		else {
			cdata.currentKitchen = kitchen;
			for (Recipe recipe : cdata.kitchenRecipes) {
				if (recipe.getKitchenId() == kitchen.id);
					cdata.currentKitchenRecipes.add(recipe);
			}
			
			eventBus.fireEvent(new KitchenChangedEvent(kitchen.id));
		}
	}
	
	public void saveKitchen() {}
	
	public void deleteKitchen() {}

	
	// reload will do it...
	public void approveRecipe(final Recipe recipe, final Boolean approve) {
		dataRpcService.approveRezept(recipe.getId(), approve,new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Boolean ignore) {
				// display in information window: the recipe <name> was successfully approved. 
			}
		});
	}
	
	/*
	 * 
	 */
	public void searchIngredients(String searchString, List<Ingredient> ingredients, List<Ingredient> alternatives) {
		ingredients.clear();
		alternatives.clear();
		
		if(searchString.trim().length() != 0){

			String[] searches = searchString.split(" ");

			// consider strings with whitespaces, seek for each word individually
			for(String search : searches)
			{
				// TODO this search algorithm is extremely slow, make faster
				for(Ingredient zutat : cdata.ingredients){
					if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
						if(!ingredients.contains(zutat)){
							zutat.setNoAlternative(true);
							ingredients.add(zutat);
						}
					}
				}
				// only look for alternatives, if there is only 1 result
				if(ingredients.size() == 1){
					for(Ingredient zutat : ingredients){
						if(zutat.getAlternatives() != null){
							for(Long alternativen_id : zutat.getAlternatives()){
								for(Ingredient zutat2 : cdata.ingredients){
									if(zutat2.getId().equals(alternativen_id)){
										if(!alternatives.contains(zutat2)){
											zutat2.setNoAlternative(false);
											alternatives.add(zutat2);
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		else {
			for(Ingredient zutat : cdata.ingredients){
				ingredients.add(zutat);
				zutat.setNoAlternative(true);
			}
		}
	}
	
	private List<Recipe> searchRecipe(String searchString, List<Recipe> recipes) {
		List<Recipe> result = new ArrayList<Recipe>();
		String[] searches = searchString.split(" ");
		
		if(searchString.trim().length() != 0){
			for(Recipe recipe : recipes){
				// search by Name
				if( getLevenshteinDistance(recipe.getSymbol(),searchString) < 5){
					result.add(recipe);
				}
	
				// search by matching Ingredient names included in the recipes
				List<IngredientSpecification> recipeIngredients = recipe.getIngredients();
				if(recipeIngredients != null){
					int i = 0;
					for(IngredientSpecification ZutatImRezept : recipeIngredients ){
						if(ZutatImRezept != null){
							for(String search2 : searches){
								if( search2.trim().length() <= ZutatImRezept.getName().length() &&  ZutatImRezept.getName().substring(0, search2.trim().length()).compareToIgnoreCase(search2) == 0){
									i++;
								}
							}
							if(i == searches.length)
								result.add(recipe);
						}
					}
				}
			}
		}
		else {
			return recipes;
		}
		return result;
	}
	
	public List<Recipe> searchUserRecipes(String searchString) {
		return searchRecipe(searchString, cdata.userRecipes);
	}
	
	public List<Recipe> searchKitchenRecipes(String searchString) {
		return searchRecipe(searchString, cdata.currentKitchenRecipes);
	}
	
	public List<Recipe> searchPublicRecipes(String searchString) {
		return searchRecipe(searchString, cdata.publicRecipes);
	}
	
	
	
	
	// Select the KitchenRecipes
	// Always call updateResults after for propper loading!
	public void changeKitchenRecipes(Long id) {
		cdata.currentKitchenRecipes.clear();
		for(Recipe recipe : cdata.kitchenRecipes){
			if(id.equals(recipe.getKitchenId()))
			{
				cdata.currentKitchenRecipes.add(recipe);
			}
		}
		cdata.currentKitchen = cdata.getKitchenByID(id);
	}
	
	/**
	 * make sure you fetched all distances before!! otherwise not all ingredients will be updated
	 * @param processedLocation
	 */

	public void changeCurrentLocation(String processedLocation) {
		cdata.currentLocation = processedLocation;
		
		// Iterate over all IngredientSpecificatino of Kitchen Recipes or editRecipe
		List<IngredientSpecification> ingSpecs = new ArrayList<IngredientSpecification>();
		if (cdata.currentKitchen != null ) {
			for (Recipe recipe : cdata.currentKitchenRecipes)
				ingSpecs.addAll(recipe.getIngredients());
		}
		else
			ingSpecs.addAll(cdata.editRecipe.getIngredients());
		
		for (IngredientSpecification ingSpec : ingSpecs) {
				ingSpec.setDistance(cdata.distances.getDistance(processedLocation, ingSpec.getExtraction().symbol));
		}		
			
		// change stdExtraction of all Ingredients
		for (Ingredient ing : cdata.ingredients) {
			ing.getExtractions().add(0, ing.getStdExtraction()); 
			ing.setStdExtraction(ing.getExtractions().get(0));
		}
		
		eventBus.fireEvent(new CollectionsChangedEvent());
	}
	
	/**
	 * After call to this function, editRecipe is != null for sure
	 * @param id
	 * @return true if recipe with id was found and propperly loaded, false if new recipe was created
	 */
	public boolean setEditRecipe(Long id) {
		boolean found = false;
		Recipe editRecipe = cdata.getUserRecipeByID(id);
		if (editRecipe != null) {
			found = true;
			cdata.editRecipe = editRecipe;
		}
		else {
			editRecipe = cdata.getKitchenRecipeByID(id);
			if (editRecipe != null){
				found = true;
				cdata.editRecipe = editRecipe;
				changeKitchenRecipes(editRecipe.getKitchenId());
			}
		}		
		
		if (!found)
			createRecipe();
		return found;
	}


	
	
	/**
	 * This function may proof to be useful for a more fuzzy matching!
	 * This is used for a matching in the recipes names
	 */
	private static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
       The difference between this impl. and the previous is that, rather 
       than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
       we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
       is the 'current working' distance array that maintains the newest distance cost
       counts as we iterate through the characters of String s.  Each time we increment
       the index of String t we are comparing, d is copied to p, the second int[].  Doing so
       allows us to retain the previous cost counts as required by the algorithm (taking 
       the minimum of the cost count to the left, up one, and diagonally up and to the left
       of the current cost count being calculated).  (Note that the arrays aren't really 
       copied anymore, just switched...this is clearly much better than cloning an array 
       or doing a System.arraycopy() each time  through the outer loop.)

       Effectively, the difference between the two implementations is this one does not 
       cause an out of memory condition when calculating the LD over two very large strings.
		 */

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}

		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}
	
	private void handleError(Throwable error) {
		Window.alert(error.getMessage()  +" "+error.getLocalizedMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(cdata.loginInfo.getLogoutUrl());
		}
	}
	
	
	// ----------------- Getters -----------------------------
	public List<Recipe> getPublicRecipes() {
		return cdata.publicRecipes;
	}

	public List<Recipe> getUserRecipes() {
		return cdata.userRecipes;
	}

	public List<Recipe> getKitchenRecipes() {
		return cdata.kitchenRecipes;
	}

	public List<Recipe> getCurrentKitchenRecipes() {
		return cdata.currentKitchenRecipes;
	}

	public Recipe getEditRecipe() {
		return cdata.editRecipe;
	}

	public List<Ingredient> getIngredients() {
		return cdata.ingredients;
	}
	
	public Ingredient getIngredientByID(long id) {
		return cdata.getIngredientByID(id);
	}

	public Distance getDist() {
		return cdata.distances;
	}

	public List<Workgroup> getKitchens() {
		return cdata.kitchens;
	}

	public LoginInfo getLoginInfo() {
		return cdata.loginInfo;
	}

	public Workgroup getCurrentKitchen() {
		return cdata.currentKitchen;
	}

	public int getCurrentMonth() {
		return cdata.currentMonth;
	}

	public String getCurrentLocation() {
		return cdata.currentLocation;
	}

	public void clearEditRecipe() {
		cdata.editRecipe = null;
	}

	
	public void setRecipeScope(RecipeScope recipeScope) {
		cdata.recipeScope = recipeScope;
	}
	public RecipeScope getRecipeScope() {
		if (cdata.recipeScope == null) 
			cdata.recipeScope = RecipeScope.PUBLIC;
		return cdata.recipeScope;
	}




}

