package ch.eaternity.client.place;

import ch.eaternity.shared.Util.RecipeScope;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class RechnerRecipeViewPlace extends Place{

	// used to indicate if we are in userrecipes, kitchenrecipes, public recipes...
	private RecipeScope recipeScope;
	
	/**
	 * 
	 * @param recipeScopeStr The Scope of the recipe view, must origin of Enum RecipeScope.toString() method
	 */
	public RechnerRecipeViewPlace(String recipeScopeStr)
	{
		if (recipeScopeStr.equals("USER")) 
			recipeScope = RecipeScope.USER;
		else if (recipeScopeStr.equals("KITCHEN"))
			recipeScope = RecipeScope.KITCHEN;
		else if (recipeScopeStr.equals("PUBLIC"))
			recipeScope = RecipeScope.PUBLIC;
		else 
			recipeScope = RecipeScope.PUBLIC;
	}

	public RecipeScope getRecipeScope()
	{
		return recipeScope;
	}

	@Prefix("view") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<RechnerRecipeViewPlace>
	{

		@Override
		public String getToken(RechnerRecipeViewPlace place)
		{
			return place.getRecipeScope().toString();
		}

		@Override
		public RechnerRecipeViewPlace getPlace(String token)
		{
			return new RechnerRecipeViewPlace(token);
		}

	}
}

