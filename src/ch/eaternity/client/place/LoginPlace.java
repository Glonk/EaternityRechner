package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class LoginPlace extends Place
{
	private String placeName;
	
	public LoginPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("login")
	public static class Tokenizer implements PlaceTokenizer<LoginPlace>
	{

		@Override
		public String getToken(LoginPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public LoginPlace getPlace(String token)
		{
			return new LoginPlace(token);
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
