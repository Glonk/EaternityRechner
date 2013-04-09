package ch.eaternity.shared;

public class Util {

	static public enum RecipeScope {
		PUBLIC, KITCHEN, USER
	}
	
	static public enum RecipePlace {
		VIEW, EDIT
	}

	/**
	 * 
	 * @param co2value
	 *            in [ kg / kg ]
	 * @return
	 */
	static public String getRecipeRatingBarUrl(Double co2value) {
		String url = "";
		if (co2value < 700) {
			url = "/images/rating_bars.png";
		} else if (co2value > 700 && co2value < 1500) {
			url = "/images/rating_bars.png";
		} else {
			url = "/images/rating_bars.png";
		}
		return url;
	}

}
