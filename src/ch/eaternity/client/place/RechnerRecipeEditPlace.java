package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class RechnerRecipeEditPlace extends Place {
	private String placeName;
	
	public RechnerRecipeEditPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("edit") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<RechnerRecipeEditPlace>
	{

		@Override
		public String getToken(RechnerRecipeEditPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public RechnerRecipeEditPlace getPlace(String token)
		{
			return new RechnerRecipeEditPlace(token);
		}

	}
}
