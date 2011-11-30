package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GoodbyePlace extends Place
{
	private String placeName;
	
	public GoodbyePlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("good_bye")
	public static class Tokenizer implements PlaceTokenizer<GoodbyePlace>
	{
		@Override
		public String getToken(GoodbyePlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public GoodbyePlace getPlace(String token)
		{
			return new GoodbyePlace(token);
		}
	}
	
}
