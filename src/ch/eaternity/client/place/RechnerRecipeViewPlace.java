package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class RechnerRecipeViewPlace extends Place{

	private String placeName;
	
	public RechnerRecipeViewPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("edit") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<RechnerRecipeViewPlace>
	{

		@Override
		public String getToken(RechnerRecipeViewPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public RechnerRecipeViewPlace getPlace(String token)
		{
			return new RechnerRecipeViewPlace(token);
		}

	}
}

