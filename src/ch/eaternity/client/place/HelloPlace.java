package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class HelloPlace extends Place
{
	private String placeName;
	
	public HelloPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("serv")
	public static class Tokenizer implements PlaceTokenizer<HelloPlace>
	{

		@Override
		public String getToken(HelloPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public HelloPlace getPlace(String token)
		{
			return new HelloPlace(token);
		}

	}
	
//	@Override
//	protected Place getPlace(String token)
//	{
//		return new HelloPlace(token);
//	}
//
//	@Override
//	protected Activity getActivity()
//	{
//		return new HelloActivity("David");
//	}
}
