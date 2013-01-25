package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class RechnerRecipeEditPlace extends Place {
	
	// null id means the recipe to edit is found in clientdata (not saved yet, duplicated or newly created)
	private String id;
	
	public RechnerRecipeEditPlace(String id)
	{
		this.id = id;
	}

	public String getID()
	{
		return id;
	}

	@Prefix("edit") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<RechnerRecipeEditPlace>
	{

		@Override
		public String getToken(RechnerRecipeEditPlace place)
		{
			return place.getID();
		}

		@Override
		public RechnerRecipeEditPlace getPlace(String token)
		{
			return new RechnerRecipeEditPlace(token);
		}

	}
}
