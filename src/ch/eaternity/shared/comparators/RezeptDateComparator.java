package ch.eaternity.shared.comparators;

import java.util.Date;
import java.util.Comparator;
import java.util.List;

import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

public class RezeptDateComparator implements Comparator<Recipe> {
	public int compare(Recipe r1, Recipe r2) {
		Date o1 = r1.cookingDate;
		Date o2 = r2.cookingDate;
		if(o1 != null && o2 != null){
		return o1.compareTo(o2);
		} else {
			return 0;
		}
	}
}