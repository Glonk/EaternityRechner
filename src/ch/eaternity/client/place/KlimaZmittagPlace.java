package ch.eaternity.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class KlimaZmittagPlace extends Place
{
	private String placeName;
	
	public KlimaZmittagPlace(String token)
	{
		this.placeName = token;
	}

	public String getPlaceName()
	{
		return placeName;
	}

	@Prefix("commit")
	public static class Tokenizer implements PlaceTokenizer<KlimaZmittagPlace>
	{

		@Override
		public String getToken(KlimaZmittagPlace place)
		{
			return place.getPlaceName();
		}

		@Override
		public KlimaZmittagPlace getPlace(String token)
		{
			return new KlimaZmittagPlace(token);
		}

	}
	

}
