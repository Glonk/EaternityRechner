package ch.eaternity.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.eaternity.shared.Data;
import ch.eaternity.shared.Ingredient;
import ch.eaternity.shared.IngredientSpecification;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.comparators.NameComparator;
import ch.eaternity.shared.comparators.RezeptNameComparator;
import ch.eaternity.shared.comparators.RezeptValueComparator;
import ch.eaternity.shared.comparators.ValueComparator;

public class DataController {
	
	// ---------------------- Class Variables

	// here is the database of all data pushed to....
	public Data clientData = new Data();
	
	public  List<Recipe> selectedKitchenRecipes = new ArrayList<Recipe>();
	
	public ArrayList<Recipe> foundRezepte = new ArrayList<Recipe>();
	public ArrayList<Recipe> foundRezepteYours = new ArrayList<Recipe>();
	public ArrayList<Ingredient> foundIngredient = new ArrayList<Ingredient>();
	public ArrayList<Ingredient> foundAlternativeIngredients = new ArrayList<Ingredient>();
	
	// re-check this list
	private ArrayList<Recipe> foundRezepteHasDesc = new ArrayList<Recipe>();
	private ArrayList<Recipe> foundRezepteYoursHasDesc = new ArrayList<Recipe>();
	
	// this was in TopPanel before
	public  boolean isNotInKitchen = true;

	private int sortMethod;
	

	
	private List<Recipe> getYourRecipes() {
		if(isNotInKitchen){
			return clientData.getYourRezepte();
		} else {
			// this should only return the selected Kitchen ones
			return selectedKitchenRecipes;
		}
		
	}
	
	//search Procedure
	public int searchProcedure(String searchString) {
		int listsize = 0;
		
		if ((clientData.getIngredients() != null) ){

			
			foundIngredient.clear();
			foundAlternativeIngredients.clear();
			foundRezepte.clear();
			foundRezepteYours.clear();
			
			
			foundRezepteHasDesc.clear();
			foundRezepteYoursHasDesc.clear();
			// Zutaten
			// when the search string has a length 
			if(searchString.trim().length() != 0){

				String[] searches = searchString.split(" ");

				// consider strings with whitespaces, ssek for each word individually
				for(String search : searches)
				{
					// Zutaten
					// TODO this search algorithm is extremely slow, make faster
					for(Ingredient zutat : clientData.getIngredients()){
						if( search.trim().length() <= zutat.getSymbol().length() &&  zutat.getSymbol().substring(0, search.trim().length()).compareToIgnoreCase(search) == 0){
							//if(,search) < 3){
							//Window.alert(zutat.getSymbol().substring(0, search.trim().length()));
							if(!foundIngredient.contains(zutat)){
								zutat.noAlternative = true;
								foundIngredient.add(zutat);
//								displayIngredient(zutat);
							}
						}
					}
					// only look for alternatives, if there is only 1 result
					// TODO mark the alternatives as Special!
					if(foundIngredient.size() == 1){
						for(Ingredient zutat :foundIngredient){
							if(zutat.getAlternatives() != null){
								for(Long alternativen_id : zutat.getAlternatives()){
									for(Ingredient zutat2 : clientData.getIngredients()){
										if(zutat2.getId().equals(alternativen_id)){
											if(!foundAlternativeIngredients.contains(zutat2)){
												zutat2.noAlternative = false;
												foundAlternativeIngredients.add(zutat2);
//												displayIngredient(zutat2);
											}
										}
									}
								}
							}
							break;
						}
					}
				}
				// Rezepte
				if(	getYourRecipes() != null){
					searchRezept(searchString, getYourRecipes(), searches,true);
				}

				if(	clientData.getPublicRezepte() != null){
					searchRezept(searchString, clientData.getPublicRezepte(), searches,false);
				}
			} 
			// the search string was empty (so just display everything!)
			// TODO yet a little slow...
			else {
				for(Ingredient zutat : clientData.getIngredients()){
//					if(!foundIngredient.contains(zutat)){ // not necessary, as we are getting anyway all of them (no alternatives...)
						foundIngredient.add(zutat);
						zutat.noAlternative = true;
//					}
				}

				if(	getYourRecipes() != null && getYourRecipes().size() != 0){
					
					for(Recipe recipe : getYourRecipes()){
							foundRezepteYours.add(recipe);
					}
				} 
//				else {
//					yourMealsPanel.setVisible(false);
//				}

				if(	clientData.getPublicRezepte() != null){
					for(Recipe recipe : clientData.getPublicRezepte()){
						foundRezepte.add(recipe);
					}
				}

			}
			// all found items are now displayed
			
			// mark descendant also!!!!!
			
			// display recipes if there is no descendant of them in the list
			
			selectUnDescendantedRecipes(foundRezepte);
			
			selectUnDescendantedRecipes(foundRezepteYours);


			// sort and display results
			sortResults(sortMethod);
			
			
			// mark last position, cutt if needed
			listsize = foundIngredient.size() + foundAlternativeIngredients.size();
			
	
		}	

		
		return listsize;
	}


	private void searchRezept(String searchString, List<Recipe> allRezepte, String[] searches, boolean yours) {
		if(allRezepte != null){
			for(Recipe recipe : allRezepte){
				if(recipe != null){
					if( getLevenshteinDistance(recipe.getSymbol(),searchString) < 5){
						if(yours){
							foundRezepteYours.add(recipe);
						} else {
							foundRezepte.add(recipe);
						}
					}

					List<IngredientSpecification> zutatenRezept = recipe.getZutaten();
					if(zutatenRezept != null){
						int i = 0;
						for(IngredientSpecification ZutatImRezept : zutatenRezept ){
							if(ZutatImRezept != null){

								for(String search2 : searches){
									if( search2.trim().length() <= ZutatImRezept.getName().length() &&  ZutatImRezept.getName().substring(0, search2.trim().length()).compareToIgnoreCase(search2) == 0){
										//if (getLevenshteinDistance(ZutatImRezept.getName(),search2) < 2){
										i++;
									}
								}
								if(i == searches.length){
									if(yours){
										foundRezepteYours.add(recipe);
									} else {
										foundRezepte.add(recipe);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	// Select the KitchenRecipes
	// Always call updateResults after for propper loading!
	public void updateKitchenRecipesForSearch(Long id) {
		selectedKitchenRecipes.clear();
		for(Recipe recipe : clientData.KitchenRecipes){
			for(Long kitchenId : recipe.kitchenIds){
				if(kitchenId.equals(id))
				{
					selectedKitchenRecipes.add(recipe);
				}
			}
		}
	}


	/**
	 * The sorting functions
	 * 
	 * Call displayResults if for showing effects
	 */

	public void sortResults(int sortMethod) {
		this.sortMethod = sortMethod;
		
		switch(sortMethod){
		case 1:{
			//"co2-value"
			Collections.sort(foundIngredient,new ValueComparator());
			Collections.sort(foundAlternativeIngredients,new ValueComparator());
			Collections.sort(foundRezepte,new RezeptValueComparator());
			Collections.sort(foundRezepteYours,new RezeptValueComparator());
			break;
		}
		case 2:{
			// "popularity"

		}
		case 3:{
			//"saisonal"

		}
		case 4:{
			//"kategorisch"
			// vegetarisch
			// vegan
			// etc.
		}
		case 5:{
			//"alphabetisch"
			
			// could there be a better method to do this? like that:
			//			   ComparatorChain chain = new ComparatorChain();
			//			    chain.addComparator(new NameComparator());
			//			    chain.addComparator(new NumberComparator()
			
			Collections.sort(foundIngredient,new NameComparator());
			Collections.sort(foundAlternativeIngredients,new NameComparator());
			Collections.sort(foundRezepte,new RezeptNameComparator());
			Collections.sort(foundRezepteYours,new RezeptNameComparator());
		}
		}
	}
	
	
	
	/**
	 * The filtering function
	 */
	private static void selectUnDescendantedRecipes(List<Recipe> recipes) {
		
		// check if descendant is in the own list
		Iterator<Recipe> iter = recipes.iterator();
		while(iter.hasNext()){
			Recipe recipeHasDesc = iter.next();
			for( Recipe recipeIsPossibleDesc : recipes){
				// is the descendant in the own list
				if(recipeHasDesc.getDirectDescandentID().contains(recipeIsPossibleDesc.getId())){
					iter.remove();
					break;
				}
			}
		}

		// (also mark ancestors as old... by displaying the descendant)
		//TODO ( also display the old versions, but only in RecipeEditView) so one can check beck the history of a recipe

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
