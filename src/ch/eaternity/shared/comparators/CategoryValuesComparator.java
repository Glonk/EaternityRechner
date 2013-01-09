package ch.eaternity.shared.comparators;

import java.util.Comparator;
import java.util.List;

import ch.eaternity.shared.CatRyzer.CategoryValue;
import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

public class CategoryValuesComparator implements Comparator<CategoryValue> {
	
	public int compare(CategoryValue r1, CategoryValue r2) {
		Double o1 = r1.co2value.totalValue;
		Double o2 = r2.co2value.totalValue;

		return Double.valueOf(o2).compareTo(Double.valueOf(o1));

	}
}