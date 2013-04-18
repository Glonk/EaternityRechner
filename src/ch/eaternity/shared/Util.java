package ch.eaternity.shared;

import com.google.gwt.resources.client.ImageResource;

import ch.eaternity.client.resources.Resources;

public class Util {

	static public enum RecipeScope {
		PUBLIC, KITCHEN, USER
	}
	
	static public enum RecipePlace {
		VIEW, EDIT
	}
	
	static public enum SortMethod {
		CO2VALUE, SEASON, ALPHABETIC, NONE;
	}

	/**
	 * 
	 * @param co2value
	 *            in [ kg / kg ]
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public String getRecipeRatingBarUrl(Double co2value) {
		String url = "";
		if (co2value < 700) {
			url = Resources.INSTANCE.ratingBar1().getURL();
		} else if (co2value > 700 && co2value < 1500) {
			url = Resources.INSTANCE.ratingBar2().getURL();
		} else {
			url = Resources.INSTANCE.ratingBar3().getURL();
		}
		return url;
	}
	
	/**
	 * 
	 * @param co2value
	 *            in [ kg / kg ]
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public String getIngredientRatingBarUrl(Double co2value) {
		String url = "";
		if(co2value < 40){
			url = Resources.INSTANCE.ingredientRatingBar1().getURL();
		} else if(co2value < 120){
			url = Resources.INSTANCE.ingredientRatingBar2().getURL();
		} else {
			url = Resources.INSTANCE.ingredientRatingBar3().getURL();
		}
		return url;
	}

}
