package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class EaternityRechnerPlace extends Place
{
	private String placeName;
	
	public EaternityRechnerPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("menu") // with "!" -> "!menu" this one gets indexed
	public static class Tokenizer implements PlaceTokenizer<EaternityRechnerPlace>
	{

		@Override
		public String getToken(EaternityRechnerPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public EaternityRechnerPlace getPlace(String token)
		{
			return new EaternityRechnerPlace(token);
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
