package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ch.eaternity.client.events.LoadedDataEvent;
import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.Util;
import ch.eaternity.shared.Workgroup;
import ch.eaternity.shared.comparators.NameComparator;
import ch.eaternity.shared.comparators.RezeptNameComparator;
import ch.eaternity.shared.comparators.RezeptValueComparator;
import ch.eaternity.shared.comparators.ValueComparator;

public class DataController {
	
	// ---------------------- Class Variables ----------------------

	// here is the database of all data pushed to....
	public ClientData clientData = new ClientData();

	public  boolean isInKitchen;
	public int currentKitchen;

	// ---------------------- public Methods ----------------------
	
	public DataController() {
		
	}

	public void loadData() {
		dataRpcService.getData(new AsyncCallback<Data>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(ClientData data) {
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
				dao.isInKitchen = false;
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
						dao.isInKitchen = true;
						display.getTopPanel().selectedKitchen = lastKitchen;
						display.getSearchPanel().yourRecipesText.setHTML(" in " + kitchenName + " Rezepten");
						dao.changeKitchenRecipes(display.getTopPanel().selectedKitchen.id);

					} 
				} 
				
				display.getSearchPanel().SearchInput.setText("");
				display.getSearchPanel().updateResults(" ");
				menuPreview.hide();

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
				for(Ingredient zutat : clientData.ingredients){
					if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
						if(!ingredients.contains(zutat)){
							zutat.noAlternative = true;
							ingredients.add(zutat);
						}
					}
				}
				// only look for alternatives, if there is only 1 result
				if(ingredients.size() == 1){
					for(Ingredient zutat : ingredients){
						if(zutat.getAlternatives() != null){
							for(Long alternativen_id : zutat.getAlternatives()){
								for(Ingredient zutat2 : clientData.ingredients){
									if(zutat2.getId().equals(alternativen_id)){
										if(!alternatives.contains(zutat2)){
											zutat2.noAlternative = false;
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
			for(Ingredient zutat : clientData.ingredients){
				ingredients.add(zutat);
				zutat.noAlternative = true;
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
				List<IngredientSpecification> recipeIngredients = recipe.getZutaten();
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
		return searchRecipe(searchString, clientData.userRecipesNoDescs);
	}
	
	public List<Recipe> searchKitchenRecipes(String searchString) {
		return searchRecipe(searchString, clientData.currentKitchenRecipesNoDescs);
	}
	
	public List<Recipe> searchPublicRecipes(String searchString) {
		return searchRecipe(searchString, clientData.publicRecipesNoDescs);
	}
	
	
	
	
	// Select the KitchenRecipes
	// Always call updateResults after for propper loading!
	public void changeKitchenRecipes(Long id) {
		clientData.currentKitchenRecipes.clear();
		for(Recipe recipe : clientData.kitchenRecipes){
			for(Long kitchenId : recipe.kitchenIds){
				if(kitchenId.equals(id))
				{
					clientData.currentKitchenRecipes.add(recipe);
				}
			}
		}
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
}
