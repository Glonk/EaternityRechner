package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class RechnerRecipeViewPlace extends Place{

	// used to indicate if we are in userrecipes, kitchenrecipes, public recipes...
	private String recipeScope;
	
	public RechnerRecipeViewPlace(String token)
	{
		this.recipeScope = token;
	}

	public String getRecipeScope()
	{
		return recipeScope;
	}

	@Prefix("view") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<RechnerRecipeViewPlace>
	{

		@Override
		public String getToken(RechnerRecipeViewPlace place)
		{
			return place.getRecipeScope();
		}

		@Override
		public RechnerRecipeViewPlace getPlace(String token)
		{
			return new RechnerRecipeViewPlace(token);
		}

	}
}

