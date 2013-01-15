package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Util {
	static public Date removeTime(Date date) {    
	    Calendar cal = Calendar.getInstance();  
	    cal.setTime(date);  
	    cal.set(Calendar.HOUR_OF_DAY, 0);  
	    cal.set(Calendar.MINUTE, 0);  
	    cal.set(Calendar.SECOND, 0);  
	    cal.set(Calendar.MILLISECOND, 0);  
	    return cal.getTime(); 
	}

	/**
	 * remove all descendanted recipes where the descendants must be in the own list
	 */
	public static void removeDescendantedRecipes(List<Recipe> recipes) {
		
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
	}
	
	public static List<Recipe> getUnDescendantedRecipes(List<Recipe> recipes) {
		List<Recipe> unDescRecipes = new ArrayList<Recipe>();
		unDescRecipes.addAll(recipes);
		removeDescendantedRecipes(unDescRecipes);
		return unDescRecipes;
	}
	
	public static void markDescendant(List<Recipe> recipesList) {
		for( Recipe checkRecipe: recipesList){
			// has ancestor...
			if(checkRecipe.getDirectAncestorID() != null){
				for( Recipe markRecipe: recipesList){
					if(markRecipe.getId().equals(checkRecipe.getDirectAncestorID())){
						// found descendants and mark him
						markRecipe.addDirectDescandentID(checkRecipe.getId());
					}
				}			
			}
		}
	}
	
}
